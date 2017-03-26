package com.telpo.tps550.api.demo;

/**
 * Created by cardoso on 05/11/2015.
 */
public class Bilhete {

    private int id;
    private String IdAutocarro;
    private String IdCartao;
    private String IdBilhete;
    private String IdPercurso;
    private String Latitude;
    private String Longitude;
    private String Paragem;
    private  String Turno;
    private String DataHora;
    private String Enviado;
    private String CodTurnoBilhete;

    public Bilhete(){}

    public Bilhete(String IdAutocarro,String IdCartao, String IdBilhete,  String IdPercurso, String Latitude, String Longitude, String Paragem, String Turno, String DataHora, String Enviado, String CodTurnoBilhete) {
        super();
        this.IdAutocarro = IdAutocarro;
        this.IdCartao = IdCartao;
        this.IdBilhete = IdBilhete;
        this.IdPercurso = IdPercurso;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Paragem = Paragem;
        this.Turno = Turno;
        this.DataHora = DataHora;
        this.Enviado = Enviado;
        this.CodTurnoBilhete = CodTurnoBilhete;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getIdAutocarro() {
        return IdAutocarro;
    }
    public void setIdAutocarro(String IdAutocarro) {
        this.IdAutocarro = IdAutocarro;
    }
    public String getIdCartao() {
        return IdCartao;
    }
    public void setIdCartao(String IdCartao) {
        this.IdCartao = IdCartao;
    }
    public String getIdBilhete() {
        return IdBilhete;
    }
    public void setIdBilhete(String IdBilhete) {
        this.IdBilhete = IdBilhete;
    }

    public String getIdPercurso() {
        return IdPercurso;
    }
    public void setIdPercurso(String IdPercurso) {
        this.IdPercurso = IdPercurso;
    }

    public String getLatitude() {
        return Latitude;
    }
    public void setLatitude(String Latitude) {
        this.Latitude = Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }
    public void setLongitude(String Longitude) {
        this.Longitude = Longitude;
    }

    public String getParagem() {
        return Paragem;
    }
    public void setParagem(String Paragem) {
        this.Paragem = Paragem;
    }

    public String getTurno() {
        return Turno;
    }
    public void setTurno(String Turno) {
        this.Turno = Turno;
    }

    public String getDataHora() {
        return DataHora;
    }
    public void setDataHora(String DataHora) {
        this.DataHora = DataHora;
    }

    public String getEnviado() {
        return Enviado;
    }
    public void setEnviado(String Enviado) {
        this.Enviado = Enviado;
    }

    public String getCodTurnoBilhete() {
        return CodTurnoBilhete;
    }
    public void setCodTurnoBilhete(String CodTurnoBilhete) {
        this.CodTurnoBilhete = CodTurnoBilhete;
    }


    @Override
    public String toString() {
        return "Bilhete [id=" + id + ", IdAutocarro = " + IdAutocarro + ", IdCartao = " + IdCartao + ", IdBilhete = " + IdBilhete + ", IdPercurso " + IdPercurso +", Latitude = " + Latitude + ", Longitude = " + Longitude + ", Paragem = " + Paragem + ", Turno = " + Turno + ", DataHora = " + DataHora + ", Enviado = " + Enviado+ ", CodTurnoBilhete = " + CodTurnoBilhete + "]";
    }
}
