package com.telpo.tps550.api.demo.megnetic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.magnetic.MagneticCard;

/**
 * For Magnetic stripe card test.
 * @author linhx
 * @date 2015-02-27
 *
 */
public class MegneticActivity extends Activity {
    EditText editText1;
    EditText editText2;
    EditText editText3;
    int TimeoutInms = 10*1000;
//    private MagneticStripeReader msr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnetic_main);

        editText1 = (EditText) findViewById(R.id.editText_track1);
        editText2 = (EditText) findViewById(R.id.editText_track2);
        editText3 = (EditText) findViewById(R.id.editText_track3);

        final Button click = (Button) findViewById(R.id.button_open);
        final Button quit = (Button) findViewById(R.id.button_quit);

        //msr = new MagneticStripeReader(new MyHandler());
        try {
            MagneticCard.open();
            click.setEnabled(true);
        } catch (Exception e) {
           //打开磁条卡失败
            click.setEnabled(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.error);
            alertDialog.setMessage(R.string.error_open_magnetic_card);
            alertDialog.setPositiveButton(R.string.dialog_comfirm,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MegneticActivity.this.finish();
                }
            });
            alertDialog.show();

        }


        click.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setTitle(getText(R.string.please));
                //启动线程监控刷卡
                //msr.StartReading();
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");

                try {
                    String[] TracData = MagneticCard.check(TimeoutInms);

                    for(int i=0; i<3; i++){
                        if(TracData[i] != null){
                            switch (i){
                                case 0:
                                    editText1.setText(TracData[i]);
                                    break;
                                case 1:
                                    editText2.setText(TracData[i]);
                                    break;
                                case 2:
                                    editText3.setText(TracData[i]);
                                    break;
                            }

                        }
                    }
                }catch (TelpoException e){
                    e.printStackTrace();
                    Toast.makeText(MegneticActivity.this, "Read Card Failed:"+e.getDescription(), Toast.LENGTH_LONG).show();
                }
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MegneticActivity.this.finish();
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        //msr.Close();
        MagneticCard.close();
    }

}
