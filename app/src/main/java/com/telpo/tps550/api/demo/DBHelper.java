package com.telpo.tps550.api.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by cardoso on 25/10/2015.
 */
public class DBHelper  extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 8;
    // database name
    private static final String database_NAME = "cartoes";
    private static final String table_CARTAO = "cartao";
    private static final String cartao_ID = "id";
    private static final String cartao_Numero = "numero";
    private static final String cartao_Status = "status";
    private static final String cartao_Tipo = "tipo";
    private static final String cartao_AnoMes = "anoMes";
    private static final String cartao_AnoMesAnterior = "anoMesAnterior";
    private static final String cartao_DataExpiracao = "dataExpiracao";

    private static final String table_BILHETE = "bilhetes";
    private static final String IdBil = "IdBil";
    private static final String IdAutocarro = "IdAutocarro";
    private static final String IdCartao = "IdCartao";
    private static final String IdBilhete = "IdBilhete";
    private static final String IdPercurso = "IdPercurso";
    private static final String Latitude = "Latitude";
    private static final String Longitude = "Longitude";
    private static final String Paragem = "Paragem";
    private static final String Turno = "Turno";
    private static final String DataHora = "DataHora";
    private static final String Enviado = "Enviado";
    private static final String CodTurnoBilhete = "CodTurnoBilhete";

    private static final String table_LEITURAS = "leituras";
    private static final String IdPasse = "IdPasse";
    private static final String DataHoraLeitura = "DataHoraLeitura";
    private static final String LatitudeLeitura = "LatitudeLeitura";
    private static final String LongitudeLeitura = "LongitudeLeitura";
    private static final String VelocidadeLeitura = "VelocidadeLeitura";
    private static final String AutocarroLeitura = "AutocarroLeitura";
    private static final String LinhaLeitura = "LinhaLeitura";
    private static final String ParagemLeitura = "ParagemLeitura";
    private static final String EstadoLeitura = "EstadoLeitura";
    private static final String TurnoLeitura = "TurnoLeitura";
    private static final String CodTurnoLeitura = "CodTurnoLeitura";

    private static final String table_AUTOCARRO = "autocarro";
    private static final String IdAutocarroFisico = "IdAutocarroFisico";
    private static final String IMEI = "IMEI";

    private static final String table_CODIGO = "codigoSettings";
    private static final String codigo = "codigo";

    private static final String table_TURNOTEMP = "turnoTEMP";
    private static final String autocarroTurno = "autocarroTurno";
    private static final String turnoTurno = "turnoTurno";
    private static final String dataAberturaTurno = "dataAberturaTurno";
    private static final String vendasTurno = "vendasTurno";
    private static final String prepagoTurno = "prepagoTurno";
    private static final String estudanteTurno = "estudanteTurno";
    private static final String voltasTurno = "voltasTurno";
    private static final String linhaTurno = "linhaTurno";
    private static final String SWversionTurno = "SWversionTurno";
    private static final String abertoporTurno = "abertoporTurno";
    private static final String dataFecho = "dataFecho";
    private static final String uploaded = "uploaded";
    private static final String FechoUploaded = "FechoUploaded";
    private static final String codTurno = "codTurno";

    private static final String table_CARTAOMOTORISTA = "cartaoMOTORISTA";
    private static final String nome = "nome";
    private static final String cartao = "cartao";
    private static final String codigoFuncionario = "codigoFuncionario";

    //1#Terminal Jardim Châ Alecrim VOLTA#16.898807458784155#-24.98907410520178
    private static final String table_PARAGENS = "Paragens";
    private static final String idPercursoParagem = "idPercursoParagem";
    private static final String nomeParagem = "nomeParagem";
    private static final String latitudeParagem = "latitudeParagem";
    private static final String longitudeParagem = "longitudeParagem";

    private static final String[] COLUMNS_PARAGENS = { idPercursoParagem, nomeParagem,latitudeParagem,longitudeParagem};

    private static final String[] COLUMNS = { cartao_ID, cartao_Numero, cartao_Status, cartao_Tipo, cartao_AnoMes, cartao_AnoMesAnterior, cartao_DataExpiracao};

    private static final String[] COLUMNS_BILHETES = { IdBil, IdAutocarro, IdCartao, IdBilhete,IdPercurso,Latitude,Longitude,Paragem,Turno,DataHora,Enviado,CodTurnoBilhete};

    private static final String[] COLUMNS_LEITURAS = { IdPasse, DataHoraLeitura, LatitudeLeitura, LongitudeLeitura, VelocidadeLeitura, AutocarroLeitura, LinhaLeitura, ParagemLeitura, EstadoLeitura, TurnoLeitura, CodTurnoLeitura};

    private static final String[] COLUMNS_AUTOCARRO = { IdAutocarroFisico, IMEI};

    private static final String[] COLUMNS_CODIGO = { codigo};

    private static final String[] COLUMNS_TURNO = { autocarroTurno, turnoTurno, dataAberturaTurno, vendasTurno, prepagoTurno, estudanteTurno, voltasTurno, linhaTurno, SWversionTurno, abertoporTurno,dataFecho,uploaded, codTurno, FechoUploaded};

    private static final String[] COLUMNS_CARTAOMOTORISTA = { nome, cartao, codigoFuncionario};


    public DBHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE cartao ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "numero Text, status Text, tipo Text, anoMes Text, anoMesAnterior Text,dataExpiracao Text)";
        db.execSQL(CREATE_BOOK_TABLE);

        String CREATE_Bilhetes_TABLE = "CREATE TABLE bilhetes ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "IdAutocarro Text, IdCartao Text, IdBilhete Text, IdPercurso Text, Latitude Text, Longitude Text, Paragem Text, Turno Text, DataHora Text, Enviado Text, CodTurnoBilhete Text)";
        db.execSQL(CREATE_Bilhetes_TABLE);

        String CREATE_Leituras_TABLE = "CREATE TABLE leituras ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "IdPasse Text,  DataHoraLeitura Text, LatitudeLeitura Text, LongitudeLeitura Text, VelocidadeLeitura Text, AutocarroLeitura Text, LinhaLeitura Text, ParagemLeitura Text, EstadoLeitura Text, TurnoLeitura Text, CodTurnoLeitura)";
        db.execSQL(CREATE_Leituras_TABLE);

        String CREATE_Autocarro_TABLE = "CREATE TABLE autocarro ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "IdAutocarroFisico Text,  IMEI Text)";
        db.execSQL(CREATE_Autocarro_TABLE);

        String CREATE_Codigo_TABLE = "CREATE TABLE codigoSettings ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "codigo Text)";
        db.execSQL(CREATE_Codigo_TABLE);

        String CREATE_TURNO_TABLE = "CREATE TABLE turnoTEMP ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "autocarroTurno Text, turnoTurno Text, dataAberturaTurno Text, vendasTurno Text, prepagoTurno Text, estudanteTurno Text, voltasTurno Text,  linhaTurno Text, SWversionTurno Text, abertoporTurno Text, dataFecho Text, uploaded Text, codTurno Text, FechoUploaded Text)";
        db.execSQL(CREATE_TURNO_TABLE);

        String CREATE_CARTAOMOTORISTA_TABLE = "CREATE TABLE cartaoMOTORISTA ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nome Text, cartao Text, codigoFuncionario Text )";
        db.execSQL(CREATE_CARTAOMOTORISTA_TABLE);

        String CREATE_PARAGENS_TABLE = "CREATE TABLE  " + table_PARAGENS + "( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "idPercursoParagem Text, nomeParagem Text,latitudeParagem Text,longitudeParagem Text)";
        db.execSQL(CREATE_PARAGENS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop books table if already exists
        db.execSQL("DROP TABLE IF EXISTS cartao");
        db.execSQL("DROP TABLE IF EXISTS bilhetes");
        db.execSQL("DROP TABLE IF EXISTS leituras");
        db.execSQL("DROP TABLE IF EXISTS autocarro");
        db.execSQL("DROP TABLE IF EXISTS codigoSettings");
        db.execSQL("DROP TABLE IF EXISTS turnoTEMP");
        db.execSQL("DROP TABLE IF EXISTS cartaoMOTORISTA");
        db.execSQL("DROP TABLE IF EXISTS Paragens");
        this.onCreate(db);
    }

    public void onUpgradePartial(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop books table if already exists
  /*
        db.execSQL("DROP TABLE IF EXISTS cartao");
        db.execSQL("DROP TABLE IF EXISTS bilhetes");
        //db.execSQL("DROP TABLE IF EXISTS leituras");
        db.execSQL("DROP TABLE IF EXISTS autocarro");
        //db.execSQL("DROP TABLE IF EXISTS turnoTEMP");
        db.execSQL("DROP TABLE IF EXISTS cartaoMOTORISTA");
        db.execSQL("DROP TABLE IF EXISTS Paragens");

        String CREATE_BOOK_TABLE = "CREATE TABLE cartao ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "numero Text, status Text, tipo Text, anoMes Text, anoMesAnterior Text,dataExpiracao Text)";
        db.execSQL(CREATE_BOOK_TABLE);

        String CREATE_Bilhetes_TABLE = "CREATE TABLE bilhetes ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "IdAutocarro Text, IdCartao Text, IdBilhete Text, IdPercurso Text, Latitude Text, Longitude Text, Paragem Text, Turno Text, DataHora Text, Enviado Text )";
        db.execSQL(CREATE_Bilhetes_TABLE);

        String CREATE_Autocarro_TABLE = "CREATE TABLE autocarro ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "IdAutocarroFisico Text,  IMEI Text)";
        db.execSQL(CREATE_Autocarro_TABLE);


        String CREATE_CARTAOMOTORISTA_TABLE = "CREATE TABLE cartaoMOTORISTA ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nome Text, cartao Text, codigoFuncionario Text )";
        db.execSQL(CREATE_CARTAOMOTORISTA_TABLE);

        String CREATE_PARAGENS_TABLE = "CREATE TABLE  " + table_PARAGENS + "( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "idPercursoParagem Text, nomeParagem Text,latitudeParagem Text,longitudeParagem Text)";
        db.execSQL(CREATE_PARAGENS_TABLE);
**/

//        this.onCreate(db);
    }

    public void createAutocarro(Autocarro autocarro) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(IdAutocarroFisico, autocarro.getIdAutocarro());
        values.put(IMEI, autocarro.getIMEI());
        // insert book
        db.insert(table_AUTOCARRO, null, values);

        // close database transaction
        db.close();
    }

    public void createParagens(String Paragens) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        //1#Terminal Jardim Châ Alecrim VOLTA#16.898807458784155#-24.98907410520178;

        //check record exist
        try {

            String[] arrayParagens = Paragens.split(";");
            int t = arrayParagens.length;
            if(t  > 0)
            {
                int k =0;
                while(k < t)
                {
                    String paragemIndiv[] = arrayParagens[k].split("#");

                    Cursor cursor = null;
                    String sql = "SELECT * FROM " + table_PARAGENS + " WHERE idPercursoParagem = '" + paragemIndiv[0] +
                            "' and nomeParagem = '" + paragemIndiv[1] + "'";

                    cursor = db.rawQuery(sql, null);
                    int i = cursor.getCount();

                    if (i > 0)
                    {
                        //PID Found
                        //update
                    }
                    else
                    {
                        String insert = "insert into " + table_PARAGENS + " (idPercursoParagem, nomeParagem,latitudeParagem,longitudeParagem) " +
                                "values ('" + paragemIndiv[0] + "', '" + paragemIndiv[1] + "', '" + paragemIndiv[2] + "', '" +paragemIndiv[3] + "')";
                        try
                        {
                            db.execSQL(insert);
                        }
                        catch (Exception ex)
                        {

                        }
                    }

                    k++;
                }
            }

        }
        catch(Exception ex)
        {}
        finally {
            // close database transaction
            db.close();
        }
    }

    public void createCodigo(CodigoSettings codigoSettings) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(codigo,  codigoSettings.getCodigo());
        // insert book
        db.insert(table_CODIGO, null, values);

        // close database transaction
        db.close();
    }

    public void createCartao(Cartao cartao) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(cartao_Numero, cartao.getNumero());
        values.put(cartao_Status, cartao.getStatus());
        values.put(cartao_Tipo, cartao.getTipo());
        values.put(cartao_AnoMes, cartao.getAnoMes());
        values.put(cartao_AnoMesAnterior, cartao.getAnoMesAnterior());
        values.put(cartao_DataExpiracao, cartao.getdataExpiracao());
        // insert book
        db.insert(table_CARTAO, null, values);

        // close database transaction
        db.close();
    }

    public void createCartaoMotorista(CartaoMotorista cartaoMotorista) {
        try {
            // get reference of the BookDB database
            SQLiteDatabase db = this.getWritableDatabase();


            String delete = "delete from " + table_CARTAOMOTORISTA + " where cartao = '" + cartaoMotorista.getCartao() + "'";
            db.execSQL(delete);

            String insert = "insert into " + table_CARTAOMOTORISTA + " (nome, cartao, codigoFuncionario) " +
                    "values ('" + cartaoMotorista.getNome() + "', '" + cartaoMotorista.getCartao() + "', '" + cartaoMotorista.getCodigoFuncionario() + "')";
            db.execSQL(insert);
            // close database transaction
            db.close();
        }
        catch(Exception ex)
        {
            String s = ex.toString();
        }
    }

    public void createBilhete(Bilhete bilhete) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
