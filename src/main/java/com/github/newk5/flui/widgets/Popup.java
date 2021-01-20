package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeBool;
import org.ice1000.jimgui.flag.JImPopupFlags;
import vlsi.utils.CompactHashMap;

public class Popup extends SizedWidget {

    private static long popupCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private JImStr title;
    //background color
    private Color color;

    private boolean border;
    private NativeBool opened = new NativeBool();
    private boolean open = false;
    private boolean modal = true;
    private boolean close;

    public Popup(String id) {
        super(id);
        this.init();
    }

    @Override
    protected void init() {
        popupCounter++;
        this.index(popupCounter);
        idIndex.put(id, popupCounter);
        instances.add(this);
        title = new JImStr(id);

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

    public Popup move(Direction m) {
        super.move(m);
        return this;
    }

    public static Popup withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Popup) w;
    }

    public Popup fill() {

        super.fill();
        this.children.stream().filter(child -> child instanceof SizedWidget).forEach(child -> {
            SizedWidget w = (SizedWidget) child;
            w.applyRelativeSize();
            w.applyAlignment();
            if (w instanceof Popup) {
                Popup canvas = (Popup) w;
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

    public void open() {
        open = true;
        opened.modifyValue(open);
        reapplyPos = true;
        close = false;
    }

    public void close() {
        open = false;
        opened.modifyValue(open);
        close = true;
    }

    protected static void reApplyRelativeSize() {
        instances.stream().filter(child -> child.getParent() == null)
                .forEach(child -> {

                    SizedWidget sw = (SizedWidget) child;

                    sw.reapplyPos = true;

                    sw.children.forEach(c -> {
                        if (c instanceof SizedWidget) {
                            SizedWidget sizedW = (SizedWidget) c;
                            sizedW.applyRelativeSize();
                            sizedW.applyAlignment();
                        }
                        if (c instanceof Canvas) {
                            Canvas canvas = (Canvas) c;
                            canvas.applyRelativeSizeToChildren();
                        } else if (c instanceof Tabview) {
                            Tabview tv = (Tabview) c;
                            tv.size(sw.getWidth(), sw.getHeight());
                            tv.applyRelativeSize();
                            tv.applyRelativeSizeToTabChildren();
                        }
                    });

                });
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
            if (c instanceof Popup) {
                Canvas canvas = (Canvas) c;
                canvas.applySizeOffsetToTabs();
            } else if (c instanceof Tabview) {
                Tabview tv = (Tabview) c;
                tv.size(getWidth(), getHeight() - tv.getYOffset());

            }
        });
    }

    public Popup(String id, boolean child) {
        super(id, child);
        this.init();
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

                imgui.setNextWindowPos(super.getPosX() - ((UI.windowWidth * move.getLeftRelative() / 2)), super.getPosY());

            }
            if (move.getRight() > 0) {
                imgui.setNextWindowPos(imgui.getCursorPosX() + move.getRight(), super.getPosY());
            } else if (move.getRightRelative() > 0) {

                imgui.setNextWindowPos(super.getPosX() + ((UI.windowWidth * move.getRightRelative() / 2)), super.getPosY());

            }

            if (move.getUp() > 0) {
                imgui.setNextWindowPos(super.getPosX(), super.getPosY() - move.getUp());
            } else if (move.getUpRelative() > 0) {

                imgui.setNextWindowPos(super.getPosX(), super.getPosY() - ((UI.windowHeight * move.getUpRelative() / 2))); //125,150

            }

            if (move.getDown() > 0) {
                imgui.setNextWindowPos(super.getPosX(), super.getPosY() + move.getDown());
            } else if (move.getDownRelative() > 0) {

                imgui.setNextWindowPos(super.getPosX(), super.getPosY() + ((UI.windowHeight * move.getDownRelative() / 2)));

            }
        }
    }

    protected void preRender(JImGui imgui) {

        imgui.pushID(numId);
        if (super.fontObj != null) {

            if (fontObj.getJimFont() != null) {
                imgui.pushFont(fontObj.getJimFont());
            }
        }

        // applyGeneralMove(imgui);
        imgui.pushStyleVar(JImStyleVars.Alpha, super.getAlpha());

    }

    protected void applyGeneralMove(JImGui imgui) {
        Direction move = super.getMove();
        if (move != null) {
            if (move.getLeft() > 0) {
                imgui.setNextWindowPos(super.getPosX() - move.getLeft(), super.getPosY());
                posX = imgui.getCursorPosX();
            } else if (move.getLeftRelative() > 0) {

                imgui.setNextWindowPos(imgui.getCursorPosX() - (UI.windowWidth * move.getLeftRelative()), super.getPosY());

                posX = imgui.getCursorPosX();
            }
            if (move.getRight() > 0) {

                imgui.setNextWindowPos(imgui.getCursorPosX() + move.getRight(), super.getPosY());
                posX = imgui.getCursorPosX();
            } else if (move.getRightRelative() > 0) {

                imgui.setNextWindowPos(imgui.getCursorPosX() + (UI.windowWidth * move.getRightRelative()), super.getPosY());

                posX = imgui.getCursorPosX();
            }

            if (move.getUp() > 0) {
                imgui.setCursorPosY(imgui.getCursorPosY() - move.getUp());
                posY = imgui.getCursorPosY();
            } else if (move.getUpRelative() > 0) {

                imgui.setNextWindowPos(posX, imgui.getCursorPosY() - (UI.windowHeight * move.getUpRelative()));

                posY = imgui.getCursorPosY();
            }

            if (move.getDown() > 0) {

                imgui.setCursorPosY(imgui.getCursorPosY() + move.getDown());
                posY = imgui.getCursorPosY();
            } else if (move.getDownRelative() > 0) {

                imgui.setNextWindowPos(imgui.getCursorPosX(), imgui.getCursorPosY() + (UI.windowHeight * move.getDownRelative()));

                posY = imgui.getCursorPosY();
            }
        }
    }

    private float offsetY, offsetX;

    @Override
    public void render(JImGui imgui) {
        if (!super.isHidden()) {
            preRender(imgui);

            if (reapplyPos) {

                imgui.setNextWindowSize(width + offsetX, height + offsetY);
                applyAlignment();
                imgui.setNextWindowPos(super.getPosX(), super.getPosY());
                this.applyMove(imgui);
                reapplyPos = false;
            }
            if (open) {

                imgui.openPopup(title);
                open = false;
            }

            if (color != null) {

                imgui.pushStyleColor(JImStyleColors.ChildBg, color.asVec4());
            }

            imgui.getStyle().setWindowRounding(0);
            if (getWidth() > 0 || getHeight() > 0) {
                if (super.firstRenderLoop) {
                    imgui.setNextWindowSize(width + offsetX, height + offsetY);

                }
            }
            if (modal) {
                if (imgui.beginPopupModal(title, opened)) {

                    float newY = imgui.getContentRegionMaxY();
                    float newX = imgui.getContentRegionMaxX();
                    offsetX = width - newX;
                    offsetY = height - newY;

                    if (super.width != newX || super.height != newY) {
                        width(newX);
                        height(newY);
                        applyRelativeSizeToChildren();
                    }

                    for (Widget w : children) {
                        w.render(imgui);
                    }
                    if (color != null) {
                        imgui.popStyleColor();
                    }
                    imgui.endPopup();
                } else {
                    open = false;
                }
            } else {
                if (imgui.beginPopup(title, JImPopupFlags.NoOpenOverItems)) {

                    for (Widget w : children) {
                        w.render(imgui);
                    }
                    if (color != null) {
                        imgui.popStyleColor();
                    }
                    if (close) {
                        imgui.closeCurrentPopup();
                        close = false;
                    }
                    imgui.endPopup();
                } else {
                    open = false;
                }
            }

            postRender(imgui);

        }

    }

    protected void postRender(JImGui imgui) {
        imgui.popStyleVar();
        if (super.fontObj != null && super.fontObj.getJimFont() != null) {
            imgui.popFont();

        }
        imgui.popID();

        firstRenderLoop = false;

    }

    protected void applyAlignment() {
        float w = UI.windowWidth;
        float h = UI.windowHeight;

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
                case CENTER:
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
        reapplyPos = true;
    }

    public Popup position(float x, float y) {
        this.position(x, y);
        return this;
    }

    public Popup alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public Popup modal(final boolean modal) {
        this.modal = modal;
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Popup align(Alignment a) {
        reapplyAlign = true;
        return (Popup) super.setAlign(a);
    }

    public Popup children(Widget... widgets) {
        for (Widget w : widgets) {

            add(w);
        }
        return this;
    }

    public Popup font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public void add(Widget w) {
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
        this.children.add(w);
    }

    public Popup width(final float value) {
        super.width(value);
        this.applyRelativeSizeToChildren();
        return this;
    }

    public Popup height(final float value) {
        super.height(value);
        this.applyRelativeSizeToChildren();
        return this;
    }

    public Popup width(final String widthPercent) {
        super.width(widthPercent);
        this.applyRelativeSizeToChildren();
        return this;
    }

    public Popup height(final String heightPercent) {
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

    public Popup hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public Popup title(final String value) {
        this.title = new JImStr(value);
        return this;
    }

    public Popup color(final Color value) {
        this.color = value;
        return this;
    }

    public List<Widget> getChildren() {
        return children;
    }

    public Color getColor() {
        return color;
    }

    public Popup size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

}
