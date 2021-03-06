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
import vlsi.utils.CompactHashMap;

public class InputInt extends SizedWidget {

    private static long inputCounter = 0;
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
    SerializableConsumer<InputInt> onChange;
    SerializableConsumer<InputInt> onHover;
    NativeInt value;
    JImStr label = new JImStr("");

    public InputInt(String id) {
        super(id, true);
        this.init();

    }

    public InputInt() {
        super();
        setup();
    }

    @Override
    public InputInt clone() {
        InputInt i = new InputInt();
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

        return i;
    }

    @Override
    protected void freeColors() {
        super.freeColor(color);
        super.freeColor(hoverColor);
        super.freeColor(activeColor);
        super.freeColor(borderColor);
    }

    @Override
    protected void setup() {
        value = new NativeInt();
    }

    @Override
    protected void init() {
        inputCounter++;
        this.index(inputCounter);
        idIndex.put(id, inputCounter);
        instances.add(this);
        value = new NativeInt();

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

    public InputInt sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public static InputInt withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (InputInt) w;
    }

    public InputInt font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public InputInt move(Direction d) {
        super.move(d);
        return this;
    }

    public InputInt align(Alignment a) {
        return (InputInt) super.setAlignment(a);
    }

    public boolean isDisabled() {
        return super.disabled;
    }

    public InputInt disabled(final boolean value) {
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
            if (imgui.inputInt(label, value, flags)) {
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

    public InputInt hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public InputInt showBorders() {
        hasSetBorderSize = true;
        return this;
    }


    public InputInt readOnly(boolean v) {
        this.readOnly = v;
        this.buildFlags();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public int getValue() {
        return value.accessValue();
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

    public InputInt width(final float value) {
        super.width(value);
        return this;
    }

    public InputInt alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public InputInt height(final float value) {
        super.height(value);
        return this;
    }

    public InputInt hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public InputInt borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public InputInt value(final int v) {
        this.value.modifyValue(v);
        return this;
    }

    public InputInt borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public InputInt borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public InputInt color(final Color value) {

        this.color = value;
        return this;
    }

    public InputInt hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public InputInt activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public InputInt size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public InputInt width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public InputInt height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public InputInt onChange(SerializableConsumer<InputInt> c) {
        this.onChange = c;
        return this;
    }

    public InputInt onHover(SerializableConsumer<InputInt> c) {
        this.onHover = c;
        return this;
    }

}
