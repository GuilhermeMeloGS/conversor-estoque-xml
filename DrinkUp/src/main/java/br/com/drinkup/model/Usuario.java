package br.com.drinkup.model;

import java.time.LocalDateTime;

public class Usuario {

    private int id;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private LocalDateTime dataCadastro;

    //Construtor 1 --- Para cadastro, usado pela CreateUsuarioServlet
    public Usuario(String email, String nome, String senha, String sobrenome) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.tipo = TipoUsuario.Cliente;
    }

    //Construtor 2 --- Leitura do banco
    public Usuario(int id, String nome, String sobrenome, String email, String senha,
    TipoUsuario tipo, LocalDateTime dataCadastro){
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.dataCadastro = dataCadastro;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public int getId() {
        return id;
    }

    
}
