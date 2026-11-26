const Auth = (() => {
    const STORAGE_KEY = 'drinkup_session';

    function getSession() {
        try {
            return JSON.parse(sessionStorage.getItem(STORAGE_KEY)) || null;
        } catch {
            return null;
        }
    }

    function setSession(user) {
        sessionStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    }

    function clearSession() {
        sessionStorage.removeItem(STORAGE_KEY);
    }

    async function login(email, senha) {
        try {
            const response = await fetch('logarUsuario', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ email, senha })
            });
            if (!response.ok) return false;
            setSession(await response.json());
            return true;
        } catch {
            return false;
        }
    }
    function logout() {
        clearSession();
    }

    function getUser() {
        return getSession();
    }

    function isLoggedIn() {
        return getSession() !== null;
    }

    function isAdmin() {
        const s = getSession();
        return s && s.role === 'admin';
    }

    return { login, logout, getUser, setUser: setSession, isLoggedIn, isAdmin };
})();

function renderHeader() {
    const container = document.querySelector('.acoes-cabecalho');
    if (!container) return;

    const carrinho = JSON.parse(sessionStorage.getItem('drinkup_carrinho') || '[]');
    const totalItens = carrinho.reduce((s, i) => s + i.qty, 0);

    if (!Auth.isLoggedIn()) {
        container.innerHTML = `
            <a href="paginaLogin.html"    class="botao-cabecalho botao-login-cabecalho">Entrar</a>
            <a href="cadastroPessoa.html" class="botao-cabecalho">Cadastrar</a>`;
        return;
    }

    const user = Auth.getUser();
    const adminLink = Auth.isAdmin()
        ? `<a href="admin.html" class="botao-cabecalho botao-admin-cabecalho">⚙ Admin</a>`
        : '';

    container.innerHTML = `
        ${adminLink}
        <a href="minhaConta.html"    class="botao-cabecalho">Meus Pedidos</a>
        <a href="minhaConta.html"    class="botao-cabecalho">👤 ${user.nome.split(' ')[0]}</a>
        <a href="carrinho.html"      class="botao-cabecalho" id="btnCarrinho">
            🛒 <span class="contador-carrinho">${totalItens}</span>
        </a>
        <button onclick="fazerLogout()" class="botao-cabecalho botao-sair">Sair</button>`;
}

async function fazerLogout() {
    await fetch('logoutUsuario', { method: 'POST' }).catch(() => {});
    Auth.logout();
    window.location.href = 'index.html';
}

/* Carrinho helpers */
const Carrinho = (() => {
    const KEY = 'drinkup_carrinho';
    const get  = () => JSON.parse(sessionStorage.getItem(KEY) || '[]');
    const save = (items) => sessionStorage.setItem(KEY, JSON.stringify(items));

    // Popup que convida a pessoa a entrar; criado sob demanda para funcionar em qualquer página.
    function mostrarPopupLogin() {
        if (document.getElementById('popupLoginCarrinho')) return;

        if (!document.getElementById('popupLoginEstilo')) {
            const estilo = document.createElement('style');
            estilo.id = 'popupLoginEstilo';
            estilo.textContent = `
                #popupLoginCarrinho { position: fixed; inset: 0; background: rgba(0,0,0,.6); display: flex;
                    align-items: center; justify-content: center; z-index: 10000; padding: 20px; }
                #popupLoginCarrinho .popup-caixa { background: #fff; border-radius: 16px; padding: 32px 28px;
                    max-width: 380px; width: 100%; text-align: center; font-family: inherit;
                    box-shadow: 0 20px 50px rgba(0,0,0,.35); }
                #popupLoginCarrinho h3 { font-size: 20px; margin: 0 0 8px; color: #111; }
                #popupLoginCarrinho p { font-size: 14px; color: #555; line-height: 1.5; margin: 0 0 22px; }
                #popupLoginCarrinho .popup-acoes { display: flex; gap: 10px; }
                #popupLoginCarrinho button { flex: 1; height: 46px; border-radius: 10px; font-size: 14px;
                    font-weight: 800; cursor: pointer; border: 1.5px solid #ddd; background: #f7f7f7; color: #111; }
                #popupLoginCarrinho button.popup-entrar { background: #ffcc00; border-color: #ffcc00; }
                #popupLoginCarrinho button.popup-entrar:hover { background: #e8b600; }
            `;
            document.head.appendChild(estilo);
        }

        const overlay = document.createElement('div');
        overlay.id = 'popupLoginCarrinho';
        overlay.innerHTML = `
            <div class="popup-caixa" role="dialog" aria-modal="true" aria-labelledby="popupLoginTitulo">
                <h3 id="popupLoginTitulo">Faça login para continuar</h3>
                <p>Você precisa estar logado para adicionar produtos ao carrinho.</p>
                <div class="popup-acoes">
                    <button type="button" class="popup-cancelar">Agora não</button>
                    <button type="button" class="popup-entrar">Entrar</button>
                </div>
            </div>`;

        const fechar = () => overlay.remove();
        overlay.querySelector('.popup-cancelar').addEventListener('click', fechar);
        overlay.querySelector('.popup-entrar').addEventListener('click', () => {
            window.location.href = 'paginaLogin.html';
        });
        overlay.addEventListener('click', e => { if (e.target === overlay) fechar(); });
        document.addEventListener('keydown', function esc(e) {
            if (e.key === 'Escape') { fechar(); document.removeEventListener('keydown', esc); }
        });

        document.body.appendChild(overlay);
    }

    // Devolve false (sem adicionar) quando não há usuário logado.
    function adicionar(produto) {
        if (!Auth.isLoggedIn()) {
            mostrarPopupLogin();
            return false;
        }
        const items = get();
        const existe = items.find(i => i.id === produto.id);
        if (existe) { existe.qty++; } else { items.push({ ...produto, qty: 1 }); }
        save(items);
        renderHeader();
        return true;
    }

    function remover(id) {
        save(get().filter(i => i.id !== id));
        renderHeader();
    }

    function atualizar(id, qty) {
        const items = get();
        const item = items.find(i => i.id === id);
        if (item) { item.qty = Math.max(1, qty); }
        save(items);
        renderHeader();
    }

    function limpar() { save([]); renderHeader(); }

    return { get, adicionar, remover, atualizar, limpar };
})();
