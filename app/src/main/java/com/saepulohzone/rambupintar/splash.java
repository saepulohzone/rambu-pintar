package com.saepulohzone.rambupintar;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash extends Activity {
    private Handler handler = new Handler();
    private ImageView image;
    private Animation mAnimation;
    private Intent main_intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        image=(ImageView)findViewById(R.id.image);
        mAnimation= AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.anim_rotate_spalsh);
        image.setAnimation(mAnimation);
        splashscreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getRepeatCount() == 0)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void splashscreen(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                delay(800);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                     // TODO Auto-generated method stub
                    main_intent= new Intent(
                            splash.this, home.class);
                    splash.this.startActivity(main_intent);
                    splash.this.finish();
                    }
                });
            }

            public void delay(int milis) {
                try{
                    Thread.sleep(milis);
                }catch(InterruptedException ie){
                    ie.printStackTrace();
                }
            }
        }).start();
    }
}
