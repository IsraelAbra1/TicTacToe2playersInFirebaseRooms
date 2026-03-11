package com.example.demo2playersinfbrooms;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private FbModule fbModule;
    private BoardGame boardGame;
    private GameModule gameModule;

    private String myId;
    private String roomId;

    // ADD THIS: Keep track of the current state locally!
    private GameState currentState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Get the IDs passed from your Main Menu's Matchmaker
        roomId = getIntent().getStringExtra("ROOM_ID");
        myId = getIntent().getStringExtra("MY_ID");

        // 2. Set up the custom UI View
        boardGame = new BoardGame(this);
        setContentView(boardGame);
        gameModule = new GameModule();

        // 3. Safety check to ensure we actually got the data from the Intent
        if (roomId != null && myId != null) {
            // 4. Initialize the Game Manager
            fbModule = new FbModule(roomId, myId, this);
        } else {
            // If data is missing, show an error and close the activity
            Toast.makeText(this, "Error: Could not load room.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void updateUI(GameState state) {
        // SAVE THE STATE LOCALLY EVERY TIME FIREBASE UPDATES
        this.currentState = state;

        if ("WAITING".equals(state.status)) {
            Toast.makeText(this, "Waiting for opponent to join...", Toast.LENGTH_SHORT).show();

        } else if ("PLAYING".equals(state.status)) {
            // Update the UI board with the last move
            if (state.lastMove != null) {
                boardGame.setNewValOnBoard(state.lastMove.getLine(), state.lastMove.getCol());
            }

            if (myId.equals(state.currentTurnId)) {
                Toast.makeText(this, "It's your turn!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Waiting for opponent...", Toast.LENGTH_SHORT).show();
            }

        } else if ("FINISHED".equals(state.status)) {
            // Update the board one last time for the final winning piece
            if (state.lastMove != null) {
                boardGame.setNewValOnBoard(state.lastMove.getLine(), state.lastMove.getCol());
            }

            // Whoever has the currentTurnId when the game finishes is the winner!
            if (myId.equals(state.currentTurnId)) {
                Toast.makeText(this, "YOU WON! 🎉", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "YOU LOST! 😢", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Called when a user clicks a specific cell on the grid
    public void onCellClicked(int row, int col) {
        // 1. Check if the game is ready
        if (currentState == null || !"PLAYING".equals(currentState.status)) {
            return; // Don't do anything if game hasn't started or is finished
        }

        // 2. THIS IS THE FIX: Check if it's actually your turn!
        if (!myId.equals(currentState.currentTurnId)) {
            Toast.makeText(this, "Wait for your opponent!", Toast.LENGTH_SHORT).show();
            return; // Stop right here, don't update the board!
        }

        // 3. Check if the cell is empty before allowing the move (Assuming you have a method for this)
        // if (!boardGame.isCellEmpty(row, col)) { return; }

        if (fbModule != null) {
            Position position = new Position(row, col);

            // Temporarily apply the move to your local board to test it
            boardGame.setNewValOnBoard(row, col);

            // Check if this move results in a win using your method
            int winResult = gameModule.isWin(boardGame.getArr());

            // Determine if the game is over
            boolean isGameOver = (winResult != gameModule.noWin);

            // Send the move AND the win status to Firebase
            fbModule.makeMove(position, isGameOver);
        }
    }
}