package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.ice1000.jimgui.JImFont;
import org.ice1000.jimgui.JImFontAtlas;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.flag.JImItemFlags;

public abstract class Widget {

    public String id;
    public boolean child;
    private long index;
    protected float posX;
    protected float posY;
    private boolean hasSetPos;
    private boolean hasSetPosX;
    private boolean hasSetPosY;
    protected boolean sameLine;
    private SizedWidget parent;
    private Alignment align;
    private boolean hidden;
    private float alpha = 1;
    private Direction move;
    protected boolean isInTab;
    protected int numId;
    protected boolean firstRenderLoop = true;
    protected String font;
    protected boolean deleteFlag;
    private Map<String, Object> data;
    protected Font fontObj;
    protected boolean disabled;
    protected boolean applyMove=true;

    public Widget(String id) {
        this.id = id;
        this.buildID();
        UI.add(this);
    }

    public Widget() {

    }

    public void setData(String name, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }

        data.put(name, value);

    }

    public <T> T getData(String name) {
        if (data == null) {
            return null;
        }

        Object value = data.get(name);

        try {
            return (T) value;
        } catch (Exception e) {
            return null;
        }

    }

    public <T> T getData(String name, Object defaultValue) {
        if (data == null) {
            return (T) defaultValue;
        }

        Object value = data.get(name);

        if (value == null) {
            return (T) defaultValue;
        } else {
            try {
                return (T) value;
            } catch (Exception e) {
                return null;
            }
        }

    }

    private void buildID() {
        UI.globalCounter.increment();
        numId = UI.globalCounter.intValue();
    }

    public Widget(String id, boolean child) {
        this.id = id;
        this.buildID();
        this.child = child;
        if (!child) {
            UI.add(this);
        }
    }

    protected void init() {
        this.buildID();
        UI.add(this);
    }

    public List<Widget> getChildren() {

        return new ArrayList<>();
    }

    protected static Widget getWidget(long index, CopyOnWriteArrayList<Widget> list) {

        return binarySearch(index, list);
    }

    protected static Widget binarySearch(long index, CopyOnWriteArrayList<Widget> list) {
        int l = 0, r = list.size() - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;

            Widget w = list.get(m);

            if (w.getIndex() == index) {
                return w;
            }

            if (w.getIndex() < index) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }

        return null;
    }

    protected void applyGeneralMove(JImGui imgui) {

        if (move != null && applyMove) {
            if (move.getLeft() > 0) {
                imgui.setCursorPosX(imgui.getCursorPosX() - move.getLeft());
                posX = imgui.getCursorPosX();
            } else if (move.getLeftRelative() > 0) {
                if (getParent() == null) {
                    imgui.setCursorPosX(imgui.getCursorPosX() - (UI.windowWidth * move.getLeftRelative()));
                } else {
                    imgui.setCursorPosX(imgui.getCursorPosX() - (getParent().getWidth() * move.getLeftRelative()));
                }
                posX = imgui.getCursorPosX();
            }
            if (move.getRight() > 0) {
                imgui.setCursorPosX(imgui.getCursorPosX() + move.getRight());
                posX = imgui.getCursorPosX();
            } else if (move.getRightRelative() > 0) {
                if (getParent() == null) {
                    imgui.setCursorPosX(imgui.getCursorPosX() + (UI.windowWidth * move.getRightRelative()));
                } else {
                    imgui.setCursorPosX(imgui.getCursorPosX() + (getParent().getWidth() * move.getRightRelative()));
                }
                posX = imgui.getCursorPosX();
            }

            if (move.getUp() > 0) {
                imgui.setCursorPosY(imgui.getCursorPosY() - move.getUp());
                posY = imgui.getCursorPosY();
            } else if (move.getUpRelative() > 0) {
                if (getParent() == null) {

                    //imgui.setNextWindowPos(posX, imgui.getCursorPosY() - (UI.windowHeight * move.getUpRelative()));
                    imgui.setCursorPosY(imgui.getCursorPosY() - (UI.windowHeight * move.getUpRelative()));
                } else {
                    imgui.setCursorPosY(imgui.getCursorPosY() - (getParent().getHeight() * move.getUpRelative()));
                }
                posY = imgui.getCursorPosY();
            }

            if (move.getDown() > 0) {

                imgui.setCursorPosY(imgui.getCursorPosY() + move.getDown());
                posY = imgui.getCursorPosY();
            } else if (move.getDownRelative() > 0) {
                if (getParent() == null) {
                    imgui.setCursorPosY(imgui.getCursorPosY() + (UI.windowHeight * move.getDownRelative()));
                } else {
                    imgui.setCursorPosY(imgui.getCursorPosY() + (getParent().getHeight() * move.getDownRelative()));
                }
                posY = imgui.getCursorPosY();
            }
            applyMove=false;
        }
    }

    protected void preRender(JImGui imgui) {

        if (fontObj != null) {

            if (fontObj.getJimFont() != null) {
                imgui.pushFont(fontObj.getJimFont());
            }
        }

        imgui.pushID(numId);
        if (hasSetPosX) {
            imgui.setCursorPosX(posX);
        }
        if (hasSetPosY) {

            imgui.setCursorPosY(posY);
        }
        if (hasSetPos) {
            imgui.setCursorPos(posX, posX);
        }
        this.applyGeneralMove(imgui);

        imgui.pushStyleVar(JImStyleVars.Alpha, alpha);

        imgui.pushItemFlag(JImItemFlags.Disabled, disabled);

    }

    protected void postRender(JImGui imgui) {
        if (sameLine) {
            imgui.sameLine();
        }

        //pop alpha
        imgui.popStyleVar();
        imgui.popID();
        firstRenderLoop = false;
        if (fontObj != null) {
            imgui.popFont();
        }
        imgui.popItemFlag();

    }

    public Alignment getAlign() {
        return align;
    }

    protected void render(JImGui imgui) {

    }

    public float getAlpha() {
        return alpha;
    }

    public SizedWidget getParent() {
        return parent;
    }

    protected Widget position(float x, float y) {
        hasSetPos = true;
        this.posX(x);
        this.posY(y);
        return this;
    }

    public long getIndex() {
        return index;
    }

    public float getPosX() {
        return posX;
    }

    public Direction getMove() {
        return move;
    }

    public float getPosY() {
        return posY;
    }

    public boolean isChild() {
        return child;
    }

    protected Widget child(final boolean value) {
        this.child = value;
        return this;
    }

    protected Widget alpha(final float alpha) {
        this.alpha = alpha;

        return this;
    }

    protected Widget posX(final float value) {
        this.hasSetPosX = true;
        this.posX = value;
        return this;
    }

    protected Widget posY(final float value) {
        this.hasSetPosY = true;
        this.posY = value;
        return this;
    }

    protected Widget move(final Direction value) {
        this.move = value;

        return this;
    }

    public Widget sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    protected Widget hidden(final boolean value) {
        this.hidden = value;
        return this;
    }

    protected Widget parent(final SizedWidget value) {
        this.parent = value;
        return this;
    }

    protected Widget setAlign(final Alignment value) {
        this.align = value;
        return this;
    }

    protected boolean isHidden() {
        return hidden;
    }

    protected Widget index(final long value) {
        this.index = value;
        return this;
    }

}
