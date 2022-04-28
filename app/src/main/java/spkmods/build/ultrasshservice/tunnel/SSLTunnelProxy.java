package spkmods.build.ultrasshservice.tunnel;

import android.content.SharedPreferences;
import com.trilead.ssh2.ProxyData;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import spkmods.build.ultrasshservice.logger.SkStatus;
import spkmods.build.spkmodsApplication;
import java.util.Arrays;

public class SSLTunnelProxy implements ProxyData
{
    class HandshakeTunnelCompletedListener implements HandshakeCompletedListener {
        private final String val$host;
        private final int val$port;
        private final SSLSocket val$sslSocket;

        HandshakeTunnelCompletedListener( String str, int i, SSLSocket sSLSocket) {
            this.val$host = str;
            this.val$port = i;
            this.val$sslSocket = sSLSocket;
        }

        public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
            //SkStatus.logInfo("SSL: Using cipher " + handshakeCompletedEvent.getSession().getCipherSuite());
            SkStatus.logInfo(new StringBuffer().append("SSL: Enabled protocols: <br>").append(Arrays.toString(val$sslSocket.getEnabledProtocols())).toString().replace("[", "").replace("]", "").replace(",", "<br>"));
            SkStatus.logInfo("SSL: Using protocol " + handshakeCompletedEvent.getSession().getProtocol());
            SkStatus.logInfo("SSL: Handshake finished");
        }
    }

    private String stunnelServer;
    private int stunnelPort = 443;
    private String stunnelHostSNI;

    private Socket mSocket;

    public SSLTunnelProxy(String server, int port, String hostSni) {
        this.stunnelServer = server;
        this.stunnelPort = port;
        this.stunnelHostSNI = hostSni;
    }

    @Override
    public Socket openConnection(String hostname, int port, int connectTimeout, int readTimeout) throws IOException
    {
        mSocket = SocketChannel.open().socket();
        mSocket.connect(new InetSocketAddress(stunnelServer, stunnelPort));

        if (mSocket.isConnected()) {
            mSocket = doSSLHandshake(hostname, stunnelHostSNI, port);
            mSocket.setKeepAlive(true);
            mSocket.setTcpNoDelay(true);
            SkStatus.logInfo("SSL KEEP ALIVE: " + mSocket.getKeepAlive());
            SkStatus.logInfo("SSL TCP DELAY: " + mSocket.getTcpNoDelay());
        }
        return mSocket;
    }

    private SSLSocket doSSLHandshake(String host, String sni, int port) throws IOException {
        try {
            TLSSocketFactory tsf = new TLSSocketFactory();
            SSLSocket socket = (SSLSocket) tsf.createSocket(host, port);
            try {
                socket.getClass().getMethod("setHostname", String.class).invoke(socket, sni);
                SkStatus.logInfo("Setting up SNI Host");
            } catch (Throwable e) {
                // ignore any error, we just can't set the hostname...
            }

            //socket.setEnabledProtocols(socket.getSupportedProtocols());
            socket.addHandshakeCompletedListener(new HandshakeTunnelCompletedListener(host, port, socket));
            SkStatus.logInfo("Starting SSL Handshake...");
            socket.startHandshake();
            return socket;
        } catch (Exception e) {
            IOException iOException = new IOException(new StringBuffer().append("Could not do SSL handshake: ").append(e).toString());
            throw iOException;
        }
    }

    @Override
    public void close()
    {
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch(IOException e) {}
    }

}

