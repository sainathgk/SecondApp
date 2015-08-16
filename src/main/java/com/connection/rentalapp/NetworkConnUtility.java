package com.connection.rentalapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sainath on 14-08-2015.
 */
public class NetworkConnUtility {

    private NetworkResponseListener mCallbackListener;
    AsyncNetwork networkTask;

    public interface NetworkResponseListener {
        void onResponse(String urlString, String networkResult);
    }

    public NetworkConnUtility() {
    }

    public void setNetworkListener(NetworkResponseListener listener) {
        mCallbackListener = listener;
    }

    public void saveItem(String itemPayLoad) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "POST", NetworkConstants.SAVE_ITEM, itemPayLoad);
    }

    public void getItem(int itemId) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "GET", NetworkConstants.GET_ITEM + itemId);
    }

    public void getItemsByCategory(String categoryPayLoad) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "SEARCH", NetworkConstants.GET_ITEMS, categoryPayLoad);
    }

    public void saveUser(String userPayLoad) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "POST", NetworkConstants.SAVE_USER, userPayLoad);
    }

    public void getUser(String username) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "GET", NetworkConstants.GET_USER + username);
    }

    public void saveUserAddress(String addrPayLoad) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "POST", NetworkConstants.SAVE_USER_ADDRESS, addrPayLoad);
    }

    public void getUserProfileImage(String imageUrl) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "GET", imageUrl);
    }

    private class AsyncNetwork extends AsyncTask<String, Void, String> {
        private String urlString = null;

        @Override
        protected String doInBackground(String... params) {
            String method = params[0];
            urlString = params[1];
            String result = null;
            URL url = null;

            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (method.equalsIgnoreCase("POST")) {
                String payload = params[2];
                result = doPostMethod(url, payload);
            } else if (method.equalsIgnoreCase("GET")) {
                result = doGetMethod(url);
            } else if (method.equalsIgnoreCase("SEARCH")) {
                String payload = params[2];
                result = doSearchMethod(url, payload);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String resResult) {
            super.onPostExecute(resResult);
            mCallbackListener.onResponse(urlString, resResult);
        }
    }

    private String doPostMethod(URL postUrl, String payLoad) {
        HttpURLConnection urlConnect = createHttpConnection(postUrl);
        String responseMsg = null;
        try {
            urlConnect.setRequestMethod("POST");

            OutputStreamWriter postOutput = new OutputStreamWriter(urlConnect.getOutputStream());
            postOutput.write(payLoad);
            postOutput.flush();
            postOutput.close();

            urlConnect.connect();

            Log.i("Sainath", "Http Connection is ended");

            int responseCode = urlConnect.getResponseCode();
            responseMsg = urlConnect.getResponseMessage();

            Log.i("Sainath", "Http Connection is Succeeded" + responseCode);
            Log.i("Sainath", "Http Connection Response is " + responseMsg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
        }

        return responseMsg;
    }

    private String doSearchMethod(URL postUrl, String payLoad) {
        HttpURLConnection urlConnect = createHttpConnection(postUrl);
        String responseMsg = null;
        char[] output = new char[5000];
        try {
            urlConnect.setRequestMethod("POST");

            OutputStreamWriter postOutput = new OutputStreamWriter(urlConnect.getOutputStream());
            postOutput.write(payLoad);
            postOutput.flush();
            postOutput.close();

            urlConnect.connect();

            InputStreamReader getOutput = new InputStreamReader(urlConnect.getInputStream());
            getOutput.read(output);

            Log.i("Sainath", "Http Connection is ended");

            int responseCode = urlConnect.getResponseCode();
            responseMsg = urlConnect.getResponseMessage();

            Log.i("Sainath", "Http Connection is Succeeded" + responseCode);
            Log.i("Sainath", "Http Connection Response is " + responseMsg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
        }

        return new String(output);
    }

    private String doGetMethod(URL getUrl) {
        HttpURLConnection urlConnect = createHttpConnection(getUrl);
        char[] output = new char[500];
        try {
            urlConnect.setRequestMethod("GET");
            urlConnect.connect();

            InputStreamReader getOutput = new InputStreamReader(urlConnect.getInputStream());
            getOutput.read(output);

            Log.i("Sainath", "Http Connection is ended");

            int responseCode = urlConnect.getResponseCode();

            Log.i("Sainath", "Http Connection is Succeeded" + responseCode);
            Log.i("Sainath", "Http Connection Response is " + urlConnect.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
        }

        return new String(output);
    }

    private HttpURLConnection createHttpConnection(URL url) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnection;
    }
}