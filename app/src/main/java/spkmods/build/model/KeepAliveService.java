package spkmods.build.model;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import vpn.minapronet.com.eg.R;
import spkmods.build.ultrasshservice.logger.SkStatus;

public class KeepAliveService extends Service {

    public static final String NOTIFICATION_CHANNEL_ID = "sshtunnel";


    public static String a(String str) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder;
        Exception e;
        OutOfMemoryError e2;
        Throwable th;
        try {
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                httpURLConnection.setConnectTimeout(30000);
                httpURLConnection.setReadTimeout(30000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder2 = new StringBuilder();
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    stringBuilder2.append(readLine);
                    stringBuilder2.append("\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
                stringBuilder = new StringBuilder();
                stringBuilder.append(stringBuilder2.length() / 1024);
                stringBuilder.append("K - HTTP:");
                stringBuilder.append(str);
                Log.d("KeepAliveService", stringBuilder.toString());
                String stringBuilder3 = stringBuilder2.toString();
                if (httpURLConnection != null) {
                    try {
                        httpURLConnection.disconnect();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
                return stringBuilder3;
            } catch (Exception e4) {
                e = e4;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Error Parsing: ");
                stringBuilder.append(str);
                Log.d("KeepAliveService", stringBuilder.toString());
                e.printStackTrace();
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return null;
            } catch (OutOfMemoryError e5) {
                e2 = e5;
                try {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Error Parsing: ");
                    stringBuilder.append(str);
                    Log.d("KeepAliveService", stringBuilder.toString());
                    e2.printStackTrace();
                    if (httpURLConnection != null) {
                        try {
                            httpURLConnection.disconnect();
                        } catch (Exception e32) {
                            e32.printStackTrace();
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (httpURLConnection != null) {
                        try {
                            httpURLConnection.disconnect();
                        } catch (Exception e6) {
                            e6.printStackTrace();
                        }
                    }
                    throw th;
                }
            }
        } catch (Exception e7) {
            e = e7;
            httpURLConnection = null;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Error Parsing: ");
            stringBuilder.append(str);
            Log.d("KeepAliveService", stringBuilder.toString());
            e.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return null;
        } catch (OutOfMemoryError e8) {
            e2 = e8;
            httpURLConnection = null;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Error Parsing: ");
            stringBuilder.append(str);
            Log.d("KeepAliveService", stringBuilder.toString());
            e2.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            httpURLConnection = null;
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

        }
        return null;
    }

    private void a(int i, Intent intent) {
        intent.setAction("com.nstudio.keepalive.action.UPDATE");
        ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) i), PendingIntent.getService(this, 42, intent, 134217728));
    }

    private void a(Intent intent) {
        intent.setAction("com.nstudio.keepalive.action.UPDATE");
        ((AlarmManager) getSystemService("alarm")).cancel(PendingIntent.getService(this, 42, intent, 134217728));
    }

    private void b(Intent intent) {
        new Thread(new Runnable() {                     
                public void run() {
                    String a = KeepAliveService.a("https://www.graniteapps.net/ping");
                    String str = "Connected";
                    if (a == null) {
                        str = "Not Connected";
                    } else if (!a.contains("ed443caad83f10c8c03d5c3fd94d21128a190c353e46e04f9e63390119e3246d")) {
                        str = "Restricted";
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Keep Alive: ");
                    stringBuilder.append(str);
                    showNotification(stringBuilder.toString());
                    Log.d("KeepAliveService", stringBuilder.toString());
                    SkStatus.logInfo(stringBuilder.toString());
                }
            }).start();
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onDestroy() {
        Log.d("KeepAliveService", "service destroyed, releasing resources");
        stopForeground(true);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("action - ");
            stringBuilder.append(intent.getAction());
            Log.d("KeepAliveService", stringBuilder.toString());
            if ("com.nstudio.keepalive.action.START".equals(intent.getAction())) {
                showNotification("Keep Alive Connecting");
                i2 = 1000;
            } else if ("com.nstudio.keepalive.action.UPDATE".equals(intent.getAction())) {
                b(intent);
                i2 = 60000;
            } else if ("com.nstudio.keepalive.action.CLOSE".equals(intent.getAction())) {
                a(intent);
                stopSelf();
                stopForeground(true);
            }
            a(i2, intent);
        }
        return 2;
    }

    private void showNotification(String str) {
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder =  null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel(nm);
            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(nm);
            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
        } else if (Build.VERSION.SDK_INT >= 28) {
            createNotificationChannel(nm);
            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle("KeepAlive Service");
        builder.setContentText(str);
        builder.setSmallIcon(R.drawable.ic_connection_icon);
        Notification notif = builder.getNotification();
        nm.notify(34, notif);
        startForeground(34, notif);
    }

    private void createNotificationChannel(NotificationManager nm) {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "HPIService Notification",NotificationManager.IMPORTANCE_DEFAULT);
        channel.setShowBadge(true);
        channel.setDescription("Notification showing if the Service is still running");
        nm.createNotificationChannel(channel);
    }
}
