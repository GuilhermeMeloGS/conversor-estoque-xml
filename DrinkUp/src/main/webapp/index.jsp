
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DrinkUp | Distribuidora de Bebidas</title>

    <style>
        :root {
            --yellow: #ffcc00;
            --yellow-dark: #e8b600;
            --black: #111111;
            --gray: #777777;
            --light: #f7f7f7;
            --white: #ffffff;
            --green: #0a9d50;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        html {
            scroll-behavior: smooth;
        }

        body {
            font-family: Arial, Helvetica, sans-serif;
            color: var(--black);
            background: var(--white);
        }

        a {
            text-decoration: none;
            color: inherit;
        }

        button {
            font: inherit;
            cursor: pointer;
        }

        /* CABEÇALHO */

        .header {
            min-height: 76px;
            padding: 12px 5.5%;
            background: var(--black);
            color: var(--white);
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 24px;
            border-bottom: 3px solid var(--yellow);
        }

        .logo {
            display: flex;
            align-items: center;
            gap: 11px;
            flex-shrink: 0;
        }

        .logo-icon {
            width: 43px;
            height: 43px;
            background: var(--yellow);
            color: var(--black);
            border-radius: 12px;
            display: grid;
            place-items: center;
            font-size: 26px;
            font-weight: 900;
        }

        .logo-name {
            font-size: 22px;
            font-weight: 900;
            letter-spacing: -.8px;
        }

        .logo-subtitle {
            margin-top: 3px;
            color: var(--yellow);
            font-size: 10px;
            letter-spacing: 1px;
        }

        .navigation {
            display: flex;
            align-items: center;
            gap: 28px;
        }

        .navigation a {
            color: #eeeeee;
            font-size: 14px;
            transition: color .2s;
        }

        .navigation a:hover,
        .navigation .highlight {
            color: var(--yellow);
        }

        .header-actions {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .header-button {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            min-height: 40px;
            padding: 0 15px;
            border: 1px solid #444444;
            border-radius: 8px;
            color: var(--white);
            font-size: 13px;
            white-space: nowrap;
            transition: .2s;
        }

        .header-button:hover {
            border-color: var(--yellow);
            color: var(--yellow);
        }

        .login-button {
            background: var(--yellow);
            border-color: var(--yellow);
            color: var(--black);
            font-weight: 800;
        }

        .login-button:hover {
            background: var(--yellow-dark);
            color: var(--black);
        }

        /* BANNER PRINCIPAL */

        .hero {
            padding: 65px 7%;
            display: grid;
            grid-template-columns: 1fr 1fr;
            align-items: center;
            gap: 55px;
            background: #fffaf0;
        }

        .eyebrow {
            display: inline-block;
            padding: 8px 13px;
            border-radius: 30px;
            background: #fff0b3;
            color: #785b00;
            font-size: 11px;
            font-weight: 800;
            letter-spacing: .7px;
            margin-bottom: 22px;
        }

        .hero h1 {
            font-size: clamp(38px, 4.5vw, 60px);
            line-height: 1.08;
            letter-spacing: -2px;
            margin-bottom: 20px;
        }

        .hero h1 span {
            color: #d6a600;
        }

        .hero-description {
            max-width: 500px;
            color: #666666;
            font-size: 15px;
            line-height: 1.8;
            margin-bottom: 27px;
        }

        .hero-actions {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }

        .button-primary,
        .button-secondary {
            min-height: 47px;
            padding: 0 20px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 800;
            transition: .2s;
        }

        .button-primary {
            background: var(--yellow);
            color: var(--black);
            border: 1px solid var(--yellow);
        }

        .button-primary:hover {
            background: var(--yellow-dark);
            transform: translateY(-2px);
        }

        .button-secondary {
            border: 1px solid #dddddd;
            background: var(--white);
        }

        .button-secondary:hover {
            border-color: var(--black);
        }

        .hero-benefits {
            display: flex;
            flex-wrap: wrap;
            gap: 18px;
            margin-top: 28px;
        }

        .hero-benefits span {
            color: #555555;
            font-size: 12px;
        }

        .hero-benefits strong {
            color: #b58d00;
            margin-right: 5px;
        }

        .hero-visual {
            min-height: 330px;
            padding: 38px;
            border-radius: 18px;
            background:
                radial-gradient(circle at 80% 20%, #443300 0, transparent 40%),
                linear-gradient(140deg, #292929, #101010);
            color: var(--white);
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: flex-start;
            overflow: hidden;
            position: relative;
        }

        .hero-visual::after {
            content: "D";
            position: absolute;
            right: -15px;
            bottom: -100px;
            font-size: 330px;
            font-weight: 900;
            line-height: 1;
            color: rgba(255, 204, 0, .07);
            pointer-events: none;
        }

        .hero-visual > * {
            position: relative;
            z-index: 1;
        }

        .hero-visual .visual-icon {
            font-size: 72px;
            margin-bottom: 20px;
        }

        .visual-label {
            color: var(--yellow);
            font-size: 10px;
            font-weight: 800;
            letter-spacing: 1.5px;
            margin-bottom: 12px;
        }

        .hero-visual h2 {
            font-size: 29px;
            margin-bottom: 12px;
        }

        .hero-visual p {
            color: #cccccc;
            font-size: 13px;
            line-height: 1.7;
            margin-bottom: 22px;
            max-width: 350px;
        }

        /* VANTAGENS */

        .advantages {
            padding: 25px 7%;
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 25px;
            background: var(--black);
            color: var(--white);
        }

        .advantage {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 13px;
        }

        .advantage-icon {
            display: grid;
            place-items: center;
            width: 44px;
            height: 44px;
            border-radius: 50%;
            background: #302800;
            color: var(--yellow);
            font-size: 20px;
            flex-shrink: 0;
        }

        .advantage h3 {
            font-size: 13px;
            margin-bottom: 5px;
        }

        .advantage p {
            color: #aaaaaa;
            font-size: 11px;
        }

        /* SEÇÕES */

        .section {
            padding: 55px 7%;
        }

        .section-heading {
            display: flex;
            justify-content: space-between;
            align-items: end;
            gap: 20px;
            margin-bottom: 28px;
        }

        .section-heading h2 {
            font-size: 28px;
            letter-spacing: -.7px;
        }

        .section-heading p {
            color: var(--gray);
            font-size: 13px;
            margin-top: 9px;
        }

        .section-link {
            color: #967300;
            font-size: 13px;
            font-weight: 800;
            white-space: nowrap;
        }

        /* CATEGORIAS */

        .categories {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 17px;
        }

        .category {
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 20px 16px;
            border: 1px solid #eeeeee;
            border-radius: 12px;
            transition: .2s;
        }

        .category:hover {
            border-color: var(--yellow);
            transform: translateY(-3px);
            box-shadow: 0 7px 20px rgba(0, 0, 0, .05);
        }

        .category-icon {
            width: 49px;
            height: 49px;
            display: grid;
            place-items: center;
            border-radius: 10px;
            background: #fff4c7;
            font-size: 24px;
            flex-shrink: 0;
        }

        .category h3 {
            font-size: 13px;
            margin-bottom: 5px;
        }

        .category p {
            font-size: 11px;
            color: var(--gray);
        }

        /* PRODUTOS */

        .products-section {
            background: #fafafa;
        }

        .products {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 20px;
        }

        .product-card {
            border: 1px solid #eeeeee;
            border-radius: 13px;
            background: var(--white);
            overflow: hidden;
            transition: .2s;
        }

        .product-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 25px rgba(0, 0, 0, .07);
        }

        .product-image {
            height: 175px;
            display: grid;
            place-items: center;
            background: #fff7dc;
            font-size: 64px;
        }

        .product-card:nth-child(2) .product-image {
            background: #eaf6eb;
        }

        .product-card:nth-child(3) .product-image {
            background: #fff0df;
        }

        .product-card:nth-child(4) .product-image {
            background: #e8f3ff;
        }

        .product-info {
            padding: 19px;
        }

        .product-category {
            color: #9b7600;
            font-size: 10px;
            font-weight: 800;
            text-transform: uppercase;
            margin-bottom: 8px;
        }

        .product-info h3 {
            font-size: 16px;
            margin-bottom: 8px;
        }

        .product-description {
            color: var(--gray);
            font-size: 12px;
            margin-bottom: 18px;
        }

        .product-bottom {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 10px;
        }

        .product-price {
            font-size: 19px;
            font-weight: 900;
        }

        .add-button {
            width: 38px;
            height: 38px;
            background: var(--yellow);
            border: none;
            border-radius: 9px;
            font-size: 22px;
        }

        .add-button:hover {
            background: var(--yellow-dark);
        }

        .product-stock {
            color: var(--green);
            font-size: 11px;
            margin-top: 9px;
        }

        /* CHAMADA FINAL */

        .cta {
            margin: 0 7% 55px;
            padding: 38px;
            border-radius: 15px;
            background: var(--yellow);
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 25px;
        }

        .cta h2 {
            font-size: 26px;
            margin-bottom: 10px;
        }

        .cta p {
            max-width: 560px;
            font-size: 13px;
            line-height: 1.7;
        }

        .cta .button-primary {
            background: var(--black);
            border-color: var(--black);
            color: var(--white);
            flex-shrink: 0;
        }

        .cta .button-primary:hover {
            background: #333333;
        }

        /* RODAPÉ */

        .footer {
            padding: 30px 7%;
            background: var(--black);
            color: var(--white);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 22px;
        }

        .footer-description {
            color: #aaaaaa;
            font-size: 12px;
            margin-top: 8px;
            line-height: 1.7;
        }

        .footer-links {
            display: flex;
            flex-wrap: wrap;
            gap: 22px;
            font-size: 12px;
        }

        .footer-links a:hover {
            color: var(--yellow);
        }

        /* RESPONSIVIDADE */

        @media (max-width: 1050px) {
            .header {
                padding-left: 4%;
                padding-right: 4%;
                flex-wrap: wrap;
            }

            .navigation {
                gap: 16px;
            }

            .hero {
                padding-left: 5%;
                padding-right: 5%;
                gap: 30px;
            }

            .categories,
            .products {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .section {
                padding-left: 5%;
                padding-right: 5%;
            }
        }

        @media (max-width: 720px) {
            .header {
                gap: 15px;
            }

            .logo-name {
                font-size: 19px;
            }

            .navigation {
                order: 3;
                width: 100%;
                overflow-x: auto;
                padding: 7px 0;
            }

            .header-actions {
                margin-left: auto;
            }

            .header-button {
                padding: 0 10px;
                font-size: 12px;
            }

            .hero {
                grid-template-columns: 1fr;
                padding: 40px 5%;
            }

            .hero h1 {
                font-size: 43px;
            }

            .hero-visual {
                min-height: 280px;
                padding: 27px;
            }

            .hero-visual h2 {
                font-size: 25px;
            }

            .advantages {
                grid-template-columns: 1fr;
                padding: 25px 7%;
                gap: 20px;
            }

            .advantage {
                justify-content: flex-start;
            }

            .section {
                padding: 42px 5%;
            }

            .section-heading {
                align-items: flex-start;
                flex-direction: column;
            }

            .categories,
            .products {
                grid-template-columns: 1fr;
            }

            .category {
                padding: 17px;
            }

            .product-image {
                height: 150px;
            }

            .cta {
                margin: 0 5% 40px;
                padding: 27px;
                align-items: flex-start;
                flex-direction: column;
            }

            .cta h2 {
                font-size: 23px;
            }

            .footer {
                padding: 30px 5%;
                align-items: flex-start;
                flex-direction: column;
            }
        }
    </style>
</head>

<body>

    <!-- CABEÇALHO -->
    <header class="header">

        <a href="#inicio" class="logo" aria-label="Página inicial DrinkUp">
            <div class="logo-icon">D</div>
            <div>
                <div class="logo-name">DRINK UP</div>
                <div class="logo-subtitle">DISTRIBUIDORA</div>
            </div>
        </a>

        <nav class="navigation" aria-label="Menu principal">
            <a href="#categorias">Cervejas</a>
            <a href="#categorias">Refrigerantes</a>
            <a href="#produtos">Combos</a>
            <a href="#ofertas" class="highlight">Ofertas</a>
        </nav>

        <div class="header-actions">
            <a href="#login" class="header-button login-button">Entrar</a>
            <a href="#carrinho" class="header-button">🛒 Carrinho</a>
        </div>

    </header>

    <main>

        <!-- APRESENTAÇÃO -->
        <section class="hero" id="inicio">

            <div class="hero-content">
                <span class="eyebrow">BEM-VINDO À DRINKUP</span>

                <h1>
                    Sua bebida,<br>
                    do <span>estoque até você.</span>
                </h1>

                <p class="hero-description">
                    Encontre suas bebidas favoritas em um só lugar.
                    Explore nosso catálogo, confira as opções disponíveis
                    e conheça os produtos da DrinkUp.
                </p>

                <div class="hero-actions">
                    <a href="#produtos" class="button-primary">
                        Explorar bebidas &rarr;
                    </a>

                    <a href="#categorias" class="button-secondary">
                        Ver categorias
                    </a>
                </div>

                <div class="hero-benefits">
                    <span><strong>✓</strong> Grandes marcas</span>
                    <span><strong>✓</strong> Catálogo organizado</span>
                    <span><strong>✓</strong> Compra prática</span>
                </div>
            </div>

            <div class="hero-visual">
                <div class="visual-icon">🍺</div>
                <span class="visual-label">SEU CATÁLOGO DE BEBIDAS</span>
                <h2>Encontre suas favoritas.</h2>
                <p>
                    Explore as categorias e descubra as opções
                    disponíveis para você.
                </p>
                <a href="#produtos" class="button-primary">
                    Ver produtos &rarr;
                </a>
            </div>

        </section>

        <!-- VANTAGENS -->
        <section class="advantages" aria-label="Vantagens">
            <div class="advantage">
                <div class="advantage-icon">♧</div>
                <div>
                    <h3>Variedade de bebidas</h3>
                    <p>Diferentes opções em um só lugar</p>
                </div>
            </div>

            <div class="advantage">
                <div class="advantage-icon">✓</div>
                <div>
                    <h3>Catálogo organizado</h3>
                    <p>Encontre produtos com facilidade</p>
                </div>
            </div>

            <div class="advantage">
                <div class="advantage-icon">★</div>
                <div>
                    <h3>Marcas conhecidas</h3>
                    <p>Confira os produtos em destaque</p>
                </div>
            </div>
        </section>

        <!-- CATEGORIAS -->
        <section class="section" id="categorias">

            <div class="section-heading">
                <div>
                    <h2>Explore as categorias</h2>
                    <p>Encontre a bebida ideal para cada ocasião.</p>
                </div>

                <a href="#produtos" class="section-link">
                    Ver produtos &rarr;
                </a>
            </div>

            <div class="categories">

                <a href="#produtos" class="category">
                    <div class="category-icon">🍺</div>
                    <div>
                        <h3>Cervejas</h3>
                        <p>Diferentes marcas e estilos</p>
                    </div>
                </a>

                <a href="#produtos" class="category">
                    <div class="category-icon">🥤</div>
                    <div>
                        <h3>Refrigerantes</h3>
                        <p>Opções para refrescar</p>
                    </div>
                </a>

                <a href="#produtos" class="category">
                    <div class="category-icon">🍹</div>
                    <div>
                        <h3>Combos</h3>
                        <p>Produtos para compartilhar</p>
                    </div>
                </a>

                <a href="#ofertas" class="category">
                    <div class="category-icon">✨</div>
                    <div>
                        <h3>Ofertas</h3>
                        <p>Confira os destaques</p>
                    </div>
                </a>

            </div>
        </section>

        <!-- PRODUTOS -->
        <section class="section products-section" id="produtos">

            <div class="section-heading">
                <div>
                    <h2>Produtos em destaque</h2>
                    <p>Confira algumas opções do catálogo DrinkUp.</p>
                </div>

                <a href="#categorias" class="section-link">
                    Explorar categorias &rarr;
                </a>
            </div>

            <div class="products">

                <article class="product-card">
                    <div class="product-image">🍺</div>
                    <div class="product-info">
                        <p class="product-category">Cerveja nacional</p>
                        <h3>Brahma Chopp</h3>
                        <p class="product-description">Lata 350 ml</p>
                        <div class="product-bottom">
                            <span class="product-price">R$ 8,90</span>
                            <button class="add-button" type="button"
                                    aria-label="Adicionar Brahma ao carrinho">+</button>
                        </div>
                        <p class="product-stock">● Disponível</p>
                    </div>
                </article>

                <article class="product-card">
                    <div class="product-image">🍺</div>
                    <div class="product-info">
                        <p class="product-category">Cerveja importada</p>
                        <h3>Heineken</h3>
                        <p class="product-description">Long Neck 330 ml</p>
                        <div class="product-bottom">
                            <span class="product-price">R$ 6,50</span>
                            <button class="add-button" type="button"
                                    aria-label="Adicionar Heineken ao carrinho">+</button>
                        </div>
                        <p class="product-stock">● Disponível</p>
                    </div>
                </article>

                <article class="product-card">
                    <div class="product-image">🍺</div>
                    <div class="product-info">
                        <p class="product-category">Cerveja nacional</p>
                        <h3>Skol</h3>
                        <p class="product-description">Lata 350 ml</p>
                        <div class="product-bottom">
                            <span class="product-price">R$ 4,20</span>
                            <button class="add-button" type="button"
                                    aria-label="Adicionar Skol ao carrinho">+</button>
                        </div>
                        <p class="product-stock">● Disponível</p>
                    </div>
                </article>

                <article class="product-card">
                    <div class="product-image">🥤</div>
                    <div class="product-info">
                        <p class="product-category">Refrigerante</p>
                        <h3>Coca-Cola</h3>
                        <p class="product-description">Garrafa 2 litros</p>
                        <div class="product-bottom">
                            <span class="product-price">R$ 9,90</span>
                            <button class="add-button" type="button"
                                    aria-label="Adicionar Coca-Cola ao carrinho">+</button>
                        </div>
                        <p class="product-stock">● Disponível</p>
                    </div>
                </article>

            </div>
        </section>

        <!-- OFERTAS -->
        <section class="cta" id="ofertas">
            <div>
                <h2>Conheça a DrinkUp!</h2>
                <p>
                    Explore o catálogo, conheça os produtos em destaque
                    e encontre suas bebidas favoritas.
                </p>
            </div>

            <a href="#produtos" class="button-primary">
                Ver catálogo &rarr;
            </a>
        </section>

    </main>

    <!-- RODAPÉ -->
    <footer class="footer">

        <div>
            <div class="logo-name">DRINK UP</div>
            <p class="footer-description">
                Distribuidora de bebidas<br>
                &copy; 2026 DrinkUp. Projeto acadêmico.
            </p>
        </div>

        <nav class="footer-links" aria-label="Links do rodapé">
            <a href="#inicio">Início</a>
            <a href="#categorias">Categorias</a>
            <a href="#produtos">Produtos</a>
            <a href="#login">Entrar</a>
        </nav>

    </footer>

</body>
</html>
