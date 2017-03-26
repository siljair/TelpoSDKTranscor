package com.telpo.tps550.api.demo;

/**
 * Created by cardoso on 01/12/2015.
 */
public class Leituras {
    private int id;
    private String IdPasse = "IdPasse";
    private String DataHoraLeitura = "DataHoraLeitura";
    private String LatitudeLeitura = "LatitudeLeitura";
    private String LongitudeLeitura = "LongitudeLeitura";
    private String VelocidadeLeitura = "VelocidadeLeitura";
    private String AutocarroLeitura = "AutocarroLeitura";
    private String LinhaLeitura = "LinhaLeitura";
    private String ParagemLeitura = "ParagemLeitura";
    private String EstadoLeitura = "EstadoLeitura";
    private String TurnoLeitura = "TurnoLeitura";


    public Leituras(){}

    public Leituras(String IdPasse,String DataHoraLeitura, String LatitudeLeitura,  String LongitudeLeitura, String VelocidadeLeitura, String AutocarroLeitura, String LinhaLeitura, String ParagemLeitura, String EstadoLeitura, String TurnoLeitura) {
        super();
        this.IdPasse = IdPasse;
        this.DataHoraLeitura = DataHoraLeitura;
        this.LatitudeLeitura = LatitudeLeitura;
        this.LongitudeLeitura = LongitudeLeitura;
        this.VelocidadeLeitura = VelocidadeLeitura;
        this.AutocarroLeitura = AutocarroLeitura;
        this.LinhaLeitura = LinhaLeitura;
        this.ParagemLeitura = ParagemLeitura;
        this.EstadoLeitura = EstadoLeitura;
        this.TurnoLeitura = TurnoLeitura;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getIdPasse() {
        return IdPasse;
    }
    public void setIdPasse(String IdPasse) {
        this.IdPasse = IdPasse;
    }

    public String getDataHoraLeitura() {
        return DataHoraLeitura;
    }
    public void setDataHoraLeitura(String DataHoraLeitura) {
        this.DataHoraLeitura = DataHoraLeitura;
    }

    public String getLatitudeLeitura() {
        return LatitudeLeitura;
    }
    public void setLatitudeLeitura(String LatitudeLeitura) {
        this.LatitudeLeitura = LatitudeLeitura;
    }

    public String getLongitudeLeitura() {
        return LongitudeLeitura;
    }
    public void setLongitudeLeitura(String LongitudeLeitura) {
        this.LongitudeLeitura = LongitudeLeitura;
    }

    public String getVelocidadeLeitura() {
        return VelocidadeLeitura;
    }
    public void setVelocidadeLeitura(String VelocidadeLeitura) {
        this.VelocidadeLeitura = VelocidadeLeitura;
    }

    public String getAutocarroLeitura() {
        return AutocarroLeitura;
    }
    public void setAutocarroLeitura(String AutocarroLeitura) {
        this.AutocarroLeitura = AutocarroLeitura;
    }

    public String getLinhaLeitura() {
        return LinhaLeitura;
    }
    public void setLinhaLeitura(String LinhaLeitura) {
        this.LinhaLeitura = LinhaLeitura;
    }

    public String getParagemLeitura() {
        return ParagemLeitura;
    }
    public void setParagemLeitura(String ParagemLeitura) {
        this.ParagemLeitura = ParagemLeitura;
    }

    public String getEstadoLeitura() {
        return EstadoLeitura;
    }
    public void setEstadoLeitura(String EstadoLeitura) {
        this.EstadoLeitura = EstadoLeitura;
    }

    public String getTurnoLeitura() {
        return TurnoLeitura;
    }
    public void setTurnoLeitura(String TurnoLeitura) {
        this.TurnoLeitura = TurnoLeitura;
    }


    @Override
    public String toString() {
        return "com.telpo.tps550.api.demo.Leituras [id=" + id + ", IdPasse = " + IdPasse + ", DataHoraLeitura = " + DataHoraLeitura + ", LatitudeLeitura = " + LatitudeLeitura + ", LongitudeLeitura " + LongitudeLeitura +", VelocidadeLeitura = " + VelocidadeLeitura + ", AutocarroLeitura = " + AutocarroLeitura + ", LinhaLeitura = " + LinhaLeitura + ", ParagemLeitura = " + ParagemLeitura + ", EstadoLeitura = " + EstadoLeitura + ", TurnoLeitura = " + TurnoLeitura  + "]";
    }

}
