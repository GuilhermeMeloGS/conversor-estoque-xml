package br.com.drinkup.model;

public class Categoria {

    private final int idCategoria;
    private final String nome;
    private final String icone;

    public Categoria(int idCategoria, String nome, String icone) {
        this.idCategoria = idCategoria;
        this.nome = nome;
        this.icone = icone;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNome() {
        return nome;
    }

    public String getIcone() {
        return icone;
    }
}
