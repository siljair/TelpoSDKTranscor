package com.telpo.tps550.api.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.util.OtherUtil;
import com.telpo.tps550.api.iccard.Picc;
import com.telpo.tps550.api.printer.ThermalPrinter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransportePrivado extends Activity implements TextToSpeech.OnInitListener {

    private final String NAMESPACE = "http://tempuri.org/";
    private final String URL = "http://transcor.portalsabi.com/WebServiceFleet.asmx";
    private final String SOAP_ACTION = "http://tempuri.org/ValidarBilhetePrivado";
    private final String METHOD_NAME = "ValidarBilhetePrivado";
    private final String SOAP_ACTION2 = "http://tempuri.org/insertVendaBilhete";
    private final String METHOD_NAME2 = "insertVendaBilhete";
    private final String SOAP_ACTION3 = "http://tempuri.org/ListarCoordenadasParagens";
    private final String METHOD_NAME3 = "ListarCoordenadasParagens";
    private final String SOAP_ACTION4 = "http://tempuri.org/LimparBilhetesTemp";
    private final String METHOD_NAME4 = "LimparBilhetesTemp";
    private final String SOAP_ACTION5 = "http://tempuri.org/UpdateLocalizacaoAutocarro";
    private final String METHOD_NAME5 = "UpdateLocalizacaoAutocarro";

    private int printerstatus = 0;
    MyHandler handler;
    private static String printVersion;
    private final int PRINTIT = 1;
    private final int ENABLE_BUTTON = 2;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTBARCODE = 6;
    private final int PRINTQRCODE = 7;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private boolean stop = false;
    private int printting = 0;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private boolean isClose = false;//关闭程序
    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog progressDialog;
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    private static final String TAG = "myAPP";

    boolean processingReading = true;

    TextView tvResult, tvDataHora, latituteField, tvVelField, tvLinha, tvAutocarro, tvParagens, tvUltimaParagem, tvWaves;
    ImageView iv;
    String listaParagens = "";
    String coordinates = "0;0";
    double lat, lng, speed;
    double distancia1 = 0;
    String NMEA = "";
    String Duplicacao = "";
    String SentidoPercurso = "IDA";
    String sentidoExtenso = "";
    String proxTerminal = "";
    String terminalIDA = "";
    String terminalVOLTA = "";
    String currentTimeStamp = "";
    String thread = "readThread";
    String choosed_serial2 = "/dev/ttySAC2";
    int choosed_buad2 = 9600;
    String choosed_serial3 = "/dev/ttySAC3";
    int choosed_buad3 = 115200;
    OutputStream outPrinter = null;
    InputStream inpPrinter = null;
    String t = "";
    Thread gpsThread;
    boolean stopGPSThread = true;
    boolean stopThread = true;
    Thread printerThread;
    boolean stopPrinterThread = true;
    String ultimoAvisoProximidade = "";
    boolean statusAsync = true;
    boolean statusAsync0 = true;
    boolean statusAsync2 = true;
    boolean statusAsync3 = true;
    boolean statusAsync4 = true;
    boolean statusAsync5 = true;
    String linha = "";
    String autocarro = "";
    String turno = "";
    String precoBilhete = "";
    String cartaoCondutor = "";
    String primeiroBilhete = "";
    String proxBilhete = "";
    ImageButton imButton;
    private LocationManager locationManager;
    private String provider;
    private ImageView imgViewStatus;
    private TextView tvCoordenadas = null;
    private TextToSpeech textToSpeech;
    private TextView tvWav;
    private String paragemAnterior = "";
    private String ultimaParagemFalada = "";
    private String resultadoValidacaoPasse = "";
    private String resultadoVendaBilhete = "";
    private Ringtone r;
    private Intent intents;
    private boolean isnews = true;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private String dataString;
    private NfcAdapter nfcAdapter;
    private SurfaceView sv;
    private int MY_DATA_CHECK_CODE = 0;

    final Handler h = new Handler();
    final Handler h0 = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_privado);

        handler = new MyHandler();

        Intent in = getIntent();
        linha = in.getStringExtra("linha");
        autocarro = in.getStringExtra("Autocarro");
        turno = in.getStringExtra("Turno");
        precoBilhete = in.getStringExtra("PrecoBilhete");
        cartaoCondutor = in.getStringExtra("CartaoCondutor");
        primeiroBilhete = in.getStringExtra("ProximoBilhete");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvWav = (TextView) findViewById(R.id.textViewWaves0);

        WebView wv = (WebView) findViewById(R.id.webView0);
        wv.loadUrl("http://sgtu.portalsabi.com/Pub.htm");

        sv = (SurfaceView) findViewById(R.id.surfaceViewPic);

        imButton = (ImageButton) findViewById(R.id.imageView2);
        //imButton.setOnClickListener(onButtonClick);

        tvLinha = (TextView) findViewById(R.id.textViewLinhaValid0);
        tvLinha.setText(linha);

        tvAutocarro = (TextView) findViewById(R.id.textViewAutocarroValid0);
        tvAutocarro.setText(autocarro);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);


        iv = (ImageView) findViewById(R.id.imageViewBarCode0);


        imgViewStatus = (ImageView) findViewById(R.id.imageViewStatusCartao0);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);


        tvParagens = (TextView) findViewById(R.id.textViewParagens0);
        tvUltimaParagem = (TextView) findViewById(R.id.textViewUltimaParagem0);

        tvDataHora = (TextView) findViewById(R.id.textViewDataHora0);

        tvResult = (TextView) findViewById(R.id.textViewResultLeitura0);
        tvCoordenadas = (TextView) findViewById(R.id.textViewLatitude0);
        tvVelField = (TextView) findViewById(R.id.textViewVelocidade0);
        final TextView tvCoordenadas = (TextView) findViewById(R.id.textViewLatitude0);

        final int delay0 = 5000;

        h0.postDelayed(new Runnable() {
            public void run() {
                //if (!textToSpeech.isSpeaking())
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
                    tvDataHora.setText(currentTimeStamp);

                    LocationListener locationListener = new MyLocationListener();

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    // Define o critério para selecionar o fornecedor de localização
                    // (por omissão)
                    Criteria criteria = new Criteria();
                    criteria.getAccuracy();
                    //criteria.getPowerRequirement();
                    provider = locationManager.getBestProvider(criteria, true);
                    locationManager.requestLocationUpdates(provider, 1000, 10, locationListener);
                    Location location = locationManager.getLastKnownLocation(provider);

                    // Inicializa os campos de localização
                    if (location != null) {
                        System.out.println("Fornecedor " + provider
                                + " foi selecionado.");
                        lat = (double) (location.getLatitude());
                        lng = (double) (location.getLongitude());
                        speed = (double) (location.getSpeed());
                        latituteField = (TextView) findViewById(R.id.textViewLatitude0);
                        latituteField.setText(String.valueOf(lat) + ";" + String.valueOf(lng));
                        coordinates = String.valueOf(lat) + ";" + String.valueOf(lng);
                        //tvVelField = (TextView) findViewById(R.id.textViewVelocidade);
                        tvVelField.setText(String.valueOf(2 * speed));

                    }

                    float radius = 30;
                    String nomeParagem = "";
                    double latParagem = 0;
                    double lngParagem = 0;
                    //Lista de paragens do percurso com respectivas coordenadas
                    //NomeParagem + '|' + Latitude + '|' + Longitude;

                    try {

                        if (tvParagens.getText().toString().contains("#") && !textToSpeech.isSpeaking()) {
                            String[] gruposParagens = tvParagens.getText().toString().split(";");
                            for (int i = 0; i < gruposParagens.length - 1; i++) {
                                String[] paragem = gruposParagens[i].split("#");
                                if (paragem.length > 1) {
                                    nomeParagem = paragem[0];
                                    latParagem = Double.parseDouble(paragem[1]);
                                    lngParagem = Double.parseDouble(paragem[2]);

                                    Location loc2 = new Location("");
                                    loc2.setLongitude(lngParagem);
                                    loc2.setLatitude(latParagem);

                                    Location loc1 = new Location("");
                                    loc1.setLatitude((lat));
                                    loc1.setLongitude((lng));

                                    float distance = 0;
                                    distancia1 = loc1.distanceTo(loc2);

                                    //distancia1 = getDistance(latParagem, lngParagem, Double.parseDouble(lat), Double.parseDouble(lng));
                                    //Log.i("Distancia:", String.valueOf(distancia1));
                                    if (distancia1 <= radius && !tvUltimaParagem.getText().toString().equals(nomeParagem)) {
                                        if ((nomeParagem.contains(SentidoPercurso)) || (nomeParagem.contains("Terminal"))) {
                                            paragemAnterior = tvUltimaParagem.getText().toString();
                                            tvUltimaParagem.setText(nomeParagem);

                                            //Aqui deverá ser inserida na BD a chegada na paragem

                                            if (nomeParagem.equals(terminalIDA)) {
                                                SentidoPercurso = "VOLTA";
                                                proxTerminal = terminalVOLTA;
                                                //Limpar os bilhetes
                                                if (statusAsync4)
                                                    new mySOAPAsyncTask4().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvAutocarro.getText().toString());

                                            } else if (nomeParagem.equals(terminalVOLTA)) {
                                                SentidoPercurso = "IDA";
                                                proxTerminal = terminalIDA;
                                                //Limpar os bilhetes
                                                //Limpar os bilhetes
                                                if (statusAsync4)
                                                    new mySOAPAsyncTask4().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvAutocarro.getText().toString());
                                            }


                                            if (!ultimaParagemFalada.equals(nomeParagem)) {
                                                textToSpeech.speak(nomeParagem.replace("Plateau", "Platô"), TextToSpeech.QUEUE_FLUSH, null);
                                                ultimaParagemFalada = nomeParagem;

                                                //Inseririr as coordenadas de percurso no momento da chegada na paragem
                                                //retirado 20-09-2015
                                            }
                                            //textToSpeech.stop();
                                        }
                                    }
                                    //colocado 20-09-2015
                                    if (statusAsync5) {
                                        String velocidade = tvVelField.getText().toString();
                                        //string Autocarro, string Velocidade, string Latitude, string Longitude
                                        new mySOAPAsyncTask5().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, autocarro, velocidade, String.valueOf(lat), String.valueOf(lng));
                                    }
                                }
                            }
                        } else if (statusAsync3)
                            new mySOAPAsyncTask3().execute(tvLinha.getText().toString());


                        if (tvCoordenadas != null) {

                            tvCoordenadas.setMovementMethod(ScrollingMovementMethod
                                    .getInstance());
                        }
                    } catch (Exception ex) {
                        //Log.i("Exception:", ex.toString());
                    }


                    h0.postDelayed(this, delay0);
                }
            }//fim de run
        }, delay0);


        /////////////////////////////////////////////////
