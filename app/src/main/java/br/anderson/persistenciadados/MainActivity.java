package br.anderson.persistenciadados;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    int rdEditText[] = {R.id.sprdedittext, R.id.flrdedittext, R.id.bdrdedittext};

    private void setEditTextReadOnly(){
        for(int rdet: rdEditText){
            ((EditText)findViewById(rdet)).setText("admin");
            ((EditText)findViewById(rdet)).setEnabled(false);
        }
    }

    private void setPersistence(Context contexto){
        //SharedPreferences
        SharedPreferences minhasPreferencias = contexto.getSharedPreferences("SP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = minhasPreferencias.edit();
        editor.putString("LOGIN", "admin");
        editor.commit();
        //Arquivo - JSON
        ObjectMapper objectMapper = new ObjectMapper();
        Cadastro admin = new Cadastro("admin");
        File internalStorageDir = getFilesDir();
        File arquivo = new File(internalStorageDir, "cadastro.json");
        try {
            objectMapper.writeValue(arquivo, admin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //SQLite
        SQLiteDatabase myDB = openOrCreateDatabase("cadastro.db", MODE_PRIVATE, null);
        myDB.execSQL("CREATE TABLE IF NOT EXISTS usuario (login VARCHAR(20))");
        ContentValues registro = new ContentValues();
        registro.put("login", "admin");
        myDB.insert("usuario", null, registro);
        myDB.close();
    }

    private void verifyPersistence(Context contexto){
        ((Button) findViewById(R.id.btBD)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase myDB = openOrCreateDatabase("cadastro.db", MODE_PRIVATE, null);
                Cursor myCursor = myDB.rawQuery("select login from usuario", null);
                myCursor.moveToNext();
                String loginString = myCursor.getString(0);
                ((EditText)findViewById(R.id.bdedittext)).setText(loginString);
                myDB.close();
            }
        });

        ((Button) findViewById(R.id.btFL)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final ObjectMapper mapper = new ObjectMapper();
                    File internalStorageDir = getFilesDir();
                    File jsonFile = new File(internalStorageDir, "cadastro.json");
                    Cadastro cadastro = mapper.readValue(jsonFile, Cadastro.class);
                    ((EditText)findViewById(R.id.fledittext)).setText(cadastro.getLogin());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ((Button) findViewById(R.id.btSP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.spedittext)).setText(contexto.getSharedPreferences("SP", MODE_PRIVATE).getString("LOGIN", "TESTE"));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPersistence(this.getApplicationContext());
        setEditTextReadOnly();
        verifyPersistence(this.getApplicationContext());
    }
}