package com.example.firstapp;


import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.*;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;

enum ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

class SwipeController extends Callback {

    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final int buttonWidth = 400;
    private Vibrator vibrator;


    private boolean isSwiping = false;

    private boolean isTaskOn = false;

    private Context callersContext;
    private ContactAdapter callersAdapter;

    SwipeController(Context context, ContactAdapter adapter){
        callersContext = context;
        callersAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT | RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        if (direction == LEFT){
//            Log.d("OOOO", "ㅇㅇ?");
//            callersAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
//        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ACTION_STATE_SWIPE) {
            isSwiping = true;
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        drawButtons(c, viewHolder);

    }

    private void setTouchListener(final Canvas c,
                                  final RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY,
                                  final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE && !isTaskOn){
                    Log.d("OOOO", Float.toString(dX));
                    if (dX <= -buttonWidth + 20){
                        isTaskOn = true;

                        event.setAction(MotionEvent.ACTION_DOWN);
                        vibrator = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(400);
                        }
//                        ContactAdapter.Holder contactAdapter = (ContactAdapter.Holder) viewHolder;
//                        String individContact = (String) contactAdapter.phoneNumber.getText();
//                        Uri telUri = Uri.parse("tel:"+individContact);
//                        callersContext.startActivity(new Intent("android.intent.action.CALL", telUri));
//                        Log.d("OOOO", "call");
                    }
                }
                float newDx = dX;
//                Log.d("!!!!", Boolean.toString(swipeBack));
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
//                Log.d("!!!!!", Float.toString(dX));
                Log.d("OOOO", "1--touch:" + Float.toString(dX));
                if (swipeBack){
                    Log.d("OOOO", "2-SWIPE BACK CONDITION");
                    Log.d("OOOO", Float.toString(dX));
                    Log.d("OOOO", Boolean.toString(isSwiping));
                    Log.d("OOOO", Boolean.toString(swipeBack));
                    Log.d("OOOO", Integer.toString(event.getAction()));
                }

                if (swipeBack && isSwiping && dX != 0) {
                    if (dX <= -buttonWidth){
                        newDx = -buttonWidth;
                        buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                        vibrator = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        SwipeController.super.onChildDraw(c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(400);
                        }
//                        ContactAdapter.Holder contactAdapter = (ContactAdapter.Holder) viewHolder;
//                        String individContact = (String) contactAdapter.phoneNumber.getText();
//                        Uri telUri = Uri.parse("tel:"+individContact);
//                        callersContext.startActivity(new Intent("android.intent.action.CALL", telUri));
//                        Log.d("OOOO", "call");
//
                    }
                    else if (200 < dX && dX <= buttonWidth) {
                        buttonShowedState = ButtonsState.LEFT_VISIBLE;
//                        Log.d("OOOO", "message");
                    }

                    if (buttonShowedState != ButtonsState.GONE) {
                        Log.d("????", Float.toString(dX));
                        //event.setAction(MotionEvent.ACTION_MASK);

//                        vibrator = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                        } else {
//                            vibrator.vibrate(500);
//                        }
//                        ContactAdapter.Holder contactAdapter = (ContactAdapter.Holder) viewHolder;
//                        String individContact = (String) contactAdapter.phoneNumber.getText();
//
//                        Log.d("OOOO", individContact);
////
//                        Uri telUri = Uri.parse("tel:"+individContact);
//                        callersContext.startActivity(new Intent("android.intent.action.CALL", telUri));

//                        Log.d("OOOO", (String) contactAdapter.phoneNumber.getText());
//                        setItemsClickable(recyclerView, false);

                    }

                    isSwiping = false;
                    swipeBack = false;
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("OOOO", "ONTOUCHMODIFIED");
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    Log.d("OOOO", "down");
//                    //setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("OOOO", "2-TOUCHUP");
                    SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, 0F, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Log.d("TTTTT", "오");
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;
                    buttonShowedState = ButtonsState.GONE;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView,
                                   boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        int buttonWidthWithoutPadding = buttonWidth - 20;
        float corners = 16;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

//        RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
//        p.setColor(Color.BLUE);
//        c.drawRoundRect(leftButton, corners, corners, p);
//        drawText("EDIT", c, leftButton, p);

        Rect rightButton = new Rect(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRect(rightButton, p);
        drawText("DELETE", c, rightButton, p);
    }

    private void drawText(String text, Canvas c, Rect button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }

}
