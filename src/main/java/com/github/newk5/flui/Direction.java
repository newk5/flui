package com.github.newk5.flui;

public class Direction {

    private float left;
    private float up;
    private float down;
    private float right;

    private float leftRelative;
    private float upRelative;
    private float downRelative;
    private float rightRelative;

    public Direction() {
    }

    public float getLeft() {
        return left;
    }

    public float getUp() {
        return up;
    }

    public float getDown() {
        return down;
    }

    public float getRight() {
        return right;
    }

    public float getLeftRelative() {
        return leftRelative;
    }

    public float getUpRelative() {
        return upRelative;
    }

    public float getDownRelative() {
        return downRelative;
    }

    public float getRightRelative() {
        return rightRelative;
    }

    public Direction left(final float value) {
        this.left = value;
        return this;
    }

    public Direction up(final float value) {
        this.up = value;
        return this;
    }

    public Direction down(final float value) {
        this.down = value;
        return this;
    }

    public Direction right(final float value) {
        this.right = value;
        return this;
    }

    public Direction left(final String percent) {
        this.leftRelative = Float.parseFloat(percent.replace("%", "")) / 100;
        return this;
    }

    public Direction up(final String percent) {
        this.upRelative = Float.parseFloat(percent.replace("%", "")) / 100;
        return this;
    }

    public Direction down(final String percent) {
        this.downRelative = Float.parseFloat(percent.replace("%", "")) / 100;

        return this;
    }

    public Direction right(final String percent) {
        this.rightRelative = Float.parseFloat(percent.replace("%", "")) / 100;
        return this;
    }

    @Override
    public String toString() {
        return "Direction{" + "left=" + left + ", up=" + up + ", down=" + down + ", right=" + right + ", leftRelative=" + leftRelative + ", upRelative=" + upRelative + ", downRelative=" + downRelative + ", rightRelative=" + rightRelative + '}';
    }
    
    

}