try {
    // make values to be inserted
    ContentValues values = new ContentValues();

    values.put(IdAutocarro, bilhete.getIdAutocarro());
    values.put(IdCartao, bilhete.getIdCartao());
    values.put(IdBilhete, bilhete.getIdBilhete());
    values.put(IdPercurso, bilhete.getIdPercurso());
    values.put(Latitude, bilhete.getLatitude());
    values.put(Longitude, bilhete.getLongitude());
    values.put(Paragem, bilhete.getParagem());
    values.put(Turno, bilhete.getTurno());
    values.put(DataHora, bilhete.getDataHora());
    values.put(Enviado, bilhete.getEnviado());
    values.put(CodTurnoBilhete,bilhete.getCodTurnoBilhete());
    // insert book
    //Long i = db.insert(table_BILHETE, null, values);
    String delete = "delete from " + table_BILHETE + " where IdBilhete = '" + bilhete.getIdBilhete() + "'";
    db.execSQL(delete);

    String insert = "insert into " + table_BILHETE + " (IdAutocarro, IdCartao, IdBilhete, IdPercurso, Latitude, Longitude, Paragem, Turno, DataHora, Enviado, CodTurnoBilhete) " +
            "values ('" + bilhete.getIdAutocarro() + "', '" + bilhete.getIdCartao() + "', '" + bilhete.getIdBilhete() + "', '" + bilhete.getIdPercurso()
            + "','" + bilhete.getLatitude() + "','" + bilhete.getLongitude() + "','" + bilhete.getParagem() + "','" + bilhete.getTurno() + "','" + bilhete.getDataHora() + "', '" + bilhete.getEnviado() + "', '" + bilhete.getCodTurnoBilhete() +"')";
    db.execSQL(insert);
}
catch(Exception ex){}

        finally {
        // close database transaction
        db.close();
        }

    }

    public void createLeitura(Leituras leitura) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();

        //IdPasse = "IdPasse";
        // DataHoraLeitura = "DataHoraLeitura";
        // LatitudeLeitura = "LatitudeLeitura";
        // LongitudeLeitura = "LongitudeLeitura";
        // VelocidadeLeitura = "VelocidadeLeitura";
        // AutocarroLeitura = "AutocarroLeitura";
        // LinhaLeitura = "LinhaLeitura";
        // ParagemLeitura = "ParagemLeitura";
        // EstadoLeitura = "EstadoLeitura";

        values.put(IdPasse, leitura.getIdPasse());
        values.put(DataHoraLeitura, leitura.getDataHoraLeitura());
        values.put(LatitudeLeitura, leitura.getLatitudeLeitura());
        values.put(LongitudeLeitura, leitura.getLongitudeLeitura());
        values.put(VelocidadeLeitura, leitura.getVelocidadeLeitura());
        values.put(AutocarroLeitura, leitura.getAutocarroLeitura());
        values.put(LinhaLeitura, leitura.getLinhaLeitura());
        values.put(ParagemLeitura, leitura.getParagemLeitura());
        values.put(EstadoLeitura, leitura.getEstadoLeitura());
        values.put(TurnoLeitura, leitura.getTurnoLeitura());

        // insert book
        //Long i = db.insert(table_BILHETE, null, values);
        String insert = "insert into " + table_LEITURAS + " (IdPasse,  DataHoraLeitura, LatitudeLeitura, LongitudeLeitura, VelocidadeLeitura, AutocarroLeitura, LinhaLeitura, ParagemLeitura, EstadoLeitura, TurnoLeitura) " +
                "values ('" + leitura.getIdPasse() + "', '" + leitura.getDataHoraLeitura() + "', '" +  leitura.getLatitudeLeitura() + "', '" +  leitura.getLongitudeLeitura()
                + "','" + leitura.getVelocidadeLeitura() + "','" + leitura.getAutocarroLeitura() + "','" + leitura.getLinhaLeitura() + "','" + leitura.getParagemLeitura() + "','" + leitura.getEstadoLeitura() + "','" + leitura.getTurnoLeitura()  + "')";
        try {
            db.execSQL(insert);
        }
        catch(Exception ex)
        {}
        finally {
            // close database transaction
            db.close();
        }
    }

    public String createTurno(TurnoTemp turnoTemp) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        //String table_TURNOTEMP = "turnoTEMP";
        //String autocarroTurno = "autocarroTurno";
        //String turnoTurno = "turnoTurno";
        //String dataAberturaTurno = "dataAberturaTurno";
        //String vendasTurno = "vendasTurno";
        //String prepagoTurno = "prepagoTurno";
        //String estudanteTurno = "estudanteTurno";
        //String voltasTurno = "voltasTurno";
        //String linhaTurno = "linhaTurno";
        //String SWversionTurno = "SWversionTurno";
        //String abertoporTurno = "abertoporTurno";
        //String codTurno

        String codigoTurno = "";
        //check record exist
        try {

            int min = 11111;
            int max = 99999;

            Random r = new Random();
            int i1 = r.nextInt(max - min + 1) + min;

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //like "HH:mm" or just "mm", whatever you want
            String stringRepresetnation = sdf.format(date);
            codigoTurno = turnoTemp.getautocarroTurno().replace("-", "") + turnoTemp.getabertoporTurno() + turnoTemp.getlinhaTurno() + turnoTemp.getturnoTurno() + stringRepresetnation + i1;

            Cursor cursor = null;
            String sql = "SELECT * FROM " + table_TURNOTEMP + " WHERE autocarroTurno = '" + turnoTemp.getautocarroTurno() +
                    "' and turnoTurno = '" + turnoTemp.getturnoTurno() + "' and dataFecho = ''  and abertoporTurno = '" + turnoTemp.getabertoporTurno() + "' and linhaTurno = '" + turnoTemp.getlinhaTurno() + "' and CodTurno <> 'codTurno'";

            cursor = db.rawQuery(sql, null);
            int i =cursor.getCount();


            if (i > 0)
            {
                if(cursor.moveToFirst())
                {
                    codigoTurno = cursor.getString(13);
                }
            }
            else
            {
                //Long i = db.insert(table_BILHETE, null, values);
                String insert = "insert into " + table_TURNOTEMP + " (autocarroTurno, turnoTurno, dataAberturaTurno, vendasTurno, prepagoTurno, estudanteTurno, voltasTurno,  linhaTurno, SWversionTurno, abertoporTurno, dataFecho,uploaded, codTurno) " +
                        "values ('" + turnoTemp.getautocarroTurno() + "', '" + turnoTemp.getturnoTurno() + "', '" + turnoTemp.getdataAberturaTurno() + "', '" + turnoTemp.getvendasTurno()
                        + "','" + turnoTemp.getprepagoTurno() + "','" + turnoTemp.getestudanteTurno() + "','" + turnoTemp.getvoltasTurno() + "','" + turnoTemp.getlinhaTurno() + "','" + turnoTemp.getSWversionTurno() + "','" + turnoTemp.getabertoporTurno() + "', '','0','" + codigoTurno + "')";
                try
                {
                    db.execSQL(insert);
                }
                catch (Exception ex)
                {

                }
            }
        }
        catch(Exception ex)
        {
            String er = ex.toString();
        }
        finally {
            // close database transaction
            db.close();
        }
        return codigoTurno;

    }

    public Autocarro getDadosAutocarro() {
        Autocarro auto = new Autocarro();
        // select book query
        String query = "SELECT  * FROM " + table_AUTOCARRO;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        //Autocarro auto = null;
        try {


            if (cursor.moveToFirst()) {
                do {

                    auto.setId(Integer.parseInt(cursor.getString(0)));
                    auto.setIdAutocarro(cursor.getString(1));
                    auto.setIMEI(cursor.getString(2));

                    // Add book to books
                    //auto.add(auto);
                } while (cursor.moveToNext());
            }
        }
        catch(Exception ex){}
        finally
        {
            db.close();
        }
        return auto;

    }

    public CodigoSettings getCodigoSettings() {
        CodigoSettings auto = new CodigoSettings();
        // select book query
        String query = "SELECT  * FROM " + table_CODIGO;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        //Autocarro auto = null;
        try {


            if (cursor.moveToFirst()) {
                do {

                    auto.setCodigo(cursor.getString(1));

                } while (cursor.moveToNext());
            }
        }
        catch(Exception ex){}
        finally
        {
            db.close();
        }
        return auto;

    }

    public Cartao getDadosCartao(String numero) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();
        Cartao cart = new Cartao();

        try {
            // get book query
            Cursor cursor = db.query(table_CARTAO, // a. table
                    COLUMNS, " numero = ?", new String[]{String.valueOf(numero)}, null, null, null, null);

            // if results !=null, parse the first one
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    cart.setId(Integer.parseInt(cursor.getString(0)));
                    cart.setNumero(cursor.getString(1));
                    cart.setStatus(cursor.getString(2));
                    cart.setTipo(cursor.getString(3));
                    cart.setAnoMes(cursor.getString(4));
                    cart.setAnoMesAnterior(cursor.getString(5));
                    cart.setdataExpiracao(cursor.getString(6));
                }
            }
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }

        return cart;

    }

    public CartaoMotorista getDadosCartaoMotorista(String cartao) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();
        CartaoMotorista cart = new CartaoMotorista();

        try {


            /////////////
            // get book query

            // select book query
            String query = "select * from " + table_CARTAOMOTORISTA + " where cartao = '" + cartao + "'";

            Cursor cursor = db.rawQuery(query, null);

            // if results !=null, parse the first one
            if (cursor != null) {
                if (cursor.moveToFirst())
                {
                    cart.setId(Integer.parseInt(cursor.getString(0)));
                    cart.setNome(cursor.getString(1));
                    cart.setCartao(cursor.getString(2));
                    cart.setCodigoFuncionario(cursor.getString(3));
                }
            }
        }
        catch(Exception ex)
        {
          String error =   ex.toString();
        }
        finally
        {
            db.close();
        }

        return cart;

    }

    public Bilhete getDadosBilhete(String IdBilhete) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();
        Bilhete bil = new Bilhete();
        try {
            // get book query
            String sql = "select * from " + table_BILHETE + " where IdBilhete = '" + IdBilhete + "'";
            Cursor cursor = db.rawQuery(sql, null);

            // if results !=null, parse the first one

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    //IdAutocarro, IdCartao, IdBilhete, IdPercurso, Latitude, Longitude, Paragem, Turno, DataHora, Enviado
                    bil.setId(Integer.parseInt(cursor.getString(0)));
                    bil.setIdAutocarro(cursor.getString(1));
                    bil.setIdCartao(cursor.getString(2));
                    bil.setIdBilhete(cursor.getString(3));
                    bil.setIdPercurso(cursor.getString(4));
                    bil.setLatitude(cursor.getString(5));
                    bil.setLongitude(cursor.getString(6));
                    bil.setParagem(cursor.getString(7));
                    bil.setTurno(cursor.getString(8));
                    bil.setDataHora(cursor.getString(9));
                    bil.setEnviado(cursor.getString(10));
                }
            }
        }
        catch(Exception ex)
        {

        }
        finally
        {
            db.close();
        }
        return bil;
    }

    public List getAllCartoes() {
        List cartoes = new LinkedList();

        // select book query
        String query = "SELECT  * FROM " + table_CARTAO;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        Cartao cartao = null;
        try {


        if (cursor.moveToFirst()) {
            do {
                cartao = new Cartao();
                Cartao cart = new Cartao();
                cart.setId(Integer.parseInt(cursor.getString(0)));
                cart.setNumero(cursor.getString(1));
                cart.setStatus(cursor.getString(2));
                cart.setTipo(cursor.getString(3));
                cart.setAnoMes(cursor.getString(4));
                cart.setAnoMesAnterior(cursor.getString(5));
                cart.setdataExpiracao(cursor.getString(6));

                // Add book to books
                cartoes.add(cartao);
            } while (cursor.moveToNext());
        }
        }
        catch(Exception ex){}
        finally
        {
            db.close();
        }
        return cartoes;
    }

    public String getAllBilhetes(String Autocarro) {
        List bilhetes = new LinkedList();

        // select book query
        String query = "SELECT  * FROM " + table_BILHETE + " where Enviado = 'Nao' and IdAutocarro = '" + Autocarro + "'";

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        String lista = "";
        try {

            Cursor cursor = db.rawQuery(query, null);

            // parse all results
            //Bilhete bilhete = null;

            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    /**
                     bilhete = new Bilhete();
                     Bilhete bil = new Bilhete();
                     bil.setId(Integer.parseInt(cursor.getString(0)));
                     bil.setIdAutocarro(cursor.getString(1));
                     bil.setIdCartao(cursor.getString(2));
                     bil.setIdBilhete(cursor.getString(3));
                     bil.setIdPercurso(cursor.getString(4));
                     bil.setLatitude(cursor.getString(5));
                     bil.setLongitude(cursor.getString(6));
                     bil.setParagem(cursor.getString(7));
                     bil.setTurno(cursor.getString(8));
                     // Add book to books
                     bilhetes.add(bilhete);
                     **/
                    if (i == 0)
                        lista = cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                                + ";" + cursor.getString(5) + ";" + cursor.getString(6) + ";" + cursor.getString(7) + ";" + cursor.getString(8) + ";" + cursor.getString(9) + ";" + cursor.getString(10)+ ";" + cursor.getString(11);
                    else {
                        if (!lista.contains(cursor.getString(3)))
                            lista += "#" + cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                                    + ";" + cursor.getString(5) + ";" + cursor.getString(6) + ";" + cursor.getString(7) + ";" + cursor.getString(8) + ";" + cursor.getString(9) + ";" + cursor.getString(10)+ ";" + cursor.getString(11);
                    }
                    i++;
                } while (cursor.moveToNext());
            }
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return lista;
    }

    public String getParagemPercurso(String idPercurso) {
        // select book query
        String query = "SELECT  * FROM " + table_PARAGENS + " where idPercursoParagem = '" + idPercurso + "'";

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        String lista = "";
        try {

            Cursor cursor = db.rawQuery(query, null);

            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    if (i == 0)
                        lista = cursor.getString(2) + "#" + cursor.getString(3) + "#" + cursor.getString(4) ;
                    else
                        lista += ";" + cursor.getString(2) + "#" + cursor.getString(3) + "#" + cursor.getString(4);

                    i++;
                } while (cursor.moveToNext());
            }
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return lista;
    }

    public String getAllLeituras() {

        List leituras = new LinkedList();
        String lista = "";
        // select book query
        String query = "SELECT  * FROM " + table_LEITURAS;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(query, null);

            // parse all results
            //Bilhete bilhete = null;

            int i = 0;
            if (cursor.moveToFirst()) {
                do {

                    //IdPasse = "IdPasse";
                    // DataHoraLeitura = "DataHoraLeitura";
                    // LatitudeLeitura = "LatitudeLeitura";
                    // LongitudeLeitura = "LongitudeLeitura";
                    // VelocidadeLeitura = "VelocidadeLeitura";
                    // AutocarroLeitura = "AutocarroLeitura";
                    // LinhaLeitura = "LinhaLeitura";
                    // ParagemLeitura = "ParagemLeitura";
                    // EstadoLeitura = "EstadoLeitura";
                    // TurnoLeitura

                    if (i == 0)
                        lista = cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                                + ";" + cursor.getString(5) + ";" + cursor.getString(6) + ";" + cursor.getString(7) + ";" + cursor.getString(8) + ";" + cursor.getString(9) + ";" + cursor.getString(10);
                    else {
                        lista += "#" + cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                                + ";" + cursor.getString(5) + ";" + cursor.getString(6) + ";" + cursor.getString(7) + ";" + cursor.getString(8) + ";" + cursor.getString(9) + ";" + cursor.getString(10);
                    }
                    i++;
                } while (cursor.moveToNext());
            }

        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return lista;
    }

    public String getProxBilhete(String Autocarro) {
        String Proxbilhete = "";

        // select book query
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime2 = sdf2.format(new Date());
        String formattedDate = currentDateandTime2.replace("-", "");
        String query = "SELECT  max(IdBilhete) FROM " + table_BILHETE + " where IdBilhete like '" + Autocarro.replace("-","") + formattedDate + "%'";

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(query, null);

            // parse all results

            if (cursor.moveToFirst()) {
                do {
                    Proxbilhete = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            if (Proxbilhete == null)
                Proxbilhete = "";
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return Proxbilhete;
    }

    public TurnoTemp getTurnoTemp(String Autocarro, String Turno, String Linha, String AbertoPor)
    {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();
        TurnoTemp bil = new TurnoTemp(Autocarro, Turno,"","0","0","0","0", "", "", AbertoPor,"","0","","0");

        try {
            // get query
            String sql = "select * from " + table_TURNOTEMP + " where autocarroTurno = '" + Autocarro + "' and turnoTurno = '" + Turno + "' and linhaTurno = '" + Linha + "'  and dataFecho = '' and abertoporTurno = '" + AbertoPor + "' and CodTurno <> 'codTurno'";
            Cursor cursor = db.rawQuery(sql, null);

            // if results !=null, parse the first one

            String registos = "";
            if (cursor != null) {
                //if (cursor.moveToFirst())
                {

                    while(cursor.moveToNext())
                    {
                        registos += cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                                + ";" + cursor.getString(5) + ";" + cursor.getString(6) + ";" + cursor.getString(7) + ";" + cursor.getString(8)
                                + ";" + cursor.getString(9) + ";" + cursor.getString(10) + ";" + cursor.getString(11) + ";"
                                + cursor.getString(12) + ";" +  cursor.getString(13);



                        //autocarroTurno, turnoTurno, dataAberturaTurno, vendasTurno, prepagoTurno, estudanteTurno, voltasTurno,  linhaTurno, SWversionTurno, abertoporTurno
                        //SV-24-BX;1;2017-02-09 23:12:32;3;0;0;0;1;1.09;3604079306;;0
                        bil.setautocarroTurno(cursor.getString(1));
                        bil.setturnoTurno(cursor.getString(2));
                        bil.setdataAberturaTurno(cursor.getString(3));
                        bil.setvendasTurno(cursor.getString(4));
                        bil.setprepagoTurno(cursor.getString(5));
                        bil.setestudanteTurno(cursor.getString(6));
                        bil.setvoltasTurno(cursor.getString(7));
                        bil.setlinhaTurno(cursor.getString(8));
                        bil.setSWversionTurno(cursor.getString(9));
                        bil.setabertoporTurno(cursor.getString(10));
                        bil.setDataFecho(cursor.getString(11));
                        bil.setUploaded(cursor.getString(12));
                        bil.setCodTurno(cursor.getString(13));
                    }

                }
            }
        }
        catch(Exception ex)
        {

        }
        finally
        {
            db.close();
        }
        return bil;
    }

    public String getTurnoFechadoOffline(String Autocarro, String Turno)
    {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();
        TurnoTemp bil = new TurnoTemp(Autocarro, Turno,"","0","0","0","0", "", "", "","","0","","0");
        String lista = "";

        try {
            // get query
            String sql = "select * from " + table_TURNOTEMP + " where dataFecho <> ''";
            Cursor cursor = db.rawQuery(sql, null);

            // if results !=null, parse the first one
            int i=0;

            if (cursor != null)
            {
                if (cursor.moveToFirst())
                {
                    //autocarroTurno, turnoTurno, dataAberturaTurno, vendasTurno, prepagoTurno, estudanteTurno, voltasTurno,  linhaTurno, SWversionTurno, abertoporTurno
  /**
                    bil.setautocarroTurno(cursor.getString(1));
                    bil.setturnoTurno(cursor.getString(2));
                    bil.setdataAberturaTurno(cursor.getString(3));
                    bil.setvendasTurno(cursor.getString(4));
                    bil.setprepagoTurno(cursor.getString(5));
                    bil.setestudanteTurno(cursor.getString(6));
                    bil.setvoltasTurno(cursor.getString(7));
                    bil.setlinhaTurno(cursor.getString(8));
                    bil.setSWversionTurno(cursor.getString(9));
                    bil.setabertoporTurno(cursor.getString(10));
                    bil.setDataFecho(cursor.getString(11));
                    bil.setUploaded(cursor.getString(12));
*/
                do {
                    {

                    if (i == 0)
                        lista = cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                                + ";" + cursor.getString(5) + ";" + cursor.getString(6) + ";" + cursor.getString(7) + ";" + cursor.getString(8) + ";" + cursor.getString(9) + ";" + cursor.getString(10)+ ";" + cursor.getString(11)+ ";" + cursor.getString(12) + ";" + cursor.getString(13);
                    else {
                        lista += "#" + cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                                    + ";" + cursor.getString(5) + ";" + cursor.getString(6) + ";" + cursor.getString(7) + ";" + cursor.getString(8) + ";" + cursor.getString(9) + ";" + cursor.getString(10)+ ";" + cursor.getString(11)+ ";" + cursor.getString(12) + ";" + cursor.getString(13);
                    }
                    i++;
                    }
                }while (cursor.moveToNext());
            }
        }
        }
        catch(Exception ex)
        {

        }
        finally
        {
            db.close();
        }
        return lista;
    }

    public int updateFechoUploaded(String Autocarro, String Linha, String Turno, String AbertoPor, String codTurno) {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String update = "update " + table_TURNOTEMP + " set FechoUploaded = '1' where autocarroTurno = '" + Autocarro + "' and linhaTurno = '" + Linha + "' and turnoTurno = '" + Turno + "' and AbertoPor = '" + AbertoPor + "' and CodTurno = '" + codTurno + "'";
            db.execSQL(update);
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return i;
    }


    public int updateCartoes(Cartao cartao) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        try {
            // make values to be inserted
            ////numero, status, tipo, anoMes
            ContentValues values = new ContentValues();
            values.put("numero", cartao.getNumero()); // get numero cartao
            values.put("status", cartao.getStatus()); // get status
            values.put("tipo", cartao.getTipo()); // get tipo
            values.put("anoMes", cartao.getAnoMes()); // get anoMes
            values.put("anoMesAnterior", cartao.getAnoMesAnterior());
            values.put("dataExpiracao", cartao.getdataExpiracao());

            // update
            i = db.update(table_CARTAO, values, cartao_Numero + " = ?", new String[]{String.valueOf(cartao.getNumero())});
        }
        catch (Exception ex)
        {}
        finally {
            db.close();
        }
        return i;
    }

    public int updateBilheteEnviado(String bilhete) {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String update = "update " + table_BILHETE + " set Enviado = 'Sim' where IdBilhete = '" + bilhete + "'";
            db.execSQL(update);
        }
        catch(Exception ex)
        {}
         finally
        {
            db.close();
        }
        return i;
    }

    public int updateTurnoTempVendas(String Autocarro, String Linha, String Turno, String AbertoPor) {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String sql = "select * from " + table_TURNOTEMP + " where autocarroTurno = '" + Autocarro + "' and turnoTurno = '" + Turno + "' and linhaTurno = '" + Linha + "'  and dataFecho ='' and abertoporTurno = '" + AbertoPor + "'";
            Cursor cursor = db.rawQuery(sql, null);

            // if results !=null, parse the first one

            if (cursor != null)
            {
                String update = "update " + table_TURNOTEMP + " set vendasTurno = vendasTurno + 1 " + " where autocarroTurno = '" + Autocarro + "' and turnoTurno = '" + Turno + "' and linhaTurno = '" + Linha + "'  and dataFecho ='' and abertoporTurno = '" + AbertoPor + "'";
                db.execSQL(update);
            }


        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return i;
    }

    public int updateTurnoTempUploaded(String Autocarro, String Linha, String Turno, String AbertoPor, String codTurno) {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String update = "update " + table_TURNOTEMP + " set uploaded = '1' where autocarroTurno = '" + Autocarro + "' and linhaTurno = '" + Linha + "' and turnoTurno = '" + Turno + "' and AbertoPor = '" + AbertoPor + "' and CodTurno = '" + codTurno + "'";
            db.execSQL(update);
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return i;
    }

    public int updateTurnoTempFechado(String Autocarro, String Linha, String Turno, String AbertoPor) {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            SimpleDateFormat dataFecho = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTimeFecho = dataFecho.format(new Date());

            String update = "update " + table_TURNOTEMP + " set dataFecho = '" + currentDateandTimeFecho + "' where autocarroTurno = '" + Autocarro + "' and linhaTurno = '" + Linha + "' and turnoTurno = '" + Turno + "' and dataFecho = '' and abertoporTurno = '" + AbertoPor + "'";
            db.execSQL(update);
            i = 1;
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return i;
    }

    public int deleteTurnoTempFechado(String Autocarro, String Turno) {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            SimpleDateFormat dataFecho = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTimeFecho = dataFecho.format(new Date());

            //Alterado para fechar turno de qualquer autocarro ou turno fechados
            String update = "delete from " + table_TURNOTEMP + " where dataFecho <> '' and CodTurno = '" + Turno + "'";
            db.execSQL(update);
            i = 1;
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return i;
    }

    public int deleteTurnoTempFechadoNovo(String Autocarro, String Linha, String Turno, String AbertoPor) {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {

            String update = "delete from " + table_TURNOTEMP  + " where autocarroTurno = '" + Autocarro + "' and linhaTurno = '" + Linha + "' and turnoTurno = '" + Turno + "' and abertoporTurno = '" + AbertoPor + "'";
            db.execSQL(update);
            i = 1;
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return i;
    }


    // Deleting single book
    public void deleteBook(Cartao cartao) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
try {
    // delete book
    db.delete(table_CARTAO, cartao_Numero + " = ?", new String[]{String.valueOf(cartao.getNumero())});
}
catch(Exception ex){}
        finally {
    db.close();
}
    }

    // Deleting single book
    public void deleteCartaoMotorista(CartaoMotorista cartao) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // delete book
            db.delete(table_CARTAOMOTORISTA, cartao + " = ?", new String[]{String.valueOf(cartao.getCartao())});
        }
        catch(Exception ex){}
        finally {
            db.close();
        }
    }


    // Deleting single book
    public void deleteBilhete(Bilhete bilhete) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // delete book
            db.delete(table_BILHETE, IdBilhete + " = ?", new String[]{String.valueOf(bilhete.getIdBilhete())});
        }
        catch(Exception ex)
        {}
        finally {
            db.close();
        }
    }

    // Deleting single book
    public void deleteAllAutocarros() {

        // get reference of the database
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // delete autocarro
            db.delete(table_AUTOCARRO, null, null);
        }
        catch(Exception ex)
        {}
        finally {
            db.close();
        }    }


    // Deleting single book
    public void deleteAllBilhete() {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
try {
    // delete book
    db.delete(table_BILHETE, null, null);
}
catch(Exception ex)
{}
        finally {
    db.close();
}    }

    //Delete uploaded bilhetes
    // Deleting single book
    public int deleteUploadedBilhete() {

        int i = 0;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {

            String update = "delete from " + table_BILHETE + " where Enviado = 'Sim'";

            db.execSQL(update);
            i = 1;
        }
        catch(Exception ex)
        {}
        finally
        {
            db.close();
        }
        return i;    }


    // Delete all Leituras
    public void deleteAllLeituras() {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete book
        db.delete(table_LEITURAS,null, null );
        db.close();
    }


}