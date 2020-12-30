package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import java.util.ArrayList;
import java.util.List;
import org.ice1000.jimgui.JImGui;

public abstract class SizedWidget extends Widget {

    protected float width;
    protected float height;

    protected float relativeSizeX;
    protected float relativeSizeY;

    protected boolean reapplyAlign;
    protected boolean reapplyPos;
    protected List<Widget> children = new ArrayList<>();

    public SizedWidget(String id) {
        super(id);
    }

    public SizedWidget(String id, boolean child) {
        super(id, child);

        if (UI.idExists(id)) {
            System.err.println("ERROR: '" + id + "' has already been used as an identifier, ID's must be unique!");
            throw new RuntimeException("Duplicate ID: " + id);
        }
        UI.addID(id);
    }

    protected void deleteChild(Widget w) {
        children.remove(w);
    }

    protected void applyRelativeSize() {

        if (super.getParent() == null) {
            if (relativeSizeX > 0) {
                this.width = (relativeSizeX * UI.windowWidth);

            }
            if (relativeSizeY > 0) {
                this.height = (relativeSizeY * (UI.windowHeight - Topbar.height));

            }
        } else {

            if (super.getParent() instanceof SizedWidget) {
                SizedWidget sw = (SizedWidget) super.getParent();
                if (relativeSizeX > 0) {
                    this.width = (relativeSizeX * sw.getWidth());

                }
                if (relativeSizeY > 0) {

                    this.height = (relativeSizeY * sw.getHeight());

                }
            }
        }
    }

    protected void postRender(JImGui imgui) {

      

        if (reapplyAlign && super.getAlign() != null) {
            reapplyAlign = false;
            setAlignment(super.getAlign());
        }
        super.postRender(imgui);
    }

    protected SizedWidget fill() {
        SizedWidget.this.width("100%");
        SizedWidget.this.height("100%");
        this.applyAlignment();
        return this;
    }

    protected void applyAlignment() {
        float w = super.getParent() == null ? UI.windowWidth : super.getParent().getWidth();
        float h = super.getParent() == null ? (UI.windowHeight - Topbar.height) : super.getParent().getHeight();

        if (isInTab) {
            h -= UI.tabBottomBorderOffset;
        }

        Alignment a = super.getAlign();
        if (null != a) {

            switch (a) {
                case TOP_LEFT:
                    super.posX(0);
                    super.posY(0);
                    break;
                case TOP_CENTER:
                    super.posX(w / 2 - width / 2);
                    super.posY(0);
                    break;
                case TOP_RIGHT:
                    super.posX(w - width);
                    super.posY(0);
                    break;
                case MID_LEFT:
                    super.posX(0);
                    super.posY(h / 2 - height / 2);
                    break;
                case MID_CENTER:
                    super.posX(w / 2 - width / 2);
                    super.posY(h / 2 - height / 2);

                    break;
                case MID_RIGHT:
                    super.posX(w - width);
                    super.posY(h / 2 - height / 2);
                    break;
                case BOTTOM_LEFT:
                    super.posX(0);
                    super.posY(h - height);

                    break;
                case BOTTOM_CENTER:
                    super.posY(h - height);
                    super.posX(w / 2 - width / 2);

                    break;
                case BOTTOM_RIGHT:
                    super.posX(w - width);
                    super.posY(h - height);
                    break;
                default:
                    break;
            }
        }
    }

    protected Widget setAlignment(Alignment a) {
        super.setAlign(a);
        this.applyAlignment();

        return this;
    }

    protected float getWidth() {
        return width;
    }

    protected float getHeight() {
        return height;
    }

    protected float getRelativeSizeX() {
        return relativeSizeX;
    }

    protected float getRelativeSizeY() {
        return relativeSizeY;
    }

    public SizedWidget child(final boolean value) {
        this.child = value;
        return this;
    }

    protected SizedWidget width(final float value) {
        this.width = value;
        reapplyAlign = true;
        return this;
    }

    protected SizedWidget height(final float value) {
        this.height = value;
        reapplyAlign = true;

        return this;
    }

    protected SizedWidget width(final String value) {

        this.relativeSizeX = Float.parseFloat(value.replace("%", "")) / 100;
        applyRelativeSize();
        applyAlignment();
        return this;
    }

    protected SizedWidget height(final String value) {

        this.relativeSizeY = Float.parseFloat(value.replace("%", "")) / 100;
        applyRelativeSize();
        applyAlignment();

        return this;
    }

}
