package com.github.newk5.flui.events;



public class WindowResizeEvent {

    private float oldWidth;
    private float oldHeight;

    private float newWidth;
    private float newHeight;

    public WindowResizeEvent() {
    }

    public WindowResizeEvent(float oldWidth, float oldHeight, float newWidth, float newHeight) {
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }

    public float getOldWidth() {
        return oldWidth;
    }

    public float getOldHeight() {
        return oldHeight;
    }

    public float getNewWidth() {
        return newWidth;
    }

    public float getNewHeight() {
        return newHeight;
    }

    public void setOldWidth(float oldWidth) {
        this.oldWidth = oldWidth;
    }

    public void setOldHeight(float oldHeight) {
        this.oldHeight = oldHeight;
    }

    public void setNewWidth(float newWidth) {
        this.newWidth = newWidth;
    }

    public void setNewHeight(float newHeight) {
        this.newHeight = newHeight;
    }
    
    

}
