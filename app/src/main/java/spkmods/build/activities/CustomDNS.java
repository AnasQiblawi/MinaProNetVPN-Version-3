package spkmods.build.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import vpn.minapronet.com.eg.R;
import spkmods.build.ultrasshservice.config.Settings;

public class CustomDNS extends BaseActivity implements CompoundButton.OnCheckedChangeListener
{
    private RadioButton r1;
    private RadioButton r2;
    private RadioButton r3;
    private EditText e2;
    private EditText e1;
    private EditText e3;
    private EditText e4;
    public static String PUT_CUSTOM_KEY2 = "8.8.4.4";
    public static String PUT_CUSTOM_KEY1 = "8.8.8.8";

    private EditText e5;

    private EditText e6;

    private EditText e7;

    private EditText e8;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spk_dns_activity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        r1 = (RadioButton) findViewById(R.id.hdnsRadioButton1);
        r2 = (RadioButton) findViewById(R.id.hdnsRadioButton2);
        r3 = (RadioButton) findViewById(R.id.hdnsRadioButton3);
        e1 = (EditText) findViewById(R.id.hdnsEditText1);
        e2 = (EditText) findViewById(R.id.hdnsEditText2);
        e3 = (EditText) findViewById(R.id.hdnsEditText3);
        e4 = (EditText) findViewById(R.id.hdnsEditText4);

