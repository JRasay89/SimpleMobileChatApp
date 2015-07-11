package com.simplechatapp.john.simplemobilechatapp.helper;

import android.util.Log;

import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by John on 5/18/2015.
 */
public class ClientHttpRequest  {

    private final String TAG = ClientHttpRequest.class.getSimpleName();

    private InputStream inputStream;
    private JSONObject jsonObject;

    public ClientHttpRequest() {
        inputStream = null;
    }

    public JSONObject makeHttpRequest(String url, Method method, List<NameValuePair> params) {

        try {
            //Check what type of method the request will use
            //If method is POST
            if (method == Method.POST) {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse response = client.execute(httpPost);
                responseHandler(response);
            }

            //If method is GET
            else if (method == Method.GET) {
                HttpClient client = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse response = client.execute(httpGet);
                responseHandler(response);

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;
    } // End of makeHttpRequest

    /**
     * Handles the response from the Http request
     * @param response
     */
    private void responseHandler(HttpResponse response) {
        try {
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
                Log.d(TAG, "String Builder: " + stringBuilder.toString());
            }
            bufferedReader.close();
            jsonObject = new JSONObject(stringBuilder.toString());
            //Print the jsonObject in the event log
            Log.d(TAG, "Response: " + jsonObject.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
