<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:url var="excluirProdutoUrl" value="/produtos/excluir"/>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DrinkUp | Catálogo de Bebidas</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700;900&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="listaProdutos.css">
</head>

<body>

    <!-- CABEÇALHO -->
    <header class="cabecalho">

        <a href="index.html" class="logo" aria-label="Página inicial DrinkUp">
            <img src="imagensHtml/logoDrinkUp.png" alt="Logo Drink Up" class="logo-imagem">
        </a>

        <nav class="navegacao" aria-label="Menu principal">
            <a href="index.html">Início</a>
            <a href="produtos" class="ativo">Produtos</a>
            <a href="index.html#categorias">Categorias</a>
        </nav>

        <div class="acoes-cabecalho">
            <a href="paginaLogin.html" class="botao-cabecalho botao-login-cabecalho">Entrar</a>
            <a href="carrinho.html" class="botao-cabecalho" id="btnCarrinho">
                🛒 Carrinho <span class="contador-carrinho" id="contadorCarrinho">0</span>
            </a>
        </div>

    </header>

    <!-- CONTEÚDO PRINCIPAL DA LOJA -->
    <main class="conteudo-loja">

        <!-- CABEÇALHO DA LOJA -->
        <div class="cabecalho-loja">
            <div class="titulos-loja">
                <h1>Destaques & Catálogo</h1>
                <p>Escolha suas bebidas favoritas direto do estoque para sua casa</p>
            </div>

            <div class="total-produtos-destaque">
                Exibindo <strong id="numeroProdutos">0</strong> produtos
            </div>
        </div>

        <!-- BARRA DE FILTROS E PESQUISA -->
        <section class="barra-filtros-loja" aria-label="Filtros da loja">

            <div class="campo-busca-loja">
                <span class="icone-busca">🔍</span>
                <input type="text" id="campoBusca" placeholder="Buscar por nome, marca ou categoria..." aria-label="Buscar bebidas">
            </div>

            <div class="opcoes-filtro-loja">
                <select id="filtroCategoria" class="seletor-loja" aria-label="Filtrar por Categoria">
                    <option value="">Todas as Categorias</option>
                    <c:forEach var="categoria" items="${categorias}">
                        <option value="<c:out value='${categoria.nome}'/>"><c:out value="${categoria.nome}"/></option>
                    </c:forEach>
                </select>

                <select id="filtroOrdenacao" class="seletor-loja" aria-label="Ordenar produtos">
                    <option value="padrao">Ordenar: Destaques</option>
                    <option value="menor-preco">Menor Preço</option>
                    <option value="maior-preco">Maior Preço</option>
                    <option value="nome-az">Nome (A - Z)</option>
                </select>

                <button type="button" id="btnLimparFiltros" class="btn-limpar-filtros">
                    ✕ Limpar
                </button>
            </div>

        </section>

        <!-- CHIPS DE ATALHO DE CATEGORIAS -->
        <div class="chips-categorias" id="chipsCategorias">
            <button class="chip-btn ativo" data-cat="">Todos</button>
            <c:forEach var="categoria" items="${categorias}">
                <button class="chip-btn" data-cat="<c:out value='${categoria.nome}'/>"><c:out value="${categoria.icone}"/> <c:out value="${categoria.nome}"/></button>
            </c:forEach>
        </div>

        <!-- GRADE DE PRODUTOS / VITRINE DA LOJA -->
        <section class="grade-produtos" id="gradeProdutos">

            <%-- PRODUTOS E CATEGORIAS VINDOS DO BANCO (ProdutoDao / CategoriaDao via ListaProdutosServlet) --%>
            <c:forEach var="produto" items="${produtos}">
                <c:url var="imagemProdutoUrl" value="/imagem-produto">
                    <c:param name="id" value="${produto.idProduto}"/>
                </c:url>
                <article class="card-produto" data-id="${produto.idProduto}" data-categoria="<c:out value='${produto.nomeCategoria}'/>" data-nome="<c:out value='${produto.nomeDoProduto}'/>" data-preco="${produto.preco}" data-imagem="${imagemProdutoUrl}" data-icone="<c:out value='${produto.iconeCategoria}'/>">
                    <div class="imagem-container">
                        <%-- Sem foto, o servlet /imagem-produto devolve o placeholder "SEM FOTO". --%>
                        <img class="imagem-produto-foto" src="${imagemProdutoUrl}" alt="<c:out value='${produto.nomeDoProduto}'/>" loading="lazy">
                    </div>

                    <div class="detalhes-produto">
                        <span class="categoria-tag"><c:out value="${produto.nomeCategoria}"/></span>
                        <h2 class="titulo-produto"><c:out value="${produto.nomeDoProduto}"/></h2>
                        <p class="descricao-produto"><c:out value="${produto.descricao}"/></p>

                        <div class="bloco-precos">
                            <div class="linha-preco">
                                <span class="preco-atual">
                                    R$ <fmt:formatNumber value="${produto.preco}" minFractionDigits="2" maxFractionDigits="2"/>
                                </span>
                            </div>
                            <div class="parcelamento">${produto.volumeML} ml • Em estoque: ${produto.quantidadeEstoque} unid.</div>
                        </div>

                        <c:choose>
                            <c:when test="${produto.quantidadeEstoque > 0}">
                                <div class="info-estoque-disponivel">
                                    <span>●</span> Disponível para compra
                                </div>
                                <button type="button" class="botao-comprar" onclick="adicionarAoCarrinho(this)">
                                    🛒 Comprar
                                </button>
                            </c:when>
                            <c:otherwise>
                                <div class="info-estoque-disponivel">
                                    <span>●</span> Esgotado
                                </div>
                                <button type="button" class="botao-comprar" disabled>
                                    Indisponível
                                </button>
                            </c:otherwise>
                        </c:choose>

                        <div class="acoes-produto">
                            <a href="detalheProduto.html?id=${produto.idProduto}" class="botao-detalhe">Ver detalhes</a>
                        </div>

                        <%-- Editar/Excluir: exibidos apenas para administradores (ver script no fim da página) --%>
                        <div class="acoes-produto acoes-admin" style="display:none;">
                            <c:url var="editarProdutoUrl" value="/cadastroBebida.html">
                                <c:param name="id" value="${produto.idProduto}"/>
                            </c:url>
                            <a href="${editarProdutoUrl}" class="botao-detalhe">Editar</a>
                            <form action="${excluirProdutoUrl}" method="post" onsubmit="return confirm('Excluir este produto?');">
                                <input type="hidden" name="id" value="${produto.idProduto}">
                                <button type="submit" class="botao-detalhe">Excluir</button>
                            </form>
                        </div>
                    </div>
                </article>
            </c:forEach>

            <!-- MENSAGEM QUANDO NENHUM PRODUTO FOR ENCONTRADO -->
            <div class="estado-vazio" id="estadoVazio">
                <div class="icone-vazio">🔎</div>
                <h3>Nenhuma bebida encontrada</h3>
                <p>Tente ajustar os termos de pesquisa ou remover os filtros selecionados.</p>
            </div>

        </section>

    </main>

    <!-- NOTIFICAÇÃO TOAST AO ADICIONAR NO CARRINHO -->
    <div class="toast-carrinho" id="toastCarrinho">
        <span>✓</span>
        <span id="toastMensagem">Produto adicionado ao carrinho!</span>
    </div>

    <!-- RODAPÉ -->
    <footer class="rodape">

        <div>
            <div class="logo-nome">DRINK UP</div>
            <p class="descricao-rodape">
                Distribuidora de bebidas<br>
                &copy; 2026 DrinkUp. Projeto acadêmico.
            </p>
        </div>

        <nav class="links-rodape" aria-label="Links do rodapé">
            <a href="index.html">Início</a>
            <a href="produtos">Produtos</a>
            <a href="index.html#categorias">Categorias</a>
            <a href="paginaLogin.html">Entrar</a>
        </nav>

    </footer>

    <script src="shared/auth.js"></script>
    <!-- SCRIPT DE FILTROS, BUSCA E CARRINHO -->
    <script>
        renderHeader();
        if (Auth.isAdmin()) {
            document.querySelectorAll('.acoes-admin').forEach(el => el.style.display = '');
        }

        function adicionarAoCarrinho(botao) {
            const card = botao.closest('.card-produto');
            const nome = card.dataset.nome;
            const adicionado = Carrinho.adicionar({
                id: Number(card.dataset.id),
                nome,
                preco: Number(card.dataset.preco),
                icone: card.dataset.icone,
                imagem: card.dataset.imagem || null
            });
            if (!adicionado) return;
            document.getElementById('toastMensagem').textContent = `"${nome}" adicionado ao carrinho!`;
            const toast = document.getElementById('toastCarrinho');
            toast.classList.add('visivel');
            setTimeout(() => toast.classList.remove('visivel'), 2500);
        }

        document.addEventListener('DOMContentLoaded', function () {
            const campoBusca = document.getElementById('campoBusca');
            const filtroCategoria = document.getElementById('filtroCategoria');
            const filtroOrdenacao = document.getElementById('filtroOrdenacao');
            const btnLimpar = document.getElementById('btnLimparFiltros');
            const gradeProdutos = document.getElementById('gradeProdutos');
            const cards = Array.from(gradeProdutos.querySelectorAll('.card-produto'));
            const numeroProdutos = document.getElementById('numeroProdutos');
            const estadoVazio = document.getElementById('estadoVazio');
            const chipBtns = document.querySelectorAll('.chip-btn');

            function atualizarContagem() {
                const visiveis = cards.filter(c => c.style.display !== 'none').length;
                numeroProdutos.textContent = visiveis;
                estadoVazio.style.display = visiveis === 0 ? 'block' : 'none';
            }

            function filtrar() {
                const termo = campoBusca.value.trim().toLowerCase();
                const categoria = filtroCategoria.value.toLowerCase();

                cards.forEach(card => {
                    const texto = card.innerText.toLowerCase();
                    const cardCat = (card.getAttribute('data-categoria') || '').toLowerCase();

                    const matchBusca = !termo || texto.includes(termo);
                    const matchCat = !categoria || cardCat === categoria;

                    card.style.display = (matchBusca && matchCat) ? 'flex' : 'none';
                });

                atualizarContagem();
            }

            function ordenar() {
                const tipo = filtroOrdenacao.value;
                const cardsAtuais = Array.from(gradeProdutos.querySelectorAll('.card-produto'));

                cardsAtuais.sort((a, b) => {
                    if (tipo === 'menor-preco') {
                        return parseFloat(a.dataset.preco) - parseFloat(b.dataset.preco);
                    } else if (tipo === 'maior-preco') {
                        return parseFloat(b.dataset.preco) - parseFloat(a.dataset.preco);
                    } else if (tipo === 'nome-az') {
                        return (a.dataset.nome || '').localeCompare(b.dataset.nome || '');
                    } else {
                        return parseInt(a.dataset.id) - parseInt(b.dataset.id);
                    }
                });

                cardsAtuais.forEach(card => gradeProdutos.appendChild(card));
                filtrar();
            }

            campoBusca.addEventListener('input', filtrar);
            filtroCategoria.addEventListener('change', () => {
                const valor = filtroCategoria.value;
                chipBtns.forEach(btn => {
                    btn.classList.toggle('ativo', btn.dataset.cat === valor);
                });
                filtrar();
            });

            filtroOrdenacao.addEventListener('change', ordenar);

            chipBtns.forEach(btn => {
                btn.addEventListener('click', () => {
                    chipBtns.forEach(b => b.classList.remove('ativo'));
                    btn.classList.add('ativo');
                    filtroCategoria.value = btn.dataset.cat;
                    filtrar();
                });
            });

            btnLimpar.addEventListener('click', () => {
                campoBusca.value = '';
                filtroCategoria.value = '';
                filtroOrdenacao.value = 'padrao';
                chipBtns.forEach(b => b.classList.toggle('ativo', b.dataset.cat === ''));
                ordenar();
            });

            atualizarContagem();
        });
    </script>

</body>

</html>
