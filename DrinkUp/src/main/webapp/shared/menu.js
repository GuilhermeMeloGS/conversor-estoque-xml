// Menu único do site.
const menu = `
    <a href="index.html">Início</a>
    <a href="produtos">Produtos</a>
    <a href="index.html#categorias">Categorias</a>
    <a href="index.html#ofertas" class="destaque">Ofertas</a>
    <a href="carrinho.html">Carrinho</a>
`;

document.querySelector('.navegacao').innerHTML = menu;