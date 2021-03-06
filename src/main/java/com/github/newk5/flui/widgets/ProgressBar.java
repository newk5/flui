package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import vlsi.utils.CompactHashMap;

public class ProgressBar extends SizedWidget {

    private static long progbarCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private Color color;
    private Color hoverColor;
    private Color activeColor;

    private int flags;

    private boolean hasSetBorderSize;
    private boolean hasSetBorderRounding;
    private float borderRounding;
    private float borderSize;
    private Color borderColor;
    private float value;

    SerializableConsumer<ProgressBar> onChange;
    SerializableConsumer<ProgressBar> onHover;

    public ProgressBar(String id) {
        super(id, true);
        this.init();
    }

    public ProgressBar() {
        super();
    }

    @Override
    protected ProgressBar clone() {
        ProgressBar p = new ProgressBar();
        super.copyProps(p);

        if (color != null) {
            p.color = color.clone();
        }
        if (hoverColor != null) {
            p.hoverColor = hoverColor.clone();
        }
        if (activeColor != null) {
            p.activeColor = activeColor.clone();
        }

        p.flags = flags;
        p.hasSetBorderSize = hasSetBorderSize;
        p.hasSetBorderRounding = hasSetBorderRounding;
        p.borderRounding = borderRounding;
        p.borderSize = borderSize;

        if (borderColor != null) {
            p.borderColor = borderColor.clone();
        }

        p.value = value;
        p.onChange = onChange;
        p.onHover = onHover;

        return p;

    }

    @Override
    protected void init() {
        progbarCounter++;
        this.index(progbarCounter);
        idIndex.put(id, progbarCounter);
        instances.add(this);

    }

    public ProgressBar sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public ProgressBar font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    @Override
    protected void freeColors() {
        super.freeColor(color);
        super.freeColor(hoverColor);
        super.freeColor(activeColor);
        super.freeColor(borderColor);
    }

    public void delete() {
        UI.runLater(() -> {
            freeColors();
            idIndex.remove(id);
            instances.remove(this);

            SizedWidget sw = super.getParent();
            if (sw != null) {
                sw.deleteChild(this);
            }
        });

    }

    public static ProgressBar withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (ProgressBar) w;
    }

    public ProgressBar align(Alignment a) {
        return (ProgressBar) super.setAlignment(a);
    }

    public boolean isDisabled() {
        return super.disabled;
    }

    public ProgressBar disabled(final boolean value) {
        super.disabled = value;
        if (value) {
            super.alpha(0.5f);
        } else {
            super.alpha(1);
        }
        return this;
    }

    @Override
    public void render(JImGui imgui) {

        if (!super.isHidden()) {
            super.preRender(imgui);

            if (super.getWidth() > 0) {
                imgui.pushItemWidth(super.getWidth());
            }

            if (color != null) {
                imgui.pushStyleColor(JImStyleColors.FrameBg, color.asVec4());
            }

            if (hasSetBorderRounding) {
                imgui.pushStyleVar(JImStyleVars.FrameRounding, borderRounding);
            }
            if (hasSetBorderSize) {
                imgui.pushStyleVar(JImStyleVars.FrameBorderSize, borderSize);

            }
            if (borderColor != null) {
                imgui.pushStyleColor(JImStyleColors.Border, borderColor.asVec4());
            }

            imgui.progressBar(value, super.getWidth(), super.getHeight());

            if (hasSetBorderSize) {
                imgui.popStyleVar();
            }
            if (hasSetBorderRounding) {
                imgui.popStyleVar();
            }
            if (borderColor != null) {
                imgui.popStyleColor();
            }

            if (super.getWidth() > 0) {
                imgui.popItemWidth();
            }
            if (color != null) {
                imgui.popStyleColor();
            }
            if (imgui.isItemHovered()) {
                if (onHover != null) {
                    onHover.accept(this);
                }
            }
            if (getWidth() == 0 || (getWidth() != imgui.getItemRectSizeX())) {
                width(imgui.getItemRectSizeX());
                reapplyAlign = true;

            }
            if (getHeight() == 0 || getHeight() != imgui.getItemRectSizeY()) {
                height(imgui.getItemRectSizeY());
                reapplyAlign = true;
            }

            super.postRender(imgui);
            if (deleteFlag) {
                this.delete();
            }
        }

    }

    public ProgressBar hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public ProgressBar showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public float getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public ProgressBar move(Direction d) {
        super.move(d);
        return this;
    }

    public ProgressBar value(final float value) {
        this.value = value;
        return this;
    }

    public ProgressBar width(final float value) {
        super.width(value);
        return this;
    }

    public ProgressBar alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public ProgressBar height(final float value) {
        super.height(value);
        return this;
    }

    public ProgressBar hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public ProgressBar borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public ProgressBar borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public ProgressBar borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public ProgressBar color(final Color value) {

        this.color = value;
        return this;
    }

    public ProgressBar hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public ProgressBar activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public ProgressBar size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public ProgressBar width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public ProgressBar height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public ProgressBar onChange(SerializableConsumer<ProgressBar> c) {
        this.onChange = c;
        return this;
    }

    public ProgressBar onHover(SerializableConsumer<ProgressBar> c) {
        this.onHover = c;
        return this;
    }

    @Override
    public String toString() {
        return "ProgressBar{ id= " + id + " }";
    }

}