/**
 try {
 Picc.openReader();
 }
 catch(Exception ex){

 }**/
        final int delay = 10; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {


//
                //if (processingReading)
                {
                    byte[] sn = new byte[64];
                    int length;
                    try {
                        Picc.openReader();
                        length = Picc.selectCard(sn);
                        if (OtherUtil.byteToHexString(sn, length).toString().length() > 4) {
                            tvResult.setText(OtherUtil.byteToHexString(sn, length));
                            String hexa = tvResult.getText().toString().substring(6, 8) + tvResult.getText().toString().substring(4, 6) + tvResult.getText().toString().substring(2, 4) + tvResult.getText().toString().substring(0, 2);

                            Long decimal = Long.parseLong(hexa, 16);

                            tvResult.setText(String.valueOf(decimal));
                        }

                    } catch (TelpoException e) {
                        e.printStackTrace();
                        //if(!e.getDescription().equals("Cannot find a valid IC card!"))
                        //tvWav.setText("Aguarda cartão");
                        //else
                        //tvWav.setText("Erro leitura");


                    } finally {
                        Picc.closeReader();
                    }

                    //

                    String[] coordenadas = tvCoordenadas.getText().toString().split(";");
                    String velocidade = tvVelField.getText().toString();

                    if (tvResult.getText().toString().length() > 4) {
                        if (!t.equals(tvResult.getText().toString())) {
                            t = tvResult.getText().toString();
                            String oldt = t;
                            Duplicacao = t;

                            if (t.length() != 0) {
                                processingReading = false;

                                {
                                    tvWav.setText("Mantenha cartão encostado...");
                                    r.play();
                                    if (!r.isPlaying())
                                        r.stop();

                                    Log.i("WriteRFID", "CarId:" + t);

                                    if (coordenadas.length > 0) {
                                        try {

                                            //string cartao, string Latitude, string Longitude, string Velocidade, string Autocarro, string Linha, string Paragem
                                            if (statusAsync) {

                                                {
                                                    new mySOAPAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oldt.toString(), coordenadas[0], coordenadas[1], velocidade, autocarro);


                                                }

                                            }
                                        } catch (Exception ex) {

                                            Toast.makeText(TransportePrivado.this,
                                                    ex.getMessage().toString(),
                                                    Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                    processingReading = true;
                                }
                            }

                            t = "";
                            tvResult.setText("");

                        } else if (Duplicacao.equals(t)) {
                            textToSpeech.speak("Duplicação na leitura!", TextToSpeech.QUEUE_FLUSH, null);
                            Duplicacao = "Dup";
                        }
                    }

                    h.postDelayed(this, delay);
                }
            }
        }, delay);


        /////////////////////////////////////////////


    }

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            if (stop == true)
                return;
            switch (msg.what) {
                case PRINTIT:
                    final ArrayList<String> rInfoList = new ArrayList<String>();

                    switch (printting) {
                        case PRINTBARCODE:
                            progressDialog = ProgressDialog.show(TransportePrivado.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                            new barcodePrintThread().start();
                            break;
                        case PRINTQRCODE:
                            progressDialog = ProgressDialog.show(TransportePrivado.this, getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));
                            new qrcodePrintThread().start();
                            break;
                        case PRINTPAPERWALK:
                            progressDialog = ProgressDialog.show(TransportePrivado.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                            new paperWalkPrintThread().start();
                            break;
                        case PRINTCONTENT:
                            progressDialog = ProgressDialog.show(TransportePrivado.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                            new contentPrintThread().start();
                            break;
                    }
                    break;
                case ENABLE_BUTTON:
                    //buttonBarcodePrint.setEnabled(true);
                    printting = 0;
                    break;
                case NOPAPER:
                    //noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(TransportePrivado.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dlg_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;
                case PRINTVERSION:
                    if (msg.obj.equals("1")) {
                        //textPrintVersion.setText(printVersion);
                    } else {
                        Toast.makeText(TransportePrivado.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                    break;
                case PRINTBARCODE:
                    printting = PRINTBARCODE;
                    new barcodePrintThread().start();
                    break;
                case PRINTQRCODE:
                    printting = PRINTQRCODE;
                    new qrcodePrintThread().start();
                    break;
                case PRINTPAPERWALK:
                    printting = PRINTPAPERWALK;
                    progressDialog = ProgressDialog.show(TransportePrivado.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                    new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    printting = PRINTCONTENT;
                    new contentPrintThread().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !TransportePrivado.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(TransportePrivado.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dlg_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(TransportePrivado.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private double getDistance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6371;   // Earth radius in km
        double deltaLat = Math.toRadians(toLat - fromLat);
        double deltaLon = Math.toRadians(toLon - fromLon);
        double lat1 = Math.toRadians(fromLat);
        double lat2 = Math.toRadians(toLat);
        double aVal = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double cVal = 2 * Math.atan2(Math.sqrt(aVal), Math.sqrt(1 - aVal));

        double distance = radius * cVal;
        Log.d("distance", "radius * angle = " + distance);
        return distance;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_validar_passe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back_home) {
            Intent intent = new Intent(TransportePrivado.this, MainActivity.class);
            finish();
            h.removeCallbacks(null);
            h.removeCallbacksAndMessages(null);
            h0.removeCallbacks(null);
            h0.removeCallbacksAndMessages(null);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                textToSpeech = new TextToSpeech(TransportePrivado.this.getApplicationContext(), this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }


    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(this,
                    "Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this,
                    "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
        }
    }

    private final class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)
        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
        }
    }

//Leitura Passe Async

    private class mySOAPAsyncTask extends AsyncTask<String, String, Void> {
        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            //textToSpeech.speak("Resultado: " + resultadoValidacaoPasse, TextToSpeech.QUEUE_FLUSH, null);
            //tipo;status;viagensRemanescentes
            String[] arrayResultadoValid = resultadoValidacaoPasse.split(";");
            String tipo = "";
            String status = "";
            String viagensRemanescentes = "";
            if (arrayResultadoValid.length > 0) {

                status = arrayResultadoValid[0];

                if (!status.toUpperCase().equals("ACTIVO")) {
                    imgViewStatus.setVisibility(View.VISIBLE);
                    imgViewStatus.setImageResource(R.drawable.stop);
                    imgViewStatus.setMaxWidth(100);

                    textToSpeech.speak("Stop! Cartão " + " Inválido", TextToSpeech.QUEUE_FLUSH, null);
                    tvWav.setText("Stop! Cartão " + " " + status);
                } else {
                    imgViewStatus.setVisibility(View.VISIBLE);
                    imgViewStatus.setImageResource(R.drawable.imgok);
                    tvWav.setText("Cartão  OK!");
                    textToSpeech.speak("Cartão  OK", TextToSpeech.QUEUE_FLUSH, null);

                }

            } else {
                imgViewStatus.setVisibility(View.VISIBLE);
                imgViewStatus.setImageResource(R.drawable.stop);
                imgViewStatus.setMaxWidth(100);

                textToSpeech.speak("Stop! Erro na Leitura.", TextToSpeech.QUEUE_FLUSH, null);
                tvWav.setText("Stop! Erro na Leitura.");
            }
            statusAsync = true;

            tipo = "";
            status = "";
            viagensRemanescentes = "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync = false;
            tvWav.setText("A validar...");
            imgViewStatus.setVisibility(View.INVISIBLE);

        }

        @Override
        protected Void doInBackground(String... args)
        //private void soapMessage(String IdPasse, String Latitude, String Longitude, String Velocidade, String Autocarro)
        {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                //TransportePrivado(string cartao, string Latitude, string Longitude, string Velocidade, string Autocarro, string Linha, string Paragem)
                PropertyInfo idpasse = new PropertyInfo();
                idpasse.setName("idCartao");
                idpasse.setValue(args[0]);
                idpasse.setType(String.class);
                request.addProperty(idpasse);

                PropertyInfo latitude = new PropertyInfo();
                latitude.setName("Latitude");
                latitude.setValue(args[1]);
                latitude.setType(String.class);
                request.addProperty(latitude);

                PropertyInfo longitude = new PropertyInfo();
                longitude.setName("Longitude");
                longitude.setValue(args[2]);
                longitude.setType(String.class);
                request.addProperty(longitude);

                PropertyInfo velocidade = new PropertyInfo();
                velocidade.setName("Velocidade");
                velocidade.setValue(args[3]);
                velocidade.setType(String.class);
                request.addProperty(velocidade);

                PropertyInfo autocarro = new PropertyInfo();
                autocarro.setName("Autocarro");
                autocarro.setValue(args[4]);
                autocarro.setType(String.class);
                request.addProperty(autocarro);


                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                SoapObject result = null;


                androidHttpTransport.call(SOAP_ACTION, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
                resultadoValidacaoPasse = result.getProperty(0).toString();
                Log.i("myApp", result.toString());
                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE ValidPasse: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }

    }

    //Venda Bilhete
    private class mySOAPAsyncTask2 extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!resultadoVendaBilhete.contains("Erro")) {


                //O recibo devera receber dados do cabeçalho e do preço do bilhete actualizados na abertura de turno

                /*CheckBox cbOffline = (CheckBox) findViewById(R.id.checkBoxOffline);
                if (!cbOffline.isChecked())
                {


                    byte[] buffer = null;
                    //String mensagem = "\n         SOLATLANTICO\n        NIF: 200252208\n        Tel:8001009\n Email:geral@solatlantico.cv\n";//Recibo de Compra de Bilhete\n";
                    String mensagem = "\n         SOLATLANTICO\n        NIF: 200252208\n      www.solatlantico.cv\n";//Recibo de Compra de Bilhete\n";
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    mensagem = mensagem + "\nData: " + currentDateandTime;
                    mensagem = mensagem + "\nValor: " + precoBilhete + " ECV";
                    //mensagem = mensagem + "\nLinha: " + tvLinha.getText().toString() + "\n";
                    mensagem = mensagem + "\nAutocarro: " + tvAutocarro.getText().toString() + "\n";
                    mensagem = mensagem + "\nSentido: " + sentidoExtenso.replace(" - IDA", "").replace(" - VOLTA", "").replace("Terminal ", "") + "\n";
                    mensagem = mensagem + "\nEntrada: " + tvUltimaParagem.getText().toString().replace(" - IDA", "").replace(" - VOLTA", "") + "\n";
                    // mensagem = mensagem + "\nTerminal: " + proxTerminal.replace(" - IDA","").replace(" - VOLTA","") ;
                    mensagem = mensagem + "\n\n\n\n\n";

                    byte[] set0 = {0x1B, 0x61, 0x31}; // center
                    byte[] set1 = {0x1B, 0x21, 0x30};//big font
                    byte[] set2 = {0x1b, 0x40};//reset
                    mSerialPort3.open(choosed_serial3, choosed_buad3, 0);
                    btMap = BarcodeCreater.creatBarcode(TransportePrivado.this, resultadoVendaBilhete, 384, 60, false, 1);
                    //btMap= getResizedBitmap(btMap,30,350);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    btMap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] barcodeArray = stream.toByteArray();
                    //iv.setImageBitmap(btMap);
                    PrintImageNew(btMap);

                    try {
                        buffer = mensagem.getBytes();
                        mSerialPort3.getOutputStream().write(buffer);
                        mSerialPort3.close();

                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.i("Error:", e.toString().toString());
                    }
                    imgViewStatus.setVisibility(View.VISIBLE);
                    imgViewStatus.setImageResource(R.drawable.imgok);
                    textToSpeech.speak("Bilhete comprado", TextToSpeech.QUEUE_FLUSH, null);
                    tvWav.setText("Bilhete comprado.");

                }
                */

                imgViewStatus.setVisibility(View.VISIBLE);
                imgViewStatus.setImageResource(R.drawable.imgok);
            } else {
                imgViewStatus.setVisibility(View.VISIBLE);
                imgViewStatus.setImageResource(R.drawable.stop);
                textToSpeech.speak("Erro na compra", TextToSpeech.QUEUE_FLUSH, null);
                tvWav.setText("Erro na compra.");
            }

            statusAsync2 = true;
            tvWav.setText("Ultima venda enviada para servidor");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync2 = false;
            resultadoVendaBilhete = "";
            imgViewStatus.setVisibility(View.INVISIBLE);
            //CheckBox cbOffline = (CheckBox) findViewById(R.id.checkBoxOffline);
            //if (!cbOffline.isChecked())
            //  tvWav.setText("A comprar...");


        }

        @Override
        protected Void doInBackground(String... args)
        //insertVendaBilhete(string IdAutocarro,string IdCartao, string IdBilhete, string IdPercurso, string Latitude, string Longitude)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);

            PropertyInfo IdAutocarro = new PropertyInfo();
            IdAutocarro.setName("IdAutocarro");
            IdAutocarro.setValue(args[0]);
            IdAutocarro.setType(String.class);
            request.addProperty(IdAutocarro);

            PropertyInfo IdCartao = new PropertyInfo();
            IdCartao.setName("IdCartao");
            IdCartao.setValue(args[1]);
            IdCartao.setType(String.class);
            request.addProperty(IdCartao);

            PropertyInfo IdBilhete = new PropertyInfo();
            IdBilhete.setName("IdBilhete");
            IdBilhete.setValue(args[2]);
            IdBilhete.setType(String.class);
            request.addProperty(IdBilhete);

            PropertyInfo IdPercurso = new PropertyInfo();
            IdPercurso.setName("IdPercurso");
            IdPercurso.setValue(args[3]);
            IdPercurso.setType(String.class);
            request.addProperty(IdPercurso);

            PropertyInfo Latitude = new PropertyInfo();
            Latitude.setName("Latitude");
            Latitude.setValue(args[4]);
            Latitude.setType(String.class);
            request.addProperty(Latitude);

            PropertyInfo Longitude = new PropertyInfo();
            Longitude.setName("Longitude");
            Longitude.setValue(args[5]);
            Longitude.setType(String.class);
            request.addProperty(Longitude);

            PropertyInfo Paragem = new PropertyInfo();
            Paragem.setName("Paragem");
            Paragem.setValue(args[6]);
            Paragem.setType(String.class);
            request.addProperty(Paragem);

            PropertyInfo Turno = new PropertyInfo();
            Turno.setName("Turno");
            Turno.setValue(args[7]);
            Turno.setType(String.class);
            request.addProperty(Turno);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {

                androidHttpTransport.call(SOAP_ACTION2, envelope);
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


    //Lista Paragens
    private class mySOAPAsyncTask3 extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            tvParagens.setText(listaParagens);
            String[] gruposParagens = tvParagens.getText().toString().split(";");
            for (int i = 0; i < gruposParagens.length - 1; i++) {
                String[] paragem = gruposParagens[i].split("#");
                if (paragem.length > 1) {
                    String nomeParagem = paragem[0];
                    if (nomeParagem.contains("Terminal") && nomeParagem.contains("IDA"))
                        terminalIDA = nomeParagem;
                    else if (nomeParagem.contains("Terminal") && nomeParagem.contains("VOLTA"))
                        terminalVOLTA = nomeParagem;


                    if (SentidoPercurso.equals("VOLTA")) {
                        sentidoExtenso = terminalIDA + " - " + terminalVOLTA;
                    } else if (SentidoPercurso.equals("IDA")) {
                        sentidoExtenso = terminalVOLTA + " - " + terminalIDA;
                    }

                }
            }
            Log.i("myApp", "TERMINAL IDA:" + terminalIDA + ";Terminal VOLTA:" + terminalVOLTA);
            statusAsync3 = true;

//            tvDadosWS.setText(response);

            //EditText et = (EditText)findViewById(R.id.editTextResult);
            //et.setText(response5);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync3 = false;
        }

        @Override
        protected Void doInBackground(String... args)
        //private void soapMessage(String IdPasse, String Latitude, String Longitude, String Velocidade, String Autocarro)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);

            PropertyInfo linha = new PropertyInfo();
            linha.setName("linha");
            linha.setValue(args[0]);
            linha.setType(String.class);
            request.addProperty(linha);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {
                androidHttpTransport.call(SOAP_ACTION3, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
                listaParagens = result.getProperty(0).toString();

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


    //Data e Hora
    private class mySOAPAsyncTaskTime extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            TextView tvdt = (TextView) findViewById(R.id.textViewDataHora);
            tvdt.setText(currentTimeStamp);

            TextView tvw = (TextView) findViewById(R.id.textViewWaves);
            if (tvw.getText().toString().equals("(((((( RFID ))))))"))
                tvw.setText("((( RFID )))");
            else
                tvw.setText("(((((( RFID ))))))");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... args)
        //private void soapMessage(String IdPasse, String Latitude, String Longitude, String Velocidade, String Autocarro)
        {

            try {

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                currentTimeStamp = dateFormat.format(new Date()); // Find todays date


            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }


    }

    //Limpar Bilhetes Temporarios
    private class mySOAPAsyncTask4 extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            statusAsync4 = true;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync4 = false;
        }

        @Override
        protected Void doInBackground(String... args)
        //private void soapMessage(String IdPasse, String Latitude, String Longitude, String Velocidade, String Autocarro)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);

            PropertyInfo Autocarro = new PropertyInfo();
            Autocarro.setName("Autocarro");
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
                androidHttpTransport.call(SOAP_ACTION4, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
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

    //Update Localizacao
    private class mySOAPAsyncTask5 extends AsyncTask<String, Void, Void> {
        String resultadoLocalizacao = "";

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            statusAsync5 = true;
            //Se proximo de outro da mesma linha avisar
            String[] resultado = resultadoLocalizacao.split(";");
            if (resultado.length > 1) {
                if (!ultimoAvisoProximidade.equals(resultado[1])) {
                    ultimoAvisoProximidade = resultado[1];
                    textToSpeech.speak("Autocarro da mesma linha a " + resultado[1], TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusAsync5 = false;
        }

        @Override
        protected Void doInBackground(String... args)
        //UpdateLocalizacaoAutocarro(string Autocarro, string Velocidade, string Latitude, string Longitude)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);

            PropertyInfo Autocarro = new PropertyInfo();
            Autocarro.setName("Autocarro");
            Autocarro.setValue(args[0]);
            Autocarro.setType(String.class);
            request.addProperty(Autocarro);

            PropertyInfo Velocidade = new PropertyInfo();
            Velocidade.setName("Velocidade");
            Velocidade.setValue(args[1]);
            Velocidade.setType(String.class);
            request.addProperty(Velocidade);

            PropertyInfo Latitude = new PropertyInfo();
            Latitude.setName("Latitude");
            Latitude.setValue(args[2]);
            Latitude.setType(String.class);
            request.addProperty(Latitude);

            PropertyInfo Longitude = new PropertyInfo();
            Longitude.setName("Longitude");
            Longitude.setValue(args[3]);
            Longitude.setType(String.class);
            request.addProperty(Longitude);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {
                androidHttpTransport.call(SOAP_ACTION5, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
                Log.i("myApp", result.toString());
                resultadoLocalizacao = result.toString();
                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }


    }


    private class paperWalkPrintThread extends Thread {
        public void run() {
            super.run();
            setName("paper walk Thread");
            try {
                ThermalPrinter.start();
                ThermalPrinter.walkPaper(paperWalk);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                    return;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {

                TransportePrivado.this.sleep(500);
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper = false;

                TransportePrivado.this.sleep(2000);
                if (progressDialog != null && !TransportePrivado.this.isFinishing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG, "The Print Progress End !!!");
                if (isClose) {
//                    onDestroy();
                    finish();
                }
            }
            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));
        }
    }

    private class barcodePrintThread extends Thread {
        public void run() {
            super.run();
            setName("Barcode Print Thread");

            try {
                ThermalPrinter.start();
                ThermalPrinter.printBarcode(barcodeStr);
                ThermalPrinter.walkPaper(100);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                    return;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                TransportePrivado.this.sleep(1000);
                ThermalPrinter.stop();
                nopaper = false;

                if (progressDialog != null && !TransportePrivado.this.isFinishing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG, "The Print Progress End !!!");
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (isClose) {
                    finish();
                }
            }

            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));

        }
    }

    private class qrcodePrintThread extends Thread {
        public void run() {
            super.run();
            setName("Barcode Print Thread");
            try {

                ThermalPrinter.start();
                printQrcode(qrcodeStr);
                ThermalPrinter.addString(qrcodeStr);
                ThermalPrinter.printStringAndWalk(ThermalPrinter.DIRECTION_FORWORD, ThermalPrinter.WALK_LINE, 12);
                ThermalPrinter.clearString();

            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                    return;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {

                TransportePrivado.this.sleep(1000);
                //lock.release();
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper = false;
                if (progressDialog != null && !TransportePrivado.this.isFinishing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG, "The Print Progress End !!!");
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (isClose) {
                    finish();
                }
            }

            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));

        }
    }

    private class contentPrintThread extends Thread {
        public void run() {
            super.run();
            setName("Content Print Thread");
            try {
                ThermalPrinter.start();
                ThermalPrinter.reset();
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
                ThermalPrinter.setLeftIndent(leftDistance);
                ThermalPrinter.setLineSpace(lineDistance);
                if (wordFont == 3) {
                    ThermalPrinter.setFontSize(2);
                    ThermalPrinter.enlargeFontSize(2, 2);
                } else {
                    ThermalPrinter.setFontSize(wordFont);
                }
                ThermalPrinter.setGray(printGray);
                ThermalPrinter.addString(printContent);
                ThermalPrinter.printString();
                ThermalPrinter.clearString();
                ThermalPrinter.walkPaper(100);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                    return;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                TransportePrivado.this.sleep(2000);
                //lock.release();
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                ThermalPrinter.stop();
                nopaper = false;
                if (progressDialog != null && !TransportePrivado.this.isFinishing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG, "The Print Progress End !!!");
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (isClose) {
//                    onDestroy();
                    finish();
                }
            }
            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));

        }

    }


    private void sleep(int ms) {

        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {


        stop = true;
        stopThread = false;
        try {
            ThermalPrinter.stop();
            //unregisterReceiver(printReceive);

        } catch (Exception ex) {
        }
        this.getApplicationContext();
        h.removeCallbacks(null);
        h.removeCallbacksAndMessages(null);
        h0.removeCallbacks(null);
        h0.removeCallbacksAndMessages(null);

        //onBackPressed();
        finish();
        super.onDestroy();
    }

    private BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                    if (level * 5 <= scale) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    LowBattery = false;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 生成条码
     *
     * @param str       条码内容
     * @param type      条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX, EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14, RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
     * @param bmpWidth  生成位图宽,宽不能大于384，不然大于打印纸宽度
     * @param bmpHeight 生成位图高，8的倍数
     */

    public Bitmap CreateCode(String str, com.google.zxing.BarcodeFormat type, int bmpWidth, int bmpHeight) throws WriterException {
        //生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Function printBarcode
     *
     * @return None
     * @author zhouzy
     * @date 20141223
     * @note
     */
    private void printQrcode(String str) throws Exception {
        //       Bitmap bitmap= BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/time1.bmp"));
        Bitmap bitmap = CreateCode(str, BarcodeFormat.QR_CODE, 256, 256);
        if (bitmap != null) {
            Log.v(TAG, "Find the Bmp");
            ThermalPrinter.printLogo(bitmap);
        }
    }

}
