package com.example.projbdexterne;

import static android.net.wifi.WifiConfiguration.Status.strings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class read_activity extends AppCompatActivity {

    ListView ls;
    JSONParser parser = new JSONParser();
    int success;

    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String,String>>();
    ProgressDialog dialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ls = findViewById(R.id.listEtd);

        new ReadData().execute();

        }


    class ReadData extends AsyncTask<Void, Void, Void> {

            String result;
            String[] data;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(read_activity.this);
                dialog.setMessage("fetching data...");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                JSONObject object = parser.makeHttpRequest("http://10.0.2.2/AndroidProject/api/fetch_etudiant.php", "GET", params);
                try {
                    success= object.getInt("success");
                    if (success== 1) {
                        JSONArray etudiants = object.getJSONArray("etudiants");
                        for (int i = 0; i < etudiants.length(); i++) {
                            JSONObject etudiant = etudiants.getJSONObject(i);
                            String id = etudiant.getString("id");
                            String NCIN = etudiant.getString("NCIN");
                            String NCE = etudiant.getString("NCE");
                            String nom = etudiant.getString("Nom");
                            String prenom = etudiant.getString("Prenom");
                            String classe = etudiant.getString("classe");
                            //map.put
                            HashMap<String, String> map = new HashMap<>();
                            map.put("id", id);
                            map.put("NCIN", NCIN);
                            map.put("NCE", NCE);
                            map.put("Nom", nom);
                            map.put("Prenom", prenom);
                            map.put("classe", classe);
                            values.add(map);
                        }
                    }
                    else {
                        result = object.getString("message");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
                if (success == 1) {
                   SimpleAdapter adapter = new SimpleAdapter(read_activity.this, values, R.layout.item,
                           new String[]{"id", "NCIN", "NCE","Nom", "Prenom", "classe"},
                           new int[]{R.id.num, R.id.myncin, R.id.mynce, R.id.mynom, R.id.myprenom, R.id.myclasse});
                    ls.setAdapter(adapter);
                }
            }

}}