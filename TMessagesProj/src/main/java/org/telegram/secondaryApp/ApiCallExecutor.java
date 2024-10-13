package org.telegram.secondaryApp;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.gson.Gson;
import org.telegram.tgnet.TLRPC;
import org.json.JSONObject;

public class ApiCallExecutor {

    private ExecutorService executor;

    public ApiCallExecutor() {
        executor = Executors.newFixedThreadPool(10);
    }

    public void executeApiCall(TLRPC.TL_message message, long channel) {
        // Now you can work with the message object
        Integer reply_id = 0;

        if(message.reply_to instanceof TLRPC.TL_messageReplyHeader){
            TLRPC.TL_messageReplyHeader reply_to = ( TLRPC.TL_messageReplyHeader) message.reply_to;
            reply_id=reply_to.reply_to_msg_id;
            Log.d("ApiCallExecutor", "reply_id "+ reply_id);
        }

        Integer finalReply_id = reply_id;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();

                try{
                    jsonObject.put("message", removeLineBreaks(message.message));
                    jsonObject.put("channel", channel);
                    jsonObject.put("reply_to", finalReply_id);
                    jsonObject.put("time", message.date);
                    jsonObject.put("id", message.id);
                    String jsonString = jsonObject.toString();
                    Log.d("ApiCallExecutor", "jsonString: " + jsonString);

                     String response = callApi(jsonString);
                }catch (Exception e){
                }
            }
        });
    }
    private Gson gson = new Gson();

    private String callApi(String jsonData) {
        String result = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("http://tradeauto.in/create-gtt-order");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);  // Send the JSON data
            }

            int responseCode = urlConnection.getResponseCode();

            Log.d("ApiCallExecutor", "Response Code: " + responseCode);

             if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                result = responseBuilder.toString();  // The response from the server
            } else {
                Log.e("ApiCallExecutor", "POST request failed with response code: " + responseCode);
            }

            Log.d("ApiCallExecutor", "API Response: " + result);

            // Parse the JSON response using Gson (example with a simple model)
           // ApiResponse apiResponse = gson.fromJson(result, ApiResponse.class);
           // Log.d("ApiCallExecutor", "Parsed API Response: " + apiResponse.toString());

        } catch (IOException e) {
            Log.e("ApiCallExecutor", "Error making API call", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("ApiCallExecutor", "Error closing reader", e);
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }

    // Simple model for parsing API response (replace with your actual API response structure)
    class ApiResponse {
        private String data;

        @Override
        public String toString() {
            return "ApiResponse{data='" + data + "'}";
        }
    }

    public static String removeLineBreaks(String input) {
        // Removing line breaks and converting to a single line
        return input.replaceAll("[\\r\\n]+", " ").trim();
    }
}
