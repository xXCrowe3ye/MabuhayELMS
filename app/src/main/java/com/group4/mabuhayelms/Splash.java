package com.group4.mabuhayelms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    ImageView imageView22;

    /* access modifiers changed from: protected */
    @Override // android.support.v4.app.BaseFragmentActivityGingerbread, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.imageView22 = (ImageView) findViewById(R.id.imageView22);
        Animation animate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            /* class com.sti.ehms.Splash.AnonymousClass1 */

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Splash.this.finish();
                Splash.this.startActivity(new Intent(Splash.this.getApplicationContext(), MainActivity.class));
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.imageView22.setAnimation(animate);
    }
}
