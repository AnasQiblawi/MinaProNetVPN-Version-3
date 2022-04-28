package spkmods.build.ultrasshservice.tunnel;

import android.content.Context;
import com.trilead.ssh2.ProxyData;
import com.trilead.ssh2.crypto.Base64;
import com.trilead.ssh2.transport.ClientServerHello;
import com.trilead.ssh2.transport.TransportManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import vpn.minapronet.com.eg.R;
import spkmods.build.ultrasshservice.logger.SkStatus;

/**
 * By Skank3r
 */
public class HttpProxyCustom
implements ProxyData
{

    private final String proxyHost;
    private final String proxyPass;
    private final int proxyPort;
    private final String proxyUser;
    private final String requestPayload;
    private boolean modoDropbear = false;

    private Socket sock;
    private Context mContext;

    public HttpProxyCustom(String proxyHost, int proxyPort, Context context) {
        this(proxyHost, proxyPort, null, null, context);
    }

    public HttpProxyCustom(String proxyHost, int proxyPort, String proxyUser, String proxyPass, Context context) {
        this(proxyHost, proxyPort, proxyUser, proxyPass, null, false, context);
    }

    public HttpProxyCustom(String proxyHost, int proxyPort, String proxyUser, String proxyPass, String requestPayload, boolean modoDropbear, Context context) {
        if (proxyHost == null) {
            throw new IllegalArgumentException("proxyHost must be non-null");
        } else if (proxyPort < 0) {
            throw new IllegalArgumentException("proxyPort must be non-negative");
        } else {
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
            this.proxyUser = proxyUser;
            this.proxyPass = proxyPass;
            this.requestPayload = requestPayload;
            this.modoDropbear = modoDropbear;
            this.mContext = context;
        }
    }

    @Override
    public Socket openConnection(String hostname, int port, int connectTimeout, int readTimeout) throws IOException {
        sock = new Socket();

        InetAddress addr = TransportManager.createInetAddress(this.proxyHost);
        sock.connect(new InetSocketAddress(addr, this.proxyPort), connectTimeout);
        sock.setSoTimeout(readTimeout);

        SkStatus.logInfo(R.string.state_proxy_running);

        String requestPayload = getRequestPayload(hostname, port);

        // anti vpn sniffer
        if (TunnelUtils.isActiveVpn(mContext)) {
            SkStatus.logInfo("<strong>" + mContext.getString(R.string.error_vpn_sniffer_detected) + "</strong>");

            throw new IOException("error detected");
        }

        SkStatus.logInfo(R.string.state_proxy_inject);

        OutputStream out = sock.getOutputStream();

        // suporte a [split] na payload
        if (!TunnelUtils.injectSplitPayload(requestPayload, out)) {
            try {
                out.write(requestPayload.getBytes("ISO-8859-1"));
            } catch (UnsupportedEncodingException e2) {
                out.write(requestPayload.getBytes());
            }
            out.flush();
        }

        // suporta Dropbear (SSH + PAYLOAD)
        if (modoDropbear) {
            return sock;
        }

        byte[] buffer = new byte[1024];
        InputStream in = sock.getInputStream();

        int len = ClientServerHello.readLineRN(in, buffer);

        String httpReponseFirstLine = "";
        try {
            httpReponseFirstLine = new String(buffer, 0, len, "ISO-8859-1");
        } catch (UnsupportedEncodingException e3) {
            httpReponseFirstLine = new String(buffer, 0, len);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<strong>");
        sb.append(httpReponseFirstLine);
        sb.append("</strong>");
        SkStatus.logInfo(sb.toString());
        len = Integer.parseInt(httpReponseFirstLine.substring(9, 12));
        if (len != 200) {
            String asw = String.valueOf(len);
            String replacw = httpReponseFirstLine.replace(httpReponseFirstLine, "HTTP/1.1 200 Ok");
            SkStatus.logInfo("Proxy: Auto Replace Header");
            StringBuilder su = new StringBuilder();
            su.append("<strong>");
            su.append(replacw);
            su.append("</strong>");
            //Status.logInfo(su.toString());
            len = Integer.parseInt(asw.replace(asw, "200"));
        } else {
            return sock;
        }

        // lê o restante
        String httpReponseAll = httpReponseFirstLine;
        while ((len = ClientServerHello.readLineRN(in, buffer)) != 0) {
            httpReponseAll += "\n";
            try {
                httpReponseAll += new String(buffer, 0, len, "ISO-8859-1");
            } catch (UnsupportedEncodingException e3) {
                httpReponseAll += new String(buffer, 0, len);
            }
        }

        if (!httpReponseAll.isEmpty())
            SkStatus.logDebug(httpReponseAll);

        if (httpReponseFirstLine.startsWith("HTTP/") == false) throw new IOException("The proxy did not send back a valid HTTP response.");
        if (httpReponseFirstLine.length() < 14) throw new IOException("The proxy did not send back a valid HTTP response.");
        if (httpReponseFirstLine.charAt(8) != ' ') throw new IOException("The proxy did not send back a valid HTTP response.");
        if (httpReponseFirstLine.charAt(12) != ' ') throw new IOException("The proxy did not send back a valid HTTP response.");
        if (len < 0 || len > 999) {
            throw new IOException("The proxy did not send back a valid HTTP response.");
        } else if (len != 200) {
            String stringBuffer = new StringBuffer().append(new StringBuffer().append("HTTP/1.0 200 Connection established").append("\r\n").toString()).append("\r\n").toString();
            out.write(stringBuffer.getBytes());
            out.flush();
            SkStatus.logInfo(stringBuffer.toString());
            return sock;
        } else {
            return sock;
        }
    }

    private String getRequestPayload(String hostname, int port) {
        String payload = this.requestPayload;

        if (payload != null) {
            payload = TunnelUtils.formatCustomPayload(hostname, port, payload);
        }
        else {
            StringBuffer sb = new StringBuffer();

            sb.append("CONNECT ");
            sb.append(hostname);
            sb.append(':');
            sb.append(port);
            sb.append(" HTTP/1.0\r\n");
            if (!(this.proxyUser == null || this.proxyPass == null)) {
                char[] encoded;
                String credentials = this.proxyUser + ":" + this.proxyPass;
                try {
                    encoded = Base64.encode(credentials.getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    encoded = Base64.encode(credentials.getBytes());
                }
                sb.append("Proxy-Authorization: Basic ");
                sb.append(encoded);
                sb.append("\r\n");
            }
            sb.append("\r\n");

            payload = sb.toString();
        }

        return payload;
    }

    @Override
    public void close()
    {
        if (sock == null) return;

        try {
            sock.close();
        } catch (IOException e) { /* failed */ }
    }

}

