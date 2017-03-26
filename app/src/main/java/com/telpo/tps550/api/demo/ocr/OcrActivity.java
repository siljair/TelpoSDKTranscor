package com.telpo.tps550.api.demo.ocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.idcard.IdentityInfo;

import java.util.List;

/***
 * For OCR of Chinese 2nd ID card test.
 * @author linhx
 * @date 2015-02-27
 */
public class OcrActivity extends Activity {

	private final int BAR_REQ = 1;
	private final int ID_REQ1 = 2;
	private final int ID_REQ2 = 3;

	TextView barcode, idInfo1, idInfo2;
	Button recog1, recog2, recog3;

    private int Oriental = -1;
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ocr_main);

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

		barcode = (TextView) findViewById(R.id.barcode);
		idInfo1 = (TextView) findViewById(R.id.idInfo1);
		idInfo2 = (TextView) findViewById(R.id.idInfo2);

		recog1 = (Button) findViewById(R.id.recog1);
		recog2 = (Button) findViewById(R.id.recog2);
		recog3 = (Button) findViewById(R.id.recog3);

		recog1.setEnabled(false);
		recog2.setEnabled(false);
		recog3.setEnabled(false);

		recog1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				// LinearLayout settingLayout = (LinearLayout)
				// getLayoutInflater().inflate(R.layout.lay_input, null);
				LinearLayout inputLayout = (LinearLayout) getLayoutInflater()
						.inflate(R.layout.lay_input, null);
				final EditText editText = (EditText) inputLayout
						.findViewById(R.id.input);
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						OcrActivity.this);
				alertDialog.setTitle(getResources().getString(
						R.string.input_timeout)); // "输入超时时间"
				alertDialog.setView(inputLayout);
				alertDialog.setNegativeButton(
						getResources().getString(R.string.dialog_comfirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) { //
								String additionalCode = editText.getText()
										.toString();
								int timeout;
								try {
									timeout = Integer.parseInt(additionalCode);
									if (timeout < 0) {
										Toast.makeText(
												OcrActivity.this,
												getResources().getString(
														R.string.input_illegle),
												Toast.LENGTH_LONG).show();// "输入非法"
										return;
									}
								} catch (Exception e) {
									Toast.makeText(
											OcrActivity.this,
											getResources().getString(
													R.string.input_illegle),
											Toast.LENGTH_LONG).show();// "输入非法"
									return;
								}
								recog1.setEnabled(false);
								Intent intent = new Intent();
								intent.setClassName("com.telpo.tps550.api",
										"com.telpo.tps550.api.barcode.Capture");
								intent.putExtra("timeout", timeout * 1000);
								try {
									startActivityForResult(intent, BAR_REQ);
								} catch (ActivityNotFoundException exception) {
									Toast.makeText(
											OcrActivity.this,
											getResources().getString(
													R.string.identify_fail),
											Toast.LENGTH_LONG).show();// "未安装API模块，无法进行二维码/身份证识别"
								}
							}
						});
				alertDialog.setPositiveButton(
						getResources().getString(R.string.dialog_cancel), null);// "取消"
				alertDialog.create().show();

			}
		});

		recog2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recog2.setEnabled(false);
				Intent intent = new Intent();
				intent.setClassName("com.telpo.tps550.api",
						"com.telpo.tps550.api.ocr.IdCardOcr");
				intent.putExtra("type", true);
				intent.putExtra("isKeepPicture", true);// 是否保存图片
														// true是，false:否，不传入时，默认为否
				intent.putExtra("PictPath", "/sdcard/DCIM/Camera/003.png");// 图片路径，不传入时保存到默认路径/sdcard/OCRPict
				intent.putExtra("PictFormat", "PNG");// 图片格式：JPEG，PNG，WEBP，不传入时默认为PNG格式

				try {
					startActivityForResult(intent, ID_REQ1);
				} catch (ActivityNotFoundException exception) {
					Toast.makeText(OcrActivity.this,
							getResources().getString(R.string.identify_fail),
							Toast.LENGTH_LONG).show();// "未安装API模块，无法进行二维码/身份证识别"
				}
			}
		});

		recog3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recog3.setEnabled(false);
				Intent intent = new Intent();
				intent.setClassName("com.telpo.tps550.api",
						"com.telpo.tps550.api.ocr.IdCardOcr");
				intent.putExtra("type", false);
				intent.putExtra("isKeepPicture", true);// 是否保存图片
														// true是，false:否，不传入时，默认为否
				intent.putExtra("PictPath", "/sdcard/DCIM/Camera/002.png");// 图片路径，不传入时保存到默认路径/sdcard/OCRPict
				intent.putExtra("PictFormat", "PNG");// 图片格式：JPEG，PNG，WEBP，不传入时默认为PNG格式

				try {
					startActivityForResult(intent, ID_REQ2);
				} catch (ActivityNotFoundException exception) {
					Toast.makeText(OcrActivity.this,
							getResources().getString(R.string.identify_fail),
							Toast.LENGTH_LONG).show();// "未安装API模块，无法进行二维码/身份证识别"
				}
			}
		});
	}

	@Override
	protected void onResume() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		super.onResume();
		if (!checkPackage("com.telpo.tps550.api")) {
			Toast.makeText(this,
					getResources().getString(R.string.identify_fail),
					Toast.LENGTH_LONG).show();// "未安装API模块，无法进行二维码/身份证识别"
			recog1.setEnabled(false);
			recog2.setEnabled(false);
			recog3.setEnabled(false);
		} else {
			recog1.setEnabled(true);
			recog2.setEnabled(true);
			recog3.setEnabled(true);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BAR_REQ:
			if (resultCode == 0) {
				String qrcode = null;
				try {
					qrcode = data.getStringExtra("qrCode");// 获取结果
				} catch (java.lang.NullPointerException e) {
					Toast.makeText(
							this,
							getResources().getString(R.string.get_barcode_fail),
							Toast.LENGTH_LONG).show();// "获取二维码失败或超时"
					barcode.setText(getResources().getString(R.string.none));// "无"
					recog1.setEnabled(true);
					break;
				}
				if (qrcode != null) {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.get_barcode_success),
							Toast.LENGTH_SHORT).show();// "获取二维码成功"
					barcode.setText(qrcode);
					recog1.setEnabled(true);
				}
			} else {
				Toast.makeText(this,
						getResources().getString(R.string.get_barcode_fail),
						Toast.LENGTH_LONG).show();// "获取二维码失败或超时"
				barcode.setText(getResources().getString(R.string.none));// "无"
				recog1.setEnabled(true);
			}
			break;
		case ID_REQ1:
			if (resultCode == 0) {
				IdentityInfo info = null;
				try {
					info = (IdentityInfo) data.getSerializableExtra("idInfo");
				} catch (NullPointerException e) {
					Toast.makeText(
							this,
							getResources()
									.getString(R.string.get_id_front_fail),
							Toast.LENGTH_LONG).show();// "获取身份证正面信息失败"
					idInfo1.setText(getResources().getString(R.string.none));// "无"
					recog2.setEnabled(true);
					break;
				}
				if (info != null && info.getName() != null
						&& info.getNo() != null) {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.get_id_front_success),
							Toast.LENGTH_SHORT).show();// "获取身份证正面信息成功"
					idInfo1.setText(getResources()
							.getString(R.string.idcard_xm)
							+ info.getName()
							+ // "姓名:"
							"   "
							+ getResources().getString(R.string.idcard_xb)
							+ info.getSex()
							+ //
							"   "
							+ getResources().getString(R.string.idcard_mz)
							+ info.getNation()
							+ // "民族:"
							"\n"
							+ getResources().getString(R.string.idcard_csrq)
							+ info.getBorn()
							+ // "出生日期: "
							"\n"
							+ getResources().getString(R.string.idcard_dz)
							+ "　　"
							+ info.getAddress()
							+ // "地址:     "
							"\n"
							+ getResources().getString(R.string.idcard_sfhm)
							+ info.getNo()); // "身份证号码:"
					recog2.setEnabled(true);
				} else {
					Toast.makeText(
							this,
							getResources()
									.getString(R.string.get_id_front_fail),
							Toast.LENGTH_LONG).show();// "获取身份证正面信息失败"
					idInfo1.setText(getResources().getString(R.string.none));// "无"
					recog2.setEnabled(true);
				}
			} else {
				Toast.makeText(this,
						getResources().getString(R.string.get_id_front_fail),
						Toast.LENGTH_LONG).show();// "获取身份证正面信息失败"
				idInfo1.setText(getResources().getString(R.string.none));// "无"
				recog2.setEnabled(true);
			}
			break;
		case ID_REQ2:
			if (resultCode == 0) {
				IdentityInfo info = null;
				try {
					info = (IdentityInfo) data.getSerializableExtra("idInfo");
				} catch (NullPointerException e) {
					Toast.makeText(
							this,
							getResources().getString(R.string.get_id_back_fail),
							Toast.LENGTH_LONG).show();// "获取身份证反面信息失败"
					idInfo2.setText(getResources().getString(R.string.none));// "无"
					recog3.setEnabled(true);
					break;
				}

				if (info != null && info.getPeriod() != null
						&& info.getApartment() != null) {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.get_id_back_success),
							Toast.LENGTH_SHORT).show();// "获取身份证反面信息成功"
					idInfo2.setText(getResources().getString(
							R.string.idcard_qzjg)
							+ info.getApartment()
							+ "\n"
							+ getResources().getString(R.string.idcard_yxqx)
							+ info.getPeriod());// "签证机关: ""有效期限: "
					recog3.setEnabled(true);
				} else {
					Toast.makeText(
							this,
							getResources().getString(R.string.get_id_back_fail),
							Toast.LENGTH_LONG).show();// "获取身份证反面信息失败"
					idInfo2.setText(getResources().getString(R.string.none));// "无"
					recog3.setEnabled(true);
				}
			} else {
				Toast.makeText(this,
						getResources().getString(R.string.get_id_back_fail),
						Toast.LENGTH_LONG).show();// "获取身份证反面信息失败"
				idInfo2.setText(getResources().getString(R.string.none));// "无"
				recog3.setEnabled(true);
			}
			break;
		default:
			break;
		}
	}

	private boolean checkPackage(String packageName) {
		PackageManager manager = this.getPackageManager();
		Intent intent = new Intent().setPackage(packageName);
		//List<ResolveInfo> infos = manager.queryIntentActivities(intent,PackageManager.GET_INTENT_FILTERS);
		List<ResolveInfo> infos = manager.queryIntentActivities(intent,PackageManager.GET_RESOLVED_FILTER);
		if (infos == null || infos.size() < 1) {
			return false;
		}
		return true;
	}

}
