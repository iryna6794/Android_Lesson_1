package ru.javaops.android.tamagotchi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        Button cat = findViewById(R.id.button_cat);
        Button dog = findViewById(R.id.button_dog);
        Button cthulhu = findViewById(R.id.button_cthulhu);

        final View.OnClickListener buttonListener = initButtonListener();
        cat.setOnClickListener(buttonListener);
        dog.setOnClickListener(buttonListener);
        cthulhu.setOnClickListener(buttonListener);
    }

    private View.OnClickListener initButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                Intent intent = new Intent(MainActivity.this, WalkActivity.class);
                intent.putExtra(INTENT_PET_TYPE, button.getText().toString().toUpperCase());
                startActivity(intent);
            }
        };
    }
}
