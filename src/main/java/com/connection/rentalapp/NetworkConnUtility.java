package com.connection.rentalapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sainath on 14-08-2015.
 */
public class NetworkConnUtility {

    private Context mContext;
    private NetworkResponseListener mCallbackListener;
    private ImageResponseListener mImageCbListener;
    private AsyncNetwork networkTask;
    private AsyncImageDownloadTask mImageTask;

    public interface NetworkResponseListener {
        void onResponse(String urlString, String networkResult);
    }

    public interface ImageResponseListener {
        void onImageResponse(String urlString, Bitmap imageResult);
    }

    public NetworkConnUtility(Context ctx) {
        mContext = ctx;
    }

    public void setNetworkListener(NetworkResponseListener listener) {
        mCallbackListener = listener;
    }

    public void setImageListener(ImageResponseListener listener) {
        mImageCbListener = listener;
    }

    public void saveItem(final String itemPayLoad) {
        if (NetworkConstants.isServerON) {
            networkTask = new AsyncNetwork();
            networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "POST", NetworkConstants.SAVE_ITEM, itemPayLoad);
        } else {

            Runnable runnable = new Runnable() {
                public void run() {
                    String[] itemColumns = {"id", "category", "subcategory", "name", "description", "duration",
                            "price", "userName", "thumbnailList"};
                    ContentValues values = null;
                    try {
                        JSONUtility jsonUtility = new JSONUtility();
                        jsonUtility.setColumsList(itemColumns);
                        values = jsonUtility.fromJSON(new JSONObject(itemPayLoad));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (values != null) {
                        mContext.getContentResolver().insert(Uri.parse("content://com.database.rentalapp/POST_TABLE"), values);
                        mCallbackListener.onResponse(NetworkConstants.SAVE_ITEM, "Success");
                    } else {
                        mCallbackListener.onResponse(NetworkConstants.SAVE_ITEM, "Failed");
                    }
                }
            };
            new Thread(runnable).run();
        }
    }

