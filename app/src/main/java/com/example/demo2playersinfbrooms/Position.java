package com.example.demo2playersinfbrooms;

public class  Position {
    private int line;
    private int col;

    public Position() {
    }

    public Position(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
