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

    private Color color;
    private Color hoverColor;
    private Color activeColor;

    private int flags;
    private boolean readOnly;

    private boolean hasSetBorderSize;
    private boolean hasSetBorderRounding;
    private float borderRounding;
    private float borderSize;
    private Color borderColor;

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
        setup();
    }

    @Override
    protected void setup() {
        value = new NativeInt();
    }
    
     @Override
    protected SliderInt clone() {
        SliderInt i = new SliderInt();
        super.copyProps(i);

        if (color != null) {
            i.color = color.clone();
        }
        if (hoverColor != null) {
            i.hoverColor = hoverColor.clone();
        }
        if (activeColor != null) {
            i.activeColor = activeColor.clone();
        }
        i.flags = flags;
        i.readOnly = readOnly;
        i.hasSetBorderSize = hasSetBorderSize;
        i.hasSetBorderRounding = hasSetBorderRounding;
        i.borderRounding = borderRounding;
        i.borderSize = borderSize;

        if (borderColor != null) {
            i.borderColor = borderColor.clone();
        }
        i.onChange = onChange;
        i.onHover = onHover;
        if (value != null) {
            i.value = new NativeInt();
            i.value.modifyValue(value.accessValue());
        }
        i.label = new JImStr(label.toString());
        i.min = min;
        i.max = max;

        return i;

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

    public boolean isDisabled() {
        return super.disabled;
    }

    public SliderInt disabled(final boolean value) {
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

    public Color getColor() {
        return color;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public Color getActiveColor() {
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

        this.color = value;
        return this;
    }

    public SliderInt hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public SliderInt activeColor(final Color value) {

        this.activeColor = value;
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
