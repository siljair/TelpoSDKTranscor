package com.telpo.tps550.api.demo.iccard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.demo.util.OtherUtil;
import com.telpo.tps550.api.iccard.ICCard;

/**
 * For IC Card test.
 * @author linhx
 * @date 2015-02-27
 */
public class IccActivity extends Activity {

    private final String TAG = "PcscActivity";

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    private Button connect, smartCard;
    private UsbManager usbManager;
    private PendingIntent mPermissionIntent;

    private ProgressDialog dialog;


    private byte[] recBuffer;
    private int recLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iccard_main);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        reader = new Reader(usbManager);

        // Register receiver for USB permission
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mReceiver, filter);


        recBuffer = new byte[128];

        dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.pcsc_operating)); //操作中
        dialog.setCancelable(false);

        connect = (Button) findViewById(R.id.connect);
        smartCard = (Button) findViewById(R.id.smart_card);

        smartCard.setEnabled(false);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.setEnabled(false);
                // For each device
                dialog.show();
                boolean found = false ;
                try {
                    UsbDevice device = ICCard.initReader(IccActivity.this, ICCard.ICC_ID);
                    usbManager.requestPermission(device, mPermissionIntent);
                }catch (TelpoException e){
                    e.printStackTrace();
                    showMsg(getResources().getString(R.string.pcsc_reader_not_found));//找不到IC卡读卡器
                    connect.setEnabled(true);
                }
            }
        });

        smartCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                smartCard.setEnabled(false);
                recLen = 0;
                new SmartCardTestTask().execute();

            }
        });
    }

    @Override
    protected void onDestroy() {
        // Close reader
        ICCard.close();

        // Unregister receiver
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {

                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        if (device != null) {
                            new OpenTask().execute();
                        }

                    } else {

                        showMsg(getResources().getString(R.string.pcsc_reader_access_limit));//无IC卡读卡器访问权限
                        connect.setEnabled(true);
                    }
                }

            }
        }
    };

    private class OpenTask extends AsyncTask<Void, Void, TelpoException> {


        @Override
        protected TelpoException doInBackground(Void... params) {

            TelpoException result = null;

            try {

                ICCard.open();

            } catch (TelpoException e) {

                result = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(TelpoException result) {

            if (result != null) {
                Log.e(TAG, result.toString());
                showMsg(getResources().getString(R.string.pcsc_smart_card_fail)+":"+result.getDescription());//无法连接IC卡读卡器
                connect.setEnabled(true);
            } else {
                Toast.makeText(IccActivity.this, getResources().getString(R.string.pcsc_reader_connect_success), Toast.LENGTH_SHORT).show();//连接IC读卡器成功
                smartCard.setEnabled(true);
                connect.setEnabled(false);
            }
            dialog.dismiss();

        }
    }

    private class SmartCardTestTask extends AsyncTask<Void, Void, TelpoException> {

        @Override
        protected TelpoException doInBackground(Void... params) {

            TelpoException result = null;

            try {

                ICCard.power(ICCard.CARD_WARM_RESET);

                ICCard.setProtocol(ICCard.PROTOCOL_T0);

                recLen = ICCard.transmit(new byte[]{(byte) 0xA0, (byte) 0xA4, 0x00, 0x00, 0x02, 0x3F, 0x00}, 7, recBuffer, 128);

                ICCard.power(ICCard.CARD_POWER_DOWN);
            } catch (TelpoException e) {

                result = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(TelpoException result) {

            if (result != null) {
                Log.e(TAG, result.toString());
                dialog.dismiss();
                showMsg(getResources().getString(R.string.pcsc_smart_card_fail)+":"+result.getDescription());//读取ICCID失败
            } else {
                String string = OtherUtil.byteToHexString(recBuffer,recLen);

                showMsg(getResources().getString(R.string.operation_succss) + ":" +string);//读取ICCID失败
            }

            smartCard.setEnabled(true);
        }
    }

    private void showMsg(String msg) {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(getResources().getString(R.string.pcsc_key_confirm), null);//确定
        alertDialog.create().show();
    }
}