package com.telpo.tps550.api.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends Activity {

    private final String NAMESPACE = "http://tempuri.org/";
    //private final String URL = "http://sgtu.portalsabi.com/WebServiceFleet.asmx";
    private final String URL = "http://transcor.portalsabi.com/WebServiceFleet.asmx";
    private final String SOAP_ACTION = "http://tempuri.org/listarCartoesDB";
    private final String METHOD_NAME = "listarCartoesDB";

    private final String SOAP_ACTION2 = "http://tempuri.org/ListarCoordenadasParagensGeral";
    private final String METHOD_NAME2 = "ListarCoordenadasParagensGeral";

    private String[] fignerType = null;
    private RadioOnClick OnClick = new RadioOnClick(0);
    private ListView fignerListView;
    private int Oriental = -1;
    Button btnAbrirTurno;
    Button btnFecharTurno;
    Button btnPrivado;
    Button btnBD;
    Button btnSettings;
    DBHelper db;
    ProgressBar pb;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //new mySOAPAsyncTask3().execute("sgtu.portalsabi.com/");

        if(-1 == Oriental){

            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                Oriental = 0;
            }
            else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                Oriental = 1;
            }
        }



        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        btnAbrirTurno = (Button) findViewById(R.id.buttonAbrirTurno);
        btnFecharTurno = (Button) findViewById(R.id.buttonFecharTurno);
        btnPrivado =  (Button) findViewById(R.id.buttonParticlau);
        btnBD  =  (Button) findViewById(R.id.buttonUpdateBD);
        btnSettings = (Button) findViewById(R.id.buttonSettings);
        pb = (ProgressBar)findViewById(R.id.progressBarMain);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        btnAbrirTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DBHelper db = new DBHelper(getApplicationContext());
                Autocarro au = db.getDadosAutocarro();
                try
                {
                    if (au.getIdAutocarro().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Falta configurar o Autocarro no menu Settings!", Toast.LENGTH_LONG).show();
                    } else {

                        Intent intent = new Intent(MainActivity.this, AberturaTurno.class);
                        startActivity(intent);
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Falta configurar o Autocarro no menu Settings!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnFecharTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper db = new DBHelper(getApplicationContext());
                Autocarro au = db.getDadosAutocarro();
                try {
                    if (au.getIdAutocarro().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Falta configurar o Autocarro no menu Settings!", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, FecharTurno.class);
                        startActivity(intent);
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Falta configurar o Autocarro no menu Settings!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPrivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateApp updateApp = new UpdateApp();
                updateApp.setContext(getApplicationContext());
                updateApp.execute("http://sgtu.portalsabi.com/downloads/transcor/app-debug.apk");

            }
        });

        btnBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateBD().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

/*
        Button BnPrint = (Button) findViewById(R.id.print_test);
        Button BnQRCode = (Button) findViewById(R.id.qrcode_verify);
        Button magneticCardBtn = (Button) findViewById(R.id.magnetic_card_btn);
        Button rfidBtn = (Button) findViewById(R.id.rfid_btn);
        Button pcscBtn = (Button) findViewById(R.id.pcsc_btn);
        Button identifyBtn = (Button) findViewById(R.id.identity_btn);
        Button fingerBtn = (Button) findViewById(R.id.fingerPrint_btn);

        BnQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPackage("com.telpo.tps550.api")) {
                    Intent intent = new Intent();
                    intent.setClassName("com.telpo.tps550.api", "com.telpo.tps550.api.barcode.Capture");
                    intent.putExtra("timeout", 0);
                    try {
                        startActivityForResult(intent, 0x124);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this,
                                getResources().getString(R.string.identify_fail),
                                Toast.LENGTH_LONG).show();// "未安装API模块，无法进行二维码/身份证识别"
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.identify_fail),
                            Toast.LENGTH_LONG).show();// "未安装API模块，无法进行二维码/身份证识别"
                }
            }
        });
        BnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrinterActivity.class);
                startActivity(intent);
            }
        });
        magneticCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MegneticActivity.class);
                startActivity(intent);
            }
        });
        //RFID/NFC DEMO
        rfidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RfidActivity.class);
                startActivity(intent);
            }
        });

        //IC卡 DEMO
        pcscBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IccActivity.class);
                startActivity(intent);
            }
        });

        //身份证识别ID卡
        identifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(getString(R.string.idcard_xzgn));
                dialog.setMessage(getString(R.string.idcard_xzsfsbfs));

                dialog.setNegativeButton(getString(R.string.idcard_sxtsb), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, OcrActivity.class);
                        startActivity(intent);

                    }
                });
                dialog.setPositiveButton(getString(R.string.idcard_dkqsb), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, IdCardActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();


            }

        });
        fignerType = new String[]{getString(R.string.fignerprint_01), getString(R.string.fignerprint_02), getString(R.string.fignerprint_03)};

        fingerBtn.setOnClickListener(new RadioClickListener());
*/
    }

    //选择指纹仪按钮组监听
    class RadioClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog ad = new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.fignerprint_tips_01))
                    .setSingleChoiceItems(fignerType, OnClick.getIndex(), OnClick).create();

            fignerListView = ad.getListView();

            ad.show();

        }

    }

    class RadioOnClick implements DialogInterface.OnClickListener {

        private int index;


        public RadioOnClick(int index) {

            this.index = index;

        }

        public void setIndex(int index) {

            this.index = index;

        }

        public int getIndex() {

            return index;

        }


        public void onClick(DialogInterface dialog, int whichButton) {

            setIndex(whichButton);

//            Toast.makeText(MainActivity.this, "您已经选择了 " + ":" + fignerType[index], Toast.LENGTH_LONG).show();

            switch (whichButton) {
                case 0:
                    Toast.makeText(MainActivity.this, getString(R.string.fignerprint_tips_02), Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, getString(R.string.fignerprint_tips_02), Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, getString(R.string.fignerprint_tips_02), Toast.LENGTH_LONG).show();
                    //国内用香港指纹仪
                    //Intent intent = new Intent(MainActivity.this, FtrScanDemoUsbHostActivity.class);
                    //startActivity(intent);
                    break;
            }

            dialog.dismiss();

        }

    }


    private boolean checkPackage(String packageName) {
        PackageManager manager = this.getPackageManager();
        Intent intent = new Intent().setPackage(packageName);
        //List<ResolveInfo> infos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        if (infos == null || infos.size() < 1) {
            return false;
        }
        return true;
    }

    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0xff) {
            if (data != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(data.getStringExtra("idcard"));
                builder.setPositiveButton(getString(R.string.dialog_comfirm), null);
                builder.create().show();
            }
        }

        if (requestCode == 0x124) {
            if (resultCode == 0) {
                if (data != null) {
                    //成功
                    String qrcode = data.getStringExtra("qrCode");//获取结果
                    Toast.makeText(MainActivity.this, "Scan result:" + qrcode, Toast.LENGTH_LONG).show();
                    return;
                }
            }
            // 失败或超时
            Toast.makeText(MainActivity.this, "Scan Failed" + resultCode, Toast.LENGTH_LONG).show();
        }

    }

    protected void onResume(){
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }




    //inserido em 24-09-2015
    public class UpdateApp extends AsyncTask<String,Void,Void>{
        private Context context;
        public void setContext(Context contextf){
            context = contextf;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(this.context,"Verificar novas versões",Toast.LENGTH_LONG);
        }


        @Override
        protected Void doInBackground(String... arg0) {
            try {

                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                //c.setDoOutput(true);
                c.connect();

                String PATH = "/mnt/sdcard/Download/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "app-debug.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[2048];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                final PackageManager pm = getPackageManager();
                String apkName = "app-debug.apk";
                String fullPath = "/mnt/sdcard/Download" + "/" + apkName;
                PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);

                PackageManager manager = context.getPackageManager();
                PackageInfo infoCurrent = manager.getPackageInfo(context.getPackageName(), 0);
                if(infoCurrent.versionName != info.versionName)
                 {
                     Intent intent = new Intent(Intent.ACTION_VIEW);
                     intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/app-debug.apk")), "application/vnd.android.package-archive");
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                     context.startActivity(intent);
                 }

            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage() + e.getStackTrace());
            }
            return null;
        }
    }

    //inserido em 24-09-2015
    public class UpdateBD extends AsyncTask<String,Void,Void>{
        private Context context;
        String cartoesBD = "";
        public void setContext(Context contextf){
            context = contextf;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            pb.setVisibility(View.GONE);

            //Apos os cartoes iniciar paragens
            new UpdateParagens().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(String... arg0) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject result = null;

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = (SoapObject) envelope.bodyIn;
                cartoesBD = result.getProperty(0).toString();
                String[] cartoesGerais = cartoesBD.split("!");
                int l = cartoesGerais.length;
                if(l  > 0)
                {
                    String[] cartoesRecebidos = cartoesGerais[0].split("#");
                    int totalGerais = cartoesRecebidos.length;
                    if ( totalGerais > 0)
                    {
                        db = new DBHelper(getApplicationContext());
                        //db.onUpgradePartial(db.getReadableDatabase(), 1, 1);
                        int i = 0;
                        while (i < cartoesRecebidos.length) {
                            //e79022a9;Estudante;31-10-2015;Activo
                            String[] registo = cartoesRecebidos[i].split(";");

                            Cartao cartaoDB = new Cartao();
                            cartaoDB.setNumero(registo[0]);
                            cartaoDB.setTipo(registo[1]);
                            cartaoDB.setAnoMes(registo[2]);
                            cartaoDB.setStatus(registo[3]);
                            cartaoDB.setAnoMesAnterior(registo[4]);
                            cartaoDB.setdataExpiracao(registo[5]);
                            db.deleteBook(cartaoDB);
                            db.createCartao(cartaoDB);
                            i++;
                        }
                    }

                    String[] cartoesMotorista = cartoesGerais[1].split("#");
                    if(cartoesMotorista.length > 0)
                    {
                        db = new DBHelper(getApplicationContext());
                        //db.onUpgradePartial(db.getReadableDatabase(), 1, 1);
                        int i = 0;
                        while (i < cartoesMotorista.length) {
                            //nome;cartao;codigoFuncionario
                            String[] registo = cartoesMotorista[i].split(";");

                            CartaoMotorista cartaoDB = new CartaoMotorista();
                            cartaoDB.setNome(registo[0]);
                            cartaoDB.setCartao(registo[1]);
                            cartaoDB.setCodigoFuncionario(registo[2]);
                            //db.deleteCartaoMotorista(cartaoDB);
                            db.createCartaoMotorista(cartaoDB);
                            i++;
                        }
                    }

                }
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

    public class UpdateParagens extends AsyncTask<String,Void,Void>{
        private Context context;
        String cartoesBD = "";
        public void setContext(Context contextf){
            context = contextf;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            pb.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(String... arg0) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);

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
                cartoesBD = result.getProperty(0).toString();
                db.createParagens(cartoesBD);

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

}
