package com.example.khsingh.stockysingh;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class MainActivity extends Activity implements View.OnClickListener {


    EditText mStock; //Edittext for  Number of Stocks
    TextView mFinalAmount, mCurrencyCode,mStockSymbol2; //Holds Calculated final value, and Currency Code
    int mStockValue = 0; //HoldsNumber of Stocks
    Double mStockTradingAt = 73.21, mLocalCurrencyExchangeRate = 62.55, mAmountInLocalCurrency;
    AutoCompleteTextView mStockSymbol;
    String StockSelected;
    final int REQ_CODE_CURRENCY_SYMBOL = 2, REQ_CODE_STOCK_SYMBOL=3;
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mStockValue = settings.getInt("StockCount", -1);
        Log.d("Yahoo",String.valueOf(mStockValue));
        mStock.setText(String.valueOf(mStockValue));

        StockSelected = settings.getString("StockSelected","NoStockSelected");
        Log.d("Yahoo",String.valueOf(StockSelected));
        mStockSymbol2.setText(String.valueOf(StockSelected));


    }

    private void initViews() {
        mStock = (EditText) findViewById(R.id.et_Stocks);
        mFinalAmount = (TextView) findViewById(R.id.FinalAmount_textview);
        mCurrencyCode = (TextView) findViewById(R.id.tv_CurrencyCode);
        mStockSymbol2 = (TextView) findViewById(R.id.tv_StocksSymbol);

        mStock.setOnKeyListener(mStocksFieldListener);
        mCurrencyCode.setOnClickListener(this);
        mStockSymbol2.setOnClickListener(this);
        /*
        mStockSymbol.addTextChangedListener(this);
        mStock.addTextChangedListener(); // read and use it in place of OnKeyListener
        */
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_CURRENCY_SYMBOL) {
            String message = data.getStringExtra("MESSAGE");
            mCurrencyCode.setText(message);
        }else if(requestCode == REQ_CODE_STOCK_SYMBOL){
            String message = data.getStringExtra("MESSAGE");
            mStockSymbol2.setText(message);
        }
    }

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
                        Toast.makeText(getApplicationContext(), "Finally", Toast.LENGTH_LONG).show();
                        mAmountInLocalCurrency = ((double) mStockValue * mStockTradingAt) * mLocalCurrencyExchangeRate;
                        DecimalFormat df = new DecimalFormat("#.##");
                        if (df instanceof DecimalFormat) {
                            ((DecimalFormat) df).setMinimumFractionDigits(2);
                            mFinalAmount.setText((df.format(mAmountInLocalCurrency)).toString());
                        }

                    }
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }
            return false;
        }

    };

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
        Log.d("Stop","onStop Called");

        // Commit the edits!
        editor.commit();
    }

    function searchYQL()
}


