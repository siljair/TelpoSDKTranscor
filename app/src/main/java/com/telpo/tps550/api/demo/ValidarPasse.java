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
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ValidarPasse extends Activity implements TextToSpeech.OnInitListener {

    private final static int MAX_LEFT_DISTANCE = 255;
    private static final String TAG = "myAPP";
    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private static String printVersion;
    final Handler h = new Handler();
    final Handler h0 = new Handler();
    private final String NAMESPACE = "http://tempuri.org/";
    private final String URL = "http://transcor.portalsabi.com/WebServiceFleet.asmx";
    private final String SOAP_ACTION = "http://tempuri.org/ValidarPasse";
    private final String METHOD_NAME = "ValidarPasse";
    private final String SOAP_ACTION2 = "http://tempuri.org/insertVendaBilhete";
    private final String METHOD_NAME2 = "insertVendaBilhete";
    private final String SOAP_ACTION3 = "http://tempuri.org/ListarCoordenadasParagens";
    private final String METHOD_NAME3 = "ListarCoordenadasParagens";
    private final String SOAP_ACTION4 = "http://tempuri.org/LimparBilhetesTemp";
    private final String METHOD_NAME4 = "LimparBilhetesTemp";
    private final String SOAP_ACTION5 = "http://tempuri.org/UpdateLocalizacaoAutocarro2";
    private final String METHOD_NAME5 = "UpdateLocalizacaoAutocarro2";
    private final String SOAP_ACTION6 = "http://tempuri.org/insertVendaPrePago";
    private final String METHOD_NAME6 = "insertVendaPrePago";
    private final String SOAP_ACTION7 = "http://tempuri.org/InserirLeituraPasse";
    private final String METHOD_NAME7 = "InserirLeituraPasse";
    private final String SOAP_ACTION29 = "http://tempuri.org/uploadBilhetes";
    private final String METHOD_NAME29 = "uploadBilhetes";
    private final String SOAP_ACTION30 = "http://tempuri.org/AbrirTurnoOffline";
    private final String METHOD_NAME30 = "AbrirTurnoOffline";
    private final String SOAP_ACTION31 = "http://tempuri.org/uploadFechoTurnoOffline";
    private final String METHOD_NAME31 = "uploadFechoTurnoOffline";
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
    DBHelper db;
    MyHandler handler;
    ProgressDialog dialog;
    boolean readRFIDLoop = true;
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
    String passesTemporarios = "";
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
    String codTurno = "";
    String precoBilhete = "";
    String cartaoCondutor = "";
    String primeiroBilhete = "";
    String proxBilhete = "";
    ImageButton imButton;
    CheckBox box;
    Spinner spinner;
    ArrayAdapter<String> spinnerArrayAdapter;
    String zero[] = {"0"};
    String values[] = {"0", "5", "10", "15","Cartao"};
    String ultimoBilheteImpresso= "";
    String CodCondutor = "";
    String SWVersion = "";
    TextView tvContador ;
    private int printerstatus = 0;
    private boolean stop = false;
    private int counterDup = 0;
    private int printting = 0;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private boolean isClose = false;//关闭程序
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog progressDialog;
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
    private String resultadoUploadFecho = "";
    private String listaBilhetesUploaded = "";
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_passe);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        db = new DBHelper(getApplicationContext());

        handler = new MyHandler();

        tvContador = (TextView)findViewById(R.id.textViewContador);

        Intent in = getIntent();
        linha = in.getStringExtra("linha");
        tvLinha = (TextView) findViewById(R.id.textViewLinhaValid);
        tvLinha.setText(linha);

        autocarro = in.getStringExtra("Autocarro");
        tvAutocarro = (TextView) findViewById(R.id.textViewAutocarroValid);
        tvAutocarro.setText(autocarro);
        turno = in.getStringExtra("Turno");
        codTurno = in.getStringExtra("codTurno");
        precoBilhete = in.getStringExtra("PrecoBilhete");
        cartaoCondutor = in.getStringExtra("CartaoCondutor");
        primeiroBilhete = in.getStringExtra("ProximoBilhete");
        CodCondutor = in.getStringExtra("CodigoCondutor");
        if(CodCondutor.contains(";"))
        {
            String[] spli = CodCondutor.split(";");
            CodCondutor = spli[0];
        }
        SWVersion = in.getStringExtra("SWVersion");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TextView tvTurno = (TextView)findViewById(R.id.textViewTurnoValid);
        tvTurno.setText(turno);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        primeiroBilhete = preferences.getString("ProximoBilhete", "");

        Toast.makeText(ValidarPasse.this,
                "Primeiro Bilhete:" + primeiroBilhete,
                Toast.LENGTH_SHORT).show();

        tvWav = (TextView) findViewById(R.id.textViewWaves);

        WebView wv = (WebView) findViewById(R.id.webView);
        wv.loadUrl("http://transcor.portalsabi.com/Pub.htm");


        sv = (SurfaceView) findViewById(R.id.surfaceViewPic);

        imButton = (ImageButton) findViewById(R.id.imageView2);
        //imButton.setOnClickListener(onButtonClick);



        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);


        iv = (ImageView) findViewById(R.id.imageViewBarCode);


        imgViewStatus = (ImageView) findViewById(R.id.imageViewStatusCartao);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);


        tvParagens = (TextView) findViewById(R.id.textViewParagens);
        tvUltimaParagem = (TextView) findViewById(R.id.textViewUltimaParagem);


        tvDataHora = (TextView) findViewById(R.id.textViewDataHora);


        spinner = (Spinner) findViewById(R.id.spinnerNumerPrePago);
        spinner.setEnabled(false);


        spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, zero);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        box = (CheckBox) findViewById(R.id.checkBoxVenderPrePago);

        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (!box.isChecked()) {
                    spinnerArrayAdapter = new ArrayAdapter<String>(ValidarPasse.this, R.layout.spinner_layout, zero);
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout); // The drop down view
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setSelection(0);
                    spinner.setEnabled(false);
                } else {
                    spinnerArrayAdapter = new ArrayAdapter<String>(ValidarPasse.this, R.layout.spinner_layout, values);
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout); // The drop down view
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setSelection(0);
                    spinner.setEnabled(true);
                }

            }
        });


        tvResult = (TextView) findViewById(R.id.textViewResultLeitura);
        tvCoordenadas = (TextView) findViewById(R.id.textViewLatitude);
        tvCoordenadas.setText("0;0");
        tvVelField = (TextView) findViewById(R.id.textViewVelocidade);
        final TextView tvCoordenadas = (TextView) findViewById(R.id.textViewLatitude);

        final int delay0 = 10000;

        h0.postDelayed(new Runnable() {
            public void run() {
                //if (!textToSpeech.isSpeaking())
                {

                    new mySOAPAsyncTaskTime().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    h0.postDelayed(this, delay0);
                }
            }//fim de run
        }, delay0);


        final int delay = 500; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {

                getCard();
                //new mySOAPAsyncTaskGetRFID().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                h.postDelayed(this, delay);
            }

        }, delay);


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
            Intent intent = new Intent(ValidarPasse.this, MainActivity.class);
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
                textToSpeech = new TextToSpeech(ValidarPasse.this.getApplicationContext(), this);
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

    private void getCard()
    {
        byte[] sn = new byte[64];
        int length;
        try {
            Picc.openReader();

            length = Picc.selectCard(sn);
            if (OtherUtil.byteToHexString(sn, length).toString().length() > 4) {
                tvResult.setText(OtherUtil.byteToHexString(sn, length));

                String hexa = "";
                if(tvResult.getText().length() > 7) {
                    hexa = tvResult.getText().toString().substring(6, 8) + tvResult.getText().toString().substring(4, 6) + tvResult.getText().toString().substring(2, 4) + tvResult.getText().toString().substring(0, 2);
                    Long decimal = Long.parseLong(hexa, 16);
                    tvResult.setText(String.valueOf(decimal));

                    if (tvResult.getText().toString().length() > 7)
                    {
                        new mySOAPAsyncTaskGetRFID().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
                /**
                String HEX_DIGITS = "0123456789ABCDEF";
                char[] sources = tvResult.getText().toString().toCharArray();
                int dec = 0;
                for (int i = 0; i < sources.length; i++) {
                    int digit = HEX_DIGITS.indexOf(Character.toUpperCase(sources[i]));
                    dec += digit * Math.pow(16, (sources.length - (i + 1)));
                }
                tvResult.setText(String.valueOf(dec));
                **/
            }

            }
        catch (TelpoException e) {
            e.printStackTrace();
            //if(!e.getDescription().equals("Cannot find a valid IC card!"))
            //tvWav.setText("Aguarda cartão");
            //else
            //tvWav.setText("Erro leitura");


        } finally {
            Picc.closeReader();
        }


    }

//Leitura Passe Async

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
        processingReading = false;
        Picc.closeReader();
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

    @Override
    public void onBackPressed() {
        // nothing to do here
        // … really
        onDestroy();

    }

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

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            if (stop == true)
                return;
            switch (msg.what) {
                case PRINTIT:
                    final ArrayList<String> rInfoList = new ArrayList<String>();

                    switch (printting) {
                        case PRINTBARCODE:
                            progressDialog = ProgressDialog.show(ValidarPasse.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                            new barcodePrintThread().start();
                            break;
                        case PRINTQRCODE:
                            progressDialog = ProgressDialog.show(ValidarPasse.this, getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));
                            new qrcodePrintThread().start();
                            break;
                        case PRINTPAPERWALK:
                            progressDialog = ProgressDialog.show(ValidarPasse.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                            new paperWalkPrintThread().start();
                            break;
                        case PRINTCONTENT:
                            progressDialog = ProgressDialog.show(ValidarPasse.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
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
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ValidarPasse.this);
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
                        Toast.makeText(ValidarPasse.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
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
                    progressDialog = ProgressDialog.show(ValidarPasse.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                    new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    printting = PRINTCONTENT;
                    new contentPrintThread().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !ValidarPasse.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ValidarPasse.this);
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
                    Toast.makeText(ValidarPasse.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
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

    //validar passe online
    private class mySOAPAsyncTask extends AsyncTask<String, String, Void> {

        String cartaoDB = "";
        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            //textToSpeech.speak("Resultado: " + resultadoValidacaoPasse, TextToSpeech.QUEUE_FLUSH, null);
            //tipo;status;viagensRemanescentes
            String[] arrayResultadoValid = resultadoValidacaoPasse.split(";");
            String tipo = "";
            String status = "";
            String viagensRemanescentes = "";
            if (arrayResultadoValid.length > 2)
            {
                if(arrayResultadoValid[0].length()> 2) {
                    tipo = arrayResultadoValid[0];
                    status = arrayResultadoValid[1];
                    viagensRemanescentes = arrayResultadoValid[2];

                    if (!status.toUpperCase().equals("ACTIVO")) {
                        imgViewStatus.setVisibility(View.VISIBLE);
                        imgViewStatus.setImageResource(R.drawable.stop);
                        imgViewStatus.setMaxWidth(100);

                        textToSpeech.speak("Stop! Cartão " + tipo + " " + status, TextToSpeech.QUEUE_FLUSH, null);
                        tvWav.setText("Stop! Cartão " + tipo + " " + status);
                    } else if (status.toUpperCase().equals("ACTIVO")) {
                        imgViewStatus.setVisibility(View.VISIBLE);
                        imgViewStatus.setImageResource(R.drawable.imgok);


                        if (!tipo.equals("PrePago")) {
                            tvWav.setText("Passe " + tipo + " OK!");
                            textToSpeech.speak("Passe " + tipo + " OK!", TextToSpeech.QUEUE_FLUSH, null);
                        }
                        else if (tipo.equals("PrePago"))
                        {
                            String prepagoMsg = "Bilhete " + tipo.replace("PrePago","Pré Pago") + " OK! Ficam " + viagensRemanescentes + " viagens";
                            prepagoMsg = prepagoMsg.replace("Ficam 1 viagens", "Fica uma viagem").replace("Ficam 2 viagens", "Ficam duas viagens");
                            tvWav.setText(prepagoMsg);
                            textToSpeech.speak(prepagoMsg, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }

                    db = new DBHelper(getApplicationContext());
                    Cartao cartaoDB = new Cartao();
                    cartaoDB.setNumero(this.cartaoDB);
                    cartaoDB.setTipo(tipo);
                    cartaoDB.setStatus(status);
                    if (!tipo.equals("PrePago"))
                    {
                        cartaoDB.setAnoMes(viagensRemanescentes);
                        if(arrayResultadoValid.length > 3)
                        {
                            cartaoDB.setAnoMesAnterior(arrayResultadoValid[3].toString());
                            cartaoDB.setdataExpiracao(arrayResultadoValid[4].toString());
                        }
                    }
                    db.deleteBook(cartaoDB);
                    db.createCartao(cartaoDB);
                }

            }
            else
            {
                imgViewStatus.setVisibility(View.VISIBLE);
                imgViewStatus.setImageResource(R.drawable.stop);
                imgViewStatus.setMaxWidth(100);

                textToSpeech.speak("Stop! Erro na Leitura.", TextToSpeech.QUEUE_FLUSH, null);
                tvWav.setText("Stop! Erro na Leitura.");
            }
            statusAsync = true;


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
                resultadoValidacaoPasse = "";
                //ValidarPasse(string cartao, string Latitude, string Longitude, string Velocidade, string Autocarro, string Linha, string Paragem)
                cartaoDB = args[0];
                PropertyInfo idpasse = new PropertyInfo();
                idpasse.setName("cartao");
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

                PropertyInfo linha = new PropertyInfo();
                linha.setName("Linha");
                linha.setValue(args[5]);
                linha.setType(String.class);
                request.addProperty(linha);

                PropertyInfo paragem = new PropertyInfo();
                paragem.setName("Paragem");
                paragem.setValue(args[6]);
                paragem.setType(String.class);
                request.addProperty(paragem);

                Log.i("myApp", "oldCode:" + args[7].toString());
                PropertyInfo OldCode = new PropertyInfo();
                OldCode.setName("oldCode");
                OldCode.setValue(args[7]);
                OldCode.setType(String.class);
                request.addProperty(OldCode);

                Log.i("myApp", "newCode:" + args[8].toString());

                PropertyInfo NewCode = new PropertyInfo();
                NewCode.setName("newCode");
                NewCode.setValue(args[8]);
                NewCode.setType(String.class);
                request.addProperty(NewCode);

                PropertyInfo idTurno = new PropertyInfo();
                idTurno.setName("turno");
                idTurno.setValue(args[9]);
                idTurno.setType(String.class);
                request.addProperty(idTurno);


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
    private class mySOAPAsyncTask2 extends AsyncTask<String, Void, Void>
    {
        String bilheteAsync = "";
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            statusAsync2 = true;

            if (resultadoVendaBilhete.contains(bilheteAsync) )
            {
                //imgViewStatus.setVisibility(View.VISIBLE);
                //imgViewStatus.setImageResource(R.drawable.imgok);
                db = new DBHelper(getApplicationContext());
                db.updateBilheteEnviado(bilheteAsync);
                tvWav.setText("Ultima venda enviada para servidor");
            }
            else
            {
                //imgViewStatus.setVisibility(View.VISIBLE);
                //imgViewStatus.setImageResource(R.drawable.stop);
                //textToSpeech.speak("Erro na compra", TextToSpeech.QUEUE_FLUSH, null);
                tvWav.setText(bilheteAsync);
            }



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
            bilheteAsync = args[2];
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

            PropertyInfo codTurno = new PropertyInfo();
            codTurno.setName("codTurno");
            codTurno.setValue(args[8]);
            codTurno.setType(String.class);
            request.addProperty(codTurno);

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
                statusAsync2 = true;
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
                latituteField = (TextView) findViewById(R.id.textViewLatitude);
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

            CheckInternet chkNet = new CheckInternet();
            boolean hasnet = true;
            hasnet = chkNet.isInternetAvailable(getApplicationContext());

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
                            if (distancia1 <= radius && !tvUltimaParagem.getText().toString().equals(nomeParagem))
                            {
                                if ((nomeParagem.contains(SentidoPercurso)) || (nomeParagem.contains("Terminal"))) {
                                    paragemAnterior = tvUltimaParagem.getText().toString();
                                    tvUltimaParagem.setText(nomeParagem);

                                    //Aqui deverá ser inserida na BD a chegada na paragem

                                    if (nomeParagem.equals(terminalIDA))
                                    {
                                        SentidoPercurso = "VOLTA";
                                        proxTerminal = terminalVOLTA;
                                        passesTemporarios = "";
                                        db = new DBHelper(getApplicationContext());

                                        //Limpar os bilhetes e upload passes lidos offline
                                        if (statusAsync4) {
                                            new mySOAPAsyncTask4().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvAutocarro.getText().toString());

                                            db = new DBHelper(getApplicationContext());
                                            String dados = db.getAllLeituras();

                                            if(hasnet)
                                            {
                                                new mySOAPAsyncTaskUploadLeituras().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dados);

                                            }
                                        }
                                    }
                                    else if (nomeParagem.equals(terminalVOLTA))
                                    {
                                        SentidoPercurso = "IDA";
                                        proxTerminal = terminalIDA;
                                        //Limpar os passes
                                        passesTemporarios = "";
                                        //Limpar os bilhetes
                                        // Limpar os bilhetes e upload passes lidos offline

                                        if (statusAsync4)
                                        {
                                            new mySOAPAsyncTask4().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvAutocarro.getText().toString());

                                        }
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
                                //string Autocarro, string sentido, string paragem, string Velocidade, string Latitude, string Longitude, string Linha
                                //autocarro, SentidoPercurso, nomeParagem, velocidade, String.valueOf(lat), String.valueOf(lng),linha
                                new mySOAPAsyncTask5().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, autocarro, SentidoPercurso, nomeParagem, velocidade, String.valueOf(lat), String.valueOf(lng),linha);

/**
                                CheckInternet chkNet = new CheckInternet();
                                boolean hasnet = true;
                                hasnet = chkNet.isInternetAvailable(getApplicationContext());
                                if(hasnet)
                                {

                                    String bi = "";
                                    bi = db.getAllBilhetes(tvAutocarro.getText().toString());
                                    if (bi.length() > 0)
                                    {
                                        new mySOAPAsyncTask30().execute(tvAutocarro.getText().toString(), turno, linha,SWVersion , cartaoCondutor);//async task para abrir turno
                                        new mySOAPAsyncTaskUploadBilhete().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bi);
                                    }

                                    //uploadFechoTurnoOffline( Autocarro,  Turno,  Condutor,  DataAbertura,  TotalVendas,  DataFecho,  TotalVoltas,  Linha,  SWVersion,  PrecoBilhete,  AbertoPor)
                                    TurnoTemp tmp = db.getTurnoFechadoOffline(tvAutocarro.getText().toString(),turno);
                                    if(tmp.getdataAberturaTurno().trim().length()> 0)
                                        new ValidarPasse.mySOAPAsyncTask31().execute(tmp.getautocarroTurno(),tmp.getturnoTurno(),tmp.getabertoporTurno(),tmp.getdataAberturaTurno(),tmp.getvendasTurno(),tmp.getDataFecho(),tmp.getvoltasTurno(),tmp.getlinhaTurno(),tmp.getSWversionTurno(),precoBilhete,tmp.getabertoporTurno());//async task para abrir turno

                                }
**/

                            }
                        }
                    }
                } else if (statusAsync3)
                {
                    if(hasnet)
                        new mySOAPAsyncTask3().execute(tvLinha.getText().toString());
                    else //senao ver localmente as paragens
                    {
                        // cursor.getString(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4)
                        //idPercursoParagem; nomeParagem;latitudeParagem;longitudeParagem#
                        tvParagens.setText(db.getParagemPercurso(tvLinha.getText().toString()));

                    }


                }

                if (tvCoordenadas != null) {

                    tvCoordenadas.setMovementMethod(ScrollingMovementMethod
                            .getInstance());
                }



                if(hasnet)
                {

                    String bi = "";
                    bi = db.getAllBilhetes(tvAutocarro.getText().toString());
                    if (bi.length() > 0)
                    {
                        //TurnoTemp tute = db.getTurnoTemp(tvAutocarro.getText().toString(), turno, linha , cartaoCondutor);
                        //new mySOAPAsyncTask30().execute(tvAutocarro.getText().toString(), turno, linha,SWVersion , cartaoCondutor,tute.getdataAberturaTurno());//async task para abrir turno
                        new mySOAPAsyncTaskUploadBilhete().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bi);
                    }

                    //uploadFechoTurnoOffline( Autocarro,  Turno,  Condutor,  DataAbertura,  TotalVendas,  DataFecho,  TotalVoltas,  Linha,  SWVersion,  PrecoBilhete,  AbertoPor)
                    String tmp = db.getTurnoFechadoOffline(tvAutocarro.getText().toString(),turno);
                    if(tmp.trim().length()> 0)
                    {
                        new ValidarPasse.mySOAPAsyncTask31().execute(tmp);//async task para upload fecho turno
                    }

                    //Enviar Leituras passe
                    db = new DBHelper(getApplicationContext());
                    String dados = db.getAllLeituras();
                     if(dados.trim().length()> 0)
                    {
                        new mySOAPAsyncTaskUploadLeituras().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dados);
                    }
                }
            } catch (Exception ex) {
                //Log.i("Exception:", ex.toString());
            }


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

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }


    }

    //Get RFID Processar venda e validação passe
    private class mySOAPAsyncTaskGetRFID extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            {
                //

                String[] coordenadas = tvCoordenadas.getText().toString().split(";");
                String velocidade = tvVelField.getText().toString();

                if (tvResult.getText().toString().length() > 4) {
                    if (!t.equals(tvResult.getText().toString()))
                    {
                        t = tvResult.getText().toString();
                        String oldt = t;
                        tvResult.setText("");
                        //Duplicacao = t;

                        if (t.length() != 0) {

                            if (t.equals(cartaoCondutor) && stopThread)
                            {
                                    //if (statusAsync2)
                                    {
                                        //statusAsync2 = false;
                                        r.play();
                                        if (!r.isPlaying())
                                            r.stop();

                                            try {


                                                if(db.getProxBilhete(tvAutocarro.getText().toString()) != null)
                                                    proxBilhete = db.getProxBilhete(tvAutocarro.getText().toString());

                                                if (proxBilhete.equals(""))
                                                    proxBilhete = primeiroBilhete;
                                                else
                                                {

                                                    Log.i("myAppUltimNumero", proxBilhete.toString());
                                                    int ultimoNumero = Integer.parseInt(proxBilhete.substring(proxBilhete.length() - 4)) + 1;
                                                    int ultimoNumeroServidor =Integer.parseInt(primeiroBilhete.substring(proxBilhete.length() - 4)) + 1;
                                                    if(ultimoNumeroServidor > ultimoNumero)
                                                        proxBilhete = primeiroBilhete;

                                                    proxBilhete = proxBilhete.substring(0, proxBilhete.length() - 4) + String.format("%04d", ultimoNumero);

                                                }

                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ValidarPasse.this);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("ProximoBilhete", proxBilhete);

                                                editor.apply();
                                                //contador de bilhetes comentado em 01-12-2015 v1.08
                                                //tvContador.setText((proxBilhete.substring(proxBilhete.length() - 4)));
                                                //tvAutocarro.getText().toString().length() > 6 inserido 24-04-2016 para evitar campo autocarro vazio
                                                if(ultimoBilheteImpresso != proxBilhete && tvAutocarro.getText().toString().length() > 6
                                                        && autocarro.length() > 6 && tvLinha.getText().toString().trim().length()>0
                                                        && proxBilhete.startsWith(autocarro.replace("-","")))
                                                {

                                                    try {

                                                        ThermalPrinter.start();
                                                        String mensagem = "";
                                                        //ThermalPrinter.printString();
                                                        ThermalPrinter.clearString();

                                                        mensagem = "       TRANSCOR SV, SA\n        NIF: 200505939\n";//Recibo de Compra de Bilhete\n";

                                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                        String currentDateandTime = sdf.format(new Date());
                                                        mensagem += "Data: " + currentDateandTime + "\n";

                                                        mensagem += "Valor: " + precoBilhete + " ECV\n";
                                                        mensagem += "Linha: " + tvLinha.getText().toString() + "\n";
                                                        mensagem += "Autocarro: " + tvAutocarro.getText().toString() + "\\" + CodCondutor + "\n";
                                                        mensagem += "Sentido: " + sentidoExtenso.replace(" - IDA", "").replace(" - VOLTA", "").replace("Terminal ", "") + "\n";
                                                        mensagem += "\nEntrada: " + tvUltimaParagem.getText().toString().replace(" - IDA", "").replace(" - VOLTA", "") + "\n";
                                                        mensagem += " \n  \n  \n  \n  \n  ";

                                                        Bitmap bitmap = CreateCode(proxBilhete, BarcodeFormat.CODE_128, 384, 108);
                                                        ThermalPrinter.printLogo(bitmap);
                                                        ThermalPrinter.addString(mensagem);
                                                        ThermalPrinter.printString();

                                                        ultimoBilheteImpresso = proxBilhete;

                                                        //Enviar Online


                                                        tvWav.setText("Bilhete comprado " + proxBilhete);
                                                        textToSpeech.speak("Bilhete comprado", TextToSpeech.QUEUE_FLUSH, null);

                                                        coordenadas = tvCoordenadas.getText().toString().split(";");

                                                        //Formato de data para slqlite
                                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        String currentDateandTime2 = sdf2.format(new Date());

                                                        db = new DBHelper(getApplicationContext());
                                                        Bilhete bil = new Bilhete();
                                                        bil.setIdAutocarro(autocarro);
                                                        bil.setIdCartao(t.toString());
                                                        bil.setIdBilhete(proxBilhete);
                                                        bil.setIdPercurso(linha);
                                                        bil.setLatitude(coordenadas[0]);
                                                        bil.setLongitude(coordenadas[1]);
                                                        bil.setParagem(tvUltimaParagem.getText().toString());
                                                        bil.setTurno(turno);
                                                        bil.setDataHora(currentDateandTime2);
                                                        bil.setEnviado("Nao");
                                                        bil.setCodTurnoBilhete(codTurno);
                                                        db.deleteBilhete(bil);
                                                        db.createBilhete(bil);

                                                        //atualiza total vendas
                                                        db.updateTurnoTempVendas(autocarro,linha,turno,t.toString());

                                                        //if(statusAsync2)
                                                        CheckInternet chkNet = new CheckInternet();
                                                        boolean hasnet = true;
                                                        hasnet = chkNet.isInternetAvailable(getApplicationContext());
                                                        if(hasnet)
                                                            new mySOAPAsyncTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, autocarro, t.toString(), proxBilhete, linha, coordenadas[0], coordenadas[1], tvUltimaParagem.getText().toString(), turno,codTurno);

                                                        //Bilhete bilt;
                                                        //bilt = db.getDadosBilhete(proxBilhete);
                                                        //String tt = bilt.getIdBilhete();

                                                        tvResult.setText("");
                                                        t = "";


                                                    } catch (Exception e)
                                                    {
                                                        // TODO: handle exception
                                                        Log.i("Error:", e.toString().toString());

                                                        if (e.toString().equals("com.telpo.tps550.api.printer.NoPaperException"))
                                                        {
                                                            textToSpeech.speak("Impressora sem papel", TextToSpeech.QUEUE_FLUSH, null);
                                                            tvWav.setText("Impressora sem papel");

                                                        }

                                                    }
                                                    finally
                                                    {
                                                        //statusAsync2 = true;

                                                        try {
                                                            ThermalPrinter.stop();
                                                        } catch (Exception exc) {
                                                        }
                                                        //processingReading = true;
                                                    }

                                                }


                                            } catch (SecurityException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                                Log.i("Error:", e.toString().toString());

                                            }




                                        //processingReading = true;
                                    }


                            }//Final if cartao condutor

                            else if(!t.equals(Duplicacao) || counterDup > 5 && !t.equals(cartaoCondutor))
                            {
                                counterDup = 0;
                                tvWav.setText("Mantenha cartão encostado...");
                                r.play();
                                if (!r.isPlaying())
                                    r.stop();

                                Log.i("WriteRFID", "CarId:" + t);

                                //
                                if (box.isChecked())
                                {
                                    if (spinner.getSelectedItem().toString() != "0") {
                                        try {

                                            ThermalPrinter.start();
                                            ThermalPrinter.reset();
                                            ThermalPrinter.addString(".");
                                            ThermalPrinter.printString();


                                            //string IdAutocarro, string IdCartao, string NumViagens, string IdRevendedor, string turno, string produto)
                                            String produto = "";
                                            String viagens = "0";
                                            if(spinner.getSelectedItem().toString() == "Cartao")
                                                produto = "PP001";
                                            else
                                            {
                                                viagens = spinner.getSelectedItem().toString();

                                                if (Integer.valueOf(spinner.getSelectedItem().toString()) < 10)
                                                    produto = "PP00" + spinner.getSelectedItem().toString();
                                                else
                                                    produto = "PP0" + spinner.getSelectedItem().toString();

                                            }
                                            new mySOAPAsyncTaskVendaPrePagp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, autocarro, t.toString(), viagens, cartaoCondutor, turno, produto);
                                            ThermalPrinter.stop();
                                            //t = "";
                                            //tvResult.setText("");

                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            Log.i("Error:", e.toString().toString());

                                            if (e.toString().equals("com.telpo.tps550.api.printer.NoPaperException")) {
                                                textToSpeech.speak("Impressora sem papel", TextToSpeech.QUEUE_FLUSH, null);
                                                tvWav.setText("Impressora sem papel");
                                                try {
                                                    ThermalPrinter.stop();
                                                } catch (Exception exc) {
                                                }
                                            }
                                        }
                                    } else
                                    {
                                        textToSpeech.speak("Valor inválido", TextToSpeech.QUEUE_FLUSH, null);
                                        tvWav.setText("Valor inválido");
                                    }

                                }
                                else {
                                    /////db
                                    if (passesTemporarios.contains(oldt))
                                    {
                                        imgViewStatus.setVisibility(View.VISIBLE);
                                        imgViewStatus.setImageResource(R.drawable.stop);
                                        imgViewStatus.setMaxWidth(100);
                                        tvWav.setText("Atenção!Já tinha passado! Confirmar identidade!");
                                        textToSpeech.speak("Atenção!Já tinha passado! Confirmar identidade!", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                    else
                                    {
                                        passesTemporarios += oldt + ";";


                                    db = new DBHelper(getApplicationContext());
                                    Cartao cartaoDB = new Cartao();
                                    cartaoDB = db.getDadosCartao(oldt);
                                    String status = "";
                                    String tipo = "";
                                    String anoMes = "";
                                    String anoMesAnterior = "";
                                    String dataValidade = "";



                                        if (cartaoDB != null)
                                        {
                                            status = cartaoDB.getStatus();
                                            tipo = cartaoDB.getTipo();
                                            anoMes = cartaoDB.getAnoMes();
                                            anoMesAnterior = cartaoDB.getAnoMesAnterior();
                                            if(cartaoDB.getdataExpiracao()!=null)
                                                dataValidade = cartaoDB.getdataExpiracao();
                                            if(dataValidade.length() == 0 )
                                                dataValidade = "01-01-2099 00:00";

                                        if (status != null && tipo != null && anoMes != null )//removido 03/02/2017 && anoMesAnterior != null)
                                        {
                                            //try {
                                                Calendar calendar = Calendar.getInstance();
                                                int hora = calendar.get(Calendar.HOUR_OF_DAY);
                                                int minuto = calendar.get(Calendar.MINUTE);
                                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                                int year = calendar.get(Calendar.YEAR);
                                                int month = calendar.get(Calendar.MONTH) + 1;
                                                int horaPasse = 0;
                                                int minutoPasse = 0;
                                                int diaPasse = 0;
                                                int mesPasse = 0;
                                                int anoPasse = 0;

                                                int horaPasseAnterior = 0;
                                                int minutoPasseAnterior = 0;
                                                int diaPasseAnterior = 0;
                                                int mesPasseAnterior = 0;
                                                int anoPasseAnterior = 0;

                                                if (!anoMesAnterior.contains("-"))
                                                    anoMesAnterior = "01-01-1980 00:00";
                                                if (!anoMes.contains("-"))
                                                    anoMes = "01-01-1980 00:00";

                                            Log.e("Error", "AnoMes: " + anoMes.toString());
                                            Log.e("Error", "AnoMesAnterior: " + anoMesAnterior.toString());

                                            if(!anoMes.contains(":"))
                                                anoMes = anoMes + " 00:00";

                                            Log.e("Error", "AnoMes: " + anoMes.toString());

                                            String[] dataPasse = anoMes.split("-");
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                                diaPasse = Integer.valueOf(dataPasse[0]);
                                                mesPasse = Integer.valueOf(dataPasse[1]);


                                                String[] anoHoraMinutoPasse = String.valueOf(dataPasse[2]).split(" ");
                                                anoPasse = Integer.valueOf(anoHoraMinutoPasse[0]);
                                                Log.e("Error", "Passe: " + anoHoraMinutoPasse.toString());
                                                String[] HoraMinutoPasse = String.valueOf(anoHoraMinutoPasse[1]).split(":");
                                                horaPasse = Integer.valueOf(HoraMinutoPasse[0]);
                                                minutoPasse = Integer.valueOf(HoraMinutoPasse[1]);

                                                String[] dataPasseAnterior = anoMesAnterior.split("-");
                                                SimpleDateFormat dateFormatAnterior = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                                                //Formato de data para slqlite
                                                SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                                                boolean dataOK = false;
                                                try {
                                                    //Calendar now = Calendar.getInstance();
                                                    String currentDateandTime2 = sdf2.format(new Date());

                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                    String currentDateandTime = sdf.format(new Date());

                                                    Date dataAgora =  dateFormatAnterior.parse(currentDateandTime);
                                                    Date dataCompleta = dateFormatAnterior.parse(anoMes);
                                                    Date dataCompletaAnterior = dateFormatAnterior.parse(anoMesAnterior);
                                                    Date dataCompletaValidade = dateFormatAnterior.parse(dataValidade);
                                                    Long elapsedAgora = 0L;
                                                    elapsedAgora = (dataCompleta.getTime() - dataAgora.getTime());
                                                    Long elapsedAnterior = 0L;
                                                    elapsedAnterior = dataCompletaAnterior.getTime() - dataAgora.getTime();
                                                    Long elapsedValidade = 0L;
                                                    elapsedValidade = dataCompletaValidade.getTime() - dataAgora.getTime();
                                                    //if (((elapsedAgora) > 0 || (elapsedAnterior) > 0) && (elapsedValidade) > 0)//removido Anterior em 03/02/2017
                                                    if (((elapsedAgora) > 0 ) && (elapsedValidade) > 0)
                                                    {
                                                        dataOK = true;
                                                    }
                                                    else
                                                        dataOK = false;

                                                } catch (ParseException pex)
                                                {
                                                    String er = pex.toString();
                                                    Log.e("ERROR", "ERROR IN CODE: " + pex.toString());

                                                }
                                                diaPasseAnterior = Integer.valueOf(dataPasseAnterior[0]);
                                                mesPasseAnterior = Integer.valueOf(dataPasseAnterior[1]);

                                                String[] anoHoraMinutoPasseAnterior = String.valueOf(dataPasseAnterior[2]).split(" ");
                                                anoPasseAnterior = Integer.valueOf(anoHoraMinutoPasseAnterior[0]);

                                                String[] HoraMinutoPasseAnterior = String.valueOf(anoHoraMinutoPasseAnterior[1]).split(":");
                                                horaPasseAnterior = Integer.valueOf(HoraMinutoPasseAnterior[0]);
                                                minutoPasseAnterior = Integer.valueOf(HoraMinutoPasseAnterior[1]);

                                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                String currentDateandTime = sdf.format(new Date());

                                                if (dataOK && status.toUpperCase().equals("ACTIVO") && (!tipo.contains("PrePago")) )
                                                {
                                                    // if (status.toUpperCase().equals("ACTIVO") && !tipo.contains("PrePago") && ( ( (mesPasse == month) && (anoPasse == year)) || ((mesPasseAnterior == month) && (anoPasseAnterior == year)))) {
                                                        tvWav.setText("Passe " + tipo + " OK! Local");
                                                        textToSpeech.speak("Passe " + tipo + " OK!", TextToSpeech.QUEUE_FLUSH, null);


                                                        //Inserir leitura offline na BD
                                                        Leituras leitura = new Leituras();
                                                        leitura.setIdPasse(oldt.toString());
                                                        leitura.setDataHoraLeitura(currentDateandTime);
                                                        leitura.setLatitudeLeitura(coordenadas[0]);
                                                        leitura.setLongitudeLeitura(coordenadas[1]);
                                                        leitura.setVelocidadeLeitura(velocidade);
                                                        leitura.setAutocarroLeitura(autocarro);
                                                        leitura.setLinhaLeitura(linha);
                                                        leitura.setParagemLeitura(tvUltimaParagem.getText().toString());
                                                        leitura.setEstadoLeitura("ACTIVO");
                                                        leitura.setTurnoLeitura(turno);
                                                        db.createLeitura(leitura);


                                                }
                                                else
                                                {



                                                    if (coordenadas.length > 0)
                                                    {
                                                        try {

                                                            //string cartao, string Latitude, string Longitude, string Velocidade, string Autocarro, string Linha, string Paragem
                                                            //if (statusAsync)//retirado 13/02/2017
                                                            {

                                                                {
                                                                    CheckInternet chkNet = new CheckInternet();
                                                                    boolean hasnet = true;
                                                                    hasnet = chkNet.isInternetAvailable(getApplicationContext());
                                                                    if(hasnet)
                                                                        new mySOAPAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oldt.toString(), coordenadas[0], coordenadas[1], velocidade, autocarro, linha, tvUltimaParagem.getText().toString(), "oldcode", "newcode", turno);
                                                                    else//não sendo possível verificar online inserir na BD offline
                                                                    {
                                                                        tvWav.setText("Passe " + tipo + " INACTIVO! Local");
                                                                        textToSpeech.speak("Passe " + tipo + " INACTIVO!", TextToSpeech.QUEUE_FLUSH, null);

                                                                        //Inserir leitura offline na BD
                                                                        Leituras leitura = new Leituras();
                                                                        leitura.setIdPasse(oldt.toString());
                                                                        leitura.setDataHoraLeitura(currentDateandTime);
                                                                        leitura.setLatitudeLeitura(coordenadas[0]);
                                                                        leitura.setLongitudeLeitura(coordenadas[1]);
                                                                        leitura.setVelocidadeLeitura(velocidade);
                                                                        leitura.setAutocarroLeitura(autocarro);
                                                                        leitura.setLinhaLeitura(linha);
                                                                        leitura.setParagemLeitura(tvUltimaParagem.getText().toString());
                                                                        leitura.setEstadoLeitura("INACTIVO");
                                                                        leitura.setTurnoLeitura(turno);
                                                                        db.createLeitura(leitura);

                                                                    }
                                                                    //t = "";
                                                                    //tvResult.setText("");

                                                                }

                                                            }
                                                        } catch (Exception ex)
                                                        {

                                                            Toast.makeText(ValidarPasse.this,
                                                                    ex.getMessage().toString(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                }

                                            //}
                                            //catch(Exception exce)
                                            {

                                            }
                                        }//final status cartao local null

                                        else//se cartao não existe então ir online
                                        {

                                            new mySOAPAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oldt.toString(), coordenadas[0], coordenadas[1], velocidade, autocarro, linha, tvUltimaParagem.getText().toString(), "oldcode", "newcode", turno);
                                            //t = "";
                                            //tvResult.setText("");

                                        }
                                    }//fim se cartao = null
                                    else {
                                        new mySOAPAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oldt.toString(), coordenadas[0], coordenadas[1], velocidade, autocarro, linha, tvUltimaParagem.getText().toString(), "oldcode", "newcode", turno);
                                        //t = "";
                                        //tvResult.setText("");

                                    }
                                    /////db
                                }//fim se não for passe já lido
                                }
                                //
                                processingReading = true;
                            }//fim se t not equal Duplicacao
                            else
                            {
                                Duplicacao = "Dup";
                                counterDup++;
                                if(counterDup == 5)
                                    textToSpeech.speak("Atenção: Controlo Duplicação Terminado!", TextToSpeech.QUEUE_FLUSH, null);

                            }

                        }//if t.length != 0

                        Duplicacao = t;
                        t = "";
                        tvResult.setText("");
                        processingReading = true;

                    }//End if !t.equals(tvResult.Text)

                }
            }
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


            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }


    }

    //Venda PrePago
    private class mySOAPAsyncTaskVendaPrePagp extends AsyncTask<String, Void, Void> {
        String resultadoPrePago ="OK";

        String cartaoPrepago = "";
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i("myAppPrepagoResulPost2", resultadoPrePago);

            if (resultadoPrePago.contains("OK") && spinner.getSelectedItem().toString() != "0")
            {

                try {

                    ThermalPrinter.start();
                    ThermalPrinter.reset();

                    String mensagem = "         TRANSCOR\n        NIF: 252073509\n";//Recibo de Compra de Bilhete\n";
                    String textoFeedback = "";

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    mensagem += "Data: " + currentDateandTime + "\n";


                    mensagem += "Cartao: " + cartaoPrepago + "\n";
                    if(spinner.getSelectedItem().toString() != "Cartao") {
                        mensagem += "Carregamento PrePago: " + spinner.getSelectedItem().toString() + " viagens" + "\n";
                        mensagem += "Valor Total: " + String.valueOf(Integer.parseInt(spinner.getSelectedItem().toString()) * Integer.parseInt(precoBilhete)) + " ECV\n";
                        textoFeedback = "Cartão carregado " + spinner.getSelectedItem().toString() + " viagens";
                    }
                    else {
                        mensagem += "Compra de Novo Cartao" + "\n";
                        mensagem += "Valor Total: 100 ECV\n";
                        textoFeedback = "Cartão comprado";
                    }
                    mensagem += " \n  \n  \n  \n  \n  ";
                    ThermalPrinter.addString(mensagem);
                    ThermalPrinter.printString();

                    ThermalPrinter.stop();
                    tvWav.setText(textoFeedback);
                    textToSpeech.speak(textoFeedback, TextToSpeech.QUEUE_FLUSH, null);

                    tvResult.setText("");
                    t = "";
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.i("Error:", e.toString().toString());

                    if (e.toString().equals("com.telpo.tps550.api.printer.NoPaperException")) {
                        textToSpeech.speak("Impressora sem papel", TextToSpeech.QUEUE_FLUSH, null);
                        tvWav.setText("Impressora sem papel");
                        try {
                            ThermalPrinter.stop();
                        } catch (Exception exc) {
                        }
                    }
                }
            }
            else
            {
                textToSpeech.speak("Erro na compra de Pré-Pago", TextToSpeech.QUEUE_FLUSH, null);
                tvWav.setText("Erro na compra de Pré-Pago");
            }
            box.toggle();
            if(box.isChecked())
                box.toggle();
            spinner.setSelection(0);
            spinner.setEnabled(false);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... args)
        //private void soapMessage(string IdAutocarro, string IdCartao, string NumViagens, string IdRevendedor, string turno)
        {
            /**
            String HEX_DIGITS = "0123456789ABCDEF";
            char[] sources = args[1].toCharArray();
            int dec = 0;
            for (int i = 0; i < sources.length; i++) {
                int digit = HEX_DIGITS.indexOf(Character.toUpperCase(sources[i]));
                dec += digit * Math.pow(16, (sources.length - (i + 1)));
            }
**/

            cartaoPrepago = String.valueOf(args[1]);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME6);

            PropertyInfo Autocarro = new PropertyInfo();
            Autocarro.setName("IdAutocarro");
            Autocarro.setValue(args[0]);
            Autocarro.setType(String.class);
            request.addProperty(Autocarro);

            PropertyInfo IdCartao = new PropertyInfo();
            IdCartao.setName("IdCartao");
            IdCartao.setValue(args[1]);
            IdCartao.setType(String.class);
            request.addProperty(IdCartao);

            PropertyInfo NumViagens = new PropertyInfo();
            NumViagens.setName("NumViagens");
            NumViagens.setValue(args[2]);
            NumViagens.setType(String.class);
            request.addProperty(NumViagens);

            PropertyInfo IdRevendedor = new PropertyInfo();
            IdRevendedor.setName("IdRevendedor");
            IdRevendedor.setValue(args[3]);
            IdRevendedor.setType(String.class);
            request.addProperty(IdRevendedor);

            PropertyInfo turno = new PropertyInfo();
            turno.setName("turno");
            turno.setValue(args[4]);
            turno.setType(String.class);
            request.addProperty(turno);

            PropertyInfo produto = new PropertyInfo();
            produto.setName("produto");
            produto.setValue(args[5]);
            produto.setType(String.class);
            request.addProperty(produto);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {
                androidHttpTransport.call(SOAP_ACTION6, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
                resultadoPrePago = result.getProperty(0).toString();
                Log.i("myAppPrepagoResult", result.toString());

                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
            }
            // ////////
            return null;
        }


    }

    //Insert Leituras offline
    private class mySOAPAsyncTaskUploadLeituras extends AsyncTask<String, Void, Void> {
        String resultadoPrePago ="OK";

        String cartaoPrepago = "";
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i("myAppPrepagoResulPost2", resultadoPrePago);

            try {
                if (resultadoPrePago.equals("OK"))
                    db.deleteAllLeituras();
            }
            catch(Exception ex)
            {}

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... args)
        //private void soapMessage(string IdAutocarro, string IdCartao, string NumViagens, string IdRevendedor, string turno)
        {

            //(string IdPasse,  DataHoraLeitura, string LatitudeLeitura, string LongitudeLeitura,
            // string VelocidadeLeitura, string AutocarroLeitura, string LinhaLeitura, string ParagemLeitura, string EstadoLeitura, string TurnoLeitura
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME7);

            PropertyInfo dados = new PropertyInfo();
            dados.setName("dados");
            dados.setValue(args[0]);
            dados.setType(String.class);
            request.addProperty(dados);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {
                androidHttpTransport.call(SOAP_ACTION7, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
                resultadoPrePago = result.getProperty(0).toString();
                Log.i("UploadlLeitura", result.toString());

                androidHttpTransport.getConnection().disconnect();

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
            if (resultado.length > 1 && !resultadoLocalizacao.toString().contains("OK")&& !resultadoLocalizacao.toString().contains("anyType")) {
                String[] resultadoDetalhes = resultado[0].split("#");
                if (!ultimoAvisoProximidade.equals(resultadoDetalhes[0]))
                {
                    ultimoAvisoProximidade = resultadoDetalhes[0];
                    String autProximo = resultadoDetalhes[0].substring(0,resultadoDetalhes[0].length()-1) + " " + resultadoDetalhes[0].substring(resultadoDetalhes[0].length()-1);
                    autProximo = autProximo.replace("SV", "S V").replace("-", " ");
                    textToSpeech.speak("Autocarro: " + autProximo + " da mesma linha, a poucos metros!", TextToSpeech.QUEUE_FLUSH, null);
                    tvWav.setText(resultadoDetalhes[0] +  " a poucos metros");
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
        //UpdateLocalizacaoAutocarro2
        //string Autocarro, string Sentido, string Paragem, string Velocidade, string Latitude, string Longitude, string Linha
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);

            PropertyInfo Autocarro = new PropertyInfo();
            Autocarro.setName("Autocarro");
            Autocarro.setValue(args[0]);
            Autocarro.setType(String.class);
            request.addProperty(Autocarro);

            PropertyInfo Sentido = new PropertyInfo();
            Sentido.setName("Sentido");
            Sentido.setValue(args[1]);
            Sentido.setType(String.class);
            request.addProperty(Sentido);

            PropertyInfo Paragem = new PropertyInfo();
            Paragem.setName("Paragem");
            Paragem.setValue(args[2]);
            Paragem.setType(String.class);
            request.addProperty(Paragem);

            PropertyInfo Velocidade = new PropertyInfo();
            Velocidade.setName("Velocidade");
            Velocidade.setValue(args[3]);
            Velocidade.setType(String.class);
            request.addProperty(Velocidade);

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

            PropertyInfo Linha = new PropertyInfo();
            Linha.setName("Linha");
            Linha.setValue(args[6]);
            Linha.setType(String.class);
            request.addProperty(Linha);

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
                resultadoLocalizacao = result.getProperty(0).toString();
                androidHttpTransport.getConnection().disconnect();

            } catch (Exception e) {
                Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                e.printStackTrace();
                statusAsync5 = true;
            }
            // ////////
            return null;
        }


    }

    //Venda Bilhete
    private class mySOAPAsyncTaskUploadBilhete extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(!resultadoVendaBilhete.contains("Erro"))
            {
                DBHelper db = new DBHelper(getApplicationContext());
                //SV-21-BX;1373330976;SV21BX201701130020;1;0;0;ULTIMA PARAGEM;1;2017-01-13 14:27:48;Nao#
                //IdAutocarro, IdCartao, IdBilhete,IdPercurso,Latitude,Longitude,Paragem,Turno,DataHora,Enviado
                String total[] = listaBilhetesUploaded.split("#");
                if(total.length>0)
                {
                    int j = 0;
                    while(j<total.length)
                    {

                        String detBil[] = total[j].split(";");
                        Bilhete bil = new Bilhete();
                        bil.setIdAutocarro(detBil[0]);
                        bil.setIdCartao(detBil[1]);
                        bil.setIdBilhete(detBil[2]);
                        bil.setIdPercurso(detBil[3]);
                        bil.setLatitude(detBil[4]);
                        bil.setLongitude(detBil[5]);
                        bil.setParagem(detBil[6]);
                        bil.setTurno(detBil[7]);
                        bil.setDataHora(detBil[8]);

                        if(db.getProxBilhete(tvAutocarro.getText().toString()) != null)
                            proxBilhete = db.getProxBilhete(tvAutocarro.getText().toString());

                        if(!detBil[2].toString().contains(proxBilhete))//Nao apagar ultimo bilhete da BD
                            db.deleteBilhete(bil);
                        j++;
                    }
                }

                TurnoTemp tute = db.getTurnoTemp(tvAutocarro.getText().toString(), turno, linha , cartaoCondutor);
                new mySOAPAsyncTask30().execute(tvAutocarro.getText().toString(), turno, linha,SWVersion , cartaoCondutor,tute.getdataAberturaTurno(),tute.getCodTurno());//async task para abrir turno

            }



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //statusAsync20 = false;
            //pbAbertura.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... args)
        //insertVendaBilhete(string IdAutocarro,string IdCartao, string IdBilhete, string IdPercurso, string Latitude, string Longitude)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME29);

            PropertyInfo listaBilhetes = new PropertyInfo();
            listaBilhetes.setName("listaBilhetes");
            listaBilhetes.setValue(args[0]);
            listaBilhetes.setType(String.class);
            request.addProperty(listaBilhetes);

            listaBilhetesUploaded  = args[0];

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {

                androidHttpTransport.call(SOAP_ACTION29, envelope);
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

                ValidarPasse.this.sleep(500);
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper = false;

                ValidarPasse.this.sleep(2000);
                if (progressDialog != null && !ValidarPasse.this.isFinishing()) {
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

                ValidarPasse.this.sleep(1000);
                ThermalPrinter.stop();
                nopaper = false;

                if (progressDialog != null && !ValidarPasse.this.isFinishing()) {
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

                ValidarPasse.this.sleep(1000);
                //lock.release();
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper = false;
                if (progressDialog != null && !ValidarPasse.this.isFinishing()) {
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
                ValidarPasse.this.sleep(2000);
                //lock.release();
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                ThermalPrinter.stop();
                nopaper = false;
                if (progressDialog != null && !ValidarPasse.this.isFinishing()) {
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

    //Comunicar abertura de turno
    private class mySOAPAsyncTask30 extends AsyncTask<String, Void, Void> {

        String resultadomySOAPAsyncTask30 = "";

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //status = "OK;" + getPrecoBilheteAvulso.ExecuteScalar().ToString() + ";" + codTurno;
            if(resultadomySOAPAsyncTask30.contains("OK;"))
            {
                String[] dados = resultadomySOAPAsyncTask30.split(";");
                if(dados.length > 2)
                {
                    //dados[2];
                    DBHelper db = new DBHelper(getApplicationContext());
                    db.updateTurnoTempUploaded(tvAutocarro.getText().toString(),tvLinha.getText().toString(),turno,cartaoCondutor,dados[2]);


                    //tvWav.setText(dados[2]);
                }

            }
            //pbAbertura.setVisibility(View.GONE);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pbAbertura.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... args)
        //string Autocarro, string Turno
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME30);

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

            PropertyInfo DataAbertura = new PropertyInfo();
            DataAbertura.setName("DataAbertura");
            DataAbertura.setValue(args[5]);
            DataAbertura.setType(String.class);
            request.addProperty(DataAbertura);

            PropertyInfo CodTurno = new PropertyInfo();
            CodTurno.setName("CodTurno");
            CodTurno.setValue(args[6]);
            CodTurno.setType(String.class);
            request.addProperty(CodTurno);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;


            try {
                androidHttpTransport.call(SOAP_ACTION30, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject)envelope.bodyIn;
                resultadomySOAPAsyncTask30 = result.getProperty(0).toString();
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

    //Comunicar fecho de turno
    private class mySOAPAsyncTask31 extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //pbAbertura.setVisibility(View.GONE);
            if(resultadoUploadFecho.toString().contains("#"))
            {
                DBHelper db = new DBHelper(getApplicationContext());

                String[] re = resultadoUploadFecho.split("#");
                int i = 0;
                while (i < re.length)
                {
                    if(re[i].toString().trim().contains(";"))
                    {
                        String[] reInterno = re[i].split(";");
                        if(reInterno[0].equals("OK"))
                            db.deleteTurnoTempFechado(tvAutocarro.getText().toString(),reInterno[1]);
                    }
                    i++;
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pbAbertura.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... args)
        //uploadFechoTurnoOffline(Autocarro, Turno, Condutor, DataAbertura, TotalVendas, DataFecho, TotalVoltas, Linha, SWVersion, PrecoBilhete, AbertoPor)
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME31);

            PropertyInfo ListaFecho = new PropertyInfo();
            ListaFecho.setName("ListaFecho");
            ListaFecho.setValue(args[0]);
            ListaFecho.setType(String.class);
            request.addProperty(ListaFecho);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;


            try {
                androidHttpTransport.call(SOAP_ACTION31, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject)envelope.bodyIn;
                resultadoUploadFecho = result.getProperty(0).toString();
                Log.i("resultadoUploadFecho", result.toString());
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
