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
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.flag.JImTabBarFlags;
import org.ice1000.jimgui.flag.JImWindowFlags;
import vlsi.utils.CompactHashMap;

public class Tabview extends SizedWidget {

    private static long tabviewCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private List<Tab> tabs = new ArrayList<>();

    private JImStr title = new JImStr("");
    //background color
    private Color color;

    private int flags;
    protected float tabRounding = 4;
    protected float tabSpacing= 4;

    protected float tabHeight;

    public Tabview(String id) {
        super(id, true);
        this.init();
    }

    public Tabview() {
        super();
    }

    @Override
    protected void init() {
        tabviewCounter++;
        this.index(tabviewCounter);
        idIndex.put(id, tabviewCounter);
        instances.add(this);
        title = new JImStr(id);
    }

    public void delete() {
        UI.runLater(() -> {

            if (deleteFlag) {
                idIndex.remove(id);
                instances.remove(this);

                tabs.forEach(child -> {
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

    public static Tabview withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Tabview) w;
    }

    private void applySizeOffsetToTabs() {
        children.forEach(c -> {

            if (c instanceof Canvas) {
                Canvas canvas = (Canvas) c;
                canvas.applySizeOffsetToTabs();
            } else if (c instanceof Tabview) {
                Tabview tabs = (Tabview) c;
                tabs.height(tabs.getHeight() - tabs.getYOffset());
            }

        });
    }

    public Tabview move(Direction d) {
        super.move(d);
        this.applySizeOffsetToTabs();
        return this;
    }

    protected float getYOffset() {
        if (super.getMove() != null) {
            return (super.getPosY() + super.getMove().getDown());
        }
        if (super.getPosY() > 0) {
            return super.getPosY();
        }
        return tabHeight;
    }

    protected void applyRelativeSizeToTabChildren() {
        this.tabs.stream().forEach(tab -> {
            tab.reApplyChildrenSize=true;
            tab.getChildren().forEach(child -> {
                if (child instanceof SizedWidget) {
                    SizedWidget w = (SizedWidget) child;
                    w.applyRelativeSize();
                    w.applyAlignment();
                    if (w instanceof Canvas) {
                        Canvas c = (Canvas) w;
                        if (!c.getChildren().isEmpty()) {
                            c.applyRelativeSizeToChildren();
                        }
                    } else if (w instanceof Tabview) {
                        Tabview tv = (Tabview) w;
                        tv.size(tv.getWidth() - 3, tv.getHeight() - tv.getYOffset());

                        tv.applyRelativeSizeToTabChildren();
                    }
                }

            });

        });
    }

    @Override
    public String toString() {
        return "Tabview{ id= " + id + " }";
    }

    public Tabview font(String font) {
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

                imgui.pushStyleColor(JImStyleColors.ChildBg, color.asVec4());
            }
            imgui.pushStyleVar(JImStyleVars.ItemInnerSpacing,tabSpacing,4);
            if (imgui.beginTabBar(title,JImTabBarFlags.None)) {
                tabs.forEach(tab -> tab.render(imgui));
                imgui.endTabBar();

            }

            if (color != null) {
                imgui.popStyleColor();
            }
            imgui.popStyleVar();
            postRender(imgui);
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

    public Tabview position(float x, float y) {
        this.position(x, y);
        return this;
    }

    public Tabview tabRounding(float tabRounding) {
        this.tabRounding = tabRounding;
        return this;
    }
    
     public Tabview tabSpacing(float tabSpacing) {
        this.tabSpacing = tabSpacing;
        return this;
    }


    public Tabview alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Tabview align(Alignment a) {
        return (Tabview) super.setAlignment(a);
    }

    public Tabview tabs(Tab... tabs) {
        for (Tab w : tabs) {
            w.tabRounding = this.tabRounding;
            add(w);
        }

        return this;
    }

    protected void applySizeToTabs() {

        this.tabs.forEach(tab -> {

            tab.width(getWidth());
            tab.height(getHeight());
        });
    }

    public void add(Tab w) {
        w.parent(this);

        this.tabs.add(w);
    }

    public Tabview width(final float value) {
        super.width(value);
        this.applySizeToTabs();
        return this;
    }

    public Tabview height(final float value) {
        super.height(value);
        this.applySizeToTabs();
        return this;
    }

    public float getWidth() {
        return super.getWidth();
    }

    public float getHeight() {
        return super.getHeight();
    }

    public Tabview width(final String widthPercent) {
        super.width(widthPercent);
        this.applySizeToTabs();
        return this;
    }

    public Tabview height(final String heightPercent) {
        super.height(heightPercent);
        this.applySizeToTabs();
        return this;
    }

    public Tabview hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public Tabview title(final JImStr value) {
        this.title = value;
        return this;
    }

    public Tabview color(final Color value) {
        this.color = value;
        return this;
    }

    public List<Tab> getTabs() {
        return tabs;
    }

    public Color getColor() {
        return color;
    }

    public Tabview size(float width, float height) {
        width -= 5;
        this.width(width);
        this.height(height);
        return this;
    }

}
