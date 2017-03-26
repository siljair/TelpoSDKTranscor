package com.telpo.tps550.api.demo;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.util.OtherUtil;
import com.telpo.tps550.api.iccard.Picc;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Arrays;
import java.util.List;

public class FecharTurno extends Activity {

    Button btnConfirmar;
    ProgressBar pbAbertura;
    //private final String URL = "http://sgtu.portalsabi.com/WebServiceFleet.asmx";
    private final String URL = "http://transcor.portalsabi.com/WebServiceFleet.asmx";

    private final String NAMESPACE = "http://tempuri.org/";
    private final String SOAP_ACTION = "http://tempuri.org/ValidarCondutorNovo";
    private final String METHOD_NAME = "ValidarCondutorNovo";
    private final String SOAP_ACTION2 = "http://tempuri.org/FecharTurnoNovo";
    private final String METHOD_NAME2 = "FecharTurnoNovo";
    private final String SOAP_ACTION20 = "http://tempuri.org/uploadBilhetes";
    private final String METHOD_NAME20 = "uploadBilhetes";
    private final String SOAP_ACTION4 = "http://tempuri.org/ListarLinhas";
    private final String METHOD_NAME4 = "ListarLinhas";

    private final String SWVers = "1.09";


    TextView tvCartao, tvNome,tvAutocarro,tvTurno;
    TextView tvLinha;
    Spinner spinnerTurno, spinnerLinha;
    String values[] = {"1", "2", "3", "4","5"};
    String linhas[] = {"1", "2", "3", "4","5","6","7","8","9","10","11","20"};

    String PrecoBilhete = "38";

    String resultado, resultado2 ="";
    boolean statusAsync = true;
    boolean statusAsync2 = true;
    String t,resultadoVendaBilhete, codigoTurno = "";
    final Handler h = new Handler();
    public int total, quantidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fechar_turno);

        total = 0;
        quantidade = 0;

        btnConfirmar = (Button)findViewById(R.id.buttonConfirmarFecho);
        btnConfirmar.setOnClickListener(onButtonClick);

        tvNome = (TextView)findViewById(R.id.textViewNomeFecho);
        pbAbertura = (ProgressBar)findViewById(R.id.progressBarFechoTurno);
        tvAutocarro =(TextView)findViewById(R.id.textViewAutocarroFecho);
        spinnerTurno  = (Spinner)findViewById(R.id.spinnerTurnoFecho);
        spinnerLinha  = (Spinner)findViewById(R.id.spinnerLinhaFecho);

        ArrayAdapter<String> spinnerArrayAdapter;
        spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, linhas);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout); // The drop down view
        spinnerLinha.setAdapter(spinnerArrayAdapter);

