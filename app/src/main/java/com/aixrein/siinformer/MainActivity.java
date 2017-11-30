package com.aixrein.siinformer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private AuthorsDataSource AuthorsDS;
    private WorkDataSource WorkDS;

    private final Authors Author = new Authors();
    private final Work Work = new Work();

    private String UpdatedAuthors = "";

    private final Context context = this;

    private ProgressDialog mDialog;

    private final MyHttpHelper myHttpHelper = new MyHttpHelper();

    public int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        AuthorsDS = new AuthorsDataSource(this);
        AuthorsDS.open();

        // AuthorsDS.close();
        // Use the SimpleCursorAdapter to show the elements in a ListView


        // Use the SimpleCursorAdapter to show the elements in a ListView
        // ArrayAdapter<Work> adapter = new
        // ArrayAdapter<Work>(this,R.layout.work_list, values);
        int i = 0;

        setTitle("Информатор СИ");

        List<Authors> values = AuthorsDS.getAllAuthors();
        if (values.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Данных об авторах не обнаружено");
            builder.setMessage("Загрузить начальные данные?");

            final ProgressDialog mDialog = new ProgressDialog(context);
            mDialog.setMessage("Загрузка начальных данных");
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setProgress(0);
            mDialog.setMax(6);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mDialog.show();
                    mDialog.setProgress(1);
                    Log.i("Журнал загрузки", "Загрузка http://samlib.ru/k/kontorowich_a_s/");
                    addAuthor("http://samlib.ru/k/kontorowich_a_s/");

                    mDialog.setProgress(2);
                    Log.i("Журнал загрузки", "Загрузка http://samlib.ru/k/kulakow_a_i/");
                    addAuthor("http://samlib.ru/k/kulakow_a_i/");

                    mDialog.setProgress(3);
                    Log.i("Журнал загрузки", "Загрузка http://samlib.ru/s/sirus_ilxja/");
                    addAuthor("http://samlib.ru/s/sirus_ilxja/indexvote.shtml");

                    mDialog.setProgress(4);
                    Log.i("Журнал загрузки", "Загрузка http://samlib.ru/r/rybakow_artem_olegowich");
                    addAuthor("http://samlib.ru/r/rybakow_artem_olegowich");

                    mDialog.setProgress(5);
                    Log.i("Журнал загрузки", "Загрузка http://samlib.ru/z/zemljanoj_a_b/");
                    addAuthor("http://samlib.ru/z/zemljanoj_a_b/");

                    mDialog.setProgress(6);
                    Log.i("Журнал загрузки", "Загрузка http://samlib.ru/g/gromow_b/");
                    addAuthor("http://samlib.ru/g/gromow_b/");

                    mDialog.dismiss();
                    Log.i("Журнал загрузки", "Загрузка завершена");

                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            builder.create().show();
        } else {
            // Запуск сервиса проверки обновлений
            /*if (this.startService(new Intent(this, UpdateService.class)) != null) {
                this.startService(new Intent(this, UpdateService.class));
                Log.v(this.getClass().getName(), "Service started.");
            } */

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Авторы обнаружены");
            String message = "Обнаружены следующие авторы:" + System.getProperty("line.separator");
            for(Authors author : AuthorsDS.getAllAuthors())
            {
                message = message + author.getAuthorName() + System.getProperty("line.separator");
            }
            builder.setMessage(message);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

            });

            builder.create().show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_setting:
                Intent settings_intent = new Intent(this, SettingsActivity.class);
                startActivity(settings_intent);
                return true;
            case R.id.menu_add_author:
                Intent add_author_intent = new Intent(this, AddAuthorActivity.class);
                startActivity(add_author_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected

        return networkInfo != null && networkInfo.isConnected();
    }
    boolean addAuthor(String AuthorLink) {
        myHttpHelper.parseLink(AuthorLink, Work, Author, WorkDS, AuthorsDS, context, UpdatedAuthors);
        return true;
    }
}
