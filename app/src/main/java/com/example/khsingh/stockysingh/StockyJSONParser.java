package com.example.khsingh.stockysingh;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by khsingh on 5/10/15.
 */
public class StockyJSONParser {
    static InputStream mInputStream = null;
    static JSONObject  mJSONObject = null;
    static String mJSON ="";

    StockyJSONParser(){

    }

    public JSONObject getJSONFromURL(String URL){

        try{

            Log.d("Debug", "Reached in getJSONFromURL ");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Log.d("Debug", "Reached in getJSONFromURL 1");
            HttpPost httpPost = new HttpPost(URL);
            Log.d("Debug", "Reached in getJSONFromURL 2");

            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("Debug", "Reached in getJSONFromURL 3");
            HttpEntity httpEntity = httpResponse.getEntity();

            mInputStream = httpEntity.getContent();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();}
        catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } // End DefaultHTTPClient Try/Catch block

        try{

            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream, "iso-8859-1"),8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = reader.readLine())!= null){
                stringBuilder.append(line+"\n");
            }
            mInputStream.close();
            mJSON = stringBuilder.toString();


        }catch (Exception e){
            e.printStackTrace();
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        try{
            mJSONObject = new JSONObject(mJSON);

        }catch (Exception e){
            e.printStackTrace();
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return mJSONObject;


    }
}
