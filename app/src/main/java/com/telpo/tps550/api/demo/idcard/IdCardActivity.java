package com.telpo.tps550.api.demo.idcard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.other.BeepManager;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.idcard.IdCard;
import com.telpo.tps550.api.idcard.IdentityInfo;

/**
 * For Chinese 2nd generation ID Card test.
 * @author linhx
 * @date 2015-02-27
 */
public class IdCardActivity extends Activity {
    Button getData;
    TextView idcardInfo;
    ImageView imageView;
    IdentityInfo info;
    BeepManager beepManager;
    Bitmap bitmap;
    byte[] image;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idcard_main);
        getData = (Button) findViewById(R.id.requestDataBtn);
        idcardInfo = (TextView) findViewById(R.id.showData);
        imageView = (ImageView) findViewById(R.id.imageView1);

        beepManager = new BeepManager(this, R.raw.beep);

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	new GetIDInfoTask().execute();
            }
        });

    }
    
    private class GetIDInfoTask extends AsyncTask<Void, Integer, TelpoException>{
    	ProgressDialog dialog;
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		getData.setEnabled(false);
            dialog = new ProgressDialog(IdCardActivity.this);
            dialog.setTitle(getString(R.string.idcard_czz));
            dialog.setMessage(getString(R.string.idcard_ljdkq));
            dialog.setCancelable(false);
            dialog.show();
            info = null;
            bitmap = null;
    	}
    	
		@Override
		protected TelpoException doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			TelpoException result = null;
			try{
				IdCard.open();
				publishProgress(1);
				info = IdCard.checkIdCard(10000);
				image = IdCard.getIdCardImage();
				bitmap = IdCard.decodeIdCardImage(image);
			}catch(TelpoException e){
				e.printStackTrace();
				result = e;
			}finally{
				IdCard.close();
			}
			return result;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if(values[0] == 1){
				dialog.setMessage(getString(R.string.idcard_hqsfzxx));
			}
		}
		
		@Override
		protected void onPostExecute(TelpoException result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			getData.setEnabled(true);
			if(result == null){
				beepManager.playBeepSoundAndVibrate();
                idcardInfo.setText(getString(R.string.idcard_xm) + info.getName() + "\n\n" + getString(R.string.idcard_xb) + info.getSex() + "\n\n" + getString(R.string.idcard_mz) + info.getNation() + "\n\n"
                        + getString(R.string.idcard_csrq) + info.getBorn() + "\n\n" + getString(R.string.idcard_dz) + info.getAddress() + "\n\n" + getString(R.string.idcard_sfhm) + info.getNo() + "\n\n"
                        + getString(R.string.idcard_qzjg) + info.getApartment() + "\n\n" + getString(R.string.idcard_yxqx) + info.getPeriod() );
                imageView.setImageBitmap(bitmap);
			}else{
				idcardInfo.setText(getString(R.string.idcard_dqsbhcs));
				imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
			}
		}
    	
    }

    @Override
    protected void onResume() {
        super.onResume();
        beepManager.updatePrefs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IdCard.close();
    }
}