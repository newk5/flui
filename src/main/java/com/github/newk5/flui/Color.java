package com.github.newk5.flui;

import org.ice1000.jimgui.JImVec4;


public class Color {

    private float red;
    private float green;
    private float blue;
    private float alpha;

    public Color() {
    }

    public Color(int red, int green, int blue, int alpha) {
        this.red = red / 255.0f;
        this.green = green / 255.0f;
        this.blue = blue / 255.0f;
        this.alpha = alpha / 255.0f;
    }

    public Color(int red, int green, int blue) {
        this.red = red / 255.0f;
        this.green = green / 255.0f;
        this.blue = blue / 255.0f;
    }

    public JImVec4 asVec4() {
        return new JImVec4(red, green, blue, alpha);
    }

    public JImVec4 asVec4(JImVec4 v) {
        if (v != null) {
            return v;
        }
        v = new JImVec4(red, green, blue, alpha);
        return v;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public Color red(final float value) {
        this.red = value / 255.0f;
        return this;
    }

    public Color green(final float value) {
        this.green = value / 255.0f;
        return this;
    }

    public Color blue(final float value) {
        this.blue = value / 255.0f;
        return this;
    }

    public Color alpha(final float value) {
        this.alpha = value / 255.0f;
        return this;
    }

}
