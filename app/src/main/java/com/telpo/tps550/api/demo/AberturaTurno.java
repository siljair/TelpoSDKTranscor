package com.telpo.tps550.api.demo;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class AberturaTurno extends Activity {

    Button btnConfirmar;
    ProgressBar pbAbertura;
    //private final String URL = "http://sgtu.portalsabi.com/WebServiceFleet.asmx";
    private final String URL = "http://transcor.portalsabi.com/WebServiceFleet.asmx";

    private final String NAMESPACE = "http://tempuri.org/";
    private final String SOAP_ACTION = "http://tempuri.org/ValidarCondutorNovo";
    private final String METHOD_NAME = "ValidarCondutorNovo";

    private final String SOAP_ACTION2 = "http://tempuri.org/AbrirTurnoNovo";
    private final String METHOD_NAME2 = "AbrirTurnoNovo";

    private final String SOAP_ACTION3 = "http://tempuri.org/getProxBilhete";
    private final String METHOD_NAME3 = "getProxBilhete";

    private final String SOAP_ACTION4 = "http://tempuri.org/ListarLinhas";
    private final String METHOD_NAME4 = "ListarLinhas";

    private final String SWVers = "1.09";

    TextView tvCartao, tvNome,tvAutocarro,tvLinha;
    Spinner spinnerTurno, spinnerLinha;
    ProgressBar pb;

    String resultado, resultado2= "";
    String resultado3 = "";
    String proxBilhete ="000000000000000000";
    String codTurno = "";
    String values[] = {"1", "2", "3", "4","5"};

    String linhas[] = {"1", "2", "3", "4","5","6","7","8","9","10","11","20"};

    private byte[] temp = { 0 };
    private int Datalen;

    private int FanNum = 0;
    private int BlockNum = 0;
    private int readIndex = 0;
    private java.util.Timer timer;
    private int flag = 0;
    String thread = "readThread";
    String choosed_serial = "/dev/ttySAC1";
    int choosed_buad = 9600;
    String t = "";
    String PrecoBilhete = "38";
    private Thread rthread;
    boolean statusAsync = true;
    boolean statusAsync2 = true;
    boolean statusAsync3 = true;
    boolean stopThread = true;

    private byte a[] = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff };

    private Intent intents;
    private boolean isnews = true;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private String dataString;
    private NfcAdapter nfcAdapter;
    final Handler h = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura_turno);

        btnConfirmar = (Button)findViewById(R.id.buttonConfirmarAbertura);
        btnConfirmar.setOnClickListener(onButtonClick);

        tvCartao = (TextView)findViewById(R.id.textViewIdCartaoCondutor);
        tvNome  = (TextView)findViewById(R.id.textViewNome);
        tvAutocarro =  (TextView)findViewById(R.id.textViewAutocarro);
        spinnerTurno  = (Spinner)findViewById(R.id.spinnerTurno);
        tvLinha = (TextView)findViewById(R.id.textViewLinhaValid);
        spinnerLinha = (Spinner) findViewById(R.id.spinnerLinhaValid);
        pb = (ProgressBar)findViewById(R.id.progressBarArirTurno);

        //verificar se tem internet senão linhas localmente
        CheckInternet chkNet = new CheckInternet();
        boolean hasnet = true;
        hasnet = chkNet.isInternetAvailable(getApplicationContext());
        {
            ArrayAdapter<String> spinnerArrayAdapter;
            spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, linhas);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout); // The drop down view
            spinnerLinha.setAdapter(spinnerArrayAdapter);
        }

        if(hasnet)
            new mySOAPAsyncTaskListarLinhas().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");


        try {

            //spinnerTurno = (Spinner) findViewById(R.id.spinnerNumerPrePago);
            //spinnerTurno.setEnabled(false);
            ArrayAdapter<String> spinnerArrayAdapter;


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
                    if(tvCartao.getText().length()> 7) {

                        String hexa = tvCartao.getText().toString().substring(6, 8) + tvCartao.getText().toString().substring(4, 6) + tvCartao.getText().toString().substring(2, 4) + tvCartao.getText().toString().substring(0, 2);
                        Long decimal = Long.parseLong(hexa, 16);
                        tvCartao.setText(String.valueOf(decimal));

                    }
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

                        String tipo = "Publico";
                        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroupTipoFrota);
                        RadioButton rb =  (RadioButton)findViewById(rg.getCheckedRadioButtonId());
                        tipo = rb.getText().toString();

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
                            if( (nome != null && codigoFuncionario != null))
                            {
                                tvNome.setText(codigoFuncionario + ";" + nome);
                                btnConfirmar.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                tvNome.setText("");
                                btnConfirmar.setVisibility(View.INVISIBLE);
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




    private class GetSNTask extends AsyncTask<Void, Void, TelpoException> {
        //ProgressDialog dialog;
        byte[] sn = new byte[64];
        int length;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            getSN.setEnabled(false);
//          m1Test.setEnabled(false);

            //dialog = new ProgressDialog(AberturaTurno.this);
            //dialog.setMessage(getString(R.string.operating));
            //dialog.setCancelable(false);
            //dialog.show();
        }

        @Override
        protected TelpoException doInBackground(Void... params) {
            TelpoException result = null;
            try{
                Picc.openReader();
                length = Picc.selectCard(sn);
            }catch (TelpoException e){
                e.printStackTrace();
                result = e;
            }finally {
                Picc.closeReader();
            }
            return result;
        }

        @Override
        protected void onPostExecute(TelpoException e) {
            super.onPostExecute(e);
//            dialog.dismiss();
            if(e == null){
                //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AberturaTurno.this);
                //dialogBuilder.setTitle(getString(R.string.card_sn));
                //dialogBuilder.setMessage(OtherUtil.byteToHexString(sn, length));
                tvCartao.setText(OtherUtil.byteToHexString(sn, length));

                String hexa = tvCartao.getText().toString();
                Long decimal = Long.parseLong(hexa, 16);

                tvCartao.setText(String.valueOf(decimal));

                //dialogBuilder.setPositiveButton(R.string.confirm,null);
                //dialogBuilder.create();
                //dialogBuilder.show();
            }else{
                //Toast.makeText(AberturaTurno.this, getString(R.string.operation_fail) + ":" + e.getDescription(), Toast.LENGTH_LONG).show();
            }
            //getSN.setEnabled(true);
            //m1Test.setEnabled(true);
        }
    }



    //Listar Linhas
    private class mySOAPAsyncTaskListarLinhas extends AsyncTask<String, Void, Void> {
        String resultadoAutocarros;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pb.setVisibility(View.INVISIBLE);

            try {
                if (resultadoAutocarros != null) {
                    if (resultadoAutocarros.contains(";")) {
                        List<String> list = Arrays.asList(String.valueOf(resultadoAutocarros).split(";"));

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AberturaTurno.this, R.layout.spinner_layout, list);
                        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
                        spinnerLinha.setAdapter(dataAdapter);
                    }
                }
            }
            catch(Exception ex)
            {}

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
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


    Button.OnClickListener onButtonClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v == btnConfirmar)
            {

                if( tvNome.getText().toString().length() > 0 && !tvNome.getText().toString().equals("Nome do Condutor") && !spinnerLinha.getSelectedItem().toString().equals("null") && spinnerLinha.getSelectedItem().toString().length() > 0 && tvAutocarro.getText().toString().trim().length() > 0)
                {
                    RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupTipoFrota);
                    String radiovalue = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                    if (radiovalue.equals("Publico")) {
                        if (statusAsync3)
                            new mySOAPAsyncTask3().execute(tvAutocarro.getText().toString());//async task para abrir turno
                        if (statusAsync2)
                        {
                            DBHelper db;
                            db = new DBHelper(getApplicationContext());

                            //Formato de data para slqlite
                            SimpleDateFormat dataAbertura = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateandTimeAbertura = dataAbertura.format(new Date());

                            TurnoTemp turno = new TurnoTemp(tvAutocarro.getText().toString(), spinnerTurno.getSelectedItem().toString(),currentDateandTimeAbertura,"0","0","0","0", spinnerLinha.getSelectedItem().toString(), SWVers, tvCartao.getText().toString(),"","0","","0");
                            codTurno = db.createTurno(turno);

                            CheckInternet chkNet = new CheckInternet();
                            boolean hasnet = true;
                            hasnet = chkNet.isInternetAvailable(getApplicationContext());
                            if(hasnet)
                                new mySOAPAsyncTask2().execute(tvAutocarro.getText().toString(), spinnerTurno.getSelectedItem().toString(), spinnerLinha.getSelectedItem().toString(), SWVers, tvCartao.getText().toString(),codTurno);//async task para abrir turno
                            else
                            {
                                if(db.getProxBilhete(tvAutocarro.getText().toString().replace("-","")) != null)
                                {
                                    proxBilhete = db.getProxBilhete(tvAutocarro.getText().toString());

                                    if(!proxBilhete.contains(tvAutocarro.getText().toString().replace("-","")))
                                    {
                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                        String currentDateandTime2 = sdf2.format(new Date());
                                        String formattedDate = currentDateandTime2.replace("-", "");

                                        proxBilhete = tvAutocarro.getText().toString().replace("-", "") + formattedDate + "0001";
                                    }
                                }
                                else
                                {

                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                    String currentDateandTime2 = sdf2.format(new Date());
                                    String formattedDate = currentDateandTime2.replace("-", "");

                                    proxBilhete = tvAutocarro.getText().toString().replace("-", "") + formattedDate + "0001";
                                }
                                Intent intent = new Intent(AberturaTurno.this, ValidarPasse.class);
                                intent.putExtra("linha", spinnerLinha.getSelectedItem().toString());
                                intent.putExtra("Autocarro", tvAutocarro.getText().toString());
                                intent.putExtra("Turno", spinnerTurno.getSelectedItem().toString());
                                intent.putExtra("PrecoBilhete", PrecoBilhete);
                                intent.putExtra("CartaoCondutor",tvCartao.getText().toString());
                                intent.putExtra("ProximoBilhete",proxBilhete);
                                intent.putExtra("CodigoCondutor",tvNome.getText().toString());
                                intent.putExtra("SWVersion",SWVers);
                                intent.putExtra("codTurno",codTurno);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AberturaTurno.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("ProximoBilhete",proxBilhete);
                                editor.apply();
//                        rthread.interrupt();
                                //finish();
                                Log.i("myApp", "start intent");
                                h.removeCallbacksAndMessages(null);
                                h.removeCallbacks(null);
                                AberturaTurno.this.finish();
                                startActivity(intent);
                                statusAsync2 = true;

                            }
                        }
                    }
                    else
                    {
                        Intent intent = new Intent(AberturaTurno.this, TransportePrivado.class);
                        intent.putExtra("linha", spinnerLinha.getSelectedItem().toString());
                        intent.putExtra("Autocarro", tvAutocarro.getText().toString());
                        intent.putExtra("Turno", spinnerTurno.getSelectedItem().toString());
                        intent.putExtra("PrecoBilhete", PrecoBilhete);
                        intent.putExtra("CartaoCondutor", tvCartao.getText().toString());
                        intent.putExtra("ProximoBilhete", proxBilhete);
                        startActivity(intent);

                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Condutor-Autocarro invalido!", Toast.LENGTH_LONG).show();



            }


        }};

    protected void onDestroy() {
        this.getApplicationContext();
        h.removeCallbacks(null);
        h.removeCallbacksAndMessages(null);
        //onBackPressed();
        finish();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_abertura_turno, menu);
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

    private class mySOAPAsyncTask extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;
        @Override
        protected void onPostExecute(Void result) {

            //dialog.dismiss();
            tvNome.setText("");

            super.onPostExecute(result);
            if(resultado != null) {
                try {
                    String[] res = resultado.split(";");
                    //f.Nome, t.Autocarro, t.Turno, t.Linha
                    if(!res[0].contains("anyType{}") ) {
                        tvNome.setText(res[0] + ";" + res[1]);
                        btnConfirmar.setVisibility(View.VISIBLE);
                        DBHelper db = new DBHelper(getApplicationContext());
                        CartaoMotorista cartaoDB = new CartaoMotorista();
                        String cartao = tvCartao.getText().toString();
                        cartaoDB.setCartao(cartao);
                        cartaoDB.setCodigoFuncionario(res[0]);
                        cartaoDB.setNome(res[1]);
                        db.createCartaoMotorista(cartaoDB);
                    }
                    else
                    {
                        tvNome.setText("");
                        btnConfirmar.setVisibility(View.INVISIBLE);
                    }

                }
                catch(Exception ex){}
            }
            else
            {
                tvNome.setText("");
                tvAutocarro.setText("");
                //tvLinha.setText("");
                //tvTurno.setText("");

            }
//            tvDadosWS.setText(response);

            //EditText et = (EditText)findViewById(R.id.editTextResult);
            //et.setText(response5);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvNome.setText("Aguarde");
            //tvAutocarro.setText("");
            //tvLinha.setText("");
            //tvTurno.setText("");
            //btnConfirmar.setVisibility(View.INVISIBLE);
   /*
            dialog = new ProgressDialog(AberturaTurno.this);
            dialog.setMessage(getString(R.string.operating));
            dialog.setCancelable(false);
            dialog.show();
**/
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
            //pbAbertura.setVisibility(View.GONE);
            //tvNome.setText("");

            Log.i("myApp", "Post Execute");

            if(resultado2 != null && resultado2.contains(";")) {
                String[] res = resultado2.split(";");
                //OK;PrecoBilhete;codTurno
                Log.i("myApp", "resultado2 not null");
                Log.i("myApp", resultado2);

                if(res.length >2 )
                {
                    Log.i("myApp", "res.length greater 1");

                    if(res[0].equals("OK"))
                    {
                        Log.i("myApp", "res[0] = 1");

                        PrecoBilhete = res[1];

                        DBHelper db = new DBHelper(getApplicationContext());
                        db.updateTurnoTempUploaded(tvAutocarro.getText().toString(),spinnerLinha.getSelectedItem().toString(),spinnerTurno.getSelectedItem().toString(),tvCartao.getText().toString(),res[2]);

                        Intent intent = new Intent(AberturaTurno.this, ValidarPasse.class);
                        intent.putExtra("linha", spinnerLinha.getSelectedItem().toString());
                        intent.putExtra("Autocarro", tvAutocarro.getText().toString());
                        intent.putExtra("Turno", spinnerTurno.getSelectedItem().toString());
                        intent.putExtra("PrecoBilhete", PrecoBilhete);
                        intent.putExtra("CartaoCondutor",tvCartao.getText().toString());
                        intent.putExtra("ProximoBilhete",proxBilhete);
                        String codigoCond = tvNome.getText().toString();
                        if(tvNome.getText().toString().contains(";"))
                        {
                            String[] spli = tvNome.getText().toString().split(";");
                            codigoCond = spli[0];
                        }
                        intent.putExtra("CodigoCondutor",codigoCond);
                        intent.putExtra("SWVersion",SWVers);
                        intent.putExtra("codTurno",codTurno);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AberturaTurno.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ProximoBilhete",proxBilhete);
                        editor.apply();
//                        rthread.interrupt();
                        //finish();
                        Log.i("myApp", "start intent");
                        h.removeCallbacksAndMessages(null);
                        h.removeCallbacks(null);
                        AberturaTurno.this.finish();
                        startActivity(intent);
                        statusAsync2 = true;

                    }

                }
                else
                {

                }
            }
            else
            {
                tvNome = (TextView) findViewById(R.id.textViewNome);
                tvNome.setText("");

                tvAutocarro = (TextView) findViewById(R.id.textViewAutocarro);
                tvAutocarro.setText("");
                //tvLinha.setText("");
                //tvTurno = (TextView) findViewById(R.id.textViewTurno);
                //tvTurno.setText("");

                btnConfirmar.setVisibility(View.GONE);
                spinnerLinha.setEnabled(true);
                spinnerTurno.setEnabled(true);;

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
            btnConfirmar.setVisibility(View.INVISIBLE);
            //tvNome.setText("Aguarde");

            spinnerLinha.setEnabled(false);
            spinnerTurno.setEnabled(false);;

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

            PropertyInfo Linha = new PropertyInfo();
            Linha.setName("Linha");
            Linha.setValue(args[2]);
            Linha.setType(String.class);
            request.addProperty(Linha);

            PropertyInfo SWVersion = new PropertyInfo();
            SWVersion.setName("SWVersion");
            SWVersion.setValue(args[3]);
            SWVersion.setType(String.class);
            request.addProperty(SWVersion);

            PropertyInfo CartaoCondutor = new PropertyInfo();
            CartaoCondutor.setName("CartaoCondutor");
            CartaoCondutor.setValue(args[4]);
            CartaoCondutor.setType(String.class);
            request.addProperty(CartaoCondutor);


            PropertyInfo CodTurno = new PropertyInfo();
            CodTurno.setName("CodTurno");
            CodTurno.setValue(args[5]);
            CodTurno.setType(String.class);
            request.addProperty(CodTurno);

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


    public class mySOAPAsyncTask3 extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            pbAbertura.setVisibility(View.GONE);

            if(resultado3 != null)
            {
                proxBilhete = resultado3.toString().replace(";","");
                //TextView txProxBilhete = (TextView)findViewById(R.id.textViewProxBilhete);
                //txProxBilhete.setText(proxBilhete);
            }
            else
            {
                statusAsync3 = true;
            }
//            tvDadosWS.setText(response);

            //EditText et = (EditText)findViewById(R.id.editTextResult);
            //et.setText(response5);
            //pbAbertura.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync3 = false;
           // pbAbertura.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... args)
        //string Autocarro, string Turno
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);

            PropertyInfo Autocarro = new PropertyInfo();
            Autocarro.setName("IdAutocarro");
            Autocarro.setValue(args[0]);
            Autocarro.setType(String.class);
            request.addProperty(Autocarro);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;


            try {
                androidHttpTransport.call(SOAP_ACTION3, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject)envelope.bodyIn;
                resultado3 = result.getProperty(0).toString();
                Log.i("myAppProx", result.toString());
                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }




    }


}
