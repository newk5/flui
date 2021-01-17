package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Application;
import com.github.newk5.flui.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeBool;
import org.ice1000.jimgui.flag.JImTabItemFlags;
import vlsi.utils.CompactHashMap;

public class Tab extends SizedWidget {

    private static long tabCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    protected float headerHeight;
    private JImStr title = new JImStr("");
    private JImStr childTitle = new JImStr("child");
    //background color
    private Color color;
    private JImVec4 c;
    private int flags;
    private NativeBool b;
    protected float tabRounding = 4;
    protected boolean reApplyChildrenSize;

    public Tab(String id) {

        super(id, true);
        child = true;
        this.init();
    }

    public Tab() {
        super();
    }

    @Override
    protected void init() {
        tabCounter++;
        this.index(tabCounter);
        idIndex.put(id, tabCounter);
        instances.add(this);
        title = new JImStr(id);
        b = new NativeBool();
        b.deallocateNativeObject();
    }

    public Tab closeable(boolean value) {
        if (value) {
            b = new NativeBool();
            b.modifyValue(value);
        } else {
            b.deallocateNativeObject();
        }
        return this;
    }

    public boolean isDisabled() {
        return super.disabled;
    }

    public Tab disabled(final boolean value) {
        super.disabled = value;
        if (value) {
            super.alpha(0.5f);
        } else {
            super.alpha(1);
        }
        return this;
    }

    public static Tab withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Tab) w;
    }

    public float getWidth() {
        return super.getWidth();
    }

    public float getHeight() {
        return super.getHeight();
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

    protected void applyRelativeSizeToChildren() {
        this.children.stream().filter(child -> child instanceof SizedWidget).forEach(child -> {
            SizedWidget w = (SizedWidget) child;
            w.applyRelativeSize();
            w.applyAlignment();
            if (w instanceof Tab) {
                Tab canvas = (Tab) w;
                if (!canvas.getChildren().isEmpty()) {
                    canvas.applyRelativeSizeToChildren();
                }
            }

        });
    }

    @Override
    public String toString() {
        return "Tab{ id= " + id + " }";
    }

    public Tab font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    @Override
    public void render(JImGui imgui) {
        if (!super.isHidden()) {
            super.preRender(imgui);

            if (color != null) {
                c = color.asVec4(c);
                imgui.pushStyleColor(JImStyleColors.ChildBg, c);
            }

            imgui.getStyle().setTabRounding(tabRounding);
            if (imgui.beginTabItem(title, b, JImTabItemFlags.Leading)) {
                if (headerHeight == 0) {
                    headerHeight = (imgui.getItemRectSizeY() + (imgui.getFrameHeightWithSpacing() * 2) + 5);

                    Tabview tv = ((Tabview) super.getParent());
                    if (tv.getPosY() == 0) {
                        tv.tabHeight = headerHeight;

                        tv.size(tv.getWidth() - 3, tv.getHeight() - tv.getYOffset());
                        applyRelativeSizeRecursivelyToParents(this);
                    }
                }
                if (imgui.beginChild0(childTitle, 0, 0, true)) {
                    if (super.firstRenderLoop || reApplyChildrenSize){
                        width= imgui.getContentRegionMaxX();
                        height = imgui.getContentRegionMaxY();
                        applyRelativeSizeToChildren();
                    }
                    children.forEach(child -> child.render(imgui));

                    imgui.endChild();
                }
                imgui.endTabItem();
            }

            if (color != null) {
                imgui.popStyleColor();
            }

            postRender(imgui);
            if (deleteFlag) {
                this.delete();
            }
        }

    }

    private void applyRelativeSizeRecursivelyToParents(SizedWidget sw) {
        sw.applyRelativeSize();
        if (sw.getParent() != null) {
            applyRelativeSizeRecursivelyToParents(sw.getParent());
        }
    }

    protected void postRender(JImGui imgui) {
        if (super.reapplyAlign && super.getAlign() != null) {
            super.reapplyAlign = false;
            setAlignment(super.getAlign());
        }
        if (super.font != null) {
            imgui.popFont();
        }
        imgui.popItemFlag();
        super.firstRenderLoop=false;
    }

    public Tab alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public Tab width(final float value) {
        super.width(value);
        return this;
    }

    public Tab height(final float value) {
        super.height(value);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Tab children(Widget... widgets) {
        for (Widget w : widgets) {

            add(w);
        }
        return this;
    }

    @Override
    public void add(Widget w) {
        w.parent(this);
        w.isInTab = true;
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
            }
        }
        this.children.add(w);
    }

    @Override
    public void addAtIndex(Widget w, int idx) {
        w.parent(this);
        w.isInTab = true;
        if (w instanceof SizedWidget) {
            SizedWidget sw = ((SizedWidget) w);
            sw.applyRelativeSize();
            sw.applyAlignment();
            if (sw instanceof Canvas) {
                Canvas c = (Canvas) sw;
                c.applyRelativeSizeToChildren();
            }
        }
        this.children.add(idx, w);
    }

    public Tab hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public Tab title(final String value) {
        this.title = new JImStr(value);
        return this;
    }

    public Tab color(final Color value) {
        this.color = value;
        return this;
    }

    public List<Widget> getChildren() {
        return children;
    }

    public Color getColor() {
        return color;
    }

}
