package com.telpo.tps550.api.demo;

/**
 * Created by cardoso on 25/10/2015.
 */
public class Cartao {
//numero Text status Text tipo Text anoMes Text
    //numero, status, tipo, anoMes
    private int id;
    private String numero;
    private String status;
    private String tipo;
    private String anoMes;
    private String anoMesAnterior;
    private String dataExpiracao;

    public Cartao(){}

    public Cartao(String status, String numero, String tipo, String anoMes, String anoMesAnterior, String dataExpiracao) {
        super();
        this.status = status;
        this.numero = numero;
        this.tipo = tipo;
        this.anoMes = anoMes;
        this.anoMesAnterior = anoMesAnterior;
        this.dataExpiracao = dataExpiracao;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAnoMes() {
        return anoMes;
    }
    public void setAnoMes(String anoMes) {
        this.anoMes = anoMes;
    }

    public String getdataExpiracao() {
        return dataExpiracao;
    }
    public void setdataExpiracao(String dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public String getAnoMesAnterior() {
        return anoMesAnterior;
    }
    public void setAnoMesAnterior(String anoMesAnterior) {
        this.anoMesAnterior = anoMesAnterior;
    }


    @Override
    public String toString() {
        return "Cartao [id=" + id + ", status=" + status + ", numero=" + numero + ", tipo=" + tipo + ", anoMes=" + anoMes + ", anoMesAnterior=" + anoMesAnterior +  ", dataExpiracao=" + dataExpiracao + "]";
    }
}
