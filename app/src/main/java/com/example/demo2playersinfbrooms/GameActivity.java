package com.example.demo2playersinfbrooms;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class GameActivity extends AppCompatActivity {

    private FirebaseGameManager gameManager;
    private BoardGame boardGame;

    private String myId;
    private String roomId; // Will be generated/found by the Matchmaker


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Get the IDs passed from your Main Menu's Matchmaker
        roomId = getIntent().getStringExtra("ROOM_ID");
        myId = getIntent().getStringExtra("MY_ID");

        // 2. Set up the custom UI View
        boardGame = new BoardGame(this);
        setContentView(boardGame);

        // 3. Safety check to ensure we actually got the data from the Intent
        if (roomId != null && myId != null) {
            // 4. Initialize the Game Manager
            setupGameManager();
        } else {
            // If data is missing, show an error and close the activity
            Toast.makeText(this, "Error: Could not load room.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Called only after the Matchmaker successfully finds or creates a room.
     */
    private void setupGameManager() {
        gameManager = new FirebaseGameManager(roomId, myId, new FirebaseGameManager.GameStateListener() {
            @Override
            public void onGameUpdated(GameState state) {
                updateUI(state);
            }

            @Override
            public void onGameError(String error) {
                Toast.makeText(GameActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Start listening to Firebase for updates in this specific room
        // (Make sure setupGameListener() is public inside FirebaseGameManager!)


        // TODO: 11/03/2026 check 
        //gameManager.setupGameListener();
    }

    private void updateUI(GameState state) {
        if ("WAITING".equals(state.status))
        {
            Toast.makeText(this, "Waiting for opponent to join...", Toast.LENGTH_SHORT).show();

        } else
            if ("PLAYING".equals(state.status))
        {

            // Update the UI board with the opponent's (or our own) last move
            if (state.lastMove != null) {
                boardGame.setNewValOnBoard(state.lastMove.getLine(), state.lastMove.getCol());
            }

            // Check whose turn it is
            if (myId.equals(state.currentTurnId)) {
                Toast.makeText(this, "It's your turn!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Waiting for opponent...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Called when a user clicks a specific cell on the grid
    public void onCellClicked(int row, int col) {
        if (gameManager != null) {
            Position position = new Position(row, col);
            gameManager.makeMove(position);
        }
    }
}