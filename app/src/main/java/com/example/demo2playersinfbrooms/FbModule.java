package com.example.demo2playersinfbrooms;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FbModule {
    private DatabaseReference gameRef;
    private String myPlayerId;
    //private GameStateListener listener;
    private Context context;

    public FbModule(String gameRoomId, String myPlayerId, Context context) {
        this.myPlayerId = myPlayerId;
        this.context = context;
        // Points to "games/{gameRoomId}" in your Realtime Database
        this.gameRef = FirebaseDatabase.getInstance().getReference("games").child(gameRoomId);

        // add listener for the room
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GameState state = snapshot.getValue(GameState.class);
                    if (state != null) {
                        ((GameActivity)context).updateUI(state);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

     // update the Firebase with the new move, ensuring it is the player's turn.

    public void makeMove(Position position, boolean isWinningMove) {
        // .get() This tells Firebase: "Go to the internet and fetch whatever data is currently sitting at gameRef."
        gameRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {

            @Override
            public void onSuccess(DataSnapshot snapshot) {
                GameState state = snapshot.getValue(GameState.class);

                if (state != null && "PLAYING".equals(state.status)) {

                    // Ensure it's actually this player's turn
                    if (myPlayerId.equals(state.currentTurnId)) {
                        state.lastMove = position;

                        // Did this move win the game?
                        if (isWinningMove)
                        {
                            // Change the status to FINISHED
                            state.status = "FINISHED";
                            // Note: We deliberately DO NOT switch the currentTurnId here.
                            // It stays as your ID, which makes it easy to know who won!
                        }
                        else
                        {
                            // It didn't win, so switch turns normally
                            if (myPlayerId.equals(state.player1Id)) {
                                state.currentTurnId = state.player2Id;
                            } else {
                                state.currentTurnId = state.player1Id;
                            }
                        }

                        // Push update to Firebase
                        gameRef.setValue(state);
                    } else {
                        Toast.makeText(context, "It's not your turn!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
