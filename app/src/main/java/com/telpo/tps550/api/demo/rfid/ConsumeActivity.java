package com.telpo.tps550.api.demo.rfid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.telpo.tps550.api.demo.R;
import com.google.zxing.other.BeepManager;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.iccard.Picc;

import java.lang.reflect.Field;

/**
 * For M1 card test.
 * @author linhx
 * @date 2015-02-27
 */
public class ConsumeActivity extends Activity {

	private Button init, query, consume, recharge;
	private boolean openFlag = false;

	private final int TEST_SECTOR = 1;
	private final int TEST_BLOCK = 4;
	private final byte[] Password = new byte[] { (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };

	BeepManager beepManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.consume);

		init = (Button) findViewById(R.id.init);
		query = (Button) findViewById(R.id.query);
		consume = (Button) findViewById(R.id.consume);
		recharge = (Button) findViewById(R.id.recharge);

		init.setEnabled(false);
		query.setEnabled(false);
		consume.setEnabled(false);
		recharge.setEnabled(false);

		beepManager = new BeepManager(this, R.raw.beep);
		// 蜂鸣器控制
		beepManager.updatePrefs();

		init.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showInput(getString(R.string.input_init_amount), 0);
			}
		});

		query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new QueryTask().execute();
			}
		});

		consume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showInput(getString(R.string.input_consume_amount), 1);
			}
		});

		recharge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showInput(getString(R.string.input_recharge_amount), 2);
			}
		});

		new OpenTask().execute();

	}

	private void showInput(String title, final int type) {
		LinearLayout inputLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.lay_input, null);
		final EditText editText = (EditText) inputLayout
				.findViewById(R.id.input);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				ConsumeActivity.this);
		alertDialog.setTitle(title);
		alertDialog.setView(inputLayout);
		alertDialog.setNegativeButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String additionalCode = editText.getText().toString();
						if (additionalCode == null
								|| additionalCode.length() == 0) {
							Toast.makeText(ConsumeActivity.this,
									R.string.input_amount, Toast.LENGTH_SHORT)
									.show();
							setDialogDismiss(dialog, true);
						} else {
							setDialogDismiss(dialog, false);
							int amount = Integer.parseInt(additionalCode);
							switch (type) {
							case 0:
								new InitTask().execute(amount);
								break;
							case 1:
								new ConsumeTask().execute(amount);
								break;
							case 2:
								new RechargeTask().execute(amount);
								break;
							default:
								break;
							}
						}
					}
				});
		alertDialog.setPositiveButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						setDialogDismiss(dialog, false);
					}
				});
		alertDialog.create().show();
	}

	private void setDialogDismiss(DialogInterface dialog, boolean showing) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, !showing);
			dialog.dismiss();

		} catch (Exception e) {
			Log.w("SonsumeActivity", e.getMessage());
		}
	}

	private class OpenTask extends AsyncTask<Void, Void, TelpoException> {

		ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			init.setEnabled(false);
			query.setEnabled(false);
			consume.setEnabled(false);
			recharge.setEnabled(false);

			dialog = new ProgressDialog(ConsumeActivity.this);
			// dialog.setTitle(R.string.operating);
			dialog.setMessage(getString(R.string.operating));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected TelpoException doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TelpoException result = null;
			try {
				Picc.openReader();
				openFlag = true;
			} catch (TelpoException e) {
				e.printStackTrace();
				result = e;
			}
			return result;
		}

		@Override
		protected void onPostExecute(TelpoException result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result == null) {
				Toast.makeText(ConsumeActivity.this,
						getString(R.string.operation_succss),
						Toast.LENGTH_SHORT).show();
				init.setEnabled(true);
				query.setEnabled(true);
				consume.setEnabled(true);
				recharge.setEnabled(true);
			} else {
				Toast.makeText(ConsumeActivity.this, result.getDescription(),
						Toast.LENGTH_LONG).show();
				init.setEnabled(false);
				query.setEnabled(false);
				consume.setEnabled(false);
				recharge.setEnabled(false);
			}
		}
	}

	private class InitTask extends AsyncTask<Integer, Void, TelpoException> {

		ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			init.setEnabled(false);
			query.setEnabled(false);
			consume.setEnabled(false);
			recharge.setEnabled(false);

			dialog = new ProgressDialog(ConsumeActivity.this);
			// dialog.setTitle(R.string.operating);
			dialog.setMessage(getString(R.string.operating));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected TelpoException doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			TelpoException result = null;
			byte[] atr = new byte[64];
			try {
				int ret = Picc.selectCard(atr);
				Log.w("ConsumeActivity", "select card:" + ret);
				displayMsg("选卡", atr, ret);
				Picc.m1Authority(Picc.PICC_M1_TYPE_A, TEST_SECTOR, Password);
				byte[] buffer = constructBuffer((byte) TEST_BLOCK, params[0]);
				Picc.m1Write(TEST_BLOCK, buffer);
			} catch (TelpoException e) {
				e.printStackTrace();
				result = e;
			} finally {
				try {
					Picc.haltCard();
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(TelpoException result) {
			super.onPostExecute(result);
			dialog.dismiss();
			init.setEnabled(true);
			query.setEnabled(true);
			consume.setEnabled(true);
			recharge.setEnabled(true);
			if (result == null) {
				beepManager.playBeepSoundAndVibrate();
				Toast.makeText(ConsumeActivity.this,
						getString(R.string.operation_succss),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ConsumeActivity.this,result.getDescription(), Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	private class ConsumeTask extends AsyncTask<Integer, Void, TelpoException> {

		ProgressDialog dialog = null;
		boolean enoughMoney = true;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			enoughMoney = true;
			
			init.setEnabled(false);
			query.setEnabled(false);
			consume.setEnabled(false);
			recharge.setEnabled(false);

			dialog = new ProgressDialog(ConsumeActivity.this);
			// dialog.setTitle(R.string.operating);
			dialog.setMessage(getString(R.string.operating));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected TelpoException doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			TelpoException result = null;
			byte[] atr = new byte[64];
			try {
				int ret = Picc.selectCard(atr);
				Log.w("ConsumeActivity", "select card:" + ret);
				displayMsg("选卡", atr, ret);
				Picc.m1Authority(Picc.PICC_M1_TYPE_A, TEST_SECTOR, Password);
				byte[] buffer = new byte[16];
				Picc.m1Read(TEST_BLOCK, buffer);
				if (byteToInt(buffer) < params[0]) {
					enoughMoney = false;
					return result;
				}
				Picc.m1Sub(TEST_BLOCK, intToByte(params[0]));
			} catch (TelpoException e) {
				e.printStackTrace();
				result = e;
			} finally {
				try {
					Picc.haltCard();
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(TelpoException result) {
			super.onPostExecute(result);
			dialog.dismiss();
			init.setEnabled(true);
			query.setEnabled(true);
			consume.setEnabled(true);
			recharge.setEnabled(true);
			if (result == null) {
				if(enoughMoney){
					beepManager.playBeepSoundAndVibrate();
					Toast.makeText(ConsumeActivity.this,
							getString(R.string.operation_succss),
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ConsumeActivity.this,
							R.string.no_enough_balance, Toast.LENGTH_LONG).show();
				}

			}else {
				Toast.makeText(ConsumeActivity.this,result.getDescription(), Toast.LENGTH_LONG).show();
			}
		}
	}

	private class RechargeTask extends AsyncTask<Integer, Void, TelpoException> {

		ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			init.setEnabled(false);
			query.setEnabled(false);
			consume.setEnabled(false);
			recharge.setEnabled(false);

			dialog = new ProgressDialog(ConsumeActivity.this);
			// dialog.setTitle(R.string.operating);
			dialog.setMessage(getString(R.string.operating));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected TelpoException doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			TelpoException result = null;
			byte[] atr = new byte[64];
			try {
				int ret = Picc.selectCard(atr);
				Log.w("ConsumeActivity", "select card:" + ret);
				displayMsg("选卡", atr, ret);
				Picc.m1Authority(Picc.PICC_M1_TYPE_A, TEST_SECTOR, Password);
				Picc.m1Add(TEST_BLOCK, intToByte(params[0]));
			} catch (TelpoException e) {
				e.printStackTrace();
				result  = e;
			} finally {
				try {
					Picc.haltCard();
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(TelpoException result) {
			super.onPostExecute(result);
			dialog.dismiss();
			init.setEnabled(true);
			query.setEnabled(true);
			consume.setEnabled(true);
			recharge.setEnabled(true);
			if (result == null) {
				beepManager.playBeepSoundAndVibrate();
				Toast.makeText(ConsumeActivity.this,
						getString(R.string.operation_succss),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ConsumeActivity.this,result.getDescription(), Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	private class QueryTask extends AsyncTask<Void, Void, TelpoException> {

		ProgressDialog dialog = null;
		int amount = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			init.setEnabled(false);
			query.setEnabled(false);
			consume.setEnabled(false);
			recharge.setEnabled(false);

			dialog = new ProgressDialog(ConsumeActivity.this);
			// dialog.setTitle(R.string.operating);
			dialog.setMessage(getString(R.string.operating));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected TelpoException doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TelpoException result = null;
			byte[] atr = new byte[64];
			try {
				int ret = Picc.selectCard(atr);
				Log.w("ConsumeActivity", "select card:" + ret);
				displayMsg("选卡", atr, ret);
				Picc.m1Authority(Picc.PICC_M1_TYPE_A, TEST_SECTOR, Password);
				byte[] buffer = new byte[16];
				Picc.m1Read(TEST_BLOCK, buffer);
				amount = byteToInt(buffer);
			} catch (TelpoException e) {
				e.printStackTrace();
				result = e;
			} finally {
				try {
					Picc.haltCard();
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(TelpoException result) {
			super.onPostExecute(result);
			dialog.dismiss();
			init.setEnabled(true);
			query.setEnabled(true);
			consume.setEnabled(true);
			recharge.setEnabled(true);

			if (result == null) {
				beepManager.playBeepSoundAndVibrate();
				// Toast.makeText(ConsumeActivity.this,
				// getString(R.string.operation_succss),
				// Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ConsumeActivity.this);
				builder.setTitle(R.string.balance);
				builder.setMessage("" + amount);
				builder.setPositiveButton(R.string.confirm, null);
				builder.create().show();
			} else {
				Toast.makeText(ConsumeActivity.this,result.getDescription(), Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	// private String getErrorCode() {
	// byte[] errorCode = new byte[2];
	// Picc.getLastError(errorCode);
	// return ":" + Integer.toHexString(errorCode[0] & 0xff) + " "
	// + Integer.toHexString(errorCode[1] & 0xff);
	// }

	private void displayMsg(String tag, byte[] data, int length) {
		Log.w("Test", tag);
		for (int i = 0; i < length; i++) {
			Log.w("ConsumeActivity", Integer.toHexString(data[i] & 0xff));
		}
	}

	private int byteToInt(byte[] value) {
		int result = 0;
		for (int i = 3; i >= 0; i--) {
			result = result * 0x100 + (value[i] & 0xff);
		}
		return result;
	}

	private byte[] intToByte(int value) {
		byte[] result = new byte[4];
		for (int i = 0; i < 4; i++) {
			result[i] = (byte) (value % 0x100);
			value /= 0x100;
		}
		return result;
	}

	private byte[] constructBuffer(byte blockNo, int value) {
		byte[] result = new byte[16];
		for (int i = 0; i < 4; i++) {
			result[i] = (byte) (value % 0x100);
			value /= 0x100;
		}
		// 2nd and 3rd 4 bytes
		for (int i = 0; i < 4; i++) {
			result[4 + i] = (byte) ~result[i];
			result[8 + i] = result[i];
		}

		// last 4 bytes
		result[12] = blockNo;
		result[13] = (byte) ~blockNo;
		result[14] = blockNo;
		result[15] = (byte) ~blockNo;
		return result;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (openFlag) {
			Picc.closeReader();
		}
	}
}
