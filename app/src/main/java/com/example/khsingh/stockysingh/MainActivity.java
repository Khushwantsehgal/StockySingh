package com.example.khsingh.stockysingh;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

/**
 * The application calculates, on selling the stocks, the actual amount to be remitted into account after deducting various fees and charges.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    //Edittext for  Number of Stocks and Stock Trader
    EditText mStock,mStockTrader;

    //Textview for actual amount to be remitted into account, the Currency code for the final amount/
    TextView mFinalAmount, mCurrencyCode,mStockSymbol;

    //Integer to hold the number of stocks entered by user
    int mStockValue = 0;

    //Double to specify the current trading price of the stock, exchange rate for selected currency, and the final amount after conversion from USD to local currency
    Double mStockTradingAt = 0.00, mLocalCurrencyExchangeRate = 64.0415, mAmountInLocalCurrency;

    //Strings to set the value of stocks and currency symbols saved when the app was closed last time.
    String StockSelected,CurrencySelected;

    //Whether the correct currency and stock symbols code are returned from the selection activity
    final int REQ_CODE_CURRENCY_SYMBOL = 2, REQ_CODE_STOCK_SYMBOL=3;

    // Name of the Preference file. The file saves the number of stocks, stock symbol, and currency code selected by a user.
    public static final String PREFS_NAME = "StockySinghPreferenceFile";

    //Yahoo Finance Query to extract data for ADBE
    private static String mYQLQuery = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20%28%22ADBE%22%29&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    //private static String mYQLQuery = "http://api.androidhive.info/contacts/";
    private RetrieveStockPriceTask mRetrieveStockPriceTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mRetrieveStockPriceTask = new RetrieveStockPriceTask();
    } // onCreate Ends

    private void initViews() {
        mStock = (EditText) findViewById(R.id.et_Stocks);
        mFinalAmount = (TextView) findViewById(R.id.FinalAmount_textview);
        mCurrencyCode = (TextView) findViewById(R.id.tv_CurrencyCode);
        mStockSymbol = (TextView) findViewById(R.id.tv_StocksSymbol);
        mStockTrader = (EditText) findViewById(R.id.et_StocksTrader);


        // Reads the default values from preferences and passes the value to corresponding fields.
         SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

         mStockValue = settings.getInt("StockCount", 0);
         Log.d("StockCount", String.valueOf(mStockValue));
         mStock.setText((String.valueOf(mStockValue)).replaceFirst("^0+(?!$)", ""));

         StockSelected = settings.getString("StockSelected", "ADBE");
         Log.d("StockSelected", String.valueOf(StockSelected));
         mStockSymbol.setText(String.valueOf(StockSelected));

         CurrencySelected = settings.getString("CurrencySelected", "INR");
         Log.d("CurrencySelected", String.valueOf(CurrencySelected));
         mCurrencyCode.setText(String.valueOf(CurrencySelected));

        // Calculate the final amount
         mStock.addTextChangedListener(watcher);

        // Sends the KeyEvent Action Down and it causes the calculation to happen for stored value of mStock. It is only for the value stored in preferences
         mStock.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0 ));
        // removes the 0 added by last line
         mStock.setText((String.valueOf(mStockValue)).replaceFirst("^0+(?!$)", ""));

        // Opens a new activity to select currency codes and stockSymbols
         mCurrencyCode.setOnClickListener(this);
         mStockSymbol.setOnClickListener(this);

    } //init views ends

    // Receives result from CurrencyCode/StockSymbol activities and sets the value for corresponding fields.
    TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try{
                if (!isEmpty(mStock)) {
                    mStockValue = Integer.parseInt(mStock.getText().toString());

                } else {
                    Log.d("Debug","isEmpty: The mStock is empty and set to 0");
                    mStockValue = 0;
                }

            } catch (Exception e){
                e.printStackTrace();
            }finally
            {
                try {
                    mStockTradingAt = new RetrieveFeedTask().execute(mYQLQuery).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                mStockTrader.setText(mStockTradingAt.toString());
                mAmountInLocalCurrency = ((double) mStockValue * mStockTradingAt) * mLocalCurrencyExchangeRate;
                DecimalFormat df = new DecimalFormat("#.##");
                if (df instanceof DecimalFormat) {
                    ((DecimalFormat) df).setMinimumFractionDigits(2);
                    mFinalAmount.setText((df.format(mAmountInLocalCurrency)).toString());
                }
            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };// Text watcher ends

//Evaluates the result returned by StockSymbol and Currency Selection activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Debug","requestCode: " + requestCode);
        Log.d("Debug", "resultCode: " + resultCode);

        if (requestCode == REQ_CODE_CURRENCY_SYMBOL && Activity.RESULT_OK == resultCode) {
            String message = data.getStringExtra("MESSAGE");
            mCurrencyCode.setText(message);
        }else if(requestCode == REQ_CODE_STOCK_SYMBOL && Activity.RESULT_OK == resultCode){
            String message = data.getStringExtra("MESSAGE");
            mStockSymbol.setText(message);
        }
    } // onActivityResult ends


    // validates if an EditText is empty
    private boolean isEmpty(EditText etText) {
        Log.d("Debug", "isEmpty Length of stocks:" + etText.getText().toString().trim().length());
        return etText.getText().toString().trim().length() == 0;
    }

    // Starts the Activities to select currency code and stock symbol
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_CurrencyCode: {
                Intent intent = new Intent(MainActivity.this, MainCurrencyCodeSelector.class);
                startActivityForResult(intent, REQ_CODE_CURRENCY_SYMBOL);
            }
            break;
            case R.id.tv_StocksSymbol: {
                Intent intent = new Intent(MainActivity.this, MainStockSymbolSelector.class);
                startActivityForResult(intent,REQ_CODE_STOCK_SYMBOL);
            }
            break;
            default:
            {
                Log.d("Debug", "onClick(View v): No view selected yet");
            }

        }
    }//OnClick Ends

<<<<<<< HEAD
    // Saves the value in preferences when the application stops
=======

    private View.OnKeyListener mStocksFieldListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if ((event.getAction() == KeyEvent.ACTION_UP) || (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    try {
                        if (!isEmpty(mStock)) {
                            mStockValue = Integer.parseInt(mStock.getText().toString());

                        } else {
                            Toast.makeText(getApplicationContext(), "You did not enter a username", Toast.LENGTH_SHORT).show();
                            mStockValue = 0;
                        }
                    } catch (NumberFormatException n) {
                        Toast.makeText(getApplicationContext(), "Throwing Number format Exception", Toast.LENGTH_LONG).show();
                    } finally {
                        Toast.makeText(getApplicationContext(), "mStockTradingAt: " + mStockTradingAt, Toast.LENGTH_LONG).show();
                        Log.d("Debug2",mStockTradingAt.toString());
                        mRetrieveStockPriceTask.execute(mYQLQuery);
                         }
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }
            return false;
        }

    };

    private void showFinalPrice(Double mStockTradingAt2) {
        mStockTrader.setText(mStockTradingAt.toString());
        mAmountInLocalCurrency = ((double) mStockValue * mStockTradingAt2) * mLocalCurrencyExchangeRate;
        Log.d("Debug2", mAmountInLocalCurrency.toString());
        DecimalFormat df = new DecimalFormat("#.##");
        if (df instanceof DecimalFormat) {
            ((DecimalFormat) df).setMinimumFractionDigits(2);
            mFinalAmount.setText((df.format(mAmountInLocalCurrency)).toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Pause","onPause Called");
    }

>>>>>>> origin/master
    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        Log.d("Stop","onStop Called");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        if (!isEmpty(mStock)){
            editor.putInt("StockCount", Integer.parseInt(mStock.getText().toString()));
            Log.d("Debug","onStop StockCount" + Integer.parseInt(mStock.getText().toString()));
        }else{
            editor.putInt("StockCount", 0);
            Log.d("Debug","onStop StockCount is empty so set to " + "0");
        }

        editor.putString("StockSelected", mStockSymbol.getText().toString());
        editor.putString("CurrencySelected", mCurrencyCode.getText().toString());

        Log.d("Debug","onStop StockSelected" + mStockSymbol.getText().toString());
        Log.d("Debug","onStop CurrencySelected" + mCurrencyCode.getText().toString());


        // Commit the edits!
        editor.commit();
    }

    private class RetrieveStockPriceTask extends AsyncTask<String, Void, Double> {
        String mLastTradePriceOnly;

        protected Double doInBackground(String... urls) {
            StockyJSONParser stockyJSONParser = new StockyJSONParser();
            JSONObject jsonObject = stockyJSONParser.getJSONFromURL(urls[0]);
            Double tempValue=0.0;
            try{
                JSONObject mQuery = jsonObject.getJSONObject("query");
                JSONObject mResult = mQuery.getJSONObject("results");
                JSONObject mQuote = mResult.getJSONObject("quote");
                String value = mQuote.getString("LastTradePriceOnly");
                if(!value.isEmpty()){
                    tempValue = Double.parseDouble(value);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("Debug", "doInBackground(): tempValue: " + tempValue);
<<<<<<< Updated upstream

            return tempValue;

        }

        protected void onProgressUpdate(Void... progress) {
            // setProgressPercent(progress[0]);
        }

=======

            return tempValue;

        }

        protected void onProgressUpdate(Void... progress) {
            // setProgressPercent(progress[0]);
        }

>>>>>>> Stashed changes
        protected void onPostExecute(Double result) {
            // showDialog("Downloaded " + result + " bytes");
            Log.d("Debug", " onPostUpdate() : result : " + result.toString());
            showFinalPrice(result);
        }
    }//RetrieveStockPriceTask ends here

}//MainActivity Class ends here function e


