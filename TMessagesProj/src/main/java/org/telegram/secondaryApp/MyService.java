package org.telegram.secondaryApp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the AsyncTask when the service starts
        new ApiCallTask().execute();

        // If you want to stop the service after completing the task, return START_NOT_STICKY
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  // Not used in this example
    }

    // AsyncTask implementation
    private class ApiCallTask extends AsyncTask<Void, Void, String> {
        private static final String API_URL = "https://api.example.com/data"; // Replace with your API URL

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                // API call logic here (e.g., using HttpURLConnection)
                result = "API response"; // Replace with real API call logic
            } catch (Exception e) {
                Log.e("ApiCallTask", "Error making API call", e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Handle the result (e.g., log it or perform some action)
            Log.d("ApiCallTask", "API Response: " + result);
        }
    }
}