    public void getItem(int itemId) {
        networkTask = new AsyncNetwork();
        networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "GET", NetworkConstants.GET_ITEM + itemId);
    }

    public void getItemsByCategory(final String categoryPayLoad) {
        if (NetworkConstants.isServerON) {
            networkTask = new AsyncNetwork();
            networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "SEARCH", NetworkConstants.GET_ITEMS, categoryPayLoad);
        } else {

            Runnable runnable = new Runnable() {
                public void run() {
                    String[] itemColumns = {"id", "category", "subcategory", "name", "description", "duration",
                            "price", "userName", "thumbnailList"};
                    JSONUtility jsonUtility = new JSONUtility();
                    JSONObject jsonObj;
                    String cateString = "Cameras";
                    try {
                        jsonUtility.setColumsList(itemColumns);
                        jsonObj = new JSONObject(categoryPayLoad);
                        cateString = jsonObj.getJSONArray("searchList").getJSONObject(0).getString("value");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Cursor mItemsCursor = mContext.getContentResolver().query(Uri.parse("content://com.database.rentalapp/POST_TABLE"), null, "category == '" + cateString + "'", null, null);
                    JSONObject itemsObject = new JSONObject();
                    JSONArray itemsArray = new JSONArray();
                    if (mItemsCursor != null && mItemsCursor.getCount() > 0) {

                        try {
                            while (mItemsCursor.moveToNext()) {
                                JSONObject itemJson = jsonUtility.toJSON(mItemsCursor);
                                String userName = itemJson.getString("userName");
                                Cursor userCursor = mContext.getContentResolver().query(Uri.parse("content://com.database.rentalapp/USER_ADDRESS"), null, "userName == '" + userName + "'", null, null);
                                if (userCursor != null && userCursor.getCount() > 0) {
                                    userCursor.moveToFirst();
                                    itemJson.put("Latitude", userCursor.getFloat(userCursor.getColumnIndex("Latitude")));
                                    itemJson.put("Longitude", userCursor.getFloat(userCursor.getColumnIndex("Longitude")));
                                }

                                itemsArray.put(itemJson);
                            }
                            itemsObject.put("items", itemsArray);
                            mItemsCursor.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (itemsObject != null) {
                        Log.i("Sainath", itemsObject.toString());
                        mCallbackListener.onResponse(NetworkConstants.GET_ITEMS, itemsObject.toString());
                    } else {
                        mCallbackListener.onResponse(NetworkConstants.GET_ITEMS, "Failed");
                    }
                }
            };

            new Thread(runnable).run();
        }
    }

    public void saveUser(final String userPayLoad) {
        if (NetworkConstants.isServerON) {
            networkTask = new AsyncNetwork();
            networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "POST", NetworkConstants.SAVE_USER, userPayLoad);
        } else {

            Runnable runnable = new Runnable() {
                public void run() {
                    String[] itemColumns = {"userName", "password", "firstName", "lastName", "initials", "birthDate",
                            "gender", "emailId", "mobileNumber", "officeNumber", "profileImage"};
                    ContentValues values = null;
                    try {
                        JSONUtility jsonUtility = new JSONUtility();
                        jsonUtility.setColumsList(itemColumns);
                        values = jsonUtility.fromJSON(new JSONObject(userPayLoad));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (values != null) {
                        mContext.getContentResolver().insert(Uri.parse("content://com.database.rentalapp/USER_DETAILS"), values);
                        mCallbackListener.onResponse(NetworkConstants.SAVE_USER, "Success");
                    } else {
                        mCallbackListener.onResponse(NetworkConstants.SAVE_USER, "Failed");
                    }
                }
            };

            new Thread(runnable).run();
        }
    }

    public void getUser(final String username) {
        if (NetworkConstants.isServerON) {
            if (username != null) {
                networkTask = new AsyncNetwork();
                networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "GET", NetworkConstants.GET_USER + username);
            }
        } else {

            Runnable runnable = new Runnable() {
                public void run() {
                    String[] itemColumns = {"userName", "password", "firstName", "lastName", "initials", "birthDate",
                            "gender", "emailId", "mobileNumber", "officeNumber", "profileImage"};
                    JSONUtility jsonUtility = new JSONUtility();
                    String cateString = username;
                    jsonUtility.setColumsList(itemColumns);

                    Cursor mItemsCursor = mContext.getContentResolver().query(Uri.parse("content://com.database.rentalapp/USER_DETAILS"), null, "userName == '" + cateString+"'", null, null);
                    String mItemsOutput = null;
                    if (mItemsCursor != null && mItemsCursor.getCount() > 0) {
                        mItemsCursor.moveToFirst();
                        try {
                            mItemsOutput = jsonUtility.toJSON(mItemsCursor).toString();
                            mItemsCursor.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mItemsOutput != null) {
                        Log.i("Sainath", mItemsOutput);
                        mCallbackListener.onResponse(NetworkConstants.GET_USER, mItemsOutput);
                    } else {
                        mCallbackListener.onResponse(NetworkConstants.GET_USER, "Failed");
                    }
                }
            };

            new Thread(runnable).run();
        }
    }

    public void saveUserAddress(final String addrPayLoad) {
        if (NetworkConstants.isServerON) {
            networkTask = new AsyncNetwork();
            networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "POST", NetworkConstants.SAVE_USER_ADDRESS, addrPayLoad);
        } else {

            Runnable runnable = new Runnable() {
                public void run() {
                    String[] itemColumns = {"userName", "houseNumber", "addressLine1", "addressLine2", "addressLine3", "addressLine4",
                            "addressLine5", "Latitude", "Longitude"};
                    ContentValues values = null;
                    try {
                        JSONUtility jsonUtility = new JSONUtility();
                        jsonUtility.setColumsList(itemColumns);
                        values = jsonUtility.fromJSON(new JSONObject(addrPayLoad));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (values != null) {
                        mContext.getContentResolver().insert(Uri.parse("content://com.database.rentalapp/USER_ADDRESS"), values);
                        mCallbackListener.onResponse(NetworkConstants.SAVE_USER_ADDRESS, "Success");
                    } else {
                        mCallbackListener.onResponse(NetworkConstants.SAVE_USER_ADDRESS, "Failed");
                    }
                }
            };
            new Thread(runnable).run();
        }
    }

    public void getUserAddress(final String userName) {
        if (NetworkConstants.isServerON) {
            if (userName != null) {
                networkTask = new AsyncNetwork();
                networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "GET", NetworkConstants.GET_USER_ADDRESS, userName);
            }
        } else {

            Runnable runnable = new Runnable() {
                public void run() {
                    String[] itemColumns = {"userName", "houseNumber", "addressLine1", "addressLine2", "addressLine3", "addressLine4",
                            "addressLine5", "Latitude", "Longitude"};
                    Cursor mItemsCursor = mContext.getContentResolver().query(Uri.parse("content://com.database.rentalapp/USER_ADDRESS"), null, "userName == '" + userName +"'", null, null);
                    String mItemsOutput = null;
                    JSONUtility jsonUtility = new JSONUtility();
                    if (mItemsCursor != null && mItemsCursor.getCount() > 0) {
                        mItemsCursor.moveToFirst();
                        try {
                            mItemsOutput = jsonUtility.toJSON(mItemsCursor).toString();
                            mItemsCursor.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mItemsOutput != null) {
                        Log.i("Sainath", mItemsOutput);
                        mCallbackListener.onResponse(NetworkConstants.GET_USER_ADDRESS, mItemsOutput);
                    } else {
                        mCallbackListener.onResponse(NetworkConstants.GET_USER_ADDRESS, "Failed");
                    }
                }
            };
            new Thread(runnable).run();
        }
    }

    public void reserveItem(String itemPayLoad) {
        if (NetworkConstants.isServerON) {
            networkTask = new AsyncNetwork();
            networkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "POST", NetworkConstants.RESERVE_ITEM, itemPayLoad);
        }
        else {
            mCallbackListener.onResponse(NetworkConstants.RESERVE_ITEM, "Success");
        }
    }

    public void getUserProfileImage(String imageUrl) {
        if (imageUrl != null) {
            mImageTask = new AsyncImageDownloadTask();
            mImageTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "GET", imageUrl);
        }
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

    private class AsyncImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private String urlString = null;

        @Override
        protected Bitmap doInBackground(String... params) {
            String method = params[0];
            urlString = params[1];
            Bitmap result = null;
            URL url = null;

            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (method.equalsIgnoreCase("GET")) {
                result = doGetImageBlob(url);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bitmap resResult) {
            super.onPostExecute(resResult);
            mImageCbListener.onImageResponse(urlString, resResult);
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

    private Bitmap doGetImageBlob(URL imageUrl) {
        HttpURLConnection urlConnect = createHttpConnection(imageUrl);
        Bitmap myBitmap = null;

        try {
            urlConnect.setRequestMethod("GET");
            urlConnect.connect();

            InputStream input = urlConnect.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

            Log.i("Sainath", "Image Download is ended");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
        }

        return myBitmap;
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