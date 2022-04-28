package spkmods.build;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import spkmods.build.activities.CoinsActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.pedant.SweetAlert.widget.SweetAlertDialog;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sdsmdg.tastytoast.TastyToast;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vpn.minapronet.com.eg.R;
import spkmods.build.Graph.DataTransferGraph2;
import spkmods.build.Graph.GraphHelper;
import spkmods.build.Graph.RetrieveData;
import spkmods.build.Graph.StoredData;
import spkmods.build.activities.AboutActivity;
import spkmods.build.activities.BaseActivity;
import spkmods.build.activities.ConfigGeralActivity;
import spkmods.build.adapter.LogsAdapter;
import spkmods.build.adapter.PromoAdapter;
import spkmods.build.adapter.SpinnerAdapter;
import spkmods.build.ultrasshservice.SocksHttpService;
import spkmods.build.ultrasshservice.config.Settings;
import spkmods.build.ultrasshservice.logger.ConnectionStatus;
import spkmods.build.ultrasshservice.logger.SkStatus;
import spkmods.build.ultrasshservice.tunnel.TunnelManagerHelper;
import spkmods.build.ultrasshservice.tunnel.TunnelUtils;
import spkmods.build.util.AESCrypt;
import spkmods.build.util.ConfigUpdate;
import spkmods.build.util.ConfigUtil;
import spkmods.build.util.GoogleFeedbackUtils;
import spkmods.build.util.Utils;
import spkmods.build.view.AccountDialog;
import spkmods.build.view.MaterialButton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.justicecoder.jcsecurity.JcProtect;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends BaseActivity implements View.OnClickListener, SkStatus.StateListener {

    private static final String UPDATE_VIEWS = "MainUpdate";
    public static final String OPEN_LOGS = "spkmods.build:openLogs";
    public static ArrayList<HashMap<String, String>> SerList =
    new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> NetList =
    new ArrayList<HashMap<String, String>>();
    public static FilePickerDialog filedialog;

    private Settings mConfig;
    private Handler mHandler;
    private LinearLayout mainLayout;
    private MaterialButton starterButton;
    public static final String app_name = new String(new byte[] {77, 105, 110, 97, 80, 114, 111, 78, 101, 116, 32, 86, 80, 78});
    public static final String pckg = new String(new byte[]{118,112,110,46,109,105,110,97,112,114,111,110,101,116,46,99,111,109,46,101,103});

    private static final String[] tabTitle = {"HOME", "LOG"};
    private ConfigUtil config;
    private Spinner serverSpinner;
    private Spinner payloadSpinner;
    private SpinnerAdapter serverAdapter;
    private PromoAdapter payloadAdapter;
    private ArrayList<JSONObject> serverList;
    private ArrayList<JSONObject> payloadList;
    private Toolbar toolbar_main;
    private DrawerPanelMain mDrawerPanel;
    private SweetAlertDialog SpkAlert;
    private TextView connectionStatus;

    private Thread dataThread;
    public static View graph_flip;
    public static View image_flip;
    private LineChart mChart;
    private GraphHelper graph;
    private Handler fHandler = new Handler();
    private Thread dataUpdate;
    private FloatingActionButton deleteLogs;
    private static final int START_VPN_PROFILE = 2002;

    private LogsAdapter mLogAdapter;
    private RecyclerView logList;
    private ViewPager vp;
    private TabLayout tabs;
    private SharedPreferences sp;
    private static final String TAG = "MainActivity";
    private int PICK_FILE;

    private InterstitialAd interstitialAd;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long saved_ads_time;
    private boolean mTimerEnabled;
    private MaterialButton btnAddTime;
    private TextView tvTimeRemain;
    private AdView adsBannerView;
    public static String isUserVip = "vKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        antiRemod1();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        mHandler = new Handler();
        mConfig = new Settings(this);
        mDrawerPanel = new DrawerPanelMain(this);

        SharedPreferences mPref = getSharedPreferences(spkmodsApplication.PREFS_GERAL, Context.MODE_PRIVATE);
        boolean showFirstTime = mPref.getBoolean("connect_first_time", true);
        if (showFirstTime) {
            SharedPreferences.Editor pEdit = mPref.edit();
            pEdit.putBoolean("connect_first_time", false);
            pEdit.apply();
            Settings.setDefaultConfig(this);
        }
        // set layout
        doLayout();

        // recebe local dados
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_VIEWS);
        filter.addAction(OPEN_LOGS);

        LocalBroadcastManager.getInstance(this).registerReceiver(mActivityReceiver, filter);
        doUpdateLayout();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(
                    @NonNull InitializationStatus initializationStatus) {}
            });
        loadAd();
        if (!StoredData.isSetData) {
            StoredData.setZero();
        }
        graph.start();
        liveData();
    }

    private void doLayout() {
        setContentView(R.layout.activity_main_drawer);
        mChart = (LineChart) findViewById(R.id.chart1);
        graph = GraphHelper.getHelper().with(this).color(Color.parseColor(getString(R.color.colorPrimary))).chart(mChart);
        this.sp = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
        mDrawerPanel.setDrawer(toolbar_main);
        setSupportActionBar(toolbar_main);

        doTabs();
        config = new ConfigUtil(this);

        // set ADS
        adsBannerView = (AdView) findViewById(R.id.adView1);
        if (TunnelUtils.isNetworkOnline(MainActivity.this)) {
            adsBannerView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (adsBannerView != null) {
                            adsBannerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            adsBannerView.loadAd(new AdRequest.Builder().build());
        }
        showInterstitial();
        serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
        payloadSpinner = (Spinner) findViewById(R.id.payloadSpinner);
        serverList = new ArrayList<>();
        payloadList = new ArrayList<>();

        serverAdapter = new SpinnerAdapter(this, R.id.serverSpinner, serverList);
        payloadAdapter = new PromoAdapter(this, R.id.payloadSpinner, payloadList);
        serverSpinner.setAdapter(serverAdapter);
        payloadSpinner.setAdapter(payloadAdapter);

        loadServer();
        loadNetworks();
        updateConfig(true);

        mainLayout = (LinearLayout) findViewById(R.id.activity_mainLinearLayout);
        starterButton = (MaterialButton) findViewById(R.id.activity_starterButtonMain);
        connectionStatus = (TextView) findViewById(R.id.connection_status);
        starterButton.setOnClickListener(this);

        tvTimeRemain = findViewById(R.id.tvTimeRemain);
        btnAddTime = findViewById(R.id.btnAddTime);
        btnAddTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,CoinsActivity.class));
                }
            });
    }

    public void doTabs() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLogAdapter = new LogsAdapter(layoutManager, this);
        deleteLogs = (FloatingActionButton) findViewById(R.id.delete_log);
        logList = (RecyclerView) findViewById(R.id.recyclerLog);
        logList.setAdapter(mLogAdapter);
        logList.setLayoutManager(layoutManager);
        mLogAdapter.scrollToLastPosition();
        deleteLogs.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View p1) {
                        mLogAdapter.clearLog();
                        //    SkStatus.logInfo("<font color='red'>Log Cleared!</font>");
                        // TODO: Implement this method
                    }
                });
        vp = (ViewPager) findViewById(R.id.viewpager);
        tabs = (TabLayout) findViewById(R.id.tablayout);
        vp.setAdapter(new MyAdapter(Arrays.asList(tabTitle)));
        vp.setOffscreenPageLimit(2);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setupWithViewPager(vp);
    }

    public class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            // TODO: Implement this method
            return 2;
        }

        @Override
        public boolean isViewFromObject(View p1, Object p2) {
            // TODO: Implement this method
            return p1 == p2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int[] ids = new int[] {R.id.tab1, R.id.tab2};
            int id = 0;
            id = ids[position];
            // TODO: Implement this method
            return findViewById(id);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO: Implement this method
            return titles.get(position);
        }

        private List<String> titles;
        public MyAdapter(List<String> str) {
            titles = str;
        }
    }

    public class DrawerPanelMain implements NavigationView.OnNavigationItemSelectedListener {

        private AppCompatActivity mActivity;
        private SharedPreferences sp;
        public DrawerPanelMain(AppCompatActivity activity) {
            mActivity = activity;
        }

        private DrawerLayout drawerLayout;
        private ActionBarDrawerToggle toggle;

        public void setDrawer(Toolbar toolbar) {
            this.sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            NavigationView drawerNavigationView =
                (NavigationView) mActivity.findViewById(R.id.drawerNavigationView);
            drawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawerLayoutMain);
            // set drawer
            toggle =
                new ActionBarDrawerToggle(
                mActivity, drawerLayout, toolbar, R.string.open, R.string.cancel);
            drawerLayout.setDrawerListener(toggle);
            toggle.syncState();

            // set app info
            PackageInfo pinfo = Utils.getAppInfo(mActivity);
            if (pinfo != null) {
                String version_nome = pinfo.versionName;
                int version_code = pinfo.versionCode;
                String header_text = String.format("v. %s (%d)", version_nome, version_code);

                View view = drawerNavigationView.getHeaderView(0);
                TextView app_info_text = view.findViewById(R.id.nav_headerAppVersion);
                app_info_text.setText(header_text);
            }
            addSwitch(
                drawerNavigationView,
                R.id.login,
                "login",
                false,
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Toast.makeText(
                                MainActivity.this,
                                "VIP Login Enabled",
                                Toast.LENGTH_SHORT)
                                .show();
                        }
                        sp.edit().putBoolean("login", isChecked).commit();
                    }
                });
            // set navigation view
            drawerNavigationView.setNavigationItemSelectedListener(this);
        }

        public ActionBarDrawerToggle getToogle() {
            return toggle;
        }

        public DrawerLayout getDrawerLayout() {
            return drawerLayout;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            switch (id) {
                case R.id.login:
                    AccountDialog AccDialog = new AccountDialog(MainActivity.this);
                    AccDialog.show();
                    return true;

                case R.id.offlineUpdate:
                    offlineUpdate();
                    drawerLayout.closeDrawers();
                    break;

                case R.id.miPhoneConfg:
                    if (Build.VERSION.SDK_INT >= 30) {
                        Intent in = new Intent(Intent.ACTION_MAIN);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        in.setClassName(
                            "com.android.phone", "com.android.phone.settings.RadioInfo");
                        this.mActivity.startActivity(in);
                    } else {
                        Intent inTen = new Intent(Intent.ACTION_MAIN);
                        inTen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        inTen.setClassName(
                            "com.android.settings", "com.android.settings.RadioInfo");
                        this.mActivity.startActivity(inTen);
                    }
                    break;

                case R.id.miSettings:
                    Intent intent = new Intent(mActivity, ConfigGeralActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                    break;

                case R.id.buyAcc:
                    Intent inTe =
                        new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://telegram.me/MinaProNet"));
                    inTe.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(
                        Intent.createChooser(inTe, mActivity.getText(R.string.open_with)));
                    break;

                case R.id.tg:
                    String url = "https://telegram.me/MinaProNet";
                    Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(
                        Intent.createChooser(intent3, mActivity.getText(R.string.open_with)));
                    break;

                case R.id.tg_channel:
                    Intent intent4 =
                        new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/MinaProNetVPN"));
                    intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(
                        Intent.createChooser(intent4, mActivity.getText(R.string.open_with)));
                    break;

                case R.id.playStoreupdater:
                    Intent intply = new Intent(Intent.ACTION_VIEW);
                    // intply.setData(Uri.parse("https://play.google.com/store/apps/details?id=vpn.minapronet.com.eg"));
                    intply.setData(Uri.parse("market://details?id=vpn.minapronet.com.eg"));
                    mActivity.startActivity(intply);
                    break;

                case R.id.miSendFeedback:
                    if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        try {
                            GoogleFeedbackUtils.bindFeedback(mActivity);
                        } catch (Exception e) {
                            Toast.makeText(mActivity, "No Mail Installed", Toast.LENGTH_SHORT)
                                .show();
                            SkStatus.logDebug("Error: " + e.getMessage());
                        }
                    } else {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[] {"minapronet@gmail.com"});
                        email.putExtra(
                            Intent.EXTRA_SUBJECT,
                            "MinaProNet VPN - " + mActivity.getString(R.string.feedback));
                        // email.putExtra(Intent.EXTRA_TEXT, "");
                        // need this to prompts email client only
                        email.setType("message/rfc822");
                        mActivity.startActivity(
                            Intent.createChooser(email, "Choose an Email client:"));
                    }
                    break;

                case R.id.miAbout:
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawers();
                    }
                    Intent aboutIntent = new Intent(mActivity, AboutActivity.class);
                    mActivity.startActivity(aboutIntent);
                    break;
            }
            return true;
        }
    }

    private void addSwitch(
        NavigationView navView,
        int id,
        final String tag,
        boolean defval,
        CompoundButton.OnCheckedChangeListener listener) {
        MenuItem bleh = navView.getMenu().findItem(id);
        bleh.setActionView(new Switch(this));
        Switch dnsSwitch = (Switch) bleh.getActionView();
        dnsSwitch.setChecked(sp.getBoolean(tag, defval));
        dnsSwitch.setOnCheckedChangeListener(listener);
    }

    private void doUpdateLayout() {
        boolean isRunning = SkStatus.isTunnelActive();
        setStarterButton(starterButton, this);
        if (isRunning) {
            serverSpinner.setEnabled(false);
            payloadSpinner.setEnabled(false);
        } else {
            serverSpinner.setEnabled(true);
            payloadSpinner.setEnabled(true);
        }
    }
    // end

    private void setSpinner() {
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        int server = prefs.getInt("LastSelectedServer", 0);
        int payload = prefs.getInt("LastSelectedPayload", 0);
        serverSpinner.setSelection(server);
        payloadSpinner.setSelection(payload);
    }

    private void saveSpinner() {
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        SharedPreferences.Editor edit = prefs.edit();
        int server = serverSpinner.getSelectedItemPosition();
        int payload = payloadSpinner.getSelectedItemPosition();
        edit.putInt("LastSelectedServer", server);
        edit.putInt("LastSelectedPayload", payload);
        edit.apply();
    }

    /** Tunnel SSH */
    private void liveData() {
        dataUpdate =
            new Thread(
            new Runnable() {
                @Override
                public void run() {
                    while (!dataUpdate.getName().equals("stopped")) {
                        fHandler.post(
                            new Runnable() {
                                // private static final long xup = 0;
                                @Override
                                public void run() {
                                    if (toString().equals("Connected")) {
                                        graph.start();
                                    }
                                }
                            });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //  progressStatus--;
                    }
                }
            });
        dataUpdate.setName("started");
        dataUpdate.start();
    }

    final class MyThreadClass implements Runnable {
        @Override
        public void run() {
            int i = 0;
            synchronized (this) {
                while (dataThread.getName() == "showDataGraph") {
                    //  Log.e("insidebroadcast", Integer.toString(service_id) + " " +
                    // Integer.toString(i));
                    getData2();
                    try {
                        wait(1000);
                        i++;
                    } catch (InterruptedException e) {
                        // sshMsg(e.getMessage());
                    }
                }
                // stopSelf(service_id);
            }
        }
    }

    public void getData2() {
        List<Long> allData;
        allData = RetrieveData.findData();
        long mDownload = DataTransferGraph2.download;
        long mUpload = DataTransferGraph2.upload;
        mDownload = allData.get(0);
        mUpload = allData.get(1);
        storedData2(mUpload, mDownload);
    }

    public void storedData2(Long mUpload, Long mDownload) {
        StoredData.downloadSpeed = mDownload;
        StoredData.uploadSpeed = mUpload;
        if (StoredData.isSetData) {
            StoredData.downloadList.remove(0);
            StoredData.uploadList.remove(0);
            StoredData.downloadList.add(mDownload);
            StoredData.uploadList.add(mUpload);
        }
    }

    private void loadServerData() {
        try {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
            SharedPreferences.Editor edit = prefs.edit();
            int pos = payloadSpinner.getSelectedItemPosition();
            int pos1 = serverSpinner.getSelectedItemPosition();
            String isnt = config.getNetworksArray().getJSONObject(pos).getString("Payload").replace("[host]", config.getServersArray().getJSONObject(pos1).getString("ServerIP"));
            boolean directType =
                config.getNetworksArray().getJSONObject(pos).getBoolean("isDirect");
            boolean httpType = config.getNetworksArray().getJSONObject(pos).getBoolean("isHTTP");
            boolean sslType = config.getNetworksArray().getJSONObject(pos).getBoolean("isSSL");
            boolean sslPayType =
                config.getNetworksArray().getJSONObject(pos).getBoolean("isSSLPayload");
            boolean slowDnsType =
                config.getNetworksArray().getJSONObject(pos).getBoolean("isSlowDNS");

            if (directType) {
                edit.putString(
                    Settings.SERVIDOR_KEY,
                    config.getServersArray().getJSONObject(pos1).getString("ServerIP"))
                    .apply();
                edit.putString(
                    Settings.SERVIDOR_PORTA_KEY,
                    config.getServersArray()
                    .getJSONObject(pos1)
                    .getString("ServerPort"))
                    .commit();
                edit.putString(
                    Settings.CUSTOM_PAYLOAD_KEY,
                    config.getNetworksArray().getJSONObject(pos).getString("Payload"))
                    .commit();
                /*   edit.putString(Settings.PROXY_PORTA_KEY, jSONObject.getString("httpPort")).commit();
                 //   edit.putString(Settings.PROXY_IP_KEY, jSONObject.getString("ProxyIP")).commit();*/
                edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
                edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false);

            } else if (httpType) {
                edit.putString(
                    Settings.SERVIDOR_KEY,
                    config.getServersArray().getJSONObject(pos1).getString("ServerIP"))
                    .apply();
                edit.putString(
                    Settings.SERVIDOR_PORTA_KEY,
                    config.getServersArray()
                    .getJSONObject(pos1)
                    .getString("ServerPort"))
                    .commit();
                edit.putString(
                    Settings.CUSTOM_PAYLOAD_KEY,
                    config.getNetworksArray().getJSONObject(pos).getString("Payload"))
                    .commit();
                edit.putString(
                    Settings.PROXY_PORTA_KEY,
                    config.getServersArray().getJSONObject(pos1).getString("ProxyPort"))
                    .commit();
                edit.putString(
                    Settings.PROXY_IP_KEY,
                    config.getServersArray().getJSONObject(pos1).getString("ProxyIP"))
                    .commit();
                edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_PROXY);
                edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false);

            } else if (sslType) {
                edit.putString(
                    Settings.SERVIDOR_KEY,
                    config.getServersArray().getJSONObject(pos1).getString("ServerIP"))
                    .commit();
                edit.putString(
                    Settings.SERVIDOR_PORTA_KEY,
                    config.getServersArray().getJSONObject(pos1).getString("SSLPort"))
                    .commit();
                // edit.putString(Settings.CUSTOM_PAYLOAD_KEY,
                // config.getNetworksArray().getJSONObject(pos).getString("Payload")).commit();
                edit.putString(
                    Settings.CUSTOM_SNI,
                    config.getNetworksArray().getJSONObject(pos).getString("SNI"))
                    .commit();
                edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL);
                edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false);

            } else if (sslPayType) {
                edit.putString(
                    Settings.SERVIDOR_KEY,
                    config.getNetworksArray().getJSONObject(pos).getString("SNI"))
                    .commit();
                edit.putString(
                    Settings.SERVIDOR_PORTA_KEY,
                    config.getServersArray().getJSONObject(pos1).getString("SSLPort"))
                    .commit();

                edit.putString(
                    Settings.CUSTOM_PAYLOAD_KEY,
                    isnt)
                    .commit();
                edit.putString(
                    Settings.CUSTOM_SNI,
                    config.getNetworksArray().getJSONObject(pos).getString("SNI"))
                    .commit();
                edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_PAY_SSL);
                edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false);

            } else if (slowDnsType) {
                edit.putString(Settings.SERVIDOR_KEY, "127.0.0.1");
                edit.putString(Settings.SERVIDOR_PORTA_KEY, "2222");
                edit.putString(
                    Settings.NAMESERVER_KEY,
                    config.getNetworksArray()
                    .getJSONObject(pos)
                    .getString("serverNameKey"))
                    .commit();
                edit.putString(
                    Settings.CHAVE_KEY,
                    config.getNetworksArray().getJSONObject(pos).getString("chaveKey"))
                    .commit();
                edit.putString(
                    Settings.DNS_KEY,
                    config.getNetworksArray().getJSONObject(pos).getString("dnsKey"))
                    .commit();
                edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SLOWDNS);
                edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true);
            }

            String remote_proxy = config.getServersArray().getJSONObject(pos1).getString("ProxyIP");
            String proxy_port = config.getServersArray().getJSONObject(pos1).getString("ProxyPort");
            String ssh_user = "";
            String ssh_password = "";
            if (this.sp.getBoolean("login", false)) {
                ssh_user = prefs.getString("user", "");
                ssh_password = prefs.getString("pass", "");
                Toast.makeText(this, "VIP Login", 0).show();
            } else {
                ssh_user = config.getServersArray().getJSONObject(pos1).getString("ServerUser");
                ssh_password = config.getServersArray().getJSONObject(pos1).getString("ServerPass");
            }
            edit.putString(Settings.USUARIO_KEY, ssh_user);
            edit.putString(Settings.SENHA_KEY, ssh_password);
            edit.putString(Settings.PROXY_IP_KEY, remote_proxy);
            edit.putString(Settings.PROXY_PORTA_KEY, proxy_port);
            edit.apply();
        } catch (Exception e) {
            SkStatus.logInfo(e.getMessage());
        }
    }

    private void loadServer() {
        try {
            if (serverList.size() > 0) {
                serverList.clear();
                serverAdapter.notifyDataSetChanged();
            }
            for (int i = 0; i < config.getServersArray().length(); i++) {
                JSONObject obj = config.getServersArray().getJSONObject(i);
                serverList.add(obj);
                serverAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNetworks1() {
        try {
            if (payloadList.size() > 0) {
                payloadList.clear();
                payloadAdapter.clear();
            }
            JSONObject obj = getJSONConfig2(this);
            JSONArray networkPayload = obj.getJSONArray("Networks");
            for (int i = 0; i < networkPayload.length(); i++) {
                payloadList.add(networkPayload.getJSONObject(i));
            }
            // Collections.sort(listNetwork, NetworkNameComparator());
            payloadAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNetworks() {
        try {
            if (payloadList.size() > 0) {
                payloadList.clear();
                payloadAdapter.notifyDataSetChanged();
            }
            for (int i = 0; i < config.getNetworksArray().length(); i++) {
                JSONObject obj = config.getNetworksArray().getJSONObject(i);
                payloadList.add(obj);
                payloadAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateConfig(final boolean isOnCreate) {
        new ConfigUpdate(
            this,
            new ConfigUpdate.OnUpdateListener() {
                @Override
                public void onUpdateListener(String result) {
                    try {
                        if (!result.contains("Error on getting data")) {
                            String json_data =
                                AESCrypt.decrypt(config.minaPass, result);
                            if (isNewVersion(json_data)) {
                                newUpdateDialog(result);
                            } else {
                                if (!isOnCreate) {
                                    noUpdateDialog();
                                }
                            }
                        } else if (result.contains("Error on getting data")
                                   && !isOnCreate) {
                            errorUpdateDialog(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
            .start(isOnCreate);
    }

    private boolean isNewVersion(String result) {
        try {
            String current = config.getVersion();
            String update = new JSONObject(result).getString("Version");
            return config.versionCompare(update, current);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void newUpdateDialog(final String result)
    throws JSONException, GeneralSecurityException, GeneralSecurityException {
        String json_data = AESCrypt.decrypt(config.minaPass, result);
        String notes = new JSONObject(json_data).getString("ReleaseNotes");
        SpkAlert = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
        SpkAlert.setTitleText("New Update Available");
        SpkAlert.setContentText(notes);
        SpkAlert.setConfirmText("Update Now");
        SpkAlert.setCancelText("Cancel");
        SpkAlert.setConfirmClickListener(
            new SweetAlertDialog.OnSweetClickListener() {

                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    // TODO: Implement this method
                    try {
                        File file = new File(getFilesDir(), "Config.json");
                        OutputStream out = new FileOutputStream(file);
                        out.write(result.getBytes());
                        out.flush();
                        out.close();
                        recreateActivity(MainActivity.class, new Bundle());
                        Toast.makeText(
                            MainActivity.this,
                            "Config Updating & Restarting App",
                            Toast.LENGTH_SHORT)
                            .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        SpkAlert.setCancelClickListener(
            (new SweetAlertDialog.OnSweetClickListener() {

                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    SpkAlert.cancel();
                }
            }));
        SpkAlert.show();
    }

    private void noUpdateDialog() {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("MinaProNet VPN")
            .setContentText("Your config is on Latest Version")
            .show();
    }

    private void errorUpdateDialog(String error) {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Oops..")
            .setContentText("Something went wrong, Please try again.")
            .show();
    }

    public void offlineUpdate() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.extensions = new String[] {".json", ".JSON"};
        properties.root = Environment.getExternalStorageDirectory();
        filedialog = new FilePickerDialog(this, properties);
        filedialog.setTitle("Select a File");
        filedialog.setPositiveBtnName("Select");
        filedialog.setNegativeBtnName("Cancel");
        filedialog.setDialogSelectionListener(
            new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    for (String path : files) {
                        File file = new File(path);
                        if (file.getName().endsWith(".json")
                            || file.getName().endsWith(".JSON")) {
                            final String inet = inet(file.getAbsolutePath());
                            if (TextUtils.isEmpty(inet)) {
                                Toast.makeText(
                                    getApplicationContext(),
                                    "Empty Data!",
                                    Toast.LENGTH_LONG)
                                    .show();
                            } else {
                                try {
                                    if (isNewVersion(
                                            AESCrypt.decrypt(ConfigUtil.minaPass, inet))) {
                                        new SweetAlertDialog(
                                            MainActivity.this,
                                            SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("New Update Available")
                                            .setContentText(
                                            "This will override your current"
                                            + " configuration.")
                                            .setConfirmText("Update Now")
                                            .setConfirmClickListener(
                                            new SweetAlertDialog
                                            .OnSweetClickListener() {
                                                @Override
                                                public void onClick(
                                                    SweetAlertDialog
                                                    sweetAlertDialog) {
                                                    try {
                                                        // TODO: Implement this
                                                        // method
                                                        File file =
                                                            new File(
                                                            getFilesDir(),
                                                            "Config.json");
                                                        OutputStream out =
                                                            new FileOutputStream(
                                                            file);
                                                        out.write(inet.getBytes());
                                                        out.flush();
                                                        out.close();
                                                        loadServer();
                                                        loadNetworks();
                                                        recreateActivity(
                                                            MainActivity
                                                            .class,
                                                            new Bundle());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                            .show();
                                    } else {
                                        new SweetAlertDialog(
                                            MainActivity.this,
                                            SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("MinaProNet VPN")
                                            .setContentText(
                                            "Your config is on Latest Version")
                                            .show();
                                        // pDialog.dismiss();
                                    }

                                    Toast.makeText(
                                        getApplicationContext(),
                                        "Imported successfully!",
                                        Toast.LENGTH_LONG)
                                        .show();

                                } catch (GeneralSecurityException e) {
                                    Toast.makeText(
                                        getApplicationContext(),
                                        "Wrong password!",
                                        Toast.LENGTH_LONG)
                                        .show();
                                }
                            }
                        }
                    }
                }
            });
        filedialog.show();
    }

    private String inet(String Path) {
        try {
            InputStream openRawResource = new FileInputStream(Path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (int read = openRawResource.read(); read != -1; read = openRawResource.read()) {
                byteArrayOutputStream.write(read);
            }
            openRawResource.close();
            return byteArrayOutputStream.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    private void antiRemod1() {
        if (!(((String) getPackageManager().getApplicationLabel(getApplicationInfo())).equals(MainActivity.app_name) && getPackageName().equals(MainActivity.pckg))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(getLayoutInflater().inflate(R.layout.mina_protection, null));
            builder.setCancelable(false);
            builder.setPositiveButton(
                    "Exit",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface p1, int p2) {
                            // TODO: Implement this method
                            if (android.os.Build.VERSION.SDK_INT >= 21) {
                                finishAndRemoveTask();
                            } else {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                            System.exit(0);
                        }
                    });
            builder.show();
        }
    }
    
    
    /** Tunnel SSH */
    public void startOrStopTunnel(Activity activity) {
        if (SkStatus.isTunnelActive()) {
            TunnelManagerHelper.stopSocksHttp(activity);
            payloadSpinner.setEnabled(true);
            serverSpinner.setEnabled(true);
        } else {
            payloadSpinner.setEnabled(false);
            serverSpinner.setEnabled(false);
            launchVPN();
        }
    }

    private void launchVPN() {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            SkStatus.updateStateString(
                "USER_VPN_PERMISSION",
                "",
                R.string.state_user_vpn_permission,
                ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                SkStatus.logError(R.string.no_vpn_support_image);
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                if (!TunnelUtils.isNetworkOnline(this)) {
                    Toast.makeText(this, "No Internet Connection", 0).show();
                } else TunnelManagerHelper.startSocksHttp(this);
            }
        }
    }

    public void setStarterButton(Button starterButton, Activity activity) {
        String state = SkStatus.getLastState();
        boolean isRunning = SkStatus.isTunnelActive();

        if (starterButton != null) {
            int resId;

            if (SkStatus.SSH_INICIANDO.equals(state)) {
                resId = R.string.stop;
                dataThread = new Thread(new MyThreadClass());
                dataThread.setName("showDataGraph");
                dataThread.start();
                starterButton.setEnabled(false);
            } else if (SkStatus.SSH_PARANDO.equals(state)) {
                resId = R.string.state_stopping;
                dataThread = new Thread(new MyThreadClass());
                dataThread.setName("stopDataGraph");
                starterButton.setEnabled(false);
                serverSpinner.setEnabled(true);
                payloadSpinner.setEnabled(true);
            } else {
                resId = isRunning ? R.string.stop : R.string.start;
                starterButton.setEnabled(true);
            }
            starterButton.setText(resId);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        if (mDrawerPanel.getToogle() != null) mDrawerPanel.getToogle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerPanel.getToogle() != null)
            mDrawerPanel.getToogle().onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.activity_starterButtonMain:
                //   doSaveData();
                loadServerData();
                startOrStopTunnel(this);
                saveSpinner();
                break;
        }
    }

    @Override
    public void updateState(
        final String state,
        String msg,
        int localizedResId,
        final ConnectionStatus level,
        Intent intent) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mHandler.post(
            new Runnable() {
                @Override
                public void run() {
                    doUpdateLayout();
                    if (SkStatus.isTunnelActive()) {

                        if (level.equals(ConnectionStatus.LEVEL_CONNECTED)) {
                            connectionStatus.setText("Connected");
                            connectionStatus.setTextColor(Color.GREEN);
                            serverSpinner.setEnabled(false);
                            payloadSpinner.setEnabled(false);

                            TastyToast.makeText(
                                getApplicationContext(),
                                "MinaProNet VPN Connected",
                                TastyToast.LENGTH_LONG,
                                TastyToast.SUCCESS);
                        }

                        if (level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)) {
                            connectionStatus.setText("Stop Service");
                        }

                        if (level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)) {
                            connectionStatus.setText("Authenticating");
                              if(!sp.getBoolean("login", false)){
                                start();
                                showInterstitial();
                            }
                        }

                        if (level.equals(
                                ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET)) {
                            connectionStatus.setText("Connecting");
                        }
                        if (level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)) {
                            connectionStatus.setText("Authenticating Failed");
                        }
                        if (level.equals(ConnectionStatus.UNKNOWN_LEVEL)) {
                            TastyToast.makeText(
                                getApplicationContext(),
                                "MinaProNet VPN Disconnected",
                                TastyToast.LENGTH_LONG,
                                TastyToast.ERROR);
                            connectionStatus.setText("Disconnected");
                            connectionStatus.setTextColor(Color.RED);
                            if(!sp.getBoolean("login", false)){
                                stop();
                            }
                        }
                    }
                    // if (level.equals(ConnectionStatus.LEVEL_RECONNECTING)){
                    //      status.setText(R.string.reconnecting);

                    if (level.equals(ConnectionStatus.LEVEL_NONETWORK)) {
                        connectionStatus.setText("No Network");
                    }
                }
            });

        switch (state) {
            case SkStatus.SSH_CONECTADO:
                  showInterstitial();
                // carrega ads banner
                if (adsBannerView != null && TunnelUtils.isNetworkOnline(MainActivity.this)) {
                    adsBannerView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                if (adsBannerView != null && !isFinishing()) {
                                    adsBannerView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                }
                break;
        }
    }

    /** Recebe locais Broadcast */
    private BroadcastReceiver mActivityReceiver =
    new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            if (action.equals(UPDATE_VIEWS) && !isFinishing()) {
                doUpdateLayout();
            }
        }
    };

    public void recreateActivity(final Class clazz, final Bundle b) {
        // Add a 250 ms delay so that it has a nicer transition.
        SleepyTime(
            new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, clazz);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    if (b != null) {
                        i.putExtra("recreate", b);
                    }
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            },
            200);
    }

    public void SleepyTime(final Runnable run, final int SleepyTime) {
        final Handler h = new Handler();
        Thread sleepyTime =
            new Thread() {
            public void run() {
                try {
                    Thread.sleep(SleepyTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                h.post(run);
            }
        };
        sleepyTime.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerPanel.getToogle() != null
            && mDrawerPanel.getToogle().onOptionsItemSelected(item)) {
            return true;
        }

        // Menu Itens
        switch (item.getItemId()) {
            case R.id.updateConfig:
                updateConfig(false);
                break;

            case R.id.resetData:
                if (!SkStatus.isTunnelActive()) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("MinaProNet VPN")
                        .setContentText(getString(R.string.alert_clear_settings))
                        .setConfirmText("Yes")
                        .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                try {
                                    // clearing app data
                                    Settings.clearSettings(MainActivity.this);
                                    // limpa logs
                                    SkStatus.clearLog();
                                    updateMainViews(MainActivity.this);
                                    Intent intent =
                                        Intent.makeRestartActivityTask(
                                        MainActivity.this
                                        .getPackageManager()
                                        .getLaunchIntentForPackage(
                                            MainActivity
                                            .this
                                            .getPackageName())
                                        .getComponent());
                                    startActivity(intent);
                                    Runtime.getRuntime().exit(0);
                                    Toast.makeText(
                                        MainActivity.this,
                                        R.string.success_clear_settings,
                                        Toast.LENGTH_SHORT)
                                        .show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setCancelText("No")
                        .showCancelButton(true)
                        .setCancelClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        }).show();
                } else {
                    Toast.makeText(
                        this,
                        R.string.error_tunnel_service_execution,
                        Toast.LENGTH_SHORT)
                        .show();
                }
                break;

            case R.id.miExit:
                if (Build.VERSION.SDK_INT >= 16) {
                    finishAffinity();
                }
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.attention))
            .setContentText(getString(R.string.alert_exit))
            .setConfirmText(getString(R.string.exit))
            .setConfirmClickListener(
            new SweetAlertDialog.OnSweetClickListener() {

                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    // TODO: Implement this method
                    Utils.exitAll(MainActivity.this);
                }
            })
            .setCancelText(getString(R.string.minimize))
            .showCancelButton(true)
            .setCancelClickListener(
            new SweetAlertDialog.OnSweetClickListener() {

                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    // TODO: Implement this method
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            })
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mTimerEnabled) {
            resumeTime(); 
        }
        addTime();
        setSpinner();
        SkStatus.addStateListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // doSaveData();
        SkStatus.removeStateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityReceiver);
    }

    /** Utils */
    public static void updateMainViews(Context context) {
        Intent updateView = new Intent(UPDATE_VIEWS);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateView);
    }

    // SILVER PRO KING DEVELOPER
    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            //  doSaveData();
            loadServerData();
            startOrStopTunnel(this);
            graph.start();
            Log.d("Network", "Connected");
            return true;
        } else {
            Log.d("Network", "Not Connected");
            Toast.makeText(
                MainActivity.this,
                "Please connect to the internet",
                Toast.LENGTH_SHORT)
                .show();
            return true;
        }
    }

    ////AD ACTIONS TAKEN HERE////////

    private void start() {
        if (saved_ads_time == 0) {
            Toast.makeText(MainActivity.this, "Your time is expiring soon, please click ADD TIME to renew access!", Toast.LENGTH_LONG).show();
            long millisInput = 1000 * 500;
            setTime(millisInput);
        }
        if (!mTimerRunning) {
            startTimer();
        }
    }


    private void stop() {
        if (mTimerRunning) {
            pauseTimer();
        }
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    private void updateCountDownText() {
        long days = TimeUnit.MILLISECONDS.toDays(mTimeLeftInMillis);
        long daysMillis = TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis - daysMillis);
        long hoursMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis - daysMillis - hoursMillis);
        long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis - daysMillis - hoursMillis - minutesMillis);
        String resultString = days + "d:" + hours + "h:" + minutes + "m:" + seconds + "s";
        tvTimeRemain.setText(resultString);
    }

    private void setTime(long milliseconds) {
        saved_ads_time = mTimeLeftInMillis + milliseconds;
        mTimeLeftInMillis = saved_ads_time;
        updateCountDownText();
    }


    private void addTime(long time){
        setTime(time);
        if (mTimerRunning){
            pauseTimer();
        }
        startTimer();
    }


    private void saveTime(long time) {
        SharedPreferences mTime = getSharedPreferences("time", Context.MODE_PRIVATE);
        SharedPreferences.Editor time_edit = mTime.edit();
        time_edit.putLong("SAVED_TIME", time).apply();
    }

    private void addTime(){
        long added = sp.getLong("isAdded", 0);
        if (added == 1){
            long added_time = sp.getLong("AddedTime", 0);
            if (mTimerRunning){
                addTime(added_time);
            }else{
                setTime(added_time);
            }
            sp.edit().putLong("isAdded", 0).apply();
            saveTime(mTimeLeftInMillis);
        }
    }

    private void resumeTime() {
        SharedPreferences mTime = getSharedPreferences("time", Context.MODE_PRIVATE);
        long saved_time = mTime.getLong("SAVED_TIME", 1);
        setTime(saved_time);
        mTimerEnabled = true;
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                saveTime(mTimeLeftInMillis);
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                pauseTimer();
                saved_ads_time = 0;

                // Code for auto stop vpn (sockshtttp)
                Intent stopVPN = new Intent(SocksHttpService.TUNNEL_SSH_STOP_SERVICE);
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(stopVPN);
                Toast.makeText(MainActivity.this, "Time expired! Click Add + Time to renew access!", Toast.LENGTH_LONG).show();
            }
        }.start();
        mTimerRunning = true;
    }

    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
         //   loadAd();
            //Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-5130321482958780/4259051859", adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    MainActivity.this.interstitialAd = interstitialAd;
                    //Log.i(TAG, "onAdLoaded");
                    //Toast.makeText(MainActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                    interstitialAd.setFullScreenContentCallback(
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.

                                MainActivity.this.interstitialAd = null;
                                //Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                loadAd();
                                MainActivity.this.interstitialAd = null;
                                //Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                //Log.d("TAG", "The ad was shown.");
                            }
                        });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    //Log.i(TAG, loadAdError.getMessage());
                    interstitialAd = null;
                    String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                    /* Toast.makeText(
                     MainActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                     .show();*/
                }
            });
    }

}
