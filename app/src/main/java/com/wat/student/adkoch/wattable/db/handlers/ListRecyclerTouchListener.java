package com.wat.student.adkoch.wattable.db.handlers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ListRecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private ClickListener clickListener;
    private GestureDetector gestureDetector;

    public ListRecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
        this.clickListener=clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e){
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
            clickListener.onClick(rv.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    public interface ClickListener{
        void onClick(int position);
    }
}
