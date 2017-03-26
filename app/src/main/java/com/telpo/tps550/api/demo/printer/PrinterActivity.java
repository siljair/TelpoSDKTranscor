package com.telpo.tps550.api.demo.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.printer.ThermalPrinter;


import java.util.ArrayList;

/**
 * 单位 广东天波信息技术股份有限公司
 * 功能 打印测试的处理
 * Created by hjx on 14-6-24.
 */
public class PrinterActivity extends Activity {
    private  TextView textPrintVersion;
    private static String printVersion;
    private final int PRINTIT = 1;
    private final int ENABLE_BUTTON = 2;
    private final int NOPAPER = 3;
    private final int LOWBATTERY=4;
    private final int PRINTVERSION=5;
    private final int PRINTBARCODE = 6;
    private final int PRINTQRCODE = 7;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT=10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;

    private boolean stop = false;
    private static final String TAG = "ConsoleTestActivity";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    MyHandler handler;
    private EditText editTextLeftDistance;
    private EditText editTextRightDistance;
    private EditText editTextLineDistance;
    private EditText editTextWordFont;
    private EditText editTextPrintGray;
    private EditText editTextBarcode;
    private EditText editTextQrcode;
    private EditText editTextPaperWalk;
    private EditText editTextContent;
    private Button buttonBarcodePrint;
    private Button buttonPaperWalkPrint;
    private Button buttonContentPrint;
    private Button buttonQrcodePrint;
    private Button buttonGetExampleText;
    private Button buttonGetZhExampleText;
    private Button buttonClearText;
    private int printting = 0;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private boolean isClose=false;//关闭程序

    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog progressDialog;
    private final static int MAX_LEFT_DISTANCE=255;
    ProgressDialog dialog;

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            if (stop == true)
                return;
            switch (msg.what) {
                case PRINTIT:
                    final ArrayList<String> rInfoList = new ArrayList<String>();
                    buttonBarcodePrint.setEnabled(false);

                    switch(printting){
                        case PRINTBARCODE:
                            progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.bl_dy),getString(R.string.printing_wait));
                            new barcodePrintThread().start();
                            break;
                        case PRINTQRCODE:
                            progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.D_barcode_loading),getString(R.string.generate_barcode_wait));
                            new qrcodePrintThread().start();
                            break;
                        case PRINTPAPERWALK:
                        	progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.bl_dy),getString(R.string.printing_wait));
                            new paperWalkPrintThread().start();
                            break;
                        case PRINTCONTENT:
                            progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.bl_dy),getString(R.string.printing_wait));
                            new contentPrintThread().start();
                            break;
                    }
                    break;
                case ENABLE_BUTTON:
                    buttonBarcodePrint.setEnabled(true);
                    printting = 0;
                    break;
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrinterActivity.this);
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
                        textPrintVersion.setText(printVersion);
                    }else{
                        Toast.makeText(PrinterActivity.this,R.string.operation_fail,Toast.LENGTH_LONG).show();
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
                    progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.bl_dy),getString(R.string.printing_wait));
                    new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    printting = PRINTCONTENT;
                    new contentPrintThread().start();
                    break;
                case CANCELPROMPT:
                    if(progressDialog != null && !PrinterActivity.this.isFinishing() ){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(PrinterActivity.this);
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
                    Toast.makeText(PrinterActivity.this,"Print Error!",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "ConsoleTestActivity====onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_main);
        setTitle("Serail Port Console");
        handler = new MyHandler();

        buttonBarcodePrint = (Button) findViewById(R.id.print_barcode);
        preferences = getSharedPreferences("logoStorePreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        IntentFilter pIntentFilter = new IntentFilter();
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        pIntentFilter.addAction(PRINT_VERSION_CHANGE);
        registerReceiver(printReceive, pIntentFilter);

        editTextLeftDistance = (EditText)findViewById(R.id.set_leftDistance);
        editTextLeftDistance.setText("0");
        editTextRightDistance = (EditText)findViewById(R.id.set_rightDistance);
        editTextLineDistance = (EditText)findViewById(R.id.set_lineDistance);
        editTextLineDistance.setText("0");
        editTextWordFont = (EditText)findViewById(R.id.set_wordFont);
        editTextWordFont.setText("1");
        editTextPrintGray = (EditText)findViewById(R.id.set_printGray);
        editTextPrintGray.setText("3");
        editTextBarcode = (EditText)findViewById(R.id.set_Barcode);
        editTextPaperWalk = (EditText)findViewById(R.id.set_paperWalk);
        editTextPaperWalk.setText("1");
        editTextContent = (EditText)findViewById(R.id.set_content);
        textPrintVersion = (TextView)findViewById(R.id.print_version);

        editTextQrcode=(EditText)findViewById(R.id.set_Qrcode);
        buttonQrcodePrint=(Button)findViewById(R.id.print_qrcode);
        buttonQrcodePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 qrcodeStr=editTextQrcode.getText().toString();
                if(qrcodeStr==null||qrcodeStr.length()==0){
                    Toast.makeText(PrinterActivity.this,getString(R.string.input_print_data),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if(!nopaper) {
                        setTitle("QRcode Print");
                        progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.D_barcode_loading),getString(R.string.generate_barcode_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null));
                    }
                    else {
                        Toast.makeText(PrinterActivity.this,getString(R.string.ptintInit),Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        buttonBarcodePrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                barcodeStr = editTextBarcode.getText().toString();
                if(barcodeStr == null || barcodeStr.length() == 0){
                    Toast.makeText(PrinterActivity.this,getString(R.string.empty),Toast.LENGTH_LONG).show();
                    return;
                }else if (barcodeStr.length() < 11){
                    Toast.makeText(PrinterActivity.this,getString(R.string.lengthNotEnougth),Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {

                    if(!nopaper) {
                        setTitle("Barcode Print");
                        progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.bl_dy),getString(R.string.printing_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null));
                    }
                    else {
                        Toast.makeText(PrinterActivity.this,getString(R.string.ptintInit),Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        buttonPaperWalkPrint = (Button)findViewById(R.id.print_paperWalk);
        buttonPaperWalkPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exditText;
                exditText = editTextPaperWalk.getText().toString();
                if(exditText == null || exditText.length() == 0){
                    Toast.makeText(PrinterActivity.this,getString(R.string.empty),Toast.LENGTH_LONG).show();
                    return;
                }
                paperWalk = Integer.parseInt(exditText);
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    //      progressDialog.setMessage(error_msg + "  打印中......");
                    //      new MainActivity.WriteThread(data).start();
                    if(!nopaper) {
                        setTitle("print character");
                        handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, 0, null));
                    }
                    else {
                        Toast.makeText(PrinterActivity.this,getString(R.string.ptintInit),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        buttonClearText=(Button)findViewById(R.id.clearText);
        buttonClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextContent.setText("");
            }
        });
        buttonGetExampleText=(Button)findViewById(R.id.getPrintExample);
        buttonGetExampleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str= "Print Test:\n"
                        +"Device Base Information\n"
                        +"-----------------------------\n"
                        +"Printer Version:\n"+
                        "  V05.2.0.3\n"
                        +"Printer Gray: 3\n"
                        +"Soft Version:\n" +
                        "  TPDemo.G50.0.Build140313\n"
                        +"Battery Level: 100%\n"
                        +"CSQ Value: 24\n"
                        +"IMEI:86378902177527"+"\n"
                        +getString(R.string.PrintTemp1)+"\n"
                        +getString(R.string.PrintTemp2)+"\n"
                        +"\n\n"
                        +"Device Base Information\n"
                        +"--------------0---------------\n"
                        +"Printer Version:\n"+
                        "  V05.2.0.3\n"
                        +"Printer Gray: 3\n"
                        +"Soft Version:\n" +
                        "  TPDemo.G50.0.Build140313\n"
                        +"Battery Level: 100%\n"
                        +"CSQ Value: 24\n"
                        +"IMEI:86378902177527"+"\n"
                        +getString(R.string.PrintTemp1)+"\n"
                        +getString(R.string.PrintTemp2)+"\n"
                        +"Device Base Information\n"
                        +"--------------1---------------\n"
                        +"Printer Version:\n"+
                        "  V05.2.0.3\n"
                        +"Printer Gray: 3\n"
                        +"Soft Version:\n" +
                        "  TPDemo.G50.0.Build140313\n"
                        +"Battery Level: 100%\n"
                        +"CSQ Value: 24\n"
                        +"IMEI:86378902177527"+"\n"
                        +getString(R.string.PrintTemp1)+"\n"
                        +getString(R.string.PrintTemp2)+"\n"
                        +"\n\n"
                        +"Device Base Information\n"
                        +"--------------2---------------\n"
                        +"Printer Version:\n"+
                        "  V05.2.0.3\n"
                        +"Printer Gray: 3\n"
                        +"Soft Version:\n" +
                        "  TPDemo.G50.0.Build140313\n"
                        +"Battery Level: 100%\n"
                        +"CSQ Value: 24\n"
                        +"IMEI:86378902177527"+"\n"
                        +getString(R.string.PrintTemp1)+"\n"
                        +getString(R.string.PrintTemp2)+"\n"
                        +"Device Base Information\n"
                        +"--------------3---------------\n";
                editTextContent.setText(str);
            }
        });

        buttonGetZhExampleText=(Button)findViewById(R.id.getZhPrintExample);
        buttonGetZhExampleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str= "            百度烧烤"
                        +"\n----------------------------"
                        +"\n日期：2015-01-01 16:18:20"
                        +"\n卡号：12378945664"
                        +"\n单号：1001000000000529142"
                        +"\n----------------------------"
                        +"\n  项目    数量   单价  小计"
                        +"\n秘制烤羊腿  1    56    56"
                        +"\n黯然牛排    2    24    48"
                        +"\n烤火鸡      2    50    100"
                        +"\n炭烧鳗鱼    1    40    40"
                        +"\n烤全羊      1    200   200"
                        +"\n荔枝树烧鸡  1    50    50"
                        +"\n冰镇乳鸽    2    23    46"
                        +"\n秘制烤羊腿  1    56    56"
                        +"\n黯然牛排    2    24    48"
                        +"\n烤火鸡      2    50    100"
                        +"\n炭烧鳗鱼    1    40    40"
                        +"\n烤全羊      1    200   200"
                        +"\n荔枝树烧鸡  1    50    50"
                        +"\n冰镇乳鸽    2    23    46"
                        +"\n秘制烤羊腿  1    56    56"
                        +"\n黯然牛排    2    24    48"
                        +"\n烤火鸡      2    50    100"
                        +"\n炭烧鳗鱼    1    40    40"
                        +"\n烤全羊      1    200   200"
                        +"\n荔枝树烧鸡  1    50    50"
                        +"\n冰镇乳鸽    2    23    46"
                        +"\n秘制烤羊腿  1    56    56"
                        +"\n黯然牛排    2    24    48"
                        +"\n烤火鸡      2    50    100"
                        +"\n炭烧鳗鱼    1    40    40"
                        +"\n烤全羊      1    200   200"
                        +"\n荔枝树烧鸡  1    50    50"
                        +"\n冰镇乳鸽    2    23    46"
                        +"\n冰镇乳鸽    2    23    46"
                        +"\n秘制烤羊腿  1    56    56"
                        +"\n黯然牛排    2    24    48"
                        +"\n烤火鸡      2    50    100"
                        +"\n炭烧鳗鱼    1    40    40"
                        +"\n烤全羊      1    200   200"
                        +"\n荔枝树烧鸡  1    50    50"
                        +"\n冰镇乳鸽    2    23    46"
                        +"\n 合计：1000：00元"
                        +"\n----------------------------"
                        +"\n本卡金额：10000.00"
                        +"\n累计消费：1000.00"
                        +"\n本卡结余：9000.00"
                        +"\n----------------------------"
                        +"\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺"
                        +"\n欢迎您的再次光临";
                editTextContent.setText(str);
            }
        });

        buttonContentPrint = (Button)findViewById(R.id.print_content);
        buttonContentPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exditText;
                exditText = editTextLeftDistance.getText().toString();
                if (exditText == null || exditText.length() < 1)
                {
                	Toast.makeText(PrinterActivity.this,getString(R.string.left_margin)+getString(R.string.lengthNotEnougth),Toast.LENGTH_LONG).show();
                	return;
                }
                leftDistance = Integer.parseInt(exditText);
                exditText = editTextLineDistance.getText().toString();
                if (exditText == null || exditText.length() < 1)
                {
                	Toast.makeText(PrinterActivity.this,getString(R.string.row_space)+getString(R.string.lengthNotEnougth),Toast.LENGTH_LONG).show();
                	return;
                }
                lineDistance = Integer.parseInt(exditText);
                printContent = editTextContent.getText().toString();
                exditText = editTextWordFont.getText().toString();
                if (exditText == null || exditText.length() < 1)
                {
                	Toast.makeText(PrinterActivity.this,getString(R.string.font_size)+getString(R.string.lengthNotEnougth),Toast.LENGTH_LONG).show();
                	return;
                }
                wordFont = Integer.parseInt(exditText);
                exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1)
                {
                	Toast.makeText(PrinterActivity.this,getString(R.string.gray_level)+getString(R.string.lengthNotEnougth),Toast.LENGTH_LONG).show();
                	return;
                }
                printGray = Integer.parseInt(exditText);
                if(leftDistance > MAX_LEFT_DISTANCE){
                    Toast.makeText(PrinterActivity.this,getString(R.string.outOfLeft),Toast.LENGTH_LONG).show();
                    return;
                }else if(lineDistance > 255){
                    Toast.makeText(PrinterActivity.this,getString(R.string.outOfLine),Toast.LENGTH_LONG).show();
                    return;
                 }else if ( wordFont > 3 || wordFont< 1 ){
                    Toast.makeText(PrinterActivity.this,getString(R.string.outOfFont),Toast.LENGTH_LONG).show();
                    return;
                }else if (printGray < 1 || printGray > 20){
                    Toast.makeText(PrinterActivity.this,getString(R.string.outOfGray),Toast.LENGTH_LONG).show();
                    return;
                }
                if(printContent == null || printContent.length() == 0){
                    Toast.makeText(PrinterActivity.this,getString(R.string.empty),Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if(!nopaper) {
                        setTitle("print character");
                        progressDialog=ProgressDialog.show(PrinterActivity.this,getString(R.string.bl_dy),getString(R.string.printing_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
                    }
                    else {
                        Toast.makeText(PrinterActivity.this,getString(R.string.ptintInit),Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        dialog = new ProgressDialog(PrinterActivity.this);
        dialog.setTitle("Processing");
        dialog.setMessage("Please wait while checking driver version");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    ThermalPrinter.start();
                    printVersion = ThermalPrinter.getVersion();
                    Message message = new Message();
                    message.obj = "1";
                    message.what = PRINTVERSION;
                    handler.sendMessage(message);
                }catch (TelpoException e){
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = PRINTVERSION;
                    message.obj = "0";
                    handler.sendMessage(message);
                }finally {
                    ThermalPrinter.stop();
                }
            }
        }.start();

    }

    /* Called when the application resumes */
    @Override
    protected void onResume() {
        super.onResume();
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

    private void noPaperDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(PrinterActivity.this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!nopaper) {
                    handler.sendMessage(handler.obtainMessage(PRINTIT, 1, 0, null));
                }else{
                    Toast.makeText(PrinterActivity.this,getString(R.string.ptintInit),Toast.LENGTH_LONG).show();
                    handler.sendMessage(handler.obtainMessage(ENABLE_BUTTON, 1, 0, null));
                }
            }
        });
        dlg.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handler.sendMessage(handler.obtainMessage(ENABLE_BUTTON, 1, 0, null));
            }
        });
        dlg.show();
    }




    private class paperWalkPrintThread extends Thread{
        public void run(){
            super.run();
            setName("paper walk Thread");
            try {
                ThermalPrinter.start();
                ThermalPrinter.walkPaper(paperWalk);
            }catch (Exception e){
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")){
                    nopaper = true;
                    return;
                }else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException"))
                {
                	handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                }
                else
                {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            }finally {

                PrinterActivity.this.sleep(500);
                if(nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper=false;
                
                PrinterActivity.this.sleep(2000);
                if(progressDialog != null && !PrinterActivity.this.isFinishing() ){
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG,"The Print Progress End !!!");
                if(isClose) {
                    onDestroy();
                    finish();
                }
            }
            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));
        }
    }

    private class barcodePrintThread extends Thread{
        public void run(){
            super.run();
            setName("Barcode Print Thread");

            try {
                ThermalPrinter.start();
                ThermalPrinter.printBarcode(barcodeStr);
                ThermalPrinter.walkPaper(100);
            }catch (Exception e){
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")){
                    nopaper = true;
                    return;
                }else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException"))
                {
                	handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                }
                else
                {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            }finally {
                if(nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                PrinterActivity.this.sleep(1000);
                ThermalPrinter.stop();
                nopaper=false;

                if(progressDialog != null && !PrinterActivity.this.isFinishing() ){
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG,"The Print Progress End !!!");
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if(isClose) {
                    finish();
                }
            }

            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));

        }
    }

    private class qrcodePrintThread extends Thread{
        public void run(){
            super.run();
            setName("Barcode Print Thread");
            try {

                ThermalPrinter.start();
                printQrcode(qrcodeStr);
                ThermalPrinter.addString(qrcodeStr);
                ThermalPrinter.printStringAndWalk(ThermalPrinter.DIRECTION_FORWORD,ThermalPrinter.WALK_LINE,12);
                ThermalPrinter.clearString();

            }catch (Exception e){
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")){
                    nopaper = true;
                    return;
                }else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException"))
                {
                	handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                }
                else
                {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            }finally {

                PrinterActivity.this.sleep(1000);
                //lock.release();
                if(nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));

                ThermalPrinter.stop();
                nopaper=false;
                if(progressDialog != null && !PrinterActivity.this.isFinishing() ){
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG,"The Print Progress End !!!");
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if(isClose) {
                    finish();
                }
            }

            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));

        }
    }

    private class contentPrintThread extends Thread{
        public void run(){
            super.run();
            setName("Content Print Thread");
            try {
                ThermalPrinter.start();
                ThermalPrinter.reset();
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
                ThermalPrinter.setLeftIndent(leftDistance);
                ThermalPrinter.setLineSpace(lineDistance);
                if(wordFont == 3){
                    ThermalPrinter.setFontSize(2);
                    ThermalPrinter.enlargeFontSize(2,2);
                }else{
                    ThermalPrinter.setFontSize(wordFont);
                }
                ThermalPrinter.setGray(printGray);
                ThermalPrinter.addString(printContent);
                ThermalPrinter.printString();
                ThermalPrinter.clearString();
                ThermalPrinter.walkPaper(100);
            }catch (Exception e){
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")){
                    nopaper = true;
                    return;
                }else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException"))
                {
                	handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                }else
                {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            }finally {
                PrinterActivity.this.sleep(2000);
                //lock.release();
                if(nopaper)
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                ThermalPrinter.stop();
                nopaper=false;
                if(progressDialog != null && !PrinterActivity.this.isFinishing() ){
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Log.v(TAG,"The Print Progress End !!!");
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if(isClose) {
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

        if(progressDialog != null && !PrinterActivity.this.isFinishing() ){
            progressDialog.dismiss();
            progressDialog = null;
        }
        stop = true;
        unregisterReceiver(printReceive);
        ThermalPrinter.stop();
        super.onDestroy();
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
     * @param str 条码内容
     * @param type 条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX, EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14, RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
     *  @param bmpWidth 生成位图宽,宽不能大于384，不然大于打印纸宽度
     *  @param bmpHeight 生成位图高，8的倍数
     */

    public Bitmap CreateCode(String str,com.google.zxing.BarcodeFormat type,int bmpWidth,int bmpHeight) throws WriterException {
        //生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, type,bmpWidth,bmpHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }else{
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
     * @return None
     * @author  zhouzy
     * @date 20141223
     * @note
     */
    private void printQrcode(String str)throws Exception
    {
        //       Bitmap bitmap= BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/time1.bmp"));
        Bitmap bitmap=CreateCode(str, BarcodeFormat.QR_CODE,256,256);
        if(bitmap!=null){
            Log.v(TAG,"Find the Bmp");
            ThermalPrinter.printLogo(bitmap);
        }
    }

}
