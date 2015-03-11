package com.rabor.tipcalculator;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.rabor.tipcalculator.R;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;   
    private Button   percentUpButton;
    private Button   percentDownButton;
    private Button saveButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private TipDB tipData;
    
    // define the SharedPreferences object
    private SharedPreferences savedValues;
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        
        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        saveButton = (Button) findViewById(R.id.saveButton);

        tipData = new TipDB(getApplicationContext());

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        
        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);        
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        ArrayList<Tip> tipList = tipData.getTips();

        for( Tip tip : tipList){
            Log.d("Saved Tips", "Bill Date Milliseconds: " + tip.getBill_date() +
                " Bill Amount: " + tip.getBill_amount() + " Tip Percent: " + tip.getTip_percent());
        }
        Log.d("Saved Tips", "Average Tip Percent: " + tipData.getAverageTipPercent());
        
        // calculate and display
        calculateAndDisplay();
    }    
    
    public void calculateAndDisplay() {        

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount; 
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }
        
        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;
        
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.percentDownButton:
                tipPercent = tipPercent - .01f;
                calculateAndDisplay();
                break;
            case R.id.percentUpButton:
                tipPercent = tipPercent + .01f;
                calculateAndDisplay();
                break;
            case R.id.saveButton:
                Tip tip = new Tip();
                tip.setBill_amount((Double.parseDouble(billAmountEditText.getText().toString())));
                tip.setTip_percent(tipPercent);

                tipData.saveTip(tip);
                tipPercent = tipData.getAverageTipPercent();
                break;
        }
    }
}