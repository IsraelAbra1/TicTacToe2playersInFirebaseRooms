package com.example.demo2playersinfbrooms;

public class GameModule {
    public static int xWin = 0;
    public static int oWin = 1;
    public static int noWin = 2;

    public int isWin(Cell[][] arr) {

        // 1. Check all 3 Rows
        for (int i = 0; i < 3; i++) {
            if (!arr[i][0].isEmpty()
                    && arr[i][0].getVal() == arr[i][1].getVal()
                    && arr[i][0].getVal() == arr[i][2].getVal()) {
                return arr[i][0].getVal();
            }
        }

        // 2. Check all 3 Columns
        for (int j = 0; j < 3; j++) {
            if (!arr[0][j].isEmpty()
                    && arr[0][j].getVal() == arr[1][j].getVal()
                    && arr[0][j].getVal() == arr[2][j].getVal()) {
                return arr[0][j].getVal();
            }
        }

        // 3. Check Main Diagonal (Top-Left to Bottom-Right)
        if (!arr[0][0].isEmpty()
                && arr[0][0].getVal() == arr[1][1].getVal()
                && arr[0][0].getVal() == arr[2][2].getVal()) {
            return arr[0][0].getVal();
        }

        // 4. Check Anti-Diagonal (Top-Right to Bottom-Left)
        if (!arr[0][2].isEmpty()
                && arr[0][2].getVal() == arr[1][1].getVal()
                && arr[0][2].getVal() == arr[2][0].getVal()) {
            return arr[0][2].getVal();
        }

        // If no win condition is met
        return noWin;
    }
}
