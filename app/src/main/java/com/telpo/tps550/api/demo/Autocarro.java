package com.telpo.tps550.api.demo;

/**
 * Created by cardoso on 09/11/2016.
 */
public class Autocarro {

    private int id;
    private String idAutocarro;
    private String IMEI;

    public Autocarro(){}

    public Autocarro(String idAutocarro, String IMEI) {
        super();
        this.idAutocarro = idAutocarro;
        this.IMEI = IMEI;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getIdAutocarro() {
        return idAutocarro;
    }
    public void setIdAutocarro(String idAutocarro) {
        this.idAutocarro = idAutocarro;
    }
    public String getIMEI() {
        return IMEI;
    }
    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    @Override
    public String toString() {
        return "Autocarro [id=" + id + ", idAutocarro=" + idAutocarro + ", IMEI=" + IMEI + "]";
    }
}
