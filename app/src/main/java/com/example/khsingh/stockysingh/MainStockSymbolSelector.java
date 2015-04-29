package com.example.khsingh.stockysingh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by khsingh on 4/28/2015.
 */
public class MainStockSymbolSelector extends Activity {

    ListView mStockSymbol;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocksymbolselector);
        initViews();

        String[] StockSymbol = getResources().getStringArray(R.array.USA_National_Stock_Exchange);
        ArrayAdapter<String> a_StockSymbol = new ArrayAdapter<String>(this, R.layout.layout_for_stock_symbols, StockSymbol);
        mStockSymbol.setAdapter(a_StockSymbol);

        mStockSymbol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    message = mStockSymbol.getItemAtPosition(position).toString();
                    Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplication(), "Exception Occurred", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(MainStockSymbolSelector.this, MainActivity.class);
                intent.putExtra("MESSAGE", message);
                setResult(3, intent);
                finish();
            }
        });
    }

    private void initViews() {
        mStockSymbol = (ListView) findViewById(R.id.lv_StockSymbol_ListView);
    }
}
