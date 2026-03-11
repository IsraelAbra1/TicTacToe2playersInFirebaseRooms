package com.example.demo2playersinfbrooms;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.view.View;
import android.widget.Button;   // Import Button

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String myId = UUID.randomUUID().toString(); // get random string
    //  String myId = "player_" + System.currentTimeMillis();


    // if the app use Authentication
/*
    // 1. Get the Firebase Auth instance
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // 2. Get the currently logged-in user
    FirebaseUser currentUser = mAuth.getCurrentUser();

    if (currentUser != null) {
        // 3. The user is logged in! Grab their official Firebase UID.
        String myId = currentUser.getUid();*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Find the button by its ID
        Button btnStartGame = findViewById(R.id.btnStartGame);

        //myId = UUID.randomUUID().toString(); // get random string

        // 2. Set the click listener
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show a loading spinner on your UI here

                Matchmaker matchmaker = new Matchmaker(myId, new Matchmaker.MatchmakerListener() {
                    @Override
                    public void onMatchFound(String roomId) {
                        // We have a room! Let's go to the Game Activity.
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra("ROOM_ID", roomId);
                        intent.putExtra("MY_ID", myId);
                        startActivity(intent);
                    }
                });

                matchmaker.findMatch();
            }
        });
    }


}