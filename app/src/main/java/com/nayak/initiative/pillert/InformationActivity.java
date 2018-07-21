package com.nayak.initiative.pillert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.firebase.client.Firebase;
import java.util.Calendar;

public class InformationActivity extends AppCompatActivity {

    Button button;
    TextView timeView;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    String amPm;
    Button save;
    EditText tabletNameInput;
    EditText tabletQtyInput;
    EditText consumptionQtyInput;
    Button cancelBtn;
    static Firebase compOne;
    static Firebase compTwo;
    String val1;
    String val2;
    String val3;
    String val4;
    int totalQty;
    int consQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //Setting the database links to each compartment
        compOne = new Firebase("https://pillert-a9551.firebaseio.com/Compartment/Compartment One");
        compTwo = new Firebase("https://pillert-a9551.firebaseio.com/Compartment/Compartment Two");

        //Toolbar for the information activity
        Toolbar toolbar = findViewById(R.id.toolbarCompInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Compartment information");

        //Initializing the views
        button = findViewById(R.id.consTimeInput);
        timeView = findViewById(R.id.consTimeText);
        save = findViewById(R.id.saveBtn);
        tabletNameInput = findViewById(R.id.tabletNameInput);
        tabletQtyInput = findViewById(R.id.tabletQtyInput);
        cancelBtn = findViewById(R.id.cancelBtn);
        consumptionQtyInput = findViewById(R.id.consumptionQtyInput);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                //Setting the time picker
                timePickerDialog = new TimePickerDialog(InformationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay >= 12){
                            amPm = "PM";
                        }else{
                            amPm = "AM";
                        }
                        timeView.setText(String.format("%02d:%02d", hourOfDay, minute) + " " + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val1 = tabletNameInput.getText().toString();
                val2 = tabletQtyInput.getText().toString();
                val3 = timeView.getText().toString();
                val4 = consumptionQtyInput.getText().toString();

                //Validating if the input fields are filled
                if((val1.equals("")) || (val2.equals("")) || (val3.equals("TIME")) || (val4.equals(""))){
                    Toast.makeText(InformationActivity.this, "Fill in the details",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                totalQty = Integer.parseInt(val2);
                consQty = Integer.parseInt(val4);

                //If the consumable quantity is greater than the total quantity it is not accepted
                if(consQty > totalQty){
                    Toast.makeText(InformationActivity.this,
                            "Consumption quantity should not be more than total quantity!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                //Checking which compartment was selected and storing data to firebase
                if(CompartmentSelectionActivity.compartmentOne){
                    Firebase childRef1 = compOne.child("Tablet_Name");
                    Firebase childRef2 = compOne.child("Tablet_Quantity");
                    Firebase childRef3 = compOne.child("Consumable_Time");
                    Firebase childRef4 = compOne.child("Consumption_Quantity");

                    childRef1.setValue(val1);
                    childRef2.setValue(totalQty);
                    childRef3.setValue(val3);
                    childRef4.setValue(consQty);

                }else if(CompartmentSelectionActivity.compartmentTwo){
                    Firebase childRef1 = compTwo.child("Tablet_Name");
                    Firebase childRef2 = compTwo.child("Tablet_Quantity");
                    Firebase childRef3 = compTwo.child("Consumable_Time");
                    Firebase childRef4 = compTwo.child("Consumption_Quantity");

                    childRef1.setValue(val1);
                    childRef2.setValue(totalQty);
                    childRef3.setValue(val3);
                    childRef4.setValue(consQty);

                }

                Toast.makeText(InformationActivity.this, "Saved successfully!",
                        Toast.LENGTH_SHORT).show();

                System.out.println("Time is");
                System.out.println(timeView.getText().toString());
                int h = Integer.parseInt(timeView.getText().toString().substring(0,2));
                int m = Integer.parseInt(timeView.getText().toString().substring(3,5));
                int c = consQty;

                System.out.println("hour "+ h);
                System.out.println("minute "+ m);
                System.out.println("cons quantity "+ c);

                LoginActivity.mBluetoothConnection.write(h);
                LoginActivity.mBluetoothConnection.write(m);
                LoginActivity.mBluetoothConnection.write(c);

                if(CompartmentSelectionActivity.compartmentOne){
                    Calendar calendar = Calendar.getInstance();

                    //Setting the alarm for the compartment one
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);
                    calendar.set(Calendar.SECOND, 0);

                    Intent intent = new Intent(getApplicationContext(), NotificationReceiverOne.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                            1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                }else if(CompartmentSelectionActivity.compartmentTwo){
                    Calendar calendar = Calendar.getInstance();

                    //Setting the alarm for the compartment two
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);
                    calendar.set(Calendar.SECOND, 0);

                    Intent intent = new Intent(getApplicationContext(), NotificationReceiverTwo.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                            2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                }

                //Opens compartment selection activity after saving all the data
                openCompartmentSelectionActivity();

            }
        });

        //Goes back to the compartment selection activity when cancel button is clicked
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If cancel is clicked -1 is sent to the Pillert
                //LoginActivity.mBluetoothConnection.write(-1);
                //LoginActivity.mBluetoothConnection.write(-1);
                //LoginActivity.mBluetoothConnection.write(-1);
                openCompartmentSelectionActivity();
            }
        });
    }




    //Method to go to compartment selection activity
    public void openCompartmentSelectionActivity(){
        Intent intent = new Intent(this, CompartmentSelectionActivity.class);
        startActivity(intent);
    }




    //Goes back to compartment selection activity when back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openCompartmentSelectionActivity();
    }
}
