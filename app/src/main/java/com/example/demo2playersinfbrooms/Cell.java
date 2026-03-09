package com.example.demo2playersinfbrooms;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Cell {
    private final int Xval = 0;
    private final int Oval = 1;
    private final int EmptyVal = 2;
    private final int x;
    private final int y;
    private final int cellWidth;
    private Bitmap bitmapX, bitmapO;
    private int val;
    private Paint p;



    public Cell(int x, int y, int cellWidth, Bitmap bitmapX, Bitmap bitmapO) {
        this.x = x;
        this.y = y;
        this.cellWidth = cellWidth;
        this.bitmapX = bitmapX;
        this.bitmapO = bitmapO;
        this.val = EmptyVal;

        p = new Paint();
        p.setStrokeWidth(30);
        p.setStyle(Paint.Style.STROKE);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(x,y,x+cellWidth,y+cellWidth, p);
        if(val == Xval)
            canvas.drawBitmap(bitmapX,x+10,y+10,null);
        if(val == Oval)
            canvas.drawBitmap(bitmapO,x+10,y+10,null);

    }

    public boolean setVal(int val) {
        if(this.val == EmptyVal)
        {
            this.val = val;
            return true;
        }
        return false;
    }

    public int getVal() {
        return val;
    }

    public boolean isEmpty() {
        return this.val == EmptyVal;
    }
}