//        new mySOAPAsyncTaskListarLinhas().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

        tvCartao = (TextView)findViewById(R.id.textViewIdCartaoCondutorFecho);

        try {

            //spinnerTurno = (Spinner) findViewById(R.id.spinnerNumerPrePago);
            //spinnerTurno.setEnabled(false);
           // ArrayAdapter<String> spinnerArrayAdapter;


            spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, values);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout); // The drop down view
            spinnerTurno.setAdapter(spinnerArrayAdapter);

            DBHelper db = new DBHelper(getApplicationContext());
            Autocarro au = db.getDadosAutocarro();
            tvAutocarro.setText(au.getIdAutocarro());
        }
        catch(Exception exc){}


        final int delay = 500; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {

                //new GetSNTask().execute();
                //I2CTools.ReadUID();
//
                byte[] sn = new byte[64];
                int length;
                try{
                    Picc.openReader();
                    length = Picc.selectCard(sn);
                    tvCartao.setText(OtherUtil.byteToHexString(sn, length));

                    String hexa = tvCartao.getText().toString().substring(6, 8) + tvCartao.getText().toString().substring(4, 6) + tvCartao.getText().toString().substring(2, 4) + tvCartao.getText().toString().substring(0, 2);

                    Long decimal = Long.parseLong(hexa, 16);
                    tvCartao.setText(String.valueOf(decimal));

/**
                    String HEX_DIGITS = "0123456789ABCDEF";
                    char[] sources = tvCartao.getText().toString().toCharArray();
                    int dec = 0;
                    for (int i = 0; i < sources.length; i++) {
                        int digit = HEX_DIGITS.indexOf(Character.toUpperCase(sources[i]));
                        dec += digit * Math.pow(16, (sources.length - (i + 1)));
                    }

                    tvCartao.setText(String.valueOf(dec));
**/

                }catch (TelpoException e){
                    e.printStackTrace();

                }finally {
                    Picc.closeReader();
                }

                //


                if (tvCartao.getText().toString() != "" && t != tvCartao.getText().toString())
                {
                    t = tvCartao.getText().toString();
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();

                    try {

                        Log.i("StartAsyncSoap", "Call MyAsyncSoap");
                        //new mySOAPAsyncTask().execute(t);


                        //Ir buscar dados motorista online
                        CheckInternet chkNet = new CheckInternet();
                        boolean hasnet = true;
                        hasnet = chkNet.isInternetAvailable(getApplicationContext());
                        if(hasnet)
                            new mySOAPAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,t);
                        else
                        {
                            DBHelper db = new DBHelper(getApplicationContext());
                            CartaoMotorista cartaoDB = new CartaoMotorista();
                            cartaoDB = db.getDadosCartaoMotorista(t);

                            String nome = "";
                            nome = cartaoDB.getNome();
                            String codigoFuncionario = "";
                            codigoFuncionario = cartaoDB.getCodigoFuncionario();
                            if(nome.length() > 0 && codigoFuncionario.length() > 0)
                            {
                                tvNome.setText(codigoFuncionario + ";" + nome);
                                btnConfirmar.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    catch (Exception ex)
                    {

                    }


                }
                h.postDelayed(this, delay);
            }

        }, delay);

    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v == btnConfirmar)
            {
                if(tvNome.getText().toString().length() > 0 && !tvNome.getText().toString().equals("Nome do Condutor") )
                {
                    DBHelper db = new DBHelper(getApplicationContext());
                    String bi = "";
                    bi = db.getAllBilhetes(tvAutocarro.getText().toString());

                    TurnoTemp tmp = db.getTurnoTemp(tvAutocarro.getText().toString(),spinnerTurno.getSelectedItem().toString(),spinnerLinha.getSelectedItem().toString(),tvCartao.getText().toString());


                    //Se houver internet
                    CheckInternet chkNet = new CheckInternet();
                    boolean hasnet = true;
                    hasnet = chkNet.isInternetAvailable(getApplicationContext());
                    if(hasnet)
                    {
                        if (bi.length() > 0)
                            new mySOAPAsyncTask20().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bi);
                        else
                            new mySOAPAsyncTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvAutocarro.getText().toString(), spinnerTurno.getSelectedItem().toString(), tvCartao.getText().toString(), spinnerLinha.getSelectedItem().toString(), SWVers,tmp.getCodTurno());//async task para fechar turno
                    }
                    else
                    {


                        Intent intent = new Intent(FecharTurno.this, ResumoFecho.class);
                        intent.putExtra("Autocarro", tvAutocarro.getText().toString());
                        intent.putExtra("Turno", spinnerTurno.getSelectedItem().toString());
                        intent.putExtra("Linha",spinnerLinha.getSelectedItem().toString());
                        String nome = tvNome.getText().toString();
                        if(tvNome.getText().toString().contains(";"))
                        {
                            String[] spl = tvNome.getText().toString().split(";");
                            nome = spl[0];
                        }

                        intent.putExtra("Cartao",tvCartao.getText().toString() + "; Id: " + nome );
                        intent.putExtra("TotalVendas", tmp.getvendasTurno().toString());
                        intent.putExtra("PrecoBilhete", PrecoBilhete);
                        intent.putExtra("PrePago", tmp.getprepagoTurno().toString());
                        h.removeCallbacksAndMessages(null);
                        h.removeCallbacks(null);
                        FecharTurno.this.finish();
                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"Condutor invalido!", Toast.LENGTH_LONG).show();


            }


        }};

    private class mySOAPAsyncTaskListarLinhas extends AsyncTask<String, Void, Void> {
        String resultadoAutocarros;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Spinner spin = (Spinner) findViewById(R.id.spinnerLinhaFecho);
            List<String> list = Arrays.asList(String.valueOf(resultadoAutocarros).split(";"));

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FecharTurno.this,R.layout.spinner_layout, list);
            dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
            spin.setAdapter(dataAdapter);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... args)
        //insertVendaBilhete(string IdAutocarro,string IdCartao, string IdBilhete, string IdPercurso, string Latitude, string Longitude)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);
            /**
             PropertyInfo linha = new PropertyInfo();
             linha.setName("linha");
             linha.setValue(args[0]);
             linha.setType(String.class);
             request.addProperty(linha);
             **/

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {
                androidHttpTransport.call(SOAP_ACTION4, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                //result = (SoapObject)envelope.bodyIn;
                //Log.i("myApp", result.toString());
                if (envelope.bodyIn instanceof SoapFault) {
                    String str= ((SoapFault) envelope.bodyIn).faultstring;
                    Log.i("", str);

                    // Another way to travers through the SoapFault object
                    /*  Node detailsString =str= ((SoapFault) envelope.bodyIn).detail;
                        Element detailElem = (Element) details.getElement(0)
                                     .getChild(0);
                        Element e = (Element) detailElem.getChild(2);faultstring;
                        Log.i("", e.getName() + " " + e.getText(0)str); */
                } else {
                    result = (SoapObject) envelope.bodyIn;
                    Log.d("WS", String.valueOf(result));
                    resultadoAutocarros = String.valueOf(result.getProperty(0));
                }
                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString() + e.getLocalizedMessage());
                e.printStackTrace();
            }
            // ////////
            return null;
        }




    }


    private class mySOAPAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pbAbertura.setVisibility(View.GONE);

            if(resultado != null) {
                String[] res = resultado.split(";");
                //f.Nome, t.Autocarro, t.Turno, t.Linha
                if(res.length >1 )
                {
                    tvNome = (TextView) findViewById(R.id.textViewNomeFecho);
                    tvNome.setText(res[0]);

                    //tvAutocarro = (TextView) findViewById(R.id.textViewAutocarroFecho);
                    //tvAutocarro.setText(res[1]);
                    //tvLinha = (TextView) findViewById(R.id.textViewLinhaValidFecho);
                    //tvLinha.setText(res[3]);
                    //tvTurno = (TextView) findViewById(R.id.textViewTurnoFecho);
                    //tvTurno.setText(res[2]);

                    btnConfirmar.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvNome = (TextView) findViewById(R.id.textViewNomeFecho);
                    tvNome.setText("");

                    //tvAutocarro = (TextView) findViewById(R.id.textViewAutocarroFecho);
                    //tvAutocarro.setText("");
                    //tvLinha = (TextView) findViewById(R.id.textViewLinhaValidFecho);
                    //tvLinha.setText("");
                    //tvTurno = (TextView) findViewById(R.id.textViewTurnoFecho);
                    //tvTurno.setText("");

                    btnConfirmar.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                tvNome = (TextView) findViewById(R.id.textViewNomeFecho);
                tvNome.setText("");

                //tvAutocarro = (TextView) findViewById(R.id.textViewAutocarroFecho);
                //tvAutocarro.setText("");
                //tvLinha = (TextView) findViewById(R.id.textViewLinhaValidFecho);
                //tvLinha.setText("");
                //tvTurno = (TextView) findViewById(R.id.textViewTurnoFecho);
                //tvTurno.setText("");

                btnConfirmar.setVisibility(View.GONE);

                statusAsync = true;
            }
//            tvDadosWS.setText(response);

            //EditText et = (EditText)findViewById(R.id.editTextResult);
            //et.setText(response5);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync = false;
            pbAbertura.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... args)
        //private void soapMessage(String IdPasse, String Latitude, String Longitude, String Velocidade, String Autocarro)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo cartao = new PropertyInfo();
            cartao.setName("cartao");
            cartao.setValue(args[0]);
            cartao.setType(String.class);
            request.addProperty(cartao);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;


            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject)envelope.bodyIn;
                resultado = result.getProperty(0).toString();
                Log.i("myApp", result.toString());
                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }




    }


    private class mySOAPAsyncTask2 extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pbAbertura.setVisibility(View.GONE);

            if(resultado2 != null && resultado2.contains(";"))
            {
                String[] res = resultado2.split(";");
                //OK;PrecoBilhete

                TextView tvStatus = (TextView)findViewById(R.id.textViewStatusFecharTurno);
                if(res.length >1 )
                {
                    if(res[0].contains("OK"))
                    {

                        tvStatus.setText(resultado2.toString());

                        //Atualizar tabela turno
                        DBHelper db = new  DBHelper(getApplicationContext());
                        int i = db.updateTurnoTempFechado(tvAutocarro.getText().toString(),spinnerLinha.getSelectedItem().toString(), spinnerTurno.getSelectedItem().toString(),tvCartao.getText().toString());
                        db.deleteTurnoTempFechadoNovo(tvAutocarro.getText().toString(),spinnerLinha.getSelectedItem().toString(), spinnerTurno.getSelectedItem().toString(),tvCartao.getText().toString());

                        if(res.length>2)
                        {
                            Intent intent = new Intent(FecharTurno.this, ResumoFecho.class);
                            intent.putExtra("Autocarro", tvAutocarro.getText().toString());
                            intent.putExtra("Turno", spinnerTurno.getSelectedItem().toString());
                            intent.putExtra("Linha",spinnerLinha.getSelectedItem().toString());
                            intent.putExtra("Cartao",tvCartao.getText().toString() + "; Id: " + tvNome.getText().toString() );
                            intent.putExtra("TotalVendas", res[2]);
                            intent.putExtra("PrecoBilhete", res[3]);
                            if(res.length>3)
                                intent.putExtra("PrePago", res[4]);
                            else
                                intent.putExtra("PrePago", "Sem PrePago");
                            //rthread.interrupt();
                            h.removeCallbacksAndMessages(null);
                            h.removeCallbacks(null);
                            FecharTurno.this.finish();
                            startActivity(intent);
                        }

                    }
                    else
                        tvStatus.setText("Erro no fecho");

                }
                else
                {
                    tvStatus.setText("Retorno invalido");
                }
            }
            else
            {
                tvNome = (TextView) findViewById(R.id.textViewNomeFecho);
                tvNome.setText("");

                //tvAutocarro = (TextView) findViewById(R.id.textViewAutocarroFecho);
                //tvAutocarro.setText("");
                //tvLinha = (TextView) findViewById(R.id.textViewLinhaValidFecho);
               // tvLinha.setText("");
                //tvTurno = (TextView) findViewById(R.id.textViewTurnoFecho);
                //tvTurno.setText("");

                btnConfirmar.setVisibility(View.GONE);

                statusAsync2 = true;
            }
