package ru.alex.book_pager.sort;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ru.alex.book_pager.R;
import ru.alex.book_pager.Utils;

public class ItemView extends androidx.appcompat.widget.AppCompatImageView {

    @IntDef({State.NORMAL, State.DRAGGABLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int NORMAL = 0;
        int DRAGGABLE = 1;
    }

    public interface OnDragListener {
        void startDrag();
    }

    private static Drawable dragHandleDrawable;
    private static int margins;

    private final Rect dragHandleBounds = new Rect();
    @Nullable
    private OnDragListener onDragListener;
    @State private int state = State.NORMAL;

    public ItemView(Context context) {
        super(context);
        staticInit(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        staticInit(context);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        staticInit(context);
    }

    private static void staticInit(Context context) {
        if (dragHandleDrawable != null) {
            return;
        }
        margins = context.getResources().getDimensionPixelSize(R.dimen.item_view_cell_margin);
        dragHandleDrawable = Utils.withTintColorRes(
            context,
            R.drawable.ic_drag_24,
            R.color.secondary
        );
    }

    public void setOnItemDragListener(@Nullable OnDragListener listener) {
        onDragListener = listener;
    }

    public void setState(@State int state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateDragHandleBounds();
    }

    private void updateDragHandleBounds() {
        int top = getPaddingTop() + margins;
        int right = getMeasuredWidth() - getPaddingRight() - margins;
        int left = right - dragHandleDrawable.getIntrinsicWidth();
        int bottom = top + dragHandleDrawable.getIntrinsicHeight();
        dragHandleBounds.set(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (State.DRAGGABLE == state) {
            drawDragHandle(canvas);
        }
    }

    private void drawDragHandle(@NonNull Canvas canvas) {
        dragHandleDrawable.setBounds(dragHandleBounds);
        dragHandleDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (State.DRAGGABLE == state && onDragListener != null) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                if (dragHandleBounds.contains((int) event.getX(), (int) event.getY())) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    onDragListener.startDrag();
                }
            }
            result = true;
        }
        return result || super.onTouchEvent(event);
    }
}
