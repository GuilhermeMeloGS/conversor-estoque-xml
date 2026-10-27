package br.com.drinkup.model;

import java.time.LocalDateTime;

public class Usuario {

    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private TipoUsuario tipo = TipoUsuario.Cliente;
    private LocalDateTime dataCadastro;

    public Usuario(LocalDateTime dataCadastro, String email, String nome, String senha, String sobrenome) {
        this.dataCadastro = dataCadastro;
        this.email = email;
        this.nome = nome;
        this.senha = senha;
        this.sobrenome = sobrenome;
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

    
}
