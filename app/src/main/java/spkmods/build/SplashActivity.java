package spkmods.build;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.wangyuwei.particleview.ParticleView;
import vpn.minapronet.com.eg.*;

/**
 * 作者： 巴掌 on 16/8/31 15:18
 * Github: https://github.com/JeasonWong
 */
public class SplashActivity extends AppCompatActivity {

    ParticleView mPvGithub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        mPvGithub = (ParticleView) findViewById(R.id.pv_github);
        mPvGithub.startAnim();
        mPvGithub.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
				@Override
				public void onAnimationEnd() {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					SplashActivity.this.startActivity(intent);
					finish();
				}
			});
    }
}

