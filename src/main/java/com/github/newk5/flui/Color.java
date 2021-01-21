package com.github.newk5.flui;

import org.ice1000.jimgui.JImVec4;

public class Color {

    private float red;
    private float green;
    private float blue;
    private float alpha;
    private JImVec4 nativeInstance;

    public Color() {
    }
    
    public Color clone(){
        Color c = new Color();
        c.red= red;
        c.green= green;
        c.blue=blue;
        c.alpha=alpha;
        c.nativeInstance=nativeInstance;
        return c;
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
        this.alpha = 1;
    }

    public JImVec4 asVec4() {
        if (nativeInstance == null) {
            nativeInstance = new JImVec4(red, green, blue, alpha);
        }
        return nativeInstance;
    }

    public Color getNeighborColor2() {
        int r = (int) (red * 255);
        int g = (int) (green * 255);
        int b = (int) (blue * 255);
        return new Color(r - 50, g - 50, b - 50);
    }

    public Color getNeighborColor1() {
        int r = (int) (red * 255);
        int g = (int) (green * 255);
        int b = (int) (blue * 255);
        return new Color(r + 50, g + 50, b + 50);
    }

    public void free() {
        if (nativeInstance != null) {

            nativeInstance.deallocateNativeObject();
        }
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
