package com.carsmart.lockpattern;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class LockPatternView extends PatternView {

    private PatternPoint cPoint;    //发起点

    private int startX = 0;
    private int startY = 0;

    private int stopX = 0;
    private int stopY = 0;

    private boolean isDrawEnable = true;    //是否允许绘制手势密码

    private List<String> records = new ArrayList<>(count);

    private OnVerifyListener onVerifyListener;

    private OnSettingListener onSettingListener;

    public LockPatternView(Context context) {
        this(context, null);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            gotoNormal();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(startX, startY, stopX, stopY, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(runnable);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isDrawEnable)
            return false;

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                int x = (int) ev.getX();
                int y = (int) ev.getY();

                PatternPoint point = containPoint(x, y);
                if (point != null) {
                    point.select();
                    cPoint = point;
                    records.add(String.valueOf(cPoint.number));
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                PatternPoint point = containPoint((int) ev.getX(), (int) ev.getY());
                if (cPoint == null && point == null)
                    return true;

                if (point != null && cPoint == null) {
                    point.select();
                    cPoint = point;
                    records.add(String.valueOf(cPoint.number));
                }

                if (point != null && !isRecord(point.number)) {
                    point.select();
                    PatternPoint passPoint = getPassPoint(cPoint, point);
                    if (passPoint != null && !isRecord(passPoint.number)) {
                        passPoint.select();
                        records.add(String.valueOf(passPoint.number));
                        lines.add(new Pair<>(cPoint, passPoint));
                        lines.add(new Pair<>(passPoint, point));
                    } else {
                        lines.add(new Pair<>(cPoint, point));
                    }
                    cPoint = point;
                    records.add(String.valueOf(cPoint.number));
                }

                startX = cPoint.centerX();
                startY = cPoint.centerY();
                stopX = (int) ev.getX();
                stopY = (int) ev.getY();
                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {
                startX = startY = 0;
                stopX = stopY = 0;
                invalidate();
                isDrawEnable = false;
                call();
                break;
            }
        }
        return true;
    }

    private void call() {
        if (onVerifyListener != null) {
            if (onVerifyListener.isVerify(records)) {
                onVerifyListener.onSuccess();
            } else {
                onVerifyListener.onFailed();
            }
        } else if (onSettingListener != null) {
            onSettingListener.onSetting(records);
        }
    }

    private boolean isRecord(int i) {
        String s = String.valueOf(i);
        for (String item : records) {
            if (s.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private PatternPoint getPassPoint(PatternPoint sPoint, PatternPoint ePoint) {
        int start = sPoint.number;
        int end = ePoint.number;
        if ((start == 1 && end == 3) || (start == 3 && end == 1)) {
            return points.get(2 - 1);
        } else if ((start == 1 && end == 7) || (start == 7 && end == 1)) {
            return points.get(4 - 1);
        } else if ((start == 3 && end == 9) || (start == 9 && end == 3)) {
            return points.get(6 - 1);
        } else if ((start == 7 && end == 9) || (start == 9 && end == 7)) {
            return points.get(8 - 1);
        } else if ((start == 1 && end == 9) || (start == 9 && end == 1) ||
                (start == 2 && end == 8) || (start == 8 && end == 2) ||
                (start == 3 && end == 7) || (start == 7 && end == 3) ||
                (start == 4 && end == 6) || (start == 6 && end == 4)) {
            return points.get(5 - 1);
        }
        return null;
    }

    /**
     * 通过X、Y位置坐标查找对应的point手势点
     */
    private PatternPoint containPoint(int x, int y) {
        for (PatternPoint point : points) {
            if (point.contains(x, y)) {
                return point;
            }
        }
        return null;
    }

    public void gotoWarn() {
        mPaint.setColor(paintWarnColor);
        if (cPoint != null) {
            cPoint.warn();
        }
        for (Pair<PatternPoint, PatternPoint> line : lines) {
            line.first.warn();
            line.second.warn();
        }
        invalidate();
        postDelayed(runnable, 300l);
    }


    public void gotoNormal() {
        if (cPoint != null) {
            cPoint.normal();
        }
        cPoint = null;
        mPaint.setColor(paintSelectColor);
        isDrawEnable = true;
        for (Pair<PatternPoint, PatternPoint> line : lines) {
            line.first.normal();
            line.second.normal();
        }
        records.clear();
        lines.clear();
        invalidate();
    }

    public void setVerify(OnVerifyListener listener) {
        this.onVerifyListener = listener;
    }

    public void setContent(OnSettingListener listener) {
        this.onSettingListener = listener;
    }

    public interface OnVerifyListener {

        boolean isVerify(List<String> records);

        void onSuccess();

        void onFailed();
    }

    public interface OnSettingListener {

        void onSetting(List<String> records);
    }

}
