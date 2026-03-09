package com.example.demo2playersinfbrooms;

public class GameMudule {
    public static int xWin = 0;
    public static int oWin = 1;
    public static int noWin = 2;

    public int isWin(Cell[][] arr)
    {
        if(!arr[0][0].isEmpty()
                && arr[0][0].getVal() == arr[0][1].getVal()
                && arr[0][0].getVal() == arr[0][2].getVal())
            return arr[0][0].getVal();

        return noWin;
    }
}
