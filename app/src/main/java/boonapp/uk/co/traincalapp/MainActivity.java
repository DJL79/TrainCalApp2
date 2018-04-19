package boonapp.uk.co.traincalapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText dailyCost, weeklyCost, monthlyCost, travelDaysInput;
    TextView addResultLowest, addResultMiddle, addResultHighest;
    Button btnSubmit;


    public static final int DAILY_MAX = 4, WEEKLY_MAX = 19;
    int travelDaysNum = 0;
    double dailyCostPrice = 0.0, weeklyCostPrice = 0.0, monthlyCostPrice = 0.0, dailySum = 0.0, weeklySum = 0.0, monthlySum = 0.0;
    String getDaysTravelled, getDaily, getWeekly, getMonthly, userStr, username;


    ////////////////////////////////

    // using SharedPreferences to store the 3 ticket prices entered so the user doesnt have to enter each launch.
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    ///////////////////////////







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dailyCost = findViewById(R.id.edtTxtDailyPrice);
        weeklyCost = findViewById(R.id.edtTxtWeeklyPrice);
        monthlyCost = findViewById(R.id.edtTxtMonthlyPrice);
        travelDaysInput = findViewById(R.id.edtTxtDaysTravel);
        btnSubmit = findViewById(R.id.btnSubmit);

        addResultLowest = findViewById(R.id.txtViewResultLowest);
        addResultMiddle = findViewById(R.id.txtViewResultMiddle);
        addResultHighest = findViewById(R.id.txtViewResultHighest);


