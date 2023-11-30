package com.example.projbdexterne;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class JSONParser {

    private static final String CHARSET_UTF8 = "UTF-8";
    private static final int TIMEOUT_CONNECTION = 15000;
    private static final int TIMEOUT_READ = 10000;

    public JSONObject makeHttpRequest(String url, String method, HashMap<String, String> params) {
        HttpURLConnection conn = null;
        DataOutputStream wr = null;
        StringBuilder result = new StringBuilder();

        try {
            URL urlObj = new URL(url);
            conn = (HttpURLConnection) urlObj.openConnection();

            // Common settings for both GET and POST
            conn.setReadTimeout(TIMEOUT_READ);
            conn.setConnectTimeout(TIMEOUT_CONNECTION);
            conn.setRequestProperty("Accept-Charset", CHARSET_UTF8);

            if (method.equals("POST")) {
                // POST-specific settings
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Build parameters for POST request
                StringBuilder sbParams = new StringBuilder();
                int i = 0;
                for (String key : params.keySet()) {
                    if (i != 0) {
                        sbParams.append("&");
                    }
                    sbParams.append(URLEncoder.encode(key, CHARSET_UTF8))
                            .append("=")
                            .append(URLEncoder.encode(params.get(key), CHARSET_UTF8));
                    i++;
                }

                // Write parameters to the output stream
                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(sbParams.toString());
                wr.flush();
            } else if (method.equals("GET")) {
                // GET-specific settings
                conn.setRequestMethod("GET");
                conn.setDoOutput(false);
            }

            // Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            Log.e("JSON Parser", "IOException: " + e.getMessage());
        } finally {
            // Close resources
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                conn.disconnect();
            }
        }

        JSONObject jObj = null;

        // Try parsing the string to a JSON object
        try {
            if (result.length() > 0) {
                // Check for the specific string indicating an invalid response
                if (!result.toString().equals("Invalid data format")) {
                    jObj = new JSONObject(result.toString());
                } else {
                    Log.e("JSON Parser", "Server response indicates an invalid result: " + result.toString());
                }
            } else {
                Log.e("JSON Parser", "Empty response from the server");
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // Return JSON Object
        return jObj;
}
}
