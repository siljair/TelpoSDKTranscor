package com.telpo.tps550.api.demo;

/**
 * Created by cardoso on 13/11/2016.
 */
public class CodigoSettings {

    private String codigo;

    public CodigoSettings(){}

    public CodigoSettings(String codigo)
    {
        super();
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
