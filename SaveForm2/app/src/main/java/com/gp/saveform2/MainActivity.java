package com.gp.saveform2;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.BuddhistCalendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static Activity mActivity;
    private static Context mContext;
    private static View view;
    DBHelper db;
    private DrawerLayout drawer;
    private ImageView avatar;
    private ListView obj;
    private Object item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        // custom toolbar (hamburger)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // ActionBar jako default
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        NavigationView navigationView = (NavigationView) findViewById(R.id.sideBar);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        db = new DBHelper(this);
        /// LISTA ODCZYTU
        ArrayList lst = db.getAllEntrys();
        // Adapter do danych
        /*ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lst);
        obj = (ListView) findViewById(R.id.lstEntrys); // pliku layoutu XML
        obj.setAdapter(ad);
        
        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle data = new Bundle();
                int idToSearch = i+1;   // ID dla wiersza w BD
                data.putInt("id",idToSearch);
                Intent intent = new Intent(getApplicationContext(),DisplayEntry.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        /// ZAPIS
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                EditText surname, name, city, born;
                surname = (EditText) findViewById(R.id.entSurname);
                name = (EditText) findViewById(R.id.entName);
                city = (EditText) findViewById(R.id.entCity);
                born = (EditText) findViewById(R.id.entBornDate);
                CheckBox rules = (CheckBox) findViewById(R.id.cbxAcptRules);
                CheckBox gdpr = (CheckBox) findViewById(R.id.cbxAcptGdpr);
                Integer acptRules = rules.isChecked() ? 1 : 0;
                Integer acptGdpr = gdpr.isChecked() ? 1 : 0;
				// wywołanie metody zapisu z klasy DBHelper
                boolean saved = db.insertEntry(
                        surname.getText().toString(),
                        name.getText().toString(),
                        city.getText().toString(),
                        born.getText().toString(),
                        acptRules, acptGdpr
                );
                // wyświetlenie komunikatu
                String msg = (saved==true) ? "Saved" : "Did NOT";
                Snackbar.make(view,msg,Snackbar.LENGTH_LONG).setAction("Action",null).show();
            }
        }
        );
        */
        // na uruchomienie aplikacji - startowy fragment
        if(savedInstanceState == null) {
            AboutFragment af = new AboutFragment();
            //af.setArguments(generateFragmentBundle(username, uid)); // generateFragmentBundle()
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content,af).commit();
            navigationView.setCheckedItem(R.id.about);
        }
        View header = navigationView.getHeaderView(0);
        avatar = (ImageView) header.findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
                selectImage(mContext, mActivity);
            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // kliknięcie wpisu w menu
        switch(item.getItemId()) {
            case R.id.register:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content,
                        new RegisterFragment()).commit();
                break;
            case R.id.about:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content,
                        new AboutFragment()).commit();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void requestPermission() {
        System.out.println("INIT Żądanie STORAGE");
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);//WRITE_EXTERNAL_STORAGE
    }
    private void selectImage(Context context, final Activity mActivity) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            showMessageOKCancel("Aby odczytać z lokalnego dysku obraz - aplikacja wymaga dostępu do tego dysku.",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);//WRITE_EXTERNAL_STORAGE
                            }
                        }
                    });
            return;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Anuluj", null)
                .create()
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        avatar.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                avatar.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
