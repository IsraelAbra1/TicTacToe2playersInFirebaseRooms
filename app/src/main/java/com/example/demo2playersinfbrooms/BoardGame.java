package com.example.demo2playersinfbrooms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class BoardGame extends View {
    private Cell[][] arr;
    private boolean firstTime;
    private int canvasWidth, canvasHigh;
    private int cellWidth, cellHeight;
    private int count = 0;
    private Context context;

    public BoardGame(Context context) {
        super(context);

        this.context = context;

        arr = new Cell[3][3];
        firstTime = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(firstTime)
        {
            canvasWidth = canvas.getWidth();
            cellWidth = canvasWidth/3;
            cellHeight = cellWidth;

            Bitmap bitmapX = BitmapFactory.decodeResource(getResources(),R.drawable.x);
            Bitmap bitmapO = BitmapFactory.decodeResource(getResources(),R.drawable.o);
            bitmapX = Bitmap.createScaledBitmap(bitmapX,cellWidth-10,cellHeight-10,false);
            bitmapO = Bitmap.createScaledBitmap(bitmapO,cellWidth-10,cellHeight-10,false);

            for (int line=0; line<3; line++)
            {
                for(int col=0; col<3; col++)
                {
                    arr[line][col] = new Cell(col*cellWidth,line*cellHeight, cellWidth, bitmapX, bitmapO);;
                }
            }
            firstTime = false;
        }

        // paint board
        for (int line=0; line<3; line++)
        {
            for(int col=0; col<3; col++)
            {
                arr[line][col].draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            float x = event.getX();
            float y = event.getY();
            int line = (int) (y/cellWidth);
            int col = (int)(x/cellHeight);
            if(line < 3 && col < 3)
            {
                // for fb
                if(arr[line][col].isEmpty() == true)
                {
                    ((GameActivity)context).onCellClicked(line,col);
                }
                else
                {
                    Toast.makeText(getContext(), "not empty", Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(getContext(), "outside", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void setNewValOnBoard(int line, int col) {
        // this functions calls from GameActivity when new value receive from FB
        if(arr[line][col].setVal(count) == true)
            count = 1 - count;

        invalidate();
    }

    public Cell[][] getArr() {
        return arr;
    }
}
