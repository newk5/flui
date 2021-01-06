package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeFloat;
import org.ice1000.jimgui.NativeInt;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import org.ice1000.jimgui.flag.JImSliderFlags;
import vlsi.utils.CompactHashMap;

public class SliderInt extends SizedWidget {

    private static long sliderCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private JImVec4 color;
    private JImVec4 hoverColor;
    private JImVec4 activeColor;
    private Color c;
    private Color hC;
    private Color activeC;

    private int flags;
    private boolean readOnly;

    private boolean hasSetBorderSize;
    private boolean hasSetBorderRounding;
    private float borderRounding;
    private float borderSize;
    private Color borderColor;
    private JImVec4 borderColorV;
    SerializableConsumer<SliderInt> onChange;
    SerializableConsumer<SliderInt> onHover;
    NativeInt value;

    private JImStr label = new JImStr("");

    private int min;
    private int max;

    public SliderInt(String id) {
        super(id, true);
        this.init();

    }

    public SliderInt() {
        super();
    }

    @Override
    protected void init() {
        sliderCounter++;
        this.index(sliderCounter);
        idIndex.put(id, sliderCounter);
        instances.add(this);
        value = new NativeInt();
    }

    public SliderInt sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public SliderInt font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public SliderInt value(int f) {
        value.modifyValue(f);
        return this;
    }

    public float getValue() {
        return value.accessValue();
    }

    public void delete() {
        UI.runLater(() -> {

            idIndex.remove(id);
            instances.remove(this);

            SizedWidget sw = super.getParent();
            if (sw != null) {
                sw.deleteChild(this);
            }
        });

    }

    public static SliderInt withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (SliderInt) w;
    }

    public SliderInt move(Direction d) {
        super.move(d);
        return this;
    }

    public SliderInt align(Alignment a) {
        return (SliderInt) super.setAlignment(a);
    }

    @Override
    public void render(JImGui imgui) {

        if (!super.isHidden()) {
            super.preRender(imgui);

            if (super.getWidth() > 0) {
                imgui.pushItemWidth(super.getWidth());
            }

            if (c != null) {
                imgui.pushStyleColor(JImStyleColors.FrameBg, c.asVec4(color));
            }

            if (hasSetBorderRounding) {
                imgui.pushStyleVar(JImStyleVars.FrameRounding, borderRounding);
            }
            if (hasSetBorderSize) {
                imgui.pushStyleVar(JImStyleVars.FrameBorderSize, borderSize);

            }
            if (borderColor != null) {
                imgui.pushStyleColor(JImStyleColors.Border, borderColor.asVec4(borderColorV));
            }

            if (imgui.sliderInt(label, value, min, max)) {

                if (onChange != null) {
                    onChange.accept(this);
                }

            }

            if (hasSetBorderSize) {
                imgui.popStyleVar();
            }
            if (hasSetBorderRounding) {
                imgui.popStyleVar();
            }
            if (super.getWidth() > 0) {
                imgui.popItemWidth();
            }
            if (c != null) {
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

    private void buildFlags() {
        flags = 0;

        if (readOnly) {
            flags |= JImInputTextFlags.ReadOnly;
        }
    }

    public SliderInt hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public SliderInt showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public SliderInt readOnly(boolean v) {
        this.readOnly = v;
        this.buildFlags();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public SliderInt min(final int value) {
        this.min = value;
        return this;
    }

    public SliderInt max(final int value) {
        this.max = value;
        return this;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public JImVec4 getColor() {
        return color;
    }

    public JImVec4 getHoverColor() {
        return hoverColor;
    }

    public JImVec4 getActiveColor() {
        return activeColor;
    }

    public SliderInt width(final float value) {
        super.width(value);
        return this;
    }

    public SliderInt alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public SliderInt height(final float value) {
        super.height(value);
        return this;
    }

    public SliderInt hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public SliderInt borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public SliderInt borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public SliderInt borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public SliderInt color(final Color value) {
        this.c = value;
        this.color = null;
        return this;
    }

    public SliderInt hoverColor(final Color value) {
        this.hC = value;
        this.hoverColor = null;
        return this;
    }

    public SliderInt activeColor(final Color value) {
        this.activeC = value;
        this.activeColor = null;
        return this;
    }

    public SliderInt size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public SliderInt width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public SliderInt height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public SliderInt onChange(SerializableConsumer<SliderInt> c) {
        this.onChange = c;
        return this;
    }

    public SliderInt onHover(SerializableConsumer<SliderInt> c) {
        this.onHover = c;
        return this;
    }

}
