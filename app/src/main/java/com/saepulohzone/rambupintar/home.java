package com.saepulohzone.rambupintar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String Tag="home.class-->";
    private final int REQ_CODE_SPEECH_INPUT=100;
    private TextView text;
    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ImageView imageWalcome;
    private Bundle mBundle;
    private Intent mInten;
    private Toolbar toolbar;
    private Cursor mCursor;
    private ArrayList<String> mListGrid;
    private String title;
    private String kategori=null;
    private String mTitle;
    private String kategoriTitle[]=new String[500];
    private String arti[]=new String[255];
    private String SpeechToText="";
    private DatabaseHelper mHelper;
    private ConnectivityManager connect;
    private NetworkInfo mNet;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mGridView=(GridView)findViewById(R.id.grid);
        mHelper= DatabaseHelper.getInstance(this);
        mBundle=new Bundle();
        imageWalcome=(ImageView)findViewById(R.id.image);
        text=(TextView)findViewById(R.id.text);
        mInten=new Intent(this, detail_rambu.class);

        //untuk membuat tampilan wallpaper
        if(kategori==null){
            imageWalcome=(ImageView)findViewById(R.id.image);
            imageWalcome.setImageResource(R.drawable.home);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnet()){
                    //koneksi ada
                    Log.d(Tag,"bahasa default: "+
                            Locale.getDefault().getDisplayName());
                    if(Locale.getDefault().getDisplayName()
                            .equals("Bahasa Indonesia (Indonesia)")){
                        //koneksi ada dan bahasa sudah di set kebahasa indonesia
                        promptSpeech();
                    }else {
                        //koneksi ada tapi bahasa belum di set ke bahasa indonesia
                        Log.d(Tag,"bahasa default: "+
                                Locale.getDefault().getDisplayName());
                        Snackbar.make(view, "Ubah bahasa kedalam bahasa Indonesia.",
                                Snackbar.LENGTH_LONG)
                                .setAction("Ubah?", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(
                                                Settings.ACTION_LOCALE_SETTINGS)
                                        );
                                    }
                                }).show();
                    }
                }else {
                    //koneksi belum ada
                    Snackbar.make(view, "Fungsi ini membutuhkan koneksi internet.",
                            Snackbar.LENGTH_LONG)
                            .setAction("Aktifkan?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(
                                            Settings.ACTION_SETTINGS)
                                    );
                                }
                            }).show();
                }
            }
        });

        //untuk slide menu
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //untuk navigasi
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //memasukan gambar ke dalan grid view
    private void loadImage(){
        mGridAdapter = new GridViewAdapter(this, mListGrid);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                if (kategoriTitle[position].equals("larangan"))
                    mTitle = "Rambu Larangan";
                if (kategoriTitle[position].equals("perintah"))
                    mTitle = "Rambu Perintah";
                if (kategoriTitle[position].equals("petunjuk"))
                    mTitle = "Rambu Petunjuk";
                if (kategoriTitle[position].equals("peringatan"))
                    mTitle = "Rambu Peringatan";
                mBundle.putString("path", mGridAdapter.getItem(position));
                mBundle.putString("arti", arti[position]);
                mBundle.putString("title", mTitle);
                mInten.putExtras(mBundle);
                startActivity(mInten);
            }
        });
    }

    //memasukan url gambar ke dalam arraylist
    private void loadData(String Kategori,String Title) {
        toolbar.setTitle(Title);
        mListGrid=new ArrayList<String>();
        mCursor=mHelper.getRambuByCategory(Kategori);
        mCursor.moveToFirst();
        int i=0;
        do{
            mListGrid.add(mCursor.getString(3));
            Log.d(Tag,"Isi ArrayList: "+mListGrid);
            arti[i]=mCursor.getString(1);
            kategoriTitle[i] = mCursor.getString(2);
            i++;
        }while(mCursor.moveToNext());
        loadImage();
    }

    //memasukan url gambar hasil pencarian ke dalam arraylist
    private void loadSearch(String textSearch,String Title){
        toolbar.setTitle(Title);
        mListGrid=new ArrayList<String>();
        mCursor=mHelper.getRambuSearch(textSearch);
        if(mCursor.moveToFirst()) {
            int i = 0;
            do {
                mListGrid.add(mCursor.getString(3));
                Log.d(Tag, "Isi ArrayListSearch: " + mListGrid);
                arti[i] = mCursor.getString(1);
                kategoriTitle[i] = mCursor.getString(2);
                i++;
            } while (mCursor.moveToNext());
            loadImage();
        }
    }

    //SpeecToText/////
    public void promptSpeech(){
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                new Locale("in", "id"));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try{
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException a){
            Snackbar.make(null, getString(
                    R.string.speech_not_supported),
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        Log.d(Tag,"Akhir promptSpeech()");
    }
    //activity result untuk speech to text
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:{
                if(resultCode==RESULT_OK&&null!=data){
                    ArrayList<String> result=data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //text.setText(result.get(0));
                    imageWalcome.setVisibility(View.INVISIBLE);
                    title="Pencarian";
                    toolbar.setBackgroundResource(R.color.colorHome);
                    SpeechToText=result.get(0);
                    loadSearch(SpeechToText,title);
                }break;
            }
        }
        Log.d(Tag,"Akhir onActivityResult");
    }
    //Akhir Speech To Text////

    //cek internet untuk fungsi speech to text
    public boolean isConnet(){
        connect=(ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        mNet=connect.getActiveNetworkInfo();
        if(connect!=null&&mNet!=null){
            return true;
        }
        return false;
    }

    private void tentangApp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(
                R.layout.tentang_aplikasi,null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Tentang Aplikasi Rambu Pintar");
        dialogBuilder.setPositiveButton("Hubungi via e-mail",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String TO[] = {"saepulohzone@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "Bantuan Rambu Pintar");
                try {
                    startActivity(Intent.createChooser(
                            emailIntent, "Kirim e-mail"));
                    finish();
                    Log.d(Tag, "Finished sending email...");
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.d(Tag, "Failed sending email...");
                }
            }
        });
        dialogBuilder.setNegativeButton("Tutup", null);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    //fungsi untuk tombol kembali
    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout)findViewById(
                R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //fungsi untuk menu pencarian search view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextFocusChangeListener(
                new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null) {
                    //disini untuk melakukan pencarian ke dalam database
                    imageWalcome.setVisibility(View.INVISIBLE);
                    //text.setText(newText.toString());
                    title="Pencarian";
                    toolbar.setBackgroundResource(R.color.colorHome);
                    loadSearch(newText,title);
                }
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_larangan) {
            imageWalcome.setVisibility(View.INVISIBLE);;
            toolbar.setBackgroundResource(
                    R.color.colorLarangan);
            kategori="larangan";
            title="Rambu Larangan";
            loadData(kategori, title);
        } else if (id == R.id.nav_perintah) {
            imageWalcome.setVisibility(View.INVISIBLE);
            toolbar.setBackgroundResource(
                    R.color.colorPerintah);
            kategori="perintah";
            title="Rambu Perintah";
            loadData(kategori, title);
        } else if (id == R.id.nav_peringatan) {
            imageWalcome.setVisibility(View.INVISIBLE);
            toolbar.setBackgroundResource(
                    R.color.colorPeringatan);
            kategori="peringatan";
            title="Rambu Peringatan";
            loadData(kategori, title);
        } else if (id == R.id.nav_petunjuk) {
            imageWalcome.setVisibility(View.INVISIBLE);
            toolbar.setBackgroundResource(
                    R.color.colorPetunjuk);
            kategori="petunjuk";
            title="Rambu Petunjuk";
            loadData(kategori, title);
        }else if (id == R.id.nav_tentang) {
            tentangApp();
        }else if (id == R.id.nav_keluar) {
            System.exit(0);
        }

        drawer = (DrawerLayout)findViewById(
                R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
