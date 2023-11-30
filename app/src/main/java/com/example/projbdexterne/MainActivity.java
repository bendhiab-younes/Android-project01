package com.example.projbdexterne;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    EditText nom, prenom, NCIN, NCE, classe;
    Button ajouter, annuler;

    DatePicker date_naissance;
    ProgressDialog dialog;

    JSONParser parser = new JSONParser();

    int success;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_ajouter);

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        NCIN = findViewById(R.id.ncin);
        NCE = findViewById(R.id.nce);
        date_naissance = findViewById(R.id.DateN);
        classe = findViewById(R.id.classe);
        ajouter = findViewById(R.id.btn_ajouter);
        annuler = findViewById(R.id.btn_annuler);

        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAsyncTask();
            }
        });
    }

    private void executeAsyncTask() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("progress...");
        dialog.show();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                doInBackground();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute();
                    }
                });
            }
        });
    }

    private void doInBackground() {
        HashMap<String, String> params = new HashMap<>();
        params.put("Nom", nom.getText().toString());
        params.put("Prenom", prenom.getText().toString());
        params.put("NCIN", NCIN.getText().toString());
        params.put("NCE", NCE.getText().toString());

        // Get the selected date from the DatePicker
        int day = date_naissance.getDayOfMonth();
        int month = date_naissance.getMonth() + 1; // Month is 0-based, so add 1
        int year = date_naissance.getYear();

        params.put("DateNais", year + "-" + month + "-" + day);
        params.put("classe", classe.getText().toString());

        JSONObject object = parser.makeHttpRequest("http://192.168.0.3/AndroidProject/api/add_etudiant.php", "POST", params);

        try {
            if (object != null) {
                if (object.has("NCIN") && object.has("NCE") && object.has("Nom") && object.has("Prenom") && object.has("DateNais") && object.has("classe")) {
                    // Check if the response contains the expected fields
                    Log.d("Server Response", "NCIN: " + object.getString("NCIN"));
                    Log.d("Server Response", "NCE: " + object.getString("NCE"));
                    Log.d("Server Response", "Nom: " + object.getString("Nom"));
                    Log.d("Server Response", "Prenom: " + object.getString("Prenom"));
                    Log.d("Server Response", "DateNais: " + object.getString("DateNais"));
                    Log.d("Server Response", "classe: " + object.getString("classe"));

                    if (object.has("success") && object.has("message")) {
                        success = object.getInt("success");
                        String message = object.getString("message");
                        Log.d("Server Response", "Success: " + success + ", Message: " + message);
                    } else {
                        Log.e("Server Response", "Invalid response format: " + object.toString());
                    }
                } else {
                    Log.e("Server Response", "Missing fields in the response: " + object.toString());
                }
            } else {
                Log.e("Server Response", "Null response from the server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void onPostExecute() {
        dialog.cancel();

        if (success == 1) {
            Toast.makeText(MainActivity.this, "Etudiant ajouté avec succès", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Erreur lors de l'ajout", Toast.LENGTH_LONG).show();
        }
    }
}


/*
    ExecutorService service = Executors.newFixedThreadPool(3);

    service.execute(new Runnable() {
        public void run() {
            // Do something
        }
    });   service.execute(new Runnable() {
        public void run() {
            // Do something
        }
    });   service.execute(new Runnable() {
        public void run() {
            // Do something
        }
    });
*/