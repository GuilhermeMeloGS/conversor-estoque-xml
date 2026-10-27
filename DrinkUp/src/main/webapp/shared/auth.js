/**
 * DrinkUp — Gerenciador de Estado de Autenticação (MOCK / Front-end)
 * Simula 3 perfis: visitante, cliente logado e administrador.
 * Futuramente substituir por chamada real ao backend Java/Servlet.
 */

const Auth = (() => {
    const STORAGE_KEY = 'drinkup_session';

    const MOCK_USERS = [
        { id: 1, nome: 'João Silva',    email: 'joao@email.com',  senha: '12345678', role: 'cliente' },
        { id: 2, nome: 'Admin DrinkUp', email: 'admin@drinkup.com', senha: 'admin123', role: 'admin'   },
    ];

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

    function login(email, senha) {
        const user = MOCK_USERS.find(u => u.email === email && u.senha === senha);
        if (!user) return false;
        const { senha: _, ...safeUser } = user;
        setSession(safeUser);
        return true;
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

    return { login, logout, getUser, isLoggedIn, isAdmin };
})();

/* ──────────────────────────────────────────────────────────────
   renderHeader()
   Injeta o HTML correto nas .acoes-cabecalho de qualquer página
   com base no estado de sessão atual.
────────────────────────────────────────────────────────────── */
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

function fazerLogout() {
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

    return { get, adicionar, remover, atualizar, limpar };
})();
