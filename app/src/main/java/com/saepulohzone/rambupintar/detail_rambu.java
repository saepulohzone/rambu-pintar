package com.saepulohzone.rambupintar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class detail_rambu extends AppCompatActivity {

    private static String Tag="detail.class";
    private ImageView image;
    private TextView text;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private String path;
    private String arti;
    private String title;
    private Bundle mBundle;
    private AssetManager mAsset;
    private InputStream mInput;
    private Bitmap mBitmap;
    private TextToSpeech mTTS;
    private boolean isPlay=false;
    private CoordinatorLayout mCoorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Animation mAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rambu);
        image=(ImageView)findViewById(R.id.image);
        text=(TextView)findViewById(R.id.text);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mCoorLayout=(CoordinatorLayout)findViewById(R.id.cor);
        loadData();
        //setAnimationImageZoomInAndRotate();
        mTTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(mTTS.getEngines()!=null){
                    if(status==TextToSpeech.SUCCESS){
                        int result=mTTS.setLanguage(new Locale("in", "id"));
                        Log.d(String.valueOf(mTTS.getEngines()
                                .hashCode()), "Has Code");
                        Log.d(mTTS.getEngines().toString(),
                                "Engin yang dipakai");
                        if(result==TextToSpeech.LANG_MISSING_DATA ||
                                result==TextToSpeech.LANG_NOT_SUPPORTED){
                            Log.e(Tag, "Bahasa gak dukung");
                            installBahasa();
                        }
                    }else{
                        Log.e(Tag,"Gagal Inisialisasi");
                    }
                }else{
                    Log.e(Tag, "Instal engine");
                    instalEnginTTS();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTTS.isSpeaking()) {
                    speakOut();
                } else {
                    mTTS.stop();
                }
            }
        });
    }

    @Override
    public void onPause(){
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroy(){
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onBackPressed();
    }

    public void speakOut() {
        mTTS.speak("arti dari " +
                title + " ini adalah " +
                arti, TextToSpeech.QUEUE_FLUSH,
                null);
    }

    private void installBahasa() {
        // TODO Auto-generated method stub
        Snackbar.make(mCoorLayout,
                "Bahasa Indonesia belum tersedia.",
                Snackbar.LENGTH_LONG)
                .setAction("Unduh bahasa", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent installIntent = new Intent();
                        installIntent.setAction(TextToSpeech.Engine
                                .ACTION_INSTALL_TTS_DATA);
                        startActivity(installIntent);
                    }
                }).show();
    }

    private void instalEnginTTS(){
        new AlertDialog.Builder(this)
                .setTitle("Suara kemungkinan tidak keluar!")
                .setMessage("Google Text-To-Speech belum " +
                        "terpasang atau paket data tidak aktif." +
                        "\nAktifkan paket data atau pasang " +
                        "Google Text-To-Speech ?")
                .setPositiveButton("Pasang", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent installIntent = new Intent();
                        installIntent.setAction(Intent.ACTION_VIEW);
                        installIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                        installIntent.setData(Uri.parse(
                                "https://play.google.com/store/apps/" +
                                 "details?id=com.google.android.tts"));
                        startActivity(installIntent);
                    }
                })
                .setNegativeButton("Tidak", null)
                .setNeutralButton("Aktifkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new
                                Intent(Settings.ACTION_SETTINGS)
                        );
                    }
                })
                .show();
    }

    private void loadData(){
        mBundle=getIntent().getExtras();
        arti = mBundle.getString("arti");
        path=mBundle.getString("path");
        title=mBundle.getString("title");
        text.setText(arti);
        setToolbar();
        mAsset=this.getAssets();
        try{
            mInput=mAsset.open(path + ".png");
            mBitmap= BitmapFactory.decodeStream(mInput);
            image.setImageBitmap(mBitmap);
            image.setOnClickListener(
                    new ImageView.OnClickListener(){
                @Override
                public void onClick(View v) {
                    setAnimationImageZoomInAndRotate();
                }
            });
            mInput.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setAnimationImageZoomInAndRotate(){
        mAnimation=AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.anim_zoomin_rotate);
        image.startAnimation(mAnimation);
    }
    private boolean statusPlay(){
            if(!mTTS.isSpeaking()){
                fab.setImageResource(R.mipmap.play);
                return false;
            }return true;
            //Log.d(Tag,"Status Play: "+String.valueOf(mTTS.isSpeaking()));
    }

    private void setToolbarBackgroud(){
        if(title.equals("Rambu Larangan"))
            toolbar.setBackgroundResource(
                    R.color.colorLarangan);
        if(title.equals("Rambu Perintah"))
            toolbar.setBackgroundResource(
                    R.color.colorPerintah);
        if(title.equals("Rambu Petunjuk"))
            toolbar.setBackgroundResource(
                    R.color.colorPetunjuk);
        if(title.equals("Rambu Peringatan"))
            toolbar.setBackgroundResource(
                    R.color.colorPeringatan);
    }
    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setToolbarBackgroud();
        setCollapsColor();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void setCollapsColor(){
       collapsingToolbarLayout=(CollapsingToolbarLayout)
               findViewById(R.id.toolbar_layout);
        if(title.equals("Rambu Larangan"))
            collapsingToolbarLayout.setBackgroundResource(
                    R.color.colorLarangan);
        if(title.equals("Rambu Perintah"))
            collapsingToolbarLayout.setBackgroundResource(
                    R.color.colorPerintah);
        if(title.equals("Rambu Petunjuk"))
            collapsingToolbarLayout.setBackgroundResource(
                    R.color.colorPetunjuk);
        if(title.equals("Rambu Peringatan"))
            collapsingToolbarLayout.setBackgroundResource(
                    R.color.colorPeringatan);
    }
}
