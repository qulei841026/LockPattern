package com.carsmart.lockpattern;

import android.graphics.Rect;
import android.widget.ImageView;

public class PatternPoint {

    int number;

    private Rect rect;
    private int normal;
    private int select;
    private int warn;
    private ImageView imageView;

    public PatternPoint(ImageView imageView) {
        this.imageView = imageView;
    }

    public PatternPoint setImageResource(int normal, int select, int warn) {
        this.normal = normal;
        this.select = select;
        this.warn = warn;
        return this;
    }

    public int centerX() {
        return rect.centerX();
    }

    public int centerY() {
        return rect.centerY();
    }

    public void setLayout(int l, int t, int r, int b) {
        this.rect = new Rect(l, t, r, b);
    }

    public boolean contains(int x, int y) {
        return rect.contains(x, y);
    }

    public PatternPoint normal() {
        imageView.setImageResource(normal);
        return this;
    }

    public PatternPoint select() {
        imageView.setImageResource(select);
        return this;
    }

    public PatternPoint warn() {
        imageView.setImageResource(warn);
        return this;
    }

    @Override
    public String toString() {
        return "PatternPoint{" +
                "imageView=" + imageView +
                ", rect=" + rect +
                ", number=" + number +
                ", normal=" + normal +
                ", select=" + select +
                ", warn=" + warn +
                '}';
    }
}
