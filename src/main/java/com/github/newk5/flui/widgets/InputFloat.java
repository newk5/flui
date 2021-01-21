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
import org.ice1000.jimgui.NativeDouble;
import org.ice1000.jimgui.NativeFloat;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import vlsi.utils.CompactHashMap;

public class InputFloat extends SizedWidget {

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

    SerializableConsumer<InputFloat> onChange;
    SerializableConsumer<InputFloat> onHover;
    NativeFloat value;
    JImStr label = new JImStr("");

    public InputFloat(String id) {
        super(id, true);
        this.init();

    }

    public InputFloat() {
        super();
        setup();
    }

    @Override
    public InputFloat clone() {
        InputFloat i = new InputFloat();
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
            i.value = new NativeFloat();
            i.value.modifyValue(value.accessValue());
        }
        i.label = new JImStr(label.toString());

        return i;
    }

    @Override
    protected void setup() {
        value = new NativeFloat();
    }

    @Override
    protected void init() {
        inputCounter++;
        this.index(inputCounter);
        idIndex.put(id, inputCounter);
        instances.add(this);
        value = new NativeFloat();

    }

    public InputFloat sameLine(final boolean value) {
        this.sameLine = value;
        return this;
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

    public static InputFloat withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (InputFloat) w;
    }

    public InputFloat move(Direction d) {
        super.move(d);
        return this;
    }

    public InputFloat font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public InputFloat align(Alignment a) {
        return (InputFloat) super.setAlignment(a);
    }

    public boolean isDisabled() {
        return super.disabled;
    }

    public InputFloat disabled(final boolean value) {
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

            if (imgui.inputFloat(label, value, flags)) {

                if (onChange != null) {
                    onChange.accept(this);
                }

            }

            if (borderColor != null) {
                imgui.popStyleColor();
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

    public InputFloat hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public InputFloat showBorders() {
        hasSetBorderSize = true;
        return this;
    }

 

    public InputFloat readOnly(boolean v) {
        this.readOnly = v;
        this.buildFlags();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }


    public Float getValue() {
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

    public InputFloat width(final float value) {
        super.width(value);
        return this;
    }

    public InputFloat alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public InputFloat height(final float value) {
        super.height(value);
        return this;
    }

    public InputFloat hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public InputFloat borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public InputFloat value(final float v) {
        this.value.modifyValue(v);
        return this;
    }

    public InputFloat borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public InputFloat borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public InputFloat color(final Color value) {

        this.color = value;
        return this;
    }

    public InputFloat hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public InputFloat activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public InputFloat size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public InputFloat width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public InputFloat height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public InputFloat onChange(SerializableConsumer<InputFloat> c) {
        this.onChange = c;
        return this;
    }

    public InputFloat onHover(SerializableConsumer<InputFloat> c) {
        this.onHover = c;
        return this;
    }

}
