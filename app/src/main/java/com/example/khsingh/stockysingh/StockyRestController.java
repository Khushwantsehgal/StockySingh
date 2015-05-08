package com.example.khsingh.stockysingh;
import android.util.Log;

import org.apache.http.Header;
import org.json.*;

import com.google.gson.Gson;
import com.loopj.android.http.*;
/**
 * Created by khsingh on 5/9/2015.
 */
public class StockyRestController {
    public void getPublicTimeline() throws JSONException {
        StockySinghRestClient.get(StockySinghRestClient.BASE_URL_YQL, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Gson gson = new Gson();
                Query object = gson.fromJson(response.toString(), Query.class);
                Log.d("Debug",object.results.quote.LastTradePriceOnly);
            }
        });
    }
}