//            tvDadosWS.setText(response);

            //EditText et = (EditText)findViewById(R.id.editTextResult);
            //et.setText(response5);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync2 = false;
            pbAbertura.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... args)
        //string Autocarro, string Turno
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);

            PropertyInfo Autocarro = new PropertyInfo();
            Autocarro.setName("Autocarro");
            Autocarro.setValue(args[0]);
            Autocarro.setType(String.class);
            request.addProperty(Autocarro);

            PropertyInfo Turno = new PropertyInfo();
            Turno.setName("Turno");
            Turno.setValue(args[1]);
            Turno.setType(String.class);
            request.addProperty(Turno);

            PropertyInfo Condutor = new PropertyInfo();
            Condutor.setName("condutor");
            Condutor.setValue(args[2]);
            Condutor.setType(String.class);
            request.addProperty(Condutor);

            PropertyInfo Linha = new PropertyInfo();
            Linha.setName("Linha");
            Linha.setValue(args[3]);
            Linha.setType(String.class);
            request.addProperty(Linha);

            PropertyInfo SWVersion = new PropertyInfo();
            SWVersion.setName("SWVersion");
            SWVersion.setValue(args[4]);
            SWVersion.setType(String.class);
            request.addProperty(SWVersion);

            PropertyInfo CodTurno = new PropertyInfo();
            CodTurno.setName("CodTurno");
            CodTurno.setValue(args[5]);
            CodTurno.setType(String.class);
            request.addProperty(CodTurno);

            codigoTurno = args[5];

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject)envelope.bodyIn;
                resultado2 = result.getProperty(0).toString();
                Log.i("myApp", result.toString());
                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }




    }

    //Venda Bilhete
    private class mySOAPAsyncTask20 extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pbAbertura.setVisibility(View.INVISIBLE);

            if(!resultadoVendaBilhete.contains("Erro"))
            {
                DBHelper db = new DBHelper(getApplicationContext());
                //db.deleteAllBilhete();

                TurnoTemp tmp = db.getTurnoTemp(tvAutocarro.getText().toString(),spinnerTurno.getSelectedItem().toString(),spinnerLinha.getSelectedItem().toString(),tvCartao.getText().toString());

                //if (statusAsync2)
                    new mySOAPAsyncTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvAutocarro.getText().toString(), spinnerTurno.getSelectedItem().toString(), tvCartao.getText().toString(), spinnerLinha.getSelectedItem().toString(), SWVers,tmp.getCodTurno());//async task para fechar turno
                    //new mySOAPAsyncTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvAutocarro.getText().toString(), tvTurno.getText().toString(), tvNome.getText().toString());//async task para fechar turno
            }



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //statusAsync20 = false;
            pbAbertura.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... args)
        //insertVendaBilhete(string IdAutocarro,string IdCartao, string IdBilhete, string IdPercurso, string Latitude, string Longitude)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME20);

            PropertyInfo listaBilhetes = new PropertyInfo();
            listaBilhetes.setName("listaBilhetes");
            String lista = args[0];
            listaBilhetes.setValue(args[0]);
            listaBilhetes.setType(String.class);
            request.addProperty(listaBilhetes);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {

                androidHttpTransport.call(SOAP_ACTION20, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
                resultadoVendaBilhete = result.getProperty(0).toString();
                Log.i("myApp", result.toString());
                androidHttpTransport.getConnection().disconnect();
            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE Venda: " + e.toString() + e.getLocalizedMessage());
                e.printStackTrace();
            }
            // ////////
            return null;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fechar_turno, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
