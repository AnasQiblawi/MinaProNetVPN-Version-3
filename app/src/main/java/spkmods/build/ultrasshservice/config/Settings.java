package spkmods.build.ultrasshservice.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import spkmods.build.ultrasshservice.util.securepreferences.SecurePreferences;
import spkmods.build.ultrasshservice.util.securepreferences.model.SecurityConfig;

/**
 * Configurações
 */

public class Settings implements SettingsConstants
{
	private Context mContext;
	private SharedPreferences mPrefs;
	private SecurePreferences mPrefsPrivate;

	private static SecurityConfig minimumConfig = new SecurityConfig.Builder("fubgf777gf6")
	.build();

	public Settings(Context context) {
		mContext = context;

		mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mPrefsPrivate = SecurePreferences.getInstance(mContext, "SecureData", minimumConfig);
	}


	public String getPrivString(String key) {
		String defaultStr = "";

		switch (key) {
			case PORTA_LOCAL_KEY:
				defaultStr = "1080";
				break;
		}

		return mPrefsPrivate.getString(key, defaultStr);
	}

	public SecurePreferences getPrefsPrivate() {
		return mPrefsPrivate;
	}


	/**
	 * Config File
	 */

	public String getMensagemConfigExportar() {
		return mPrefs.getString(CONFIG_MENSAGEM_EXPORTAR_KEY, "");
	}

	public void setMensagemConfigExportar(String str) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(CONFIG_MENSAGEM_EXPORTAR_KEY, str);
		editor.commit();
	}


	/**
	 * Geral
	 */
	public boolean getBypass(){
		return mPrefs.getBoolean("DNS_BYPASS", false);
	}

	public void setBypass(boolean use){
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean("DNS_BYPASS", use);
		editor.commit();
	}
	

	public boolean getModoDebug() {
		return mPrefs.getBoolean(MODO_DEBUG_KEY, false);
	}

	public void setModoDebug(boolean is) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(MODO_DEBUG_KEY, is);
		editor.commit();
	}

	public int getMaximoThreadsSocks() {
		String n = mPrefs.getString(MAXIMO_THREADS_KEY, "0th");
		if (n == null || n.isEmpty()) {
			n = "0th";
		}
		return Integer.parseInt(n.replace("th", ""));
	}
	
	public boolean ssh_compression() {
		return mPrefs.getBoolean(SSH_COMPRESSION, true);
	}
    
    public boolean getWakelock() {
        return mPrefs.getBoolean(WAKELOCK_KEY, true);
	}
	
	public boolean setAutoPing() {
		return mPrefs.getBoolean(AUTO_PINGER, true);
	}

	public boolean getHideLog() {
		return mPrefs.getBoolean(HIDE_LOG_KEY, false);
	}

	public boolean getAutoClearLog() {
		return mPrefs.getBoolean(AUTO_CLEAR_LOGS_KEY, false);
	}

	public boolean getIsFilterApps() {
		return mPrefs.getBoolean(FILTER_APPS, false);
	}

	public boolean getIsFilterBypassMode() {
		return mPrefs.getBoolean(FILTER_BYPASS_MODE, false);
	}

	public String[] getFilterApps() {
		String txt = mPrefs.getString(FILTER_APPS_LIST, "");
		if (txt.isEmpty()) {
			return new String[]{};
		}
		else {
			return txt.split("\n");
		}
	}

	public boolean getIsTetheringSubnet() {
		return mPrefs.getBoolean(TETHERING_SUBNET, false);
	}

	public boolean getIsDisabledDelaySSH() {
		return mPrefs.getBoolean(DISABLE_DELAY_KEY, false);
	}


	/**
	 * Vpn Settings
	 */

	public boolean getVpnDnsForward(){
		return mPrefs.getBoolean(DNSFORWARD_KEY, true);
	}
	
	public void setVpnDnsForward(boolean use){
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(DNSFORWARD_KEY, use);
		editor.commit();
	}
	
	public String getVpnDnsResolver2(){
		return mPrefs.getString(DNSRESOLVER_KEY2, "8.8.4.4");
	}
	public String getVpnDnsResolver1(){
		return mPrefs.getString(DNSRESOLVER_KEY1, "8.8.8.8");
	}
	public void setVpnDnsResolver2(String str) {
		if (str == null || str.isEmpty()) {
			str = "8.8.4.4";
		}
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(DNSRESOLVER_KEY1, str);
		editor.commit();
	}
	public void setVpnDnsResolver1(String str) {
		if (str == null || str.isEmpty()) {
			str = "8.8.8.8";
		}
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(DNSRESOLVER_KEY2, str);
		editor.commit();
	}
	public boolean getVpnUdpForward(){
		return mPrefs.getBoolean(UDPFORWARD_KEY, false);
	}
	
	public void setVpnUdpForward(boolean use){
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(UDPFORWARD_KEY, use);
		editor.commit();
	}

	public String getVpnUdpResolver(){
		return mPrefs.getString(UDPRESOLVER_KEY, "127.0.0.1:7300");
	}
	
	public void setVpnUdpResolver(String str) {
		if (str == null || str.isEmpty()) {
			str = "127.0.0.1:7300";
		}
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(UDPRESOLVER_KEY, str);
		editor.commit();
	}
	

	/**
	 * SSH Settings
	 */

	public String getSSHKeypath() {
		return mPrefs.getString(KEYPATH_KEY, "");
	}

	public String setPinger() {
        return mPrefs.getString(PINGER, "");

	}


	/**
	 * Utils
	 */

	public static void setDefaultConfig(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putBoolean(DNSFORWARD_KEY, true);
		editor.putString(DNSRESOLVER_KEY1, "8.8.4.4");
		editor.putString(DNSRESOLVER_KEY2, "8.8.8.8");
		editor.putBoolean(UDPFORWARD_KEY, true);
		editor.putString(UDPRESOLVER_KEY, "127.0.0.1:7300");
		editor.putString(PINGER, "clients3.google.com");
		editor.putString(MAXIMO_THREADS_KEY, "0th");
		editor.putBoolean(AUTO_CLEAR_LOGS_KEY, true);
		editor.putBoolean(HIDE_LOG_KEY, false);
		editor.putBoolean(SSH_COMPRESSION, true);
        editor.putBoolean(WAKELOCK_KEY, true);
		editor.putBoolean(AUTO_PINGER, true);
		editor.putBoolean(VIBRATE, true);
		editor.remove(MODO_DEBUG_KEY);
		editor.remove(FILTER_APPS);
		editor.remove(FILTER_BYPASS_MODE);
		editor.remove(FILTER_APPS_LIST);
		editor.remove(TETHERING_SUBNET);
		editor.remove(DISABLE_DELAY_KEY);

		editor.commit();
	}

	public static void clearSettings(Context context) {
		SharedPreferences priv = SecurePreferences.getInstance(context, "SecureData", minimumConfig);
		SharedPreferences.Editor edit = priv.edit();
		edit.clear();
		edit.commit();
	}

}
