package com.carsmart.lockpattern;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PatternView extends ViewGroup {

    private int rank = 3;    //行列
    protected int count = rank * rank;

    private int awh = 0;    //真实每一项宽高
    private int iwh = 0;    //计算每一项宽高
    private int padding = 0;    //间隔

    protected final List<PatternPoint> points = new ArrayList<>(count);
    protected List<Pair<PatternPoint, PatternPoint>> lines = new ArrayList<>();//记录已经画过的线

    protected Paint mPaint;
    protected int paintWarnColor;
    protected int paintSelectColor;


    public PatternView(Context context) {
        this(context, null);
    }

    public PatternView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PatternView);

        int normal = attributes.getResourceId(R.styleable.PatternView_normal, 0);
        int select = attributes.getResourceId(R.styleable.PatternView_select, 0);
        int warn = attributes.getResourceId(R.styleable.PatternView_warn, 0);

        Drawable drawable = attributes.getDrawable(R.styleable.PatternView_normal);

        float lineWidth = attributes.getDimension(R.styleable.PatternView_line_width, 1);
        padding = (int) attributes.getDimension(R.styleable.PatternView_item_padding, 0);

        paintWarnColor = attributes.getColor(R.styleable.PatternView_line_warn, 0);
        paintSelectColor = attributes.getColor(R.styleable.PatternView_line_select, 0);

        attributes.recycle();

        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(paintSelectColor);
        mPaint.setStrokeWidth(lineWidth);

        if (drawable != null) {
            awh = Math.max(drawable.getIntrinsicHeight() + (padding * 2),
                    drawable.getIntrinsicWidth() + (padding * 2));
        }

        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            PatternPoint point = new PatternPoint(imageView);
            point.number = i + 1;
            point.setImageResource(normal, select, warn).normal();
            points.add(point);
            addView(imageView);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Pair<PatternPoint, PatternPoint> line : lines) {
            canvas.drawLine(line.first.centerX(), line.first.centerY
                    (), line.second.centerX(), line.second.centerY(), mPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            int row = i / this.rank;
            int column = i % this.rank;

            int left = column * iwh + (iwh - awh) / 2 + padding;
            int right = left + awh - (padding * 2);
            int top = row * iwh + (iwh - awh) / 2 + padding;
            int bottom = top + awh - (padding * 2);

            getChildAt(i).layout(left, top, right, bottom);
            points.get(i).setLayout(left, top, right, bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int whSize = awh * rank;

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int width, height;

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
        } else if (heightMeasureSpec == MeasureSpec.AT_MOST) {
            height = Math.min(heightSpecSize, whSize);
        } else {
            height = whSize;
        }

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            width = widthSpecSize;
        } else if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            width = Math.min(widthSpecSize, whSize);
        } else {
            width = whSize;
        }

        int wh = Math.max(Math.min(width, height), whSize);
        setMeasuredDimension(wh, wh);
        iwh = wh / rank;
        //测量每个子View的宽高
        for (int i = 0; i < getChildCount(); i++) {
            int cw = MeasureSpec.makeMeasureSpec(wh / rank, MeasureSpec.EXACTLY);
            int ch = MeasureSpec.makeMeasureSpec(wh / rank, MeasureSpec.EXACTLY);
            getChildAt(i).measure(cw, ch);
        }
    }

    public void setContent(List<String> list) {
        if (list == null) {
            return;
        }

        for (Pair<PatternPoint, PatternPoint> line : lines) {   //先清除之前的
            line.first.normal();
            line.second.normal();
        }

        lines.clear();
        invalidate();

        int size = list.size();
        if (size == 1) {    //只用一个点
            points.get(Integer.valueOf(list.get(0)) - 1).select();
        }

        for (int i = 0; i < size - 1; i++) {
            int s = Integer.valueOf(list.get(i)) - 1;
            int e = Integer.valueOf(list.get(i + 1)) - 1;
            lines.add(new Pair<>(points.get(s).select(), points.get(e).select()));
        }
        invalidate();
    }

}
