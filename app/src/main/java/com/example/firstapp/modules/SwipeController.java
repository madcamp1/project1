package com.example.firstapp.modules;


import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper.*;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.adapter.ContactAdapter;



public class SwipeController extends Callback {
    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }
    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final int buttonWidth = 300;
    private static final int buttonWidthErrorOffset = 100;
    private Vibrator vibrator;

    private Context callersContext;
    private ContactAdapter callersAdapter;

    private boolean isSwiping = false;
    private boolean currentTaskon = false;

    private final int vibrateSeconds = 100;
    private final int callInvokeSeconds = 300;

    public SwipeController(Context context, ContactAdapter adapter){
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
        //완전히 swipe된 경우. swipe도중엔 찍히지 않음
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
            drawButtons(c, viewHolder, (int)dX);
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive); //여기조건걸
            if (dX >= buttonWidth|| dX <= -buttonWidth){
                return;
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setTouchListener(Canvas c,
                                  RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {
        Log.d("TTEST", "DD");
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack && !currentTaskon) {
                    currentTaskon = true;
                    if (buttonWidth < dX){
                        call(viewHolder);
                        activateVibrator();
                        recyclerView.getChildAt(viewHolder.getAdapterPosition()).setX(buttonWidth);
                    }
                    else if (-buttonWidth > dX){
                        message(viewHolder);
                        activateVibrator();
                        recyclerView.getChildAt(viewHolder.getAdapterPosition()).setX(-buttonWidth);
                    }
                    currentTaskon = false;
                }
                return false;
            }
        });
    }

    public void activateVibrator(){
        vibrator = (Vibrator) callersContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(vibrateSeconds, VibrationEffect.DEFAULT_AMPLITUDE));

        } else {
            vibrator.vibrate(vibrateSeconds);
        }
        currentTaskon = false;
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder, int direction) {
        int buttonWidthWithPadding = buttonWidth + 60;
        float corners = 37;
        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        if (direction > 0){
            RectF leftButton = new RectF(itemView.getLeft() + 7, itemView.getTop()+10, itemView.getLeft() + buttonWidthWithPadding, itemView.getBottom()-10);
            p.setColor(Color.parseColor("#295140"));
            c.drawRoundRect(leftButton, corners, corners, p);
            drawText("CALL", c, leftButton, p);
        }
        else if (direction < 0){
            RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithPadding, itemView.getTop()+10, itemView.getRight() - 7, itemView.getBottom()-10);
            p.setColor(Color.DKGRAY);
            c.drawRoundRect(rightButton, corners, corners, p);
            drawText("MESSAGE", c, rightButton, p);
        }

    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);
        p.setTextAlign(Paint.Align.LEFT);
        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }

    private void call(RecyclerView.ViewHolder viewHolder){
        ContactAdapter.Holder contactAdapter = (ContactAdapter.Holder) viewHolder;
        String individContact = (String) contactAdapter.phoneNumber.getText();
        Uri telUri = Uri.parse("tel:"+individContact);
        callersContext.startActivity(new Intent("android.intent.action.CALL", telUri));
    }

    private void message(RecyclerView.ViewHolder viewHolder){
        ContactAdapter.Holder contactAdapter = (ContactAdapter.Holder) viewHolder;
        Uri msgUri = Uri.parse("tel:"+contactAdapter.phoneNumber.getText());
        Intent intent = new Intent(Intent.ACTION_VIEW, msgUri);
        intent.putExtra("address", contactAdapter.phoneNumber.getText());
        intent.putExtra("sms_body", "");
        intent.setType("vnd.android-dir/mms-sms");
        callersContext.startActivity(intent);
    }
}
