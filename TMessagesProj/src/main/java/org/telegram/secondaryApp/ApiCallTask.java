package org.telegram.secondaryApp;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCallTask extends AsyncTask<Void, Void, String> {
    private static final String API_URL = "http://tradeauto.in"; // Replace with your API URL

    @Override
    protected String doInBackground(Void... voids) {
        String result = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create a URL object from the string URL
            URL url = new URL(API_URL);
            // Open a connection to the API
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET"); // Use GET or POST based on your API

            // Read the response from the API
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            result = stringBuilder.toString();  // Get the response as a string
            Log.d("ApiCallTask", "API Response: " + result);
            /* create a function to parse the result as json and return the data */


        } catch (Exception e) {
            Log.e("ApiCallTask", "Error making API call", e);
        } finally {
            // Clean up and close streams
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e("ApiCallTask", "Error closing reader", e);
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result; // Return the result to onPostExecute
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Handle the result on the main thread
        if (result != null && !result.isEmpty()) {
            Log.d("ApiCallTask", "API Response: " + result);
            // Do something with the API response here
        } else {
            Log.e("ApiCallTask", "Error: No response or empty response");
        }
    }
}
