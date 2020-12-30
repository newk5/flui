package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.flag.JImWindowFlags;
import vlsi.utils.CompactHashMap;

public class Window extends SizedWidget {

    private static long windowsCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private JImStr title = new JImStr("");
    //background color
    private Color color;
    private JImVec4 c;
    private int flags;
    private boolean moveable;
    private boolean resizable;
    private boolean collapsible;
    private boolean showTitlebar;
    private boolean noBackground;

    private boolean appliedSizeOnce;

    private BiConsumer<Float, Float> onResize;
    int iterations = 1;

    public Window(String id) {
        super(id);
        this.init();
    }

    private void init() {
        windowsCounter++;
        this.index(windowsCounter);
        idIndex.put(id, windowsCounter);
        instances.add(this);
        title = new JImStr(id);
        this.buildFlags();
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

    public static Window withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Window) w;
    }

    public Window fill() {

        super.fill();
        this.applyRelativeSizeToChildren();
        return this;
    }

    public Window font(String font) {
        super.font = font;
        return this;
    }

    public String getFont() {
        return super.font;
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

    public Window move(Direction d) {
        super.move(d);

        return this;
    }

    protected static void reApplyRelativeSize() {
        instances.stream().filter(child -> child.getParent() == null)
                .forEach(window -> {

                    SizedWidget sw = (SizedWidget) window;
                    if (sw.getRelativeSizeX() > 0 || sw.getRelativeSizeY() > 0) {

                        sw.applyRelativeSize();
                        sw.applyAlignment();

                        sw.children.forEach(c -> {
                            if (c instanceof SizedWidget) {
                                SizedWidget sizedW = (SizedWidget) c;
                                sizedW.applyRelativeSize();
                                sizedW.applyAlignment();
                            }
                            if (c instanceof Canvas) {
                                Canvas canvas = (Canvas) c;
                                canvas.applyRelativeSizeToChildren();
                            } else if (c instanceof Window) {
                                Window w = (Window) c;
                                w.applyRelativeSize();
                                w.applyRelativeSizeToChildren();
                            } else if (c instanceof Tabview) {
                                Tabview tv = (Tabview) c;
                                tv.size(sw.getWidth(), sw.getHeight());
                                tv.applyRelativeSize();
                                tv.applyRelativeSizeToTabChildren();
                            }
                        });

                    }

                });
    }

    public Window(String id, boolean child) {
        super(id, child);
        this.init();
    }

    @Override
    public String toString() {
        return "Window{ id= " + id + " }";
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

    private void buildFlags() {
        flags = 0;
        flags |= JImWindowFlags.NoSavedSettings;

        if (!moveable) {
            flags |= JImWindowFlags.NoMove;
        }

        if (!resizable) {
            flags |= JImWindowFlags.NoResize;
        }
        if (!collapsible) {
            flags |= JImWindowFlags.NoCollapse;
        }
        if (!showTitlebar) {
            flags |= JImWindowFlags.NoTitleBar;
        }
        if (noBackground) {
            flags |= JImWindowFlags.NoBackground;
        }

    }

    private float offsetY, offsetX;
    private boolean allChildrenAdded;

    @Override
    public void render(JImGui imgui) {
        if (!super.isHidden()) {
            if (font != null ) {
                imgui.pushFont(Application.fonts.get(font).getJimFont());
            }
            imgui.pushID(numId);
            if (!moveable) {
                float heightOffset = super.getParent() == null && relativeSizeY == 1 ? Topbar.height : 0f;
                imgui.setNextWindowPos(super.getPosX(), super.getPosY() + heightOffset);

            }
            if (color != null) {
                c = color.asVec4(c);
                imgui.pushStyleColor(JImStyleColors.WindowBg, c);
            }

            if (!resizable || !appliedSizeOnce) {
                imgui.setNextWindowSize(super.getWidth() + offsetX, super.getHeight() + offsetY);
                appliedSizeOnce = true;
            }

            imgui.getStyle().setWindowRounding(0);
            imgui.begin(title, flags);

            float newY = imgui.getContentRegionMaxY();
            float newX = imgui.getContentRegionMaxX();
            if (super.firstRenderLoop) {
                offsetX = width - newX;
                offsetY = height - newY;
                super.width = newX;
                super.height = newY;

            }
            if (super.getWidth() != newX || super.getHeight() != newY) {

                width(newX);
                height(newY);
                applyRelativeSizeToChildren();
                if (onResize != null && resizable) {

                    onResize.accept(newX, newY);

                }
            }

            if (super.firstRenderLoop || allChildrenAdded) {
                applyRelativeSizeToChildren();
                allChildrenAdded = false;
            }
            this.applyMove(imgui);

            if (color != null) {
                imgui.popStyleColor();
            }
            for (Widget w : children) {
                w.render(imgui);
            }

            if (reapplyAlign && super.getAlign() != null) {
                reapplyAlign = false;
                setAlignment(super.getAlign());
            }

            imgui.end();

            imgui.popID();
            firstRenderLoop = false;
            if (font != null) {
                imgui.popFont();
            }
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

    public float getWidth() {
        return super.getWidth();
    }

    public float getHeight() {
        return super.getHeight();
    }

    public Window position(float x, float y) {
        this.position(x, y);
        return this;
    }

    public Window onResize(final BiConsumer<Float, Float> bc) {
        this.onResize = bc;
        return this;
    }

    public Window alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Window align(Alignment a) {
        return (Window) super.setAlignment(a);
    }

    public Window children(Widget... widgets) {
        for (Widget w : widgets) {

            add(w);
        }
        // allChildrenAdded=true;
        return this;
    }

    public void add(Widget w) {
        w.parent(this);
        if (w instanceof SizedWidget) {
            SizedWidget sw = ((SizedWidget) w);
            sw.applyRelativeSize();
            sw.applyAlignment();
            if (sw instanceof Tabview) {
                Tabview t = (Tabview) sw;
                t.size(getWidth(), getHeight() - t.getYOffset());

            }
        }
        this.children.add(w);
    }

    public Window width(final float value) {
        super.width(value);
        return this;
    }

    public Window height(final float value) {
        super.height(value);
        return this;
    }

    public Window width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public Window height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public Window hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public Window title(final String value) {
        this.title = new JImStr(value);
        return this;
    }

    public Window color(final Color value) {
        this.color = value;
        return this;
    }

    public List<Widget> getChildren() {
        return children;
    }

    public Color getColor() {
        return color;
    }

    public Window size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public Window moveable(final boolean value) {
        this.moveable = value;
        this.buildFlags();
        return this;
    }

    public Window resizable(final boolean value) {
        this.resizable = value;
        this.buildFlags();
        return this;
    }

    public Window collapsible(final boolean value) {
        this.collapsible = value;
        this.buildFlags();
        return this;
    }

    public Window showTitlebar(final boolean value) {
        this.showTitlebar = value;
        this.buildFlags();
        return this;
    }

    public Window noBackground(final boolean value) {
        this.noBackground = value;
        this.buildFlags();
        return this;
    }

}
