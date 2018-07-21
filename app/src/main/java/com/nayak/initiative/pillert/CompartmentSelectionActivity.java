package com.nayak.initiative.pillert;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class CompartmentSelectionActivity extends AppCompatActivity {

    //Declaring static boolean variables to check from which compartment the details are set
    static boolean compartmentOne;
    static boolean compartmentTwo;
    static Button eject;
    private Firebase mRef1;
    private Firebase mRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartment_selection);

        //Toolbar for the compartment selection activity
        Toolbar toolbar = findViewById(R.id.toolbarCompSelect);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Compartment");

        //Defining each compartment button
        Button compOne = findViewById(R.id.compOne);
        Button compTwo = findViewById(R.id.compTwo);
        eject = findViewById(R.id.ejectBtn);

        //Defining the text views
        final TextView comp2Name = findViewById(R.id.nameSpc2);
        final TextView comp2Qty = findViewById(R.id.qtySpc2);
        final TextView comp2Time = findViewById(R.id.timeSpc2);
        final TextView comp1Name = findViewById(R.id.nameSpc1);
        final TextView comp1Qty = findViewById(R.id.qtySpc1);
        final TextView comp1Time = findViewById(R.id.timeSpc1);

        //Database references
        mRef1 = new Firebase("https://pillert-a9551.firebaseio.com/Compartment/Compartment One");
        mRef2 = new Firebase("https://pillert-a9551.firebaseio.com/Compartment/Compartment Two");

        mRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map2 = dataSnapshot.getValue(Map.class);
                comp1Name.setText("Tablet Name: " + map2.get("Tablet_Name"));
                comp1Qty.setText("Tablet Quantity: " + String.valueOf(map2.get("Tablet_Quantity")));
                comp1Time.setText("Time: " + map2.get("Consumable_Time"));
                Log.v("E_VALUE","Data: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map2 = dataSnapshot.getValue(Map.class);
                comp2Name.setText("Tablet Name: " + map2.get("Tablet_Name"));
                comp2Qty.setText("Tablet Quantity: " + String.valueOf(map2.get("Tablet_Quantity")));
                comp2Time.setText("Time: " + map2.get("Consumable_Time"));
                Log.v("E_VALUE","Data: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        compartmentOne = false;
        compartmentTwo = false;

        //Opens the information activity
        compOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    compartmentOne = true;

                    //When save button is clicked 'a' is sent to arduino
                    LoginActivity.mBluetoothConnection.write('a');

                    openCompartmentInformationActivity();
                }else{
                    Toast.makeText(CompartmentSelectionActivity.this,
                            "Please connect to any network",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        compTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    compartmentTwo = true;

                    //When save button is clicked 'b' is sent to arduino
                    LoginActivity.mBluetoothConnection.write('b');

                    openCompartmentInformationActivity();
                }else{
                    Toast.makeText(CompartmentSelectionActivity.this,
                            "Please connect to any network",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        eject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.mBluetoothConnection.write('c');
            }
        });
    }




    //Method to check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    //Opens compartment information activity
    public void openCompartmentInformationActivity(){
        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
    }




    //Goes back to login activity when back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
