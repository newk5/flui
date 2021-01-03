package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImVec4;
import vlsi.utils.CompactHashMap;

public class Canvas extends SizedWidget {

    private static long canvasCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private JImStr title;
    //background color
    private Color color;
    private JImVec4 c;
    private boolean border;

    public Canvas(String id) {
        super(id, true);
        this.init();
    }

    @Override
    protected void init() {
        canvasCounter++;
        this.index(canvasCounter);
        idIndex.put(id, canvasCounter);
        instances.add(this);
        title = new JImStr(id);

    }

    public static Canvas withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Canvas) w;
    }

    public void delete() {
        UI.runLater(() -> {

            if (deleteFlag) {
                idIndex.remove(id);
                instances.remove(this);

                children.forEach(child -> {
                    ((SizedWidget) child).deleteFlag = true;
                });

                SizedWidget sw = super.getParent();
                if (sw != null) {
                    sw.deleteChild(this);
                }
            }
            deleteFlag = true;

        });

    }

    public Canvas sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public Canvas fill() {

        super.fill();
        this.children.stream().filter(child -> child instanceof SizedWidget).forEach(child -> {
            SizedWidget w = (SizedWidget) child;
            w.applyRelativeSize();
            w.applyAlignment();
            if (w instanceof Canvas) {
                Canvas canvas = (Canvas) w;
                if (!canvas.getChildren().isEmpty()) {
                    canvas.applyRelativeSizeToChildren();
                }
            } else if (w instanceof Tabview) {
                Tabview t = (Tabview) w;
                w.width(this.getWidth());
                w.height(getHeight() - t.getYOffset());
                t.applyRelativeSizeToTabChildren();
            }

        });
        return this;
    }

    public Canvas move(Direction d) {
        super.move(d);
        return this;
    }

    protected void applyRelativeSizeToChildren() {
        this.children.stream().filter(child -> child instanceof SizedWidget).forEach(child -> {
            SizedWidget w = (SizedWidget) child;
            w.applyRelativeSize();
            w.applyAlignment();
            if (w instanceof Canvas) {
                Canvas canvas = (Canvas) w;
                if (!canvas.getChildren().isEmpty()) {
                    canvas.applyRelativeSizeToChildren();
                }
            } else if (w instanceof Tabview) {
                Tabview tv = (Tabview) w;
                tv.size(getWidth(), getHeight() - tv.getYOffset());

                tv.applyRelativeSizeToTabChildren();
            }

        });
    }

    protected void applySizeOffsetToTabs() {
        children.forEach(c -> {
            if (c instanceof Canvas) {
                Canvas canvas = (Canvas) c;
                canvas.applySizeOffsetToTabs();
            } else if (c instanceof Tabview) {
                Tabview tv = (Tabview) c;
                tv.size(getWidth(), getHeight() - tv.getYOffset());

            }
        });
    }

    @Override
    public String toString() {
        return "Canvas{ id= " + id + " }";
    }

    private void applyMove(JImGui imgui) {
        Direction move = super.getMove();
        if (move != null) {
            if (move.getLeft() > 0) {
                imgui.setNextWindowPos(super.getPosX() - move.getLeft(), super.getPosY());
            } else if (move.getLeftRelative() > 0) {
                if (getParent() == null) {
                    imgui.setNextWindowPos(super.getPosX() - (UI.windowWidth * move.getLeftRelative()), super.getPosY());
                } else {
                    imgui.setNextWindowPos(super.getPosX() - (getParent().getWidth() * move.getLeftRelative()), super.getPosY());
                }
            }
            if (move.getRight() > 0) {
                imgui.setNextWindowPos(imgui.getCursorPosX() + move.getRight(), super.getPosY());
            } else if (move.getRightRelative() > 0) {
                if (getParent() == null) {
                    imgui.setNextWindowPos(super.getPosX() + (UI.windowWidth * move.getRightRelative()), super.getPosY());
                } else {
                    imgui.setNextWindowPos(super.getPosX() + (getParent().getWidth() * move.getRightRelative()), super.getPosY());
                }
            }

            if (move.getUp() > 0) {
                imgui.setNextWindowPos(super.getPosX(), super.getPosY() - move.getUp());
            } else if (move.getUpRelative() > 0) {
                if (getParent() == null) {
                    imgui.setNextWindowPos(super.getPosX(), super.getPosY() - (UI.windowHeight * move.getUpRelative()));
                } else {
                    imgui.setNextWindowPos(super.getPosX(), super.getPosY() - (getParent().getHeight() * move.getUpRelative()));
                }
            }

            if (move.getDown() > 0) {
                imgui.setNextWindowPos(super.getPosX(), super.getPosY() + move.getDown());
            } else if (move.getDownRelative() > 0) {
                if (getParent() == null) {
                    imgui.setNextWindowPos(super.getPosX(), super.getPosY() + (UI.windowHeight * move.getDownRelative()));
                } else {
                    imgui.setNextWindowPos(super.getPosX(), super.getPosY() + (getParent().getHeight() * move.getDownRelative()));
                }
            }
        }
    }

    private float offsetY, offsetX;

    @Override
    public void render(JImGui imgui) {
        if (!super.isHidden()) {
            super.preRender(imgui);

            if (color != null) {
                c = color.asVec4(c);
                imgui.pushStyleColor(JImStyleColors.ChildBg, c);
            }

            this.applyMove(imgui);

            imgui.getStyle().setWindowRounding(0);
            imgui.beginChild0(title, super.getWidth() + offsetX, super.getHeight() + offsetY, border);

            //set the real dimensions
            if (super.firstRenderLoop && getWidth() > 0 && getHeight() > 0) {
                float newY = imgui.getContentRegionMaxY();
                float newX = imgui.getContentRegionMaxX();

                offsetX = width - newX;
                offsetY = height - newY;

                super.width = newX;
                super.height = newY;
            }

            if (color != null) {
                imgui.popStyleColor();
            }
            for (Widget w : children) {
                w.render(imgui);
            }

            imgui.endChild();

            super.postRender(imgui);

            if (deleteFlag) {
                this.delete();
            }
        }

    }

    protected void postRender(JImGui imgui) {
        if (super.reapplyAlign && super.getAlign() != null) {
            super.reapplyAlign = false;
            setAlignment(super.getAlign());
        }
    }

    public Canvas position(float x, float y) {
        this.position(x, y);
        return this;
    }

    public Canvas alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public Canvas border(final boolean border) {
        this.border = border;
        return this;
    }

    public boolean hasBorder() {
        return border;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Canvas align(Alignment a) {
        return (Canvas) super.setAlignment(a);
    }

    public Canvas children(Widget... widgets) {
        for (Widget w : widgets) {

            add(w);
        }
        return this;
    }

    public Canvas font(String font) {
        super.font = font;
           super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    @Override
    public void add(Widget w) {
        w.parent(this);
        if (w instanceof SizedWidget) {
            SizedWidget sw = ((SizedWidget) w);
            sw.applyRelativeSize();
            sw.applyAlignment();
            if (sw instanceof Tabview) {
                Tabview tv = (Tabview) sw;

                tv.size(getWidth(), getHeight() - tv.getYOffset());

            } else if (sw instanceof Canvas) {
                Canvas c = (Canvas) sw;
                c.applyRelativeSizeToChildren();
            } else if (sw instanceof Table) {
                Table tbl = (Table) sw;
                tbl.prevBtnParentIdx = this.children.size() + 1;
                tbl.pagesLblParentIdx = this.children.size() + 2;
                tbl.nextBtnParentIdx = this.children.size() + 3;
            }
        }
        this.children.add(w);
    }

    public void addAtIndex(Widget w, int idx) {
        w.parent(this);
        if (w instanceof SizedWidget) {
            SizedWidget sw = ((SizedWidget) w);
            sw.applyRelativeSize();
            sw.applyAlignment();
            if (sw instanceof Tabview) {
                Tabview tv = (Tabview) sw;

                tv.size(getWidth(), getHeight() - tv.getYOffset());

            }
        }
        this.children.add(idx, w);
    }

    public Canvas width(final float value) {
        super.width(value);
        this.applyRelativeSizeToChildren();
        return this;
    }

    public Canvas height(final float value) {
        super.height(value);
        this.applyRelativeSizeToChildren();
        return this;
    }

    public Canvas width(final String widthPercent) {
        super.width(widthPercent);
        this.applyRelativeSizeToChildren();
        return this;
    }

    public Canvas height(final String heightPercent) {
        super.height(heightPercent);
        this.applyRelativeSizeToChildren();
        return this;
    }

    public float getWidth() {
        return super.getWidth();
    }

    public float getHeight() {
        return super.getHeight();
    }

    public Canvas hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public Canvas title(final JImStr value) {
        this.title = value;
        return this;
    }

    public Canvas color(final Color value) {
        this.color = value;
        return this;
    }

    public List<Widget> getChildren() {
        return children;
    }

    public Color getColor() {
        return color;
    }

    public Canvas size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

}
