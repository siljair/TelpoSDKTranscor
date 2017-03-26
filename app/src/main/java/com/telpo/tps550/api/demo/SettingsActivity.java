package com.telpo.tps550.api.demo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends Activity {

    private final String URL = "http://transcor.portalsabi.com/WebServiceFleet.asmx";
    private final String NAMESPACE = "http://tempuri.org/";
    private final String SOAP_ACTION = "http://tempuri.org/listarAutocarros";
    private final String METHOD_NAME = "listarAutocarros";
Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinner = (Spinner) findViewById(R.id.spinnerAutocarro);
        //spinner.setEnabled(false);

        Button btnSettings = (Button) findViewById(R.id.buttonVerifPassword);
        final  Button btnGuardar = (Button) findViewById(R.id.buttonGuardar);
        final EditText et = (EditText)findViewById(R.id.EditText15);
        final  Button btnAtualizar = (Button) findViewById(R.id.buttonAlterarPassword);
        final EditText etNovaPwd = (EditText)findViewById(R.id.editTextNovaPassword);
        final TextView tv16 = (TextView)findViewById(R.id.textView16);
        btnSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                String codigodefault = "kodigue13579";
                String codigo = "";
                try
                {
                    DBHelper db = new DBHelper(getApplicationContext());
                    CodigoSettings au = db.getCodigoSettings();
                    codigo = au.getCodigo().toString();
                }
                catch(Exception exc)
                {
                    //Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
                }
                if(codigo.equals(""))
                    codigo = codigodefault;

                if (et.getText().toString().equals(codigo)) {
                    new mySOAPAsyncTaskListarAutocarro().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    spinner.setVisibility(View.VISIBLE);
                    btnGuardar.setVisibility(View.VISIBLE);
                    tv16.setVisibility(View.VISIBLE);
                    btnAtualizar.setVisibility(View.VISIBLE);
                    etNovaPwd.setVisibility(View.VISIBLE);
                }
                else
                {
                    spinner.setVisibility(View.INVISIBLE);
                    btnGuardar.setVisibility(View.INVISIBLE);
                    tv16.setVisibility(View.INVISIBLE);
                    btnAtualizar.setVisibility(View.INVISIBLE);
                    etNovaPwd.setVisibility(View.INVISIBLE);
                }

            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                if(spinner.getSelectedItem().toString().trim().length() > 0)
                {
                    DBHelper db;
                    db = new DBHelper(getApplicationContext());
                    db.deleteAllAutocarros();

                    Autocarro bil = new Autocarro();
                    bil.setIdAutocarro(spinner.getSelectedItem().toString());

                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String device_id = tm.getDeviceId();
                    bil.setIMEI(device_id);
                    db.createAutocarro(bil);

                    Autocarro au = db.getDadosAutocarro();
                    TextView tvv = (TextView) findViewById(R.id.textView15);
                    tvv.setText(au.getIdAutocarro() + ";" + au.getIMEI());
                }}
                catch(Exception ex)
                {}

            }
        });

        btnAtualizar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                String codigo = etNovaPwd.getText().toString();
                if(codigo.length()>0)
                {
                    try
                    {
                        DBHelper db = new DBHelper(getApplicationContext());
                        CodigoSettings au = db.getCodigoSettings();
                        au.setCodigo(codigo);
                        db.createCodigo(au);
                        Toast.makeText(getApplicationContext(),"Codigo Alterado!", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception exc)
                    {

                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"Preencher Novo Codigo!", Toast.LENGTH_LONG).show();

            }
        });


    }


    //Listar Autocarros
    private class mySOAPAsyncTaskListarAutocarro extends AsyncTask<String, Void, Void> {
        String resultadoAutocarros;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Spinner spin = (Spinner) findViewById(R.id.spinnerAutocarro);
            List<String> list = Arrays.asList(String.valueOf(resultadoAutocarros).split("#"));

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SettingsActivity.this,R.layout.spinner_layout, list);
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
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
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
                androidHttpTransport.call(SOAP_ACTION, envelope);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
