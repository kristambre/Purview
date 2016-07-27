package com.purview.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.purview.R;
import com.purview.entities.Event;
import com.purview.temp.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddEvent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            public void onClick(View v) {
                Location location = new Location("");
                location.setLatitude((double) getIntent().getExtras().get("lat"));
                location.setLongitude((double) getIntent().getExtras().get("lng"));

                Event e = new Event(location);
                e.setName(((EditText) findViewById(R.id.nameText)).getText().toString());
                e.setDescription(((EditText) findViewById(R.id.descriptionText)).getText().toString());
                try{
                    e.setStartDate(sdf.parse(((EditText) findViewById(R.id.startDateText)).getText().toString()));
                    e.setEndDate(sdf.parse(((EditText) findViewById(R.id.endDateText)).getText().toString()));
                } catch (ParseException e1) {
                    Log.e("PARSEEXCEPTION> ", "error parsing date", e1);
                }

                Constants.events.add(e);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
