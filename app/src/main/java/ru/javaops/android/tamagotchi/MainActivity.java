package ru.javaops.android.tamagotchi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String PET_NAME = "Pet name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
        startActivity(intent);
    }

    public void goWalk(View view) {
        Intent intent = new Intent(MainActivity.this, WalkActivity.class);
        Button changeButton = (Button) findViewById(view.getId());
        final String buttonText = changeButton.getText().toString().toLowerCase();
        Log.d(TAG, "view Name is " + buttonText);
        intent.putExtra(PET_NAME, buttonText);
        startActivity(intent);
    }
}
