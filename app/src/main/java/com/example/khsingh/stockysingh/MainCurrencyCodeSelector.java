package com.example.khsingh.stockysingh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

/**
 * Created by khsingh on 4/21/15.
 */
public class MainCurrencyCodeSelector extends Activity {

    ListView CurrencySymbol;
    Set<Currency> availableCurrenciesSet;
    List<Currency> availableCurrenciesList;
    ArrayAdapter<Currency> adapter;
    String message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencycodeselector);
        CurrencySymbol = (ListView) findViewById(R.id.CurrencySymbol_ListView);
        availableCurrenciesSet = Currency.getAvailableCurrencies();
        availableCurrenciesList = new ArrayList<Currency>(availableCurrenciesSet);
        try{
            adapter = new ArrayAdapter<Currency>(this, android.R.layout.simple_list_item_activated_1, availableCurrenciesList);
            Toast.makeText(getApplication(), "About to load", Toast.LENGTH_LONG).show();
            CurrencySymbol.setAdapter(adapter);
        }
        catch (Exception e){
            //e.printStackTrace();
        }

        CurrencySymbol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               try{
                   message = CurrencySymbol.getItemAtPosition(position).toString();
                   Toast.makeText(getApplication(),message,Toast.LENGTH_LONG).show();
               }catch (Exception e){
                   Toast.makeText(getApplication(), "Exception Occurred", Toast.LENGTH_LONG).show();
               }
                Intent intent = new Intent(MainCurrencyCodeSelector.this, MainActivity.class);
                intent.putExtra("MESSAGE", message);
                setResult(2, intent);
                finish();



            }
        });

    }
}