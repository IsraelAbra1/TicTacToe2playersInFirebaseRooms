package com.example.demo2playersinfbrooms;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class Matchmaker {
    private DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference("games");
    private String myId;

    private MatchmakerListener listener;

    // The interface to talk back to your UI
    public interface MatchmakerListener {
        void onMatchFound(String roomId);
    }

    // Update the constructor to accept the listener
    public Matchmaker(String myId, MatchmakerListener listener) {
        this.myId = myId;
        this.listener = listener;
    }

    /**
     * Step 1: Look for an available room
     */
    public void findMatch() {
        System.out.println("Searching for a game...");

        // Query for 1 game where the status is "WAITING"
        gamesRef.orderByChild("status").equalTo("WAITING").limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Found an open room! Grab its ID.
                            DataSnapshot gameSnap = snapshot.getChildren().iterator().next();
                            String roomId = gameSnap.getKey();

                            // Try to claim the Player 2 spot
                            tryJoinExistingGame(roomId);
                        } else {
                            // No open rooms found. Create a new one.
                            createNewGame();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println("Search failed: " + error.getMessage());
                    }
                });
    }

    /**
     * Step 2: Try to claim the spot using a Transaction to prevent double-booking
     */
    private void tryJoinExistingGame(String roomId) {
        DatabaseReference roomRef = gamesRef.child(roomId);

        roomRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                GameState state = mutableData.getValue(GameState.class);

                if (state == null) {
                    return Transaction.success(mutableData);
                }

                // Double-check that it's still waiting and the spot is empty
                if ("WAITING".equals(state.status) && state.player2Id == null) {
                    state.player2Id = myId;
                    state.status = "PLAYING";
                    mutableData.setValue(state);
                    return Transaction.success(mutableData); // We got the spot!
                } else {
                    // Someone else took it in the last millisecond!
                    return Transaction.abort();
                }
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed) {
                    System.out.println("Match found! Joined room: " + roomId);
                    // Notify the GameActivity that we successfully joined a room
                    if (listener != null) {
                        listener.onMatchFound(roomId);
                    }
                } else {
                    System.out.println("Room was taken. Searching again...");
                    // Try searching again immediately
                    findMatch();
                }
            }
        });
    }

    /**
     * Step 3: Create a new room if all others are full
     */
    private void createNewGame() {
        // .push() generates a unique, random string like "-Nxyz123abc..."
        String newRoomId = gamesRef.push().getKey();

        if (newRoomId != null) {
            GameState newState = new GameState(myId);
            gamesRef.child(newRoomId).setValue(newState).addOnSuccessListener(aVoid -> {
                System.out.println("Created new room: " + newRoomId + ". Waiting for opponent...");
                // Notify the GameActivity that we successfully created a room
                if (listener != null) {
                    listener.onMatchFound(newRoomId);
                }
            });
        }
    }
}