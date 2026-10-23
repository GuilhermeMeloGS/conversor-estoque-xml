<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
            <a href="listaProdutos.jsp" class="ativo">Produtos</a>
            <a href="index.html#categorias">Categorias</a>
            <a href="index.html#ofertas" class="destaque">Ofertas</a>
        </nav>

        <div class="acoes-cabecalho">
            <a href="paginaLogin.html" class="botao-cabecalho botao-login-cabecalho">Entrar</a>
            <a href="#carrinho" class="botao-cabecalho" id="btnCarrinho">
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
                    <option value="Cervejas">Cervejas</option>
                    <option value="Refrigerantes">Refrigerantes</option>
                    <option value="Sucos">Sucos</option>
                    <option value="Energéticos">Energéticos</option>
                    <option value="Águas">Águas</option>
                    <option value="Vinhos">Vinhos</option>
                    <option value="Destilados">Destilados</option>
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
            <button class="chip-btn" data-cat="Cervejas">🍺 Cervejas</button>
            <button class="chip-btn" data-cat="Refrigerantes">🥤 Refrigerantes</button>
            <button class="chip-btn" data-cat="Sucos">🧃 Sucos</button>
            <button class="chip-btn" data-cat="Energéticos">⚡ Energéticos</button>
            <button class="chip-btn" data-cat="Vinhos">🍷 Vinhos</button>
            <button class="chip-btn" data-cat="Destilados">🥃 Destilados</button>
            <button class="chip-btn" data-cat="Águas">💧 Águas</button>
        </div>

        <!-- GRADE DE PRODUTOS / VITRINE DA LOJA -->
        <section class="grade-produtos" id="gradeProdutos">

            <c:choose>
                <%-- ITERAÇÃO DINÂMICA COM OS PRODUTOS DO BANCO DE DADOS H2 --%>
                <c:when test="${not empty produtos}">
                    <c:forEach var="produto" items="${produtos}">
                        <article class="card-produto" data-id="${produto.id}" data-categoria="${produto.nome}" data-nome="${produto.nome}" data-preco="${produto.preco}">
                            <div class="imagem-container">
                                <span class="tag-frete-gratis">🚚 Grátis</span>
                                <div class="icone-imagem-fallback">🍺</div>
                            </div>

                            <div class="detalhes-produto">
                                <span class="categoria-tag">Bebidas</span>
                                <h2 class="titulo-produto">${produto.nome}</h2>
                                <p class="descricao-produto">${produto.discricao}</p>

                                <div class="bloco-precos">
                                    <div class="linha-preco">
                                        <span class="preco-atual">
                                            R$ <fmt:formatNumber value="${produto.preco}" minFractionDigits="2" maxFractionDigits="2"/>
                                        </span>
                                    </div>
                                    <div class="parcelamento">Em estoque: ${produto.quantidadeEstoque} unid.</div>
                                </div>

                                <div class="info-estoque-disponivel">
                                    <span>●</span> Disponível para compra
                                </div>

                                <button type="button" class="botao-comprar" onclick="adicionarAoCarrinho('${produto.nome}')">
                                    🛒 Comprar
                                </button>
                            </div>
                        </article>
                    </c:forEach>
                </c:when>

                <%-- CARDS MODELO DE DEMONSTRAÇÃO QUANDO A LISTA DO H2 ESTIVER VAZIA --%>
                <c:otherwise>
                    <article class="card-produto" data-id="1" data-categoria="Cervejas" data-nome="Cerveja Heineken Long Neck 330ml" data-preco="9.90">
                        <div class="imagem-container">
                            <span class="tag-frete-gratis">🚚 Grátis</span>
                            <span class="tag-desconto">15% OFF</span>
                            <div class="icone-imagem-fallback">🍺</div>
                        </div>

                        <div class="detalhes-produto">
                            <span class="categoria-tag">Cervejas</span>
                            <h2 class="titulo-produto">Cerveja Heineken Long Neck 330ml</h2>
                            <p class="descricao-produto">Garrafa 330 ml • Cerveja Premium Puro Malte</p>

                            <div class="bloco-precos">
                                <div class="preco-original">R$ 11,90</div>
                                <div class="linha-preco">
                                    <span class="preco-atual">R$ 9,90</span>
                                    <span class="porcentagem-off">15% OFF</span>
                                </div>
                                <div class="parcelamento">Em até 3x de R$ 3,30 sem juros</div>
                            </div>

                            <div class="info-estoque-disponivel">
                                <span>●</span> Em estoque
                            </div>

                            <button type="button" class="botao-comprar" onclick="adicionarAoCarrinho('Cerveja Heineken 330ml')">
                                🛒 Comprar
                            </button>
                        </div>
                    </article>

                    <article class="card-produto" data-id="2" data-categoria="Refrigerantes" data-nome="Coca-Cola Original Lata 350ml" data-preco="4.50">
                        <div class="imagem-container">
                            <span class="tag-frete-gratis">🚚 Grátis</span>
                            <div class="icone-imagem-fallback">🥤</div>
                        </div>

                        <div class="detalhes-produto">
                            <span class="categoria-tag">Refrigerantes</span>
                            <h2 class="titulo-produto">Coca-Cola Original Lata 350ml</h2>
                            <p class="descricao-produto">Lata 350 ml • Sabor Original gelado</p>

                            <div class="bloco-precos">
                                <div class="preco-original">R$ 5,20</div>
                                <div class="linha-preco">
                                    <span class="preco-atual">R$ 4,50</span>
                                    <span class="porcentagem-off">13% OFF</span>
                                </div>
                                <div class="parcelamento">Preço especial de estoque</div>
                            </div>

                            <div class="info-estoque-disponivel">
                                <span>●</span> Em estoque
                            </div>

                            <button type="button" class="botao-comprar" onclick="adicionarAoCarrinho('Coca-Cola Lata 350ml')">
                                🛒 Comprar
                            </button>
                        </div>
                    </article>

                    <article class="card-produto" data-id="3" data-categoria="Cervejas" data-nome="Cerveja Brahma Chopp Lata 350ml" data-preco="3.80">
                        <div class="imagem-container">
                            <span class="tag-desconto">10% OFF</span>
                            <div class="icone-imagem-fallback">🍺</div>
                        </div>

                        <div class="detalhes-produto">
                            <span class="categoria-tag">Cervejas</span>
                            <h2 class="titulo-produto">Cerveja Brahma Chopp Lata 350ml</h2>
                            <p class="descricao-produto">Lata 350 ml • Cerveja Pilsen leve e refrescante</p>

                            <div class="bloco-precos">
                                <div class="preco-original">R$ 4,20</div>
                                <div class="linha-preco">
                                    <span class="preco-atual">R$ 3,80</span>
                                    <span class="porcentagem-off">10% OFF</span>
                                </div>
                                <div class="parcelamento">Em até 2x no cartão</div>
                            </div>

                            <div class="info-estoque-disponivel">
                                <span>●</span> Em estoque
                            </div>

                            <button type="button" class="botao-comprar" onclick="adicionarAoCarrinho('Cerveja Brahma Chopp')">
                                🛒 Comprar
                            </button>
                        </div>
                    </article>

                    <article class="card-produto" data-id="4" data-categoria="Energéticos" data-nome="Red Bull Energy Drink 250ml" data-preco="11.90">
                        <div class="imagem-container">
                            <span class="tag-frete-gratis">🚚 Grátis</span>
                            <div class="icone-imagem-fallback">⚡</div>
                        </div>

                        <div class="detalhes-produto">
                            <span class="categoria-tag">Energéticos</span>
                            <h2 class="titulo-produto">Red Bull Energy Drink 250ml</h2>
                            <p class="descricao-produto">Lata 250 ml • Vitaliza mente e corpo</p>

                            <div class="bloco-precos">
                                <div class="preco-original">R$ 13,90</div>
                                <div class="linha-preco">
                                    <span class="preco-atual">R$ 11,90</span>
                                    <span class="porcentagem-off">14% OFF</span>
                                </div>
                                <div class="parcelamento">Em até 3x sem juros</div>
                            </div>

                            <div class="info-estoque-disponivel">
                                <span>●</span> Em estoque
                            </div>

                            <button type="button" class="botao-comprar" onclick="adicionarAoCarrinho('Red Bull 250ml')">
                                🛒 Comprar
                            </button>
                        </div>
                    </article>
                </c:otherwise>
            </c:choose>

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
            <a href="listaProdutos.jsp">Produtos</a>
            <a href="index.html#categorias">Categorias</a>
            <a href="paginaLogin.html">Entrar</a>
        </nav>

    </footer>

    <!-- SCRIPT DE FILTROS, BUSCA E CARRINHO -->
    <script>
        let carrinhoCount = 0;

        function adicionarAoCarrinho(nome) {
            carrinhoCount++;
            document.getElementById('contadorCarrinho').textContent = carrinhoCount;

            const toast = document.getElementById('toastCarrinho');
            document.getElementById('toastMensagem').textContent = `"${nome}" adicionado ao carrinho!`;
            toast.classList.add('visivel');

            setTimeout(() => {
                toast.classList.remove('visivel');
            }, 2500);
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
