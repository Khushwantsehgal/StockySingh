package com.example.khsingh.stockysingh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class MainActivity extends Activity implements View.OnClickListener{


    EditText mStock; //Edittext for  Number of Stocks
    TextView mFinalAmount, mCurrencyCode; //Holds Calculated final value, and Currency Code
    int mStockValue=0; //HoldsNumber of Stocks
    Double mStockTradingAt = 73.21,mLocalCurrencyExchangeRate=62.55,mAmountInLocalCurrency;
    final int REQ_CODE_CURRENCY_SYMBOL = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        mStock = (EditText) findViewById(R.id.et_Stocks);
        mFinalAmount = (TextView) findViewById(R.id.FinalAmount_textview);
        mCurrencyCode = (TextView) findViewById(R.id.tv_CurrencyCode);

        mStock.setOnKeyListener(mOnKeyListener);
        mCurrencyCode.setOnClickListener(this);

        //mStock.addTextChangedListener(); // read and use it in place of OnKeyListener
    }

    protected void  onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE_CURRENCY_SYMBOL){
            String message=data.getStringExtra("MESSAGE");
            mCurrencyCode.setText(message);
        }
    }

    private boolean isEmpty(EditText etText) {
        Toast.makeText(getApplicationContext()," Length: " + etText.getText().toString().trim().length(),Toast.LENGTH_SHORT).show();
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.tv_CurrencyCode:
            {
                Toast.makeText(getBaseContext(),"Try Reached", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainCurrencyCodeSelector.class);
                startActivityForResult(intent, REQ_CODE_CURRENCY_SYMBOL);
            }
            break;
        }
    }//OnClick Ends



    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try{
                if ((event.getAction() == KeyEvent.ACTION_UP) || (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    try{
                        if(!isEmpty(mStock)){
                            mStockValue = Integer.parseInt(mStock.getText().toString());

                        }else{
                            Toast.makeText(getApplicationContext(), "You did not enter a username", Toast.LENGTH_SHORT).show();
                            mStockValue = 0;
                        }
                    }
                    catch (NumberFormatException n){
                        Toast.makeText(getApplicationContext(),"Throwing Number format Exception",Toast.LENGTH_LONG).show();
                    }
                    finally {
                        Toast.makeText(getApplicationContext(),"Finally",Toast.LENGTH_LONG).show();
                        mAmountInLocalCurrency = ((double) mStockValue * mStockTradingAt) * mLocalCurrencyExchangeRate;
                        DecimalFormat df = new DecimalFormat("#.##");
                        if (df instanceof DecimalFormat) {
                            ((DecimalFormat)df).setMinimumFractionDigits(2);
                            mFinalAmount.setText((df.format(mAmountInLocalCurrency)).toString());
                        }

                    }
                }

            }catch (Exception e){
                //e.printStackTrace();
            }
            return false;
        }

    };

}
