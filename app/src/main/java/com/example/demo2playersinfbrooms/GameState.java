package com.example.demo2playersinfbrooms;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public String player1Id;
    public String player2Id;
    public String currentTurnId; // Whose turn is it?
    public String status; // "WAITING", "PLAYING", "FINISHED"
    public Position lastMove; // Can be a string, or a custom object representing board state


    //public List<List<String>> board;  // Using a List of Lists to represent the 2D board for Firebase compatibilit

    // Required empty constructor for Firebase
    public GameState() {
    }

    public GameState(String player1Id) {
        this.player1Id = player1Id;
        this.status = "WAITING";
        this.currentTurnId = player1Id; // Player 1 starts by default
        this.lastMove = null;

    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public String getCurrentTurnId() {
        return currentTurnId;
    }

    public void setCurrentTurnId(String currentTurnId) {
        this.currentTurnId = currentTurnId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Position getLastMove() {
        return lastMove;
    }

    public void setLastMove(Position lastMove) {
        this.lastMove = lastMove;
    }
}
