package spkmods.build.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import java.util.Locale;
import com.sdsmdg.tastytoast.TastyToast;
import vpn.minapronet.com.eg.R;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import cn.pedant.SweetAlert.widget.SweetAlertDialog;
import spkmods.build.MainActivity;

public class CoinsActivity extends AppCompatActivity  {
	
	private static final String TAG = CoinsActivity.class.getSimpleName();
    private TextView my_coins;
    private long current_coins;
    private Button addcoins, btn1, btn2, btn3, btn4, btn5;
    private boolean success;
    public static SharedPreferences sp;
    private long add_time;
    private RewardedAd rewardedAd;
    private boolean isLoading;
    private CountDownTimer mBtnCountDown;
    private long mTimeLeftBtn;
    private long hours;
    private AdView mAdView;
    private SweetAlertDialog progressDialog;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        my_coins = (TextView) findViewById(R.id.coins);
        addcoins = (Button) findViewById(R.id.addcoins);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(
                    @NonNull InitializationStatus initializationStatus) {}
            });
    //    loadRewardedAd();
        mAdView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }
                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
		
        addcoins.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {   
                    if(CoinsActivity.this.isLoading = true) {
                        loadRewardedAd();
                        loadingAds();
                    }
                  /* 	loadingAds();
					   loadRewardedAd();
				//	showRewardedVideo();*/
                }
            });

        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convert(1);
                }
            });
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convert(6);
                }
            });

        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convert(12);
                }
            });
        btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convert(24);
                }
            });
        btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convert(50);
                }
            });
        // Use an activity context to get the rewarded video instance.
        //rewardedAd = MobileAds.getRewardedVideoAdInstance(this);
        //rewardedAd.setRewardedVideoAdListener(this);
    }

    private void convert(long coins) {
        SharedPreferences mCoins = getSharedPreferences("coins", Context.MODE_PRIVATE);
        long saved_coins = mCoins.getLong("SAVED_COINS", 1);
        SharedPreferences.Editor coins_edit = mCoins.edit();
        if (saved_coins < coins) {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_LONG).show();
            success = false;
        } else {
            long x = saved_coins - coins;
            coins_edit.putLong("SAVED_COINS", x).apply();
            updateCoins();
            success = true;
        }

    if (success) {
        if (coins == 1) {
            add_time = 1 * 3600 * 1000;
            hours = 1;

        } else if (coins == 6) {
            add_time = 6 * 3600 * 1000;
            hours = 6;

        } else if (coins == 12) {
            add_time =  12 * 3600 * 1000;
            hours = 12;

        } else if (coins == 24) {
            add_time = 24 * 3600 * 1000;
            hours = 24;

        } else if (coins == 50) {
            add_time = 4 * 24 * 3600 * 1000;
            hours = 96;
        }
        sp.edit().putLong("AddedTime", add_time).apply();
        sp.edit().putLong("isAdded", 1).apply();
        Toast.makeText(this, hours+ " hours successfully added to your time!", Toast.LENGTH_LONG).show();
        finish();
    }

  }

    private void addCoins(long coins) {
        long added_coins = current_coins + coins;
        SharedPreferences mCoins = getSharedPreferences("coins", Context.MODE_PRIVATE);
        SharedPreferences.Editor coins_edit = mCoins.edit();
        coins_edit.putLong("SAVED_COINS", added_coins);
        coins_edit.apply();
        updateCoins();
    }

    private void updateCoins() {
        SharedPreferences mCoins = getSharedPreferences("coins", Context.MODE_PRIVATE);
        long saved_coins = mCoins.getLong("SAVED_COINS", 1);
        current_coins = saved_coins;
        my_coins.setText(Long.toString(current_coins));

    }

    private void loadingAds() {
        progressDialog = new SweetAlertDialog(CoinsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitleText("Requesting Ads Video");
        progressDialog.setContentText("Please wait while loading...\n\n\n" + "Note:\n" + "You need to finish the reward video to claim your\n" + "coin reward");
        progressDialog.setTitle("Loading Rewarded Ad");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }


    private void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, "ca-app-pub-5130321482958780/7206393797", adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        rewardedAd = null;
                        CoinsActivity.this.isLoading = false;
                        progressDialog.dismiss();
                        Toast.makeText(CoinsActivity.this, "Rewarded Ad Failed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        CoinsActivity.this.rewardedAd = rewardedAd;
                        Log.d(TAG, "onAdLoaded");
                        CoinsActivity.this.isLoading = false;
				    	showRewardedVideo();
                        progressDialog.dismiss();
                        //  Toast.makeText(MainActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void showRewardedVideo() {
        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        rewardedAd.setFullScreenContentCallback(
            new FullScreenContentCallback() {
				
                @Override
                public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "onAdShowedFullScreenContent");
                    }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                    }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    rewardedAd = null;
                    TastyToast.makeText(getApplicationContext(), "Thank you for supporting the app !! 💙", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    btnTimer();
                    // Preload the next rewarded ad.
                //    loadRewardedAd();
                  //  CoinsActivity.this.loadRewardedAd();
                }
            });
			
        Activity activityContext = CoinsActivity.this;
        rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    long timeLeft = sp.getLong("millisUntilFinished",0) + 1;
                    addCoins(timeLeft);
                    //sucess_text();
                    Toast.makeText(CoinsActivity.this, "1 (coin) successfully added!", Toast.LENGTH_LONG).show();
                    /*   loadRewardedAd();
                     addCoins(rewardItem.getAmount());
                     //addTime();
                     Toast.makeText(CoinsActivity.this, rewardItem.getAmount() + " coin(s) successfully added!", Toast.LENGTH_LONG).show();*/
                }
            });
       }
//  }

    @Override
    protected void onResume() {
        super.onResume();
        updateCoins();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void btnTimer() {
        mBtnCountDown = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftBtn = millisUntilFinished;
                addcoins.setEnabled(false);
                updateBtnText();
            }
            @Override
            public void onFinish() {
                addcoins.setEnabled(true);
                addcoins.setText("GET MORE COINS - FREE");
            }
        }.start();

    }


    private void updateBtnText() {
        int seconds = (int) (mTimeLeftBtn / 1000) % 60;
        String timeLeftFormatted;
        if (seconds > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),"%02d", seconds);
            addcoins.setText("Refresh in " + timeLeftFormatted);
        }
    }
    
}
