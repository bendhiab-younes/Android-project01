package com.example.projbdexterne;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
    Button ajouter, annuler, read ;

  //  DatePicker date_naissance;
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
//        date_naissance = findViewById(R.id.DateN);
        classe = findViewById(R.id.classe);
        ajouter = findViewById(R.id.btn_ajouter);
        annuler = findViewById(R.id.btn_annuler);
        read = findViewById(R.id.read_btn);

        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAsyncTask();
            }
        });
        read.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent( MainActivity.this, read_activity.class);
                        startActivity(i);
                    }
                }
        );
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

        /*// Get the selected date from the DatePicker
        int day = date_naissance.getDayOfMonth();
        int month = date_naissance.getMonth() + 1; // Month is 0-based, so add 1
        int year = date_naissance.getYear();
        params.put("DateNais", year + "-" + month + "-" + day);
        */
        params.put("classe", classe.getText().toString());

        JSONObject object = parser.makeHttpRequest("http://10.0.2.2/AndroidProject/api/add_etudiant.php", "POST", params);

        try {
            if (object != null) {
                // Check if the response contains the expected fields
                if (object.has("success") && object.has("message")) {
                    int success = object.getInt("success");
                    String message = object.getString("message");

                    if (success == 1) {
                        // The request was successful
                        Log.d("Server Response", "Success: " + success + ", Message: " + message);

                        if (object.has("data")) {
                            JSONObject data = object.getJSONObject("data");

                            // Now you can access individual fields in the 'data' object
                            Log.d("Server Response", "NCIN: " + data.getString("NCIN"));
                            Log.d("Server Response", "NCE: " + data.getString("NCE"));
                            Log.d("Server Response", "Nom: " + data.getString("Nom"));
                            Log.d("Server Response", "Prenom: " + data.getString("Prenom"));
                            Log.d("Server Response", "classe: " + data.getString("classe"));
                        } else {
                            Log.e("Server Response", "Missing 'data' field in the response");
                        }
                    } else {
                        // The request failed
                        Log.e("Server Response", "Error: " + message);
                    }
                } else {
                    Log.e("Server Response", "Invalid response format: " + object.toString());
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
            /*Intent i = new Intent( MainActivity.this, read_activity.class);
            startActivity(i);*/
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