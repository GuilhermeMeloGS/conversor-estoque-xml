package br.com.drinkup.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Imagens dos produtos de exemplo gravadas pelo IniciarBancoServlet.
 *
 * As fotos ficam em src/main/imagensProdutos/ (copiadas para o classpath em /seed-imagens pelo pom.xml),
 * com o nome do arquivo igual ao nome do produto (ex.: "Cerveja Skol Lata 350ml.png").
 * Aceita .jpg, .jpeg, .png e .webp. Sem arquivo, é gerada uma imagem branca com o texto "SEM FOTO".
 */
final class ImagensBase {

    private ImagensBase() {
    }

    // WebP primeiro: quando existem várias versões do mesmo produto, vale a mais leve.
    private static final String[] EXTENSOES = {"webp", "png", "jpg", "jpeg"};

    /** Todas as fotos da pasta para o produto (uma por extensão), na ordem de preferência. */
    static List<byte[]> arquivos(String nomeDoProduto) {
        List<byte[]> fotos = new ArrayList<>();
        for (String extensao : EXTENSOES) {
            // No macOS os nomes com acento costumam estar decompostos (NFD); no zip do WAR a comparação é exata.
            for (Normalizer.Form forma : new Normalizer.Form[]{Normalizer.Form.NFC, Normalizer.Form.NFD}) {
                String nome = Normalizer.normalize(nomeDoProduto, forma);
                try (InputStream in = ImagensBase.class.getResourceAsStream("/seed-imagens/" + nome + "." + extensao)) {
                    if (in != null) {
                        fotos.add(in.readAllBytes());
                        break;
                    }
                } catch (Exception e) {
                    // tenta a próxima combinação
                }
            }
        }
        return fotos;
    }

    /** Placeholder "SEM FOTO": fundo branco, texto cinza centralizado. */
    static byte[] gerar() {
        System.setProperty("java.awt.headless", "true");

        try {
            int largura = 400;
            int altura = 400;

            BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = imagem.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g.setColor(Color.WHITE);
                g.fillRect(0, 0, largura, altura);

                g.setColor(new Color(0x9CA3AF));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 44));
                FontMetrics fm = g.getFontMetrics();
                String texto = "SEM FOTO";
                g.drawString(texto, (largura - fm.stringWidth(texto)) / 2, (altura - fm.getHeight()) / 2 + fm.getAscent());
            } finally {
                g.dispose();
            }

            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            ImageIO.write(imagem, "png", saida);
            return saida.toByteArray();

        } catch (Throwable e) {
            // Sem suporte gráfico/fontes no servidor: o produto fica sem imagem e o servlet devolve o placeholder SVG.
            return null;
        }
    }
}
