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

    function adicionar(produto) {
        const items = get();
        const existe = items.find(i => i.id === produto.id);
        if (existe) { existe.qty++; } else { items.push({ ...produto, qty: 1 }); }
        save(items);
        renderHeader();
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

return { login, logout, getUser, setUser: setSession, isLoggedIn, isAdmin };
})();
