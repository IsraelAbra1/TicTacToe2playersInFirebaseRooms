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

        // add listener for the room
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

     // update the Firebase with the new move, ensuring it is the player's turn.
    public void makeMove(Position position) {
        gameRef.get().addOnSuccessListener(snapshot -> {
            GameState state = snapshot.getValue(GameState.class);

            if (state != null && "PLAYING".equals(state.status))
            {
                // Ensure it's actually this player's turn
                if (myPlayerId.equals(state.currentTurnId))
                {
                    state.lastMove = position;

                    // Switch turns
                    if (myPlayerId.equals(state.player1Id)) {
                        // If I am Player 1, then it is now Player 2's turn
                        state.currentTurnId = state.player2Id;
                    } else {
                        // Otherwise (I must be Player 2), so it is now Player 1's turn
                        state.currentTurnId = state.player1Id;
                    }

                    // Push update to Firebase
                    gameRef.setValue(state);
                }
                else
                {
                    listener.onGameError("It's not your turn!");
                }
            }
        });
    }

}