////////////////////////////////////////////////////////////////////
        // using SharedPreferences to store the 3 ticket prices entered so the user doesnt have to enter each launch.


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //mPreferences = getSharedPreferences("tabian.com.sharedpreferencestest", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();


        storePricesEntered();
 //////////////////////////////////////////////////////////////////////


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {





                ///////////////////////////////////////////
                // using SharedPreferences to store the 3 ticket prices entered so the user doesnt have to enter each launch.


                //save the name
                String daily = dailyCost.getText().toString();
                mEditor.putString(getString(R.string.dailyprice), daily);
                mEditor.commit();

                String weekly = weeklyCost.getText().toString();
                mEditor.putString(getString(R.string.weeklyprice), weekly);
                mEditor.commit();

                String monthly = monthlyCost.getText().toString();
                mEditor.putString(getString(R.string.monthlyprice), monthly);
                mEditor.commit();

                /////////////////////////////////////////////

                CalculateBestPrice();

/*                //resetting the values to 0 before next submit
                dailyCost.setText(Integer.toString(0));
                weeklyCost.setText(Integer.toString(0));
                monthlyCost.setText(Integer.toString(0));
                addResult.setText(Integer.toString(0));*/


            }
        });


    }



    @SuppressLint("SetTextI18n")
    public void CalculateBestPrice() {


        getDaysTravelled = travelDaysInput.getText().toString();
        getDaily = dailyCost.getText().toString();
        getWeekly = weeklyCost.getText().toString();
        getMonthly = monthlyCost.getText().toString();

        if (TextUtils.isEmpty(getDaysTravelled) || (TextUtils.isEmpty(getDaily)) || (TextUtils.isEmpty(getWeekly)) || (TextUtils.isEmpty(getMonthly))) {

            Toast.makeText(MainActivity.this, "please enter all details before submitting", Toast.LENGTH_LONG).show();

        } else {

            //convert from edittext to Double or Integer
            dailyCostPrice = Double.parseDouble(dailyCost.getText().toString());
            weeklyCostPrice = Double.parseDouble(weeklyCost.getText().toString());
            monthlyCostPrice = Double.parseDouble(monthlyCost.getText().toString());
            travelDaysNum = Integer.parseInt(travelDaysInput.getText().toString());

            // getting price of paid daily
            dailySum = dailyCostPrice * travelDaysNum;

            // getting price of paid weekly and daily
            int days, weeks;
            weeks = travelDaysNum / 5;
            days = travelDaysNum % 5;
            weeklySum = (weeks * weeklyCostPrice) + (days * dailyCostPrice);

            // getting price of paid monthly
            int days2, weeks2;
            int months = travelDaysNum / 20;
            int daysLeftOver = travelDaysNum % 20;
            weeks2 = daysLeftOver / 5;
            days2 = daysLeftOver % 5;
            monthlySum = (months * monthlyCostPrice) + (weeks2 * weeklyCostPrice) + (days2 * dailyCostPrice);

            /* This is how to declare HashMap */
            HashMap<String, Double> pricesHMap = new HashMap<String, Double>();

             /*Adding elements to HashMap*/
            pricesHMap.put("DailySum", dailySum);
            pricesHMap.put("WeeklySum", weeklySum);
            pricesHMap.put("MonthlySum", monthlySum);

            // adding the prices to an array list so we can sort prices
            List<Double> priceList = new ArrayList<>();
            priceList.add(dailySum);
            priceList.add(weeklySum);
            priceList.add(monthlySum);

            // sorts the arraylist highest to lowest
            Collections.sort(priceList);

            //setting the prices accordingly
            double highestPrice = priceList.get(2);
            double middlePrice = priceList.get(1);
            double lowestPrice = priceList.get(0);

            if (travelDaysNum <= DAILY_MAX && dailySum == lowestPrice)
            {
                addResultLowest.setText(travelDaysNum + " daily tickets. The cost will be £" + Double.toString(lowestPrice));
            }
                else if (travelDaysNum > DAILY_MAX && travelDaysNum <= WEEKLY_MAX && weeklySum == lowestPrice)
                 {
                     if (weeklySum < monthlyCostPrice)
                     {
                         if (days != 0){
                             addResultLowest.setText(weeks + " weekly & " + days + " daily ticket(s). The cost will be £" + Double.toString(lowestPrice));
                         }
                         else if (days == 0){
                             addResultLowest.setText(weeks + " weekly ticket(s). The cost will be £" + Double.toString(lowestPrice));
                         }
                     }
                     else if (weeklySum > monthlyCostPrice)
                     {
                         addResultLowest.setText(" 1 monthly ticket. The cost will be £" + Double.toString(monthlyCostPrice));
                     }
                 }
                else if (travelDaysNum > WEEKLY_MAX && monthlySum == lowestPrice)
                  {
                    //this checks to see if there are days, weeks, months or a combination and displays the correct response
                    if (weeks2 != 0 && days2 !=0){
                        addResultLowest.setText(months + " monthly ticket(s)," + weeks2 + " weekly ticket(s) & " + days2 + " daily tickets. The cost will be £" + Double.toString(lowestPrice));
                    }
                    else if (weeks2 != 0 && days2 == 0){
                        addResultLowest.setText(months + " monthly ticket(s)," + weeks2 + " & weekly ticket(s). The cost will be £" + Double.toString(lowestPrice));
                    }
                    else if (weeks2 == 0 && days2 != 0){
                        addResultLowest.setText(months + " monthly ticket(s)," + days2 + " & daily ticket(s). The cost will be £" + Double.toString(lowestPrice));
                    }
                    else if (weeks2 == 0 && days2 == 0){
                        addResultLowest.setText(months + " monthly ticket(s). The cost will be £" + Double.toString(lowestPrice));
                    }
                  }
            addResultMiddle.setText("Second option " + Double.toString(middlePrice));
            addResultHighest.setText("Most expensive option " + Double.toString(highestPrice));
        }
        }


        //////////////////////////////////////////////////////////

    /**
     * Check the shared preferences and set them accordingly
     */
    private void storePricesEntered(){
        String daily = mPreferences.getString(getString(R.string.dailyprice), "");
        dailyCost.setText(daily);
        String weekly = mPreferences.getString(getString(R.string.weeklyprice), "");
        weeklyCost.setText(weekly);
        String monthly = mPreferences.getString(getString(R.string.monthlyprice), "");
        monthlyCost.setText(monthly);
    }
}


