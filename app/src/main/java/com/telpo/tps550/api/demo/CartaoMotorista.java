package com.telpo.tps550.api.demo;

/**
 * Created by cardoso on 06/12/2016.
 */
public class CartaoMotorista {
    private int id;
    private String nome;
    private String cartao;
    private String codigoFuncionario;

    public CartaoMotorista(){}
    public CartaoMotorista(String nome, String cartao, String codigoFuncionario) {
        super();
        this.nome = nome;
        this.cartao = cartao;
        this.codigoFuncionario = codigoFuncionario;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCartao() {
        return cartao;
    }
    public void setCartao(String cartao) {
        this.cartao = cartao;
    }
    public String getCodigoFuncionario() {
        return codigoFuncionario;
    }
    public void setCodigoFuncionario(String codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }
    @Override
    public String toString() {
        return "CartaoMotorista [nome=" + nome + ", cartao=" + cartao + ", codigoFuncionario=" + codigoFuncionario + "]";
    }

}
