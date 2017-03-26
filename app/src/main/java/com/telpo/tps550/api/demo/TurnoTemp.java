package com.telpo.tps550.api.demo;

/**
 * Created by cardoso on 06/12/2016.
 */
public class TurnoTemp {

    private String autocarroTurno;
    private String turnoTurno;
    private String dataAberturaTurno;
    private String vendasTurno;
    private String prepagoTurno;
    private String estudanteTurno;
    private String voltasTurno;
    private String linhaTurno;
    private String SWversionTurno;
    private String abertoporTurno;
    private String dataFecho;
    private String uploaded;
    private String codTurno;
    private String FechoUploaded;

    public TurnoTemp(String autocarroTurno, String turnoTurno, String dataAberturaTurno, String vendasTurno, String prepagoTurno, String estudanteTurno, String voltasTurno, String linhaTurno, String SWversionTurno, String abertoporTurno, String dataFecho,String uploaded, String codTurno, String FechoUploaded) {
        super();
        this.autocarroTurno = autocarroTurno;
        this.turnoTurno = turnoTurno;
        this.dataAberturaTurno = dataAberturaTurno;
        this.vendasTurno = vendasTurno;
        this.prepagoTurno = prepagoTurno;
        this.estudanteTurno = estudanteTurno;
        this.voltasTurno = voltasTurno;
        this.linhaTurno = linhaTurno;
        this.SWversionTurno = SWversionTurno;
        this.abertoporTurno = abertoporTurno;
        this.dataFecho = dataFecho;
        this.uploaded = uploaded;
        this.codTurno = codTurno;
        this.FechoUploaded = FechoUploaded;

    }

    public String getautocarroTurno() {
        return autocarroTurno;
    }
    public void setautocarroTurno(String autocarroTurno) {
        this.autocarroTurno = autocarroTurno;
    }
    public String getturnoTurno() {
        return turnoTurno;
    }
    public void setturnoTurno(String turnoTurno) {
        this.turnoTurno = turnoTurno;
    }

    public String getdataAberturaTurno() {
        return dataAberturaTurno;
    }
    public void setdataAberturaTurno(String dataAberturaTurno) {
        this.dataAberturaTurno = dataAberturaTurno;
    }
    public String getvendasTurno() {
        return vendasTurno;
    }
    public void setvendasTurno(String vendasTurno) {
        this.vendasTurno = vendasTurno;
    }

    public String getprepagoTurno() {
        return prepagoTurno;
    }
    public void setprepagoTurno(String prepagoTurno) {
        this.prepagoTurno = prepagoTurno;
    }
    public String getestudanteTurno() {
        return estudanteTurno;
    }
    public void setestudanteTurno(String estudanteTurno) {
        this.estudanteTurno = estudanteTurno;
    }
    public String getvoltasTurno() {
        return voltasTurno;
    }
    public void setvoltasTurno(String voltasTurno) {
        this.voltasTurno = voltasTurno;
    }

    public String getlinhaTurno() {
        return linhaTurno;
    }
    public void setlinhaTurno(String linhaTurno) {
        this.linhaTurno = linhaTurno;
    }
    public String getSWversionTurno() {
        return SWversionTurno;
    }
    public void setSWversionTurno(String SWversionTurno) {
        this.SWversionTurno = SWversionTurno;
    }
    public String getabertoporTurno() {
        return abertoporTurno;
    }
    public void setabertoporTurno(String abertoporTurno) {
        this.abertoporTurno = abertoporTurno;
    }

    public String getUploaded() {
        return uploaded;
    }
    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }
    //FechoUploaded
    public String getFechoUploaded() {
        return FechoUploaded;
    }
    public void setFechoUploaded(String FechoUploaded) {
        this.FechoUploaded = FechoUploaded;
    }

    public String getDataFecho() {
        return dataFecho;
    }
    public void setDataFecho(String dataFecho) {
        this.dataFecho = dataFecho;
    }

    public String getCodTurno() {
        return codTurno;
    }
    public void setCodTurno(String codTurno) {
        this.codTurno = codTurno;
    }


    @Override
    public String toString() {
        return "TurnoTemp [ autocarroTurno=" + autocarroTurno + ",turnoTurno =" + turnoTurno + ", dataAberturaTurno=" + dataAberturaTurno + ", vendasTurno =" + vendasTurno + ", prepagoTurno=" + prepagoTurno + ", estudanteTurno=" + estudanteTurno + ", voltasTurno=" + voltasTurno + ", linhaTurno=" + linhaTurno + ", SWversionTurno=" + SWversionTurno + ", abertoporTurno=" + abertoporTurno + ", dataFecho ='" + dataFecho + "', uploaded =" + uploaded + " ]";
    }


}
