package spkmods.build.preference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import vpn.minapronet.com.eg.R;
import spkmods.build.spkmodsApplication;
import spkmods.build.MainActivity;
import spkmods.build.ultrasshservice.config.Settings;
import spkmods.build.ultrasshservice.config.SettingsConstants;
import spkmods.build.ultrasshservice.logger.ConnectionStatus;
import spkmods.build.ultrasshservice.logger.SkStatus;
import spkmods.build.util.Utils;
import spkmods.build.activities.BaseActivity;
import spkmods.build.activities.CustomDNS;

public class SettingsPreference extends PreferenceFragmentCompat
implements Preference.OnPreferenceChangeListener, SettingsConstants,
SkStatus.StateListener
{
    private Handler mHandler;
    private SharedPreferences mPref;

    private String[] settings_disabled_keys = {
        DNSFORWARD_KEY,
        UDPFORWARD_KEY,
        UDPRESOLVER_KEY,
        AUTO_PINGER,
        PINGER,
        AUTO_CLEAR_LOGS_KEY,
        HIDE_LOG_KEY,
        SSH_COMPRESSION,
        VIBRATE,
        WAKELOCK_KEY
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SkStatus.addStateListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        SkStatus.removeStateListener(this);
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String root_key)
    {
        // Load the Preferences from the XML file
        setPreferencesFromResource(R.xml.app_preferences, root_key);

        mPref = getPreferenceManager().getDefaultSharedPreferences(getContext());

        Preference udpForwardPreference = (SwitchPreferenceCompat)findPreference(UDPFORWARD_KEY);
        udpForwardPreference.setOnPreferenceChangeListener(this);

        Preference dnsForwardPreference = (SwitchPreferenceCompat)findPreference(DNSFORWARD_KEY);
        dnsForwardPreference.setOnPreferenceChangeListener(this);

        // update view
        setRunningTunnel(SkStatus.isTunnelActive());
    }

    private void onChangeUseVpn(boolean use_vpn){
        Preference udpResolverPreference = (EditTextPreference)findPreference(UDPRESOLVER_KEY);
        
        for (String key : settings_disabled_keys){
            getPreferenceManager().findPreference(key).setEnabled(use_vpn);
        }

        use_vpn = true;
        if (use_vpn) {
            boolean isUdpForward = mPref.getBoolean(UDPFORWARD_KEY, true);
            udpResolverPreference.setEnabled(isUdpForward);
        }
        else {
            String[] list = {
                UDPFORWARD_KEY,
                UDPRESOLVER_KEY,
                DNSFORWARD_KEY
            };
            for (String key : list) {
                getPreferenceManager().findPreference(key).setEnabled(false);
            }
        }
    }

    private void setRunningTunnel(boolean isRunning) {
        if (isRunning) {
            for (String key : settings_disabled_keys){
                getPreferenceManager().findPreference(key).setEnabled(false);
            }
        } else {
            onChangeUseVpn(true);
        }
    }


    /**
     * Preference.OnPreferenceChangeListener
     * Implementação
     */

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue)
    {
        switch (pref.getKey()) {
            case UDPFORWARD_KEY:
                boolean isUdpForward = (boolean) newValue;
                Preference udpResolverPreference = (EditTextPreference)findPreference(UDPRESOLVER_KEY);
                udpResolverPreference.setEnabled(isUdpForward);
                break;

            case DNSFORWARD_KEY:
				boolean isDnsForward = (boolean) newValue;
				if(isDnsForward==true){
					mPref.edit().putBoolean("Google_dns",true).apply();
					Intent TunnDNS = new Intent(getActivity(), CustomDNS.class);
					getActivity().startActivity(TunnDNS);
				}
			break;

        }
        return true;
    }

    @Override
    public void updateState(String state, String logMessage, int localizedResId, ConnectionStatus level, Intent intent)
    {
        mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setRunningTunnel(SkStatus.isTunnelActive());
                }
            });
    }
}

