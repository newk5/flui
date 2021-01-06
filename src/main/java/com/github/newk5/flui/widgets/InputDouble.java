package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeDouble;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import vlsi.utils.CompactHashMap;

public class InputDouble extends SizedWidget {

    private static long inputCounter = 0;
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
    private boolean multiline;
    private boolean password;
    private boolean hasSetBorderSize;
    private boolean hasSetBorderRounding;
    private float borderRounding;
    private float borderSize;
    private Color borderColor;
    private JImVec4 borderColorV;
    SerializableConsumer<InputDouble> onChange;
    SerializableConsumer<InputDouble> onHover;
    NativeDouble value;
    private JImStr label = new JImStr("");

    public InputDouble(String id) {
        super(id, true);
        this.init();

    }

    public InputDouble() {
        super();

    }

    @Override
    protected void init() {
        inputCounter++;
        this.index(inputCounter);
        idIndex.put(id, inputCounter);
        instances.add(this);
        value = new NativeDouble();

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

    public InputDouble sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public static InputDouble withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (InputDouble) w;
    }

    public InputDouble font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public InputDouble move(Direction d) {
        super.move(d);
        return this;
    }

    public InputDouble align(Alignment a) {
        return (InputDouble) super.setAlignment(a);
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

            if (imgui.inputDouble(label, value, flags)) {
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
        if (password) {
            flags |= JImInputTextFlags.Password;
        }
        if (multiline) {
            flags |= JImInputTextFlags.Multiline;
        }
        if (readOnly) {
            flags |= JImInputTextFlags.ReadOnly;
        }
    }

    public InputDouble hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public InputDouble showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public InputDouble isPassword(boolean password) {
        this.password = password;
        this.buildFlags();
        return this;
    }

    public InputDouble multiline(boolean v) {
        this.multiline = v;
        this.buildFlags();
        return this;
    }

    public InputDouble readOnly(boolean v) {
        this.readOnly = v;
        this.buildFlags();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public boolean isPassword() {
        return password;
    }

    public Double getValue() {
        return value.accessValue();
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

    public InputDouble width(final float value) {
        super.width(value);
        return this;
    }

    public InputDouble alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public InputDouble height(final float value) {
        super.height(value);
        return this;
    }

    public InputDouble hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public InputDouble borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public InputDouble value(final double v) {
        this.value.modifyValue(v);
        return this;
    }

    public InputDouble borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public InputDouble borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public InputDouble color(final Color value) {
        this.c = value;
        this.color = null;
        return this;
    }

    public InputDouble hoverColor(final Color value) {
        this.hC = value;
        this.hoverColor = null;
        return this;
    }

    public InputDouble activeColor(final Color value) {
        this.activeC = value;
        this.activeColor = null;
        return this;
    }

    public InputDouble size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public InputDouble width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public InputDouble height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public InputDouble onChange(SerializableConsumer<InputDouble> c) {
        this.onChange = c;
        return this;
    }

    public InputDouble onHover(SerializableConsumer<InputDouble> c) {
        this.onHover = c;
        return this;
    }

}
