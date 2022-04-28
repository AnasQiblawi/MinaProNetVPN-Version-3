package spkmods.build.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.json.JSONObject;
import spkmods.build.ultrasshservice.config.Settings;
import spkmods.build.util.AESCrypt;
import spkmods.build.util.Utils;
import static android.content.pm.PackageManager.GET_META_DATA;
import spkmods.build.util.ConfigUtil;
import vpn.minapronet.com.eg.R;


public abstract class BaseActivity extends AppCompatActivity{
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!(((String) getPackageManager().getApplicationLabel(getApplicationInfo())).equals("MinaProNet VPN") && getPackageName().equals("vpn.minapronet.com.eg"))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setTitle("OPPS APP MODIFIED");
            builder.setMessage("Please install the original application version from MinaProNet")
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Utils.exitAll(BaseActivity.this);
                    }
                });
            builder.show();
        }
		resetTitles();
	}
	
    protected JSONObject getJSONConfig2(Context context) throws Exception {
        String json = null;
        File file = new File(context.getFilesDir(), "Config.json");
        if (file.exists()) {
            String json_file = Utils.readStream(new FileInputStream(file));
            json = AESCrypt.decrypt(ConfigUtil.minaPass, json_file);
            // return new JSONObject(json);
        } else {
            InputStream inputStream = context.getAssets().open("config/config.json");
            json = AESCrypt.decrypt(ConfigUtil.minaPass, Utils.readStream(inputStream));
            // return new JSONObject(json);
        }
        return new JSONObject(json);
    }
	
    
	protected void resetTitles() {
		try {
			ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
			if (info.labelRes != 0) {
				setTitle(info.labelRes);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
