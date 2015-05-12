package com.example.khsingh.stockysingh;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by khsingh on 5/12/15.
 */
class RetrieveFeedTask extends AsyncTask<String, Void, Double> {

    String mLastTradePriceOnly;
    private Exception exception;

    protected Double doInBackground(String... URL) {

            StockyJSONParser stockyJSONParser = new StockyJSONParser();
            JSONObject jsonObject = stockyJSONParser.getJSONFromURL(URL[0]);
            try{
                JSONObject mQuery = jsonObject.getJSONObject("query");
                JSONObject mResult = mQuery.getJSONObject("results");
                JSONObject mQuote = mResult.getJSONObject("quote");
                mLastTradePriceOnly = mQuote.getString("LastTradePriceOnly");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("Debug",String.valueOf(mLastTradePriceOnly ));
            return Double.parseDouble(mLastTradePriceOnly);
    }


    protected void onPostExecute(Double result) {
        // TODO: check this.exception
        // TODO: do something with the feed


    }
}