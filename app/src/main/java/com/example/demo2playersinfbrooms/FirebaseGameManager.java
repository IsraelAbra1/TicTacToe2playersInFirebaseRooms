package com.example.demo2playersinfbrooms;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseGameManager {
    private DatabaseReference gameRef;
    private String myPlayerId;
    private GameStateListener listener;

    // Interface to pass game updates back to your UI
    public interface GameStateListener {
        void onGameUpdated(GameState state);
        void onGameError(String error);
    }

    public FirebaseGameManager(String gameRoomId, String myPlayerId, GameStateListener listener) {
        this.myPlayerId = myPlayerId;
        this.listener = listener;
        // Points to "games/{gameRoomId}" in your Realtime Database
        this.gameRef = FirebaseDatabase.getInstance().getReference("games").child(gameRoomId);

        setupGameListener();
    }

    /**
     * Joins the game. If it doesn't exist, creates it. If Player 1 is there, joins as Player 2.
     */
    public void joinGame() {
        gameRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    // Room is empty, create it as Player 1
                    GameState newState = new GameState(myPlayerId);
                    gameRef.setValue(newState);
                } else {
                    GameState currentState = snapshot.getValue(GameState.class);
                    // If room exists but needs a second player
                    if (currentState != null && currentState.player2Id == null && !currentState.player1Id.equals(myPlayerId)) {
                        currentState.player2Id = myPlayerId;
                        currentState.status = "PLAYING";
                        gameRef.setValue(currentState);
                    }
                }
            } else {
                listener.onGameError("Failed to connect to room.");
            }
        });
    }

    /**
     * Listens for any changes to the game state in real-time.
     */
    public void setupGameListener() {
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GameState state = snapshot.getValue(GameState.class);
                    if (state != null) {
                        listener.onGameUpdated(state);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onGameError(error.getMessage());
            }
        });
    }

    /**
     * Attempts to make a move, ensuring it is the player's turn.
     */
    public void makeMove(Position moveData) {
        gameRef.get().addOnSuccessListener(snapshot -> {
            GameState state = snapshot.getValue(GameState.class);

            if (state != null && "PLAYING".equals(state.status)) {
                // Ensure it's actually this player's turn
                if (myPlayerId.equals(state.currentTurnId)) {
                    state.lastMove = moveData;
                    // Switch turns
                    state.currentTurnId = myPlayerId.equals(state.player1Id) ? state.player2Id : state.player1Id;

                    // Push update to Firebase
                    gameRef.setValue(state);
                } else {
                    listener.onGameError("It's not your turn!");
                }
            }
        });
    }

    /**
     * Attempts to place a piece on the 2D board at the specified coordinates.
     */
/*    public void makeMove(int row, int col) {
        gameRef.get().addOnSuccessListener(snapshot -> {
            GameState state = snapshot.getValue(GameState.class);

            if (state != null && "PLAYING".equals(state.status)) {

                // 1. Check if it's the player's turn
                if (myPlayerId.equals(state.currentTurnId)) {

                    // 2. Check if the requested spot on the board is empty
                    if (state.board.get(row).get(col).isEmpty()) {

                        // Determine the piece ('X' for Player 1, 'O' for Player 2)
                        String piece = myPlayerId.equals(state.player1Id) ? "X" : "O";

                        // 3. Update the board
                        state.board.get(row).set(col, piece);

                        // 4. Switch turns
                        state.currentTurnId = myPlayerId.equals(state.player1Id) ? state.player2Id : state.player1Id;

                        // 5. Push the updated board and state to Firebase
                        gameRef.setValue(state);

                    } else {
                        listener.onGameError("That spot is already taken!");
                    }
                } else {
                    listener.onGameError("It's not your turn!");
                }
            }
        });
    }*/
}
