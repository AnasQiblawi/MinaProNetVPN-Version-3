package spkmods.build.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import vpn.minapronet.com.eg.R;
import spkmods.build.ultrasshservice.config.Settings;
import spkmods.build.ultrasshservice.util.securepreferences.SecurePreferences;
import spkmods.build.spkmodsApplication;
public class AccountDialog
{
	TextView textView;
    private AlertDialog.Builder adb;
    private Settings mConfig;
    private SharedPreferences sp;
    
    public AccountDialog(Context context)
    {
        sp = new Settings(context).getPrefsPrivate();
        Context context2 = context;
        Settings settings = new Settings(context2);
        this.mConfig = settings;
        final SecurePreferences prefsPrivate = this.mConfig.getPrefsPrivate();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflate = inflater.inflate(R.layout.dialog_account, (ViewGroup) null);
        final EditText userN = (EditText) inflate.findViewById(R.id.edUsername);
        userN.setText(prefsPrivate.getString("user", ""));
        final EditText userP = (EditText) inflate.findViewById(R.id.edPassword);
        userP.setText(prefsPrivate.getString("pass", ""));
		textView = inflate.findViewById(R.id.textViewLink);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
        adb = new AlertDialog.Builder(context);
        adb.setTitle("VIP Login");
        //adb.setMessage("Select VPN Mode");
        adb.setView(inflate, 40, 0, 40, 0);
		userN.setText(prefsPrivate.getString("user", ""));
		userP.setText(prefsPrivate.getString("pass", ""));
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2)
				{
                   /* conts.setUsername(userN.getText().toString());
                    conts.setPassword(userP.getText().toString());*/
                    prefsPrivate.edit().putString("user", userN.getText().toString()).commit();
                    prefsPrivate.edit().putString("pass", userP.getText().toString()).commit();
                }
           });
        adb.setNeutralButton("RESET", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                 /*   userN.setText("");
                    userP.setText("");
                   /* sp.edit().putString("user", "").commit();
                   /* sp.edit().putString("pass", "").commit();*/
                    sp.edit().putString("user", "").clear().commit();
                    sp.edit().putString("pass", "").clear().commit();
                }
            });
        adb.setNegativeButton("CANCEL", null);
    }

    public void show()
    {
        adb.create().show();
    }
}
