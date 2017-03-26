package com.telpo.tps550.api.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.iccard.Picc;
import com.telpo.tps550.api.printer.ThermalPrinter;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResumoFecho extends Activity {

    final Handler h = new Handler();
    final Handler h0 = new Handler();
    private int printerstatus = 0;
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
    private int counterDup = 0;
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
    boolean readRFIDLoop = true;
    boolean processingReading = true;


    String total = "0";
    String preco = "0";
    String linha = "";
    String autocarro = "";
    String turno = "";
    String cartao = "";
    String resumoPrepago = "";
    TextView tvNumVendas,tvTotalVendas,tvResumoPrePago;
    Button btnSair, btnImprimir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_fecho);

        Intent in = getIntent();

        if(in.hasExtra("TotalVendas"))
        total = in.getStringExtra("TotalVendas");

        if(in.hasExtra("PrecoBilhete"))
            preco = in.getStringExtra("PrecoBilhete");

        if(in.hasExtra("PrePago"))
            resumoPrepago = in.getStringExtra("PrePago");
        if(in.hasExtra("Autocarro"))
            autocarro = in.getStringExtra("Autocarro");

        if(in.hasExtra("Turno"))
            turno = in.getStringExtra("Turno");

        if(in.hasExtra("Linha"))
            linha = in.getStringExtra("Linha");

        if(in.hasExtra("Cartao"))
            cartao = in.getStringExtra("Cartao");


        tvNumVendas = (TextView)findViewById(R.id.textViewNumVendas);
        tvTotalVendas = (TextView)findViewById(R.id.textViewTotalVendas);
        tvResumoPrePago = (TextView)findViewById(R.id.textViewResumoPrePago);
        tvNumVendas.setText(total);
        int totalVendasInt =Integer.parseInt(total);
        int precoInt = Integer.parseInt(preco);
        int quant  = totalVendasInt*precoInt;
        tvTotalVendas.setText(String.valueOf(quant));
        tvResumoPrePago.setText(resumoPrepago);
        btnSair = (Button)findViewById(R.id.buttonSairResumoFecho);
        btnSair.setOnClickListener(onButtonClick);

        btnImprimir = (Button)findViewById(R.id.buttonImprimirFecho);
        btnImprimir.setOnClickListener(onButtonClick);

    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v == btnSair)
            {
                Intent intent = new Intent(ResumoFecho.this, MainActivity.class);
                ResumoFecho.this.finish();
                startActivity(intent);

            }
            else if (v == btnImprimir)
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
                    mensagem += "Cartao: " + cartao + "\n";
                    mensagem += "Autocarro: " + autocarro + "\n";
                    mensagem += "Linha: " + linha + "\n";
                    mensagem += "Turno: " + turno + "\n";
                    mensagem += "Numero Vendas: " + tvNumVendas.getText().toString() + "\n";
                    mensagem += "Valor Total: " + tvTotalVendas.getText().toString() + " ECV\n";
                    mensagem += " \n  \n  \n  \n  \n  ";
                    ThermalPrinter.addString(mensagem);
                    ThermalPrinter.printString();
                    //ThermalPrinter.stop();

                    //Atualizar tabela turno
                    DBHelper db = new  DBHelper(getApplicationContext());
                    if(cartao.contains(";"))
                    {
                        String[] split = cartao.split(";");
                        cartao = split[0];
                    }
                    int i = db.updateTurnoTempFechado(autocarro,linha, turno,cartao);
                    if(i== 1)
                        tvTotalVendas.setText(tvTotalVendas.getText().toString() + "-Fechado Local");

                    //CheckInternet chkNet = new CheckInternet();
                    //boolean hasnet = true;
                    //hasnet = chkNet.isInternetAvailable(getApplicationContext());
                    //if(hasnet)
                      //  db.updateTurnoTempUploaded(autocarro,linha,turno,cartao,"");

                }
                catch(Exception e)
                {}

            }


        }};



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resumo_fecho, menu);
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

                ResumoFecho.this.sleep(500);
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper = false;

                ResumoFecho.this.sleep(2000);
                if (progressDialog != null && !ResumoFecho.this.isFinishing()) {
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

                ResumoFecho.this.sleep(1000);
                ThermalPrinter.stop();
                nopaper = false;

                if (progressDialog != null && !ResumoFecho.this.isFinishing()) {
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

                ResumoFecho.this.sleep(1000);
                //lock.release();
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper = false;
                if (progressDialog != null && !ResumoFecho.this.isFinishing()) {
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
                ResumoFecho.this.sleep(2000);
                //lock.release();
                if (nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                ThermalPrinter.stop();
                nopaper = false;
                if (progressDialog != null && !ResumoFecho.this.isFinishing()) {
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
            Log.v("", "Find the Bmp");
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
                            progressDialog = ProgressDialog.show(ResumoFecho.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                            new barcodePrintThread().start();
                            break;
                        case PRINTQRCODE:
                            progressDialog = ProgressDialog.show(ResumoFecho.this, getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));
                            new qrcodePrintThread().start();
                            break;
                        case PRINTPAPERWALK:
                            progressDialog = ProgressDialog.show(ResumoFecho.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                            new paperWalkPrintThread().start();
                            break;
                        case PRINTCONTENT:
                            progressDialog = ProgressDialog.show(ResumoFecho.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
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
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResumoFecho.this);
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
                        Toast.makeText(ResumoFecho.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
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
                    progressDialog = ProgressDialog.show(ResumoFecho.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                    new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    printting = PRINTCONTENT;
                    new contentPrintThread().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !ResumoFecho.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ResumoFecho.this);
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
                    Toast.makeText(ResumoFecho.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}
