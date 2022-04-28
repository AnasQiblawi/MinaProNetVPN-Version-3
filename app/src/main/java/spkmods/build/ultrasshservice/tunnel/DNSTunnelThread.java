package spkmods.build.ultrasshservice.tunnel;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import spkmods.build.ultrasshservice.config.Settings;
import spkmods.build.ultrasshservice.logger.SkStatus;
import spkmods.build.ultrasshservice.tunnel.vpn.VpnUtils;
import spkmods.build.ultrasshservice.util.CustomNativeLoader;
import spkmods.build.ultrasshservice.util.StreamGobbler;

public class DNSTunnelThread extends Thread
 {

	private Context mContext;
	private static final String DNS_BIN = "libdns";
	private Process dnsProcess;
	private File filedns;
	private Settings mConfig;

	public DNSTunnelThread(Context context) {
		mContext = context;
		mConfig = new Settings(context);

	}

	@Override
	public void run(){
		try {

			String mDns = mConfig.getPrivString(Settings.DNS_KEY);
            String chave = mConfig.getPrivString(Settings.CHAVE_KEY);
			String nameserver = mConfig.getPrivString(Settings.NAMESERVER_KEY);
			StringBuilder cmd1 = new StringBuilder();
			filedns = CustomNativeLoader.loadNativeBinary(mContext, DNS_BIN, new File(mContext.getFilesDir(),DNS_BIN));

			if (filedns == null){
				throw new IOException("DNS bin not found");
			}

			cmd1.append(filedns.getCanonicalPath());
			cmd1.append(" -udp "+ mDns + ":53   -pubkey "+ chave + " " + nameserver + " 127.0.0.1:2222");
			dnsProcess = Runtime.getRuntime().exec(cmd1.toString());

			StreamGobbler.OnLineListener onLineListener = new StreamGobbler.OnLineListener(){
				@Override
				public void onLine(String log){
				//	SkStatus.logInfo("<b>DNS Client: </b>" + log);
				}
			};
			StreamGobbler stdoutGobbler = new StreamGobbler(dnsProcess.getInputStream(), onLineListener);
			StreamGobbler stderrGobbler = new StreamGobbler(dnsProcess.getErrorStream(), onLineListener);

			stdoutGobbler.start();
			stderrGobbler.start();

			dnsProcess.waitFor();		
		} catch (IOException e) {
			//SkStatus.logInfo("SlowDNS: " + e);
		}catch (InterruptedException e){
			//SkStatus.logInfo("SlowDNS: " + e);
		}

	}

	@Override
	public void interrupt(){
		if (dnsProcess != null)
			dnsProcess.destroy();
		try {
			if (filedns != null)
				VpnUtils.killProcess(filedns);
		} catch (Exception e) {}

		dnsProcess = null;
		filedns = null;
		super.interrupt();
	}

}