        e5 = (EditText) findViewById(R.id.hdnsEditText5);
        e6 = (EditText) findViewById(R.id.hdnsEditText6);
        e7 = (EditText) findViewById(R.id.hdnsEditText7);
        e8 = (EditText) findViewById(R.id.hdnsEditText8);
        r1.setOnCheckedChangeListener(this);
        r2.setOnCheckedChangeListener(this);
        r3.setOnCheckedChangeListener(this);
        //   HarliesMain.htoRelativeLayout.setVisibility(View.VISIBLE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(CustomDNS.this);
                    if(r1.isChecked()){
                        mPref.edit().putString(Settings.DNSRESOLVER_KEY1,"1.1.1.1").apply();
                        mPref.edit().putString(Settings.DNSRESOLVER_KEY2,"1.0.0.1").apply();
                        finish();
                    }else if(r2.isChecked()){
                        mPref.edit().putString(Settings.DNSRESOLVER_KEY1,"8.8.8.8").apply();
                        mPref.edit().putString(Settings.DNSRESOLVER_KEY2,"8.8.4.4").apply();
                        finish();
                    }else if(r3.isChecked()){
                        if(e1.getText().toString().isEmpty()||e2.getText().toString().isEmpty()||
                           e3.getText().toString().isEmpty()||e4.getText().toString().isEmpty()){
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.spk_snackbar,(ViewGroup) findViewById(R.id.rootLayout));
                            TextView txt1 = (TextView) layout.findViewById(R.id.toastxt);
                            TextView txt2 = (TextView)layout.findViewById(R.id.btntoastxt);
                            txt1.setText("Invalid format In custom DNS");
                            txt2.setText("");
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        }else{
                            PUT_CUSTOM_KEY1 = e1.getText().toString()+"."+e2.getText().toString()+"."+
                                e3.getText().toString()+"."+e4.getText().toString();
                            PUT_CUSTOM_KEY2 = e5.getText().toString()+"."+e6.getText().toString()+"."+
                                e7.getText().toString()+"."+e8.getText().toString();
                            mPref.edit().putBoolean(Settings.DNSFORWARD_KEY, true).apply();
                            mPref.edit().putString(Settings.DNSTYPE_KEY,Settings.DNS_CUSTOM_KEY).apply();
                            mPref.edit().putString(Settings.DNSRESOLVER_KEY1,PUT_CUSTOM_KEY1).apply();
                            mPref.edit().putString(Settings.DNSRESOLVER_KEY2,PUT_CUSTOM_KEY2).apply();
                            dnsString();
                            finish();
                        }
                    }
                }
            });
    }

    @Override
    public void onCheckedChanged(CompoundButton p1, boolean p2)
    {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        switch (p1.getId()) {
            case R.id.hdnsRadioButton1:
                if(r1.isChecked()){
                    r2.setChecked(false);
                    r3.setChecked(false);
                }
                mPref.edit().putBoolean(Settings.DNSFORWARD_KEY, true).apply();
                mPref.edit().putString(Settings.DNSTYPE_KEY, Settings.DNS_DEFAULT_KEY).apply();
                break;

            case R.id.hdnsRadioButton2:
                if(r2.isChecked()){
                    r1.setChecked(false);
                    r3.setChecked(false);
                }
                mPref.edit().putBoolean(Settings.DNSFORWARD_KEY, true).apply();
                mPref.edit().putString(Settings.DNSTYPE_KEY,Settings.DNS_GOOGLE_KEY).apply();
                break;

            case R.id.hdnsRadioButton3:
                if(r3.isChecked()){
                    r2.setChecked(false);
                    r1.setChecked(false);
                }
                mPref.edit().putBoolean(Settings.DNSFORWARD_KEY, true).apply();
                mPref.edit().putString(Settings.DNSTYPE_KEY,Settings.DNS_CUSTOM_KEY).apply();
                break;
        }
        if(r1.isChecked()||r2.isChecked()){
            e1.setEnabled(false);
            e2.setEnabled(false);
            e3.setEnabled(false);
            e4.setEnabled(false);
            e5.setEnabled(false);
            e6.setEnabled(false);
            e7.setEnabled(false);
            e8.setEnabled(false);
        }else if(r3.isChecked()){
            e1.setEnabled(true);
            e2.setEnabled(true);
            e3.setEnabled(true);
            e4.setEnabled(true);
            e5.setEnabled(true);
            e6.setEnabled(true);
            e7.setEnabled(true);
            e8.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        r1.setChecked(mPref.getBoolean("Default_dns",false));
        r2.setChecked(mPref.getBoolean("Google_dns",false));
        r3.setChecked(mPref.getBoolean("Primary_dns",false));
        if(r1.isChecked()||r2.isChecked()){
            e1.setEnabled(false);
            e2.setEnabled(false);
            e3.setEnabled(false);
            e4.setEnabled(false);
        }else if(r3.isChecked()){
            e1.setEnabled(true);
            e2.setEnabled(true);
            e3.setEnabled(true);
            e4.setEnabled(true);
        }
        e1.setText(mPref.getString("e1",""));
        e2.setText(mPref.getString("e2",""));
        e3.setText(mPref.getString("e3",""));
        e4.setText(mPref.getString("e4",""));
        e5.setText(mPref.getString("e5",""));
        e6.setText(mPref.getString("e6",""));
        e7.setText(mPref.getString("e7",""));
        e8.setText(mPref.getString("e8",""));
    }
    private void dnsString(){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPref.edit().putString("e1",e1.getText().toString()).apply();
        mPref.edit().putString("e2",e2.getText().toString()).apply();
        mPref.edit().putString("e3",e3.getText().toString()).apply();
        mPref.edit().putString("e4",e4.getText().toString()).apply();
        mPref.edit().putString("e5",e5.getText().toString()).apply();
        mPref.edit().putString("e6",e6.getText().toString()).apply();
        mPref.edit().putString("e7",e7.getText().toString()).apply();
        mPref.edit().putString("e8",e8.getText().toString()).apply();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(r1.isChecked()){
            mPref.edit().putString(Settings.DNSRESOLVER_KEY1,"1.1.1.1").apply();
            mPref.edit().putString(Settings.DNSRESOLVER_KEY2,"1.0.0.1").apply();
            finish();
        }else if(r2.isChecked()){
            mPref.edit().putString(Settings.DNSRESOLVER_KEY1,"8.8.8.8").apply();
            mPref.edit().putString(Settings.DNSRESOLVER_KEY2,"8.8.4.4").apply();
            finish();
        }else if(r3.isChecked()){
            if(e1.getText().toString().isEmpty()||e2.getText().toString().isEmpty()||
               e3.getText().toString().isEmpty()||e4.getText().toString().isEmpty()){
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.spk_snackbar,(ViewGroup) findViewById(R.id.rootLayout));
                TextView txt1 = (TextView) layout.findViewById(R.id.toastxt);
                TextView txt2 = (TextView)layout.findViewById(R.id.btntoastxt);
                txt1.setText("Invalid format In custom DNS");
                txt2.setText("");
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }else{
                PUT_CUSTOM_KEY1 = e1.getText().toString()+"."+e2.getText().toString()+"."+
                    e3.getText().toString()+"."+e4.getText().toString();
                PUT_CUSTOM_KEY2 = e5.getText().toString()+"."+e6.getText().toString()+"."+
                    e7.getText().toString()+"."+e8.getText().toString();
                mPref.edit().putBoolean(Settings.DNSFORWARD_KEY, true).apply();
                mPref.edit().putString(Settings.DNSTYPE_KEY,Settings.DNS_CUSTOM_KEY).apply();
                mPref.edit().putString(Settings.DNSRESOLVER_KEY1,PUT_CUSTOM_KEY1).apply();
                mPref.edit().putString(Settings.DNSRESOLVER_KEY2,PUT_CUSTOM_KEY2).apply();
                dnsString();
                finish();
            }
        }

    }

}
