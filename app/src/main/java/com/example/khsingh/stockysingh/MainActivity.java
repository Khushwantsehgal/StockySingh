package com.example.khsingh.stockysingh;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class MainActivity extends Activity implements View.OnClickListener {


    EditText mStock,mStockTrader; //Edittext for  Number of Stocks
    TextView mFinalAmount, mCurrencyCode,mStockSymbol2; //Holds Calculated final value, and Currency Code
    int mStockValue = 0; //HoldsNumber of Stocks
    Double mStockTradingAt = 0.00, mLocalCurrencyExchangeRate = 64.39, mAmountInLocalCurrency;
    AutoCompleteTextView mStockSymbol;
    String StockSelected,CurrencySelected;
    final int REQ_CODE_CURRENCY_SYMBOL = 2, REQ_CODE_STOCK_SYMBOL=3;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static String mLastTradePriceOnly;
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
        mStockSymbol2 = (TextView) findViewById(R.id.tv_StocksSymbol);
        mStockTrader = (EditText) findViewById(R.id.et_StocksTrader);

        // Reads the default values from preferences and passes the value to corresponding fields.

        {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            mStockValue = settings.getInt("StockCount", 0);
            Log.d("StockCount", String.valueOf(mStockValue));
            mStock.setText(String.valueOf(mStockValue));

            StockSelected = settings.getString("StockSelected", "ADBE");
            Log.d("StockSelected", String.valueOf(StockSelected));
            mStockSymbol2.setText(String.valueOf(StockSelected));

            CurrencySelected = settings.getString("CurrencySelected", "INR");
            Log.d("CurrencySelected", String.valueOf(CurrencySelected));
            mCurrencyCode.setText(String.valueOf(CurrencySelected));

        }



        {
            mStock.setOnKeyListener(mStocksFieldListener); // Reads the number of stocks and calculate the value of stocks in real time.
            mStock.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_1)); // Sends the KeyEvent Action UP and it causes the calculation to happen for stored value of mStock. It is only for the value stored in preferences.
        }

        {
            mCurrencyCode.setOnClickListener(this); // Opens a new activity to select currency codes
            mStockSymbol2.setOnClickListener(this); // Opens a new activity to select stockSymbols

        }




        /*
        mStockSymbol.addTextChangedListener(this);
        mStock.addTextChangedListener(); // read and use it in place of OnKeyListener
        */
    } //init views ends

    // Receives result from CurrencyCode/StockSymbol activities and sets the value for corresponding fields.
    

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Debug","requestCode: " + requestCode);
        Log.d("Debug", "resultCode: " + resultCode);

        if (requestCode == REQ_CODE_CURRENCY_SYMBOL && Activity.RESULT_OK == resultCode) {
            String message = data.getStringExtra("MESSAGE");
            mCurrencyCode.setText(message);
        }else if(requestCode == REQ_CODE_STOCK_SYMBOL && Activity.RESULT_OK == resultCode){
            String message = data.getStringExtra("MESSAGE");
            mStockSymbol2.setText(message);
        }
    } // onActivityResult ends

    private boolean isEmpty(EditText etText) {
        Toast.makeText(getApplicationContext(), " Length: " + etText.getText().toString().trim().length(), Toast.LENGTH_SHORT).show();
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_CurrencyCode: {
                Toast.makeText(getBaseContext(), "Try Reached", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainCurrencyCodeSelector.class);
                startActivityForResult(intent, REQ_CODE_CURRENCY_SYMBOL);
            }
            break;
            case R.id.tv_StocksSymbol: {
                Toast.makeText(getBaseContext(), "StockSymbol Reached", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainStockSymbolSelector.class);
                startActivityForResult(intent,REQ_CODE_STOCK_SYMBOL);
            }
            break;
            default:
            {
                Toast.makeText(getBaseContext(), "No view selected yet", Toast.LENGTH_SHORT).show();
            }

        }
    }//OnClick Ends


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

    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("StockCount", Integer.parseInt(mStock.getText().toString()));
        editor.putString("StockSelected", mStockSymbol2.getText().toString());
        editor.putString("CurrencySelected", mCurrencyCode.getText().toString());
        Log.d("Stop","onStop Called");

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

            return tempValue;

        }

        protected void onProgressUpdate(Void... progress) {
            // setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Double result) {
            // showDialog("Downloaded " + result + " bytes");
            Log.d("Debug", " onPostUpdate() : result : " + result.toString());
            showFinalPrice(result);
        }
    }//RetrieveStockPriceTask ends here

}//MainActivity Class ends here function e


