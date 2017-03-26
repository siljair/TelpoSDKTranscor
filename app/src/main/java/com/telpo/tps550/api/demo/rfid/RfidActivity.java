package com.telpo.tps550.api.demo.rfid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.demo.util.OtherUtil;
import com.telpo.tps550.api.iccard.Picc;

/**
 * Created by linhx on 2015/3/2.
 */
public class RfidActivity extends Activity {

    private Button getSN, m1Test;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rfid_main);
        getSN = (Button) findViewById(R.id.get_sn);
        m1Test = (Button) findViewById(R.id.m1_test);

        m1Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RfidActivity.this, ConsumeActivity.class);
                startActivity(intent);
            }
        });

        getSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetSNTask().execute();
            }
        });
    }

    private class GetSNTask extends AsyncTask<Void, Void, TelpoException> {
        ProgressDialog dialog;
        byte[] sn = new byte[64];
        int length;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getSN.setEnabled(false);
            m1Test.setEnabled(false);

            dialog = new ProgressDialog(RfidActivity.this);
            dialog.setMessage(getString(R.string.operating));
            dialog.setCancelable(false);
            dialog.show();
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
            dialog.dismiss();
            if(e == null){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RfidActivity.this);
                dialogBuilder.setTitle(getString(R.string.card_sn));
                dialogBuilder.setMessage(OtherUtil.byteToHexString(sn, length));
                dialogBuilder.setPositiveButton(R.string.confirm,null);
                dialogBuilder.create();
                dialogBuilder.show();
            }else{
                Toast.makeText(RfidActivity.this,getString(R.string.operation_fail)+":"+e.getDescription(),Toast.LENGTH_LONG).show();
            }
            getSN.setEnabled(true);
            m1Test.setEnabled(true);
        }
    }
}