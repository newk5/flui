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
import org.ice1000.jimgui.NativeInt;
import org.ice1000.jimgui.NativeString;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import org.ice1000.jimgui.flag.JImItemFlags;
import vlsi.utils.CompactHashMap;

public class InputText extends SizedWidget {

    private static long inputCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private Color color;
    private Color hoverColor;
    private Color activeColor;

    private int flags;
    private boolean readOnly;
    private boolean multiline;
    private boolean password;
    private boolean hasSetBorderSize;
    private boolean hasSetBorderRounding;
    private float borderRounding;
    private float borderSize;
    private Color borderColor;

    private SerializableConsumer<InputText> onChange;
    private SerializableConsumer<InputText> onHover;
    private JImStr label = new JImStr("");
    private NativeString value;

    public InputText(String id) {
        super(id, true);
        this.init();
    }

    public InputText() {
        super();
        value = new NativeString(50);
    }

    @Override
    public InputText clone() {
        InputText i = new InputText();
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
        i.multiline = multiline;
        i.password = password;
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
            i.value = new NativeString();
            for (char c : value.toString().toCharArray()) {
                i.value.append(c);
            }
        }
        i.label = new JImStr(label.toString());

        return i;
    }

    @Override
    protected void init() {
        inputCounter++;
        this.index(inputCounter);
        idIndex.put(id, inputCounter);
        instances.add(this);
        value = new NativeString(50);

    }

    public boolean isDisabled() {
        return super.disabled;
    }

    public InputText disabled(final boolean value) {
        super.disabled = value;
        if (value) {
            super.alpha(0.5f);
        } else {
            super.alpha(1);
        }
        return this;
    }

    @Override
    protected void setup() {
        value = new NativeString(50);
    }

    public InputText sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public InputText font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public static InputText withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (InputText) w;
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

    public InputText align(Alignment a) {
        return (InputText) super.setAlignment(a);
    }

    @Override
    public void render(JImGui imgui) {

        if (!super.isHidden()) {
            super.preRender(imgui);

            if (super.getWidth() > 0 && !multiline) {
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

            if (multiline) {
                if (width > 0 && height > 0) {
                    if (imgui.inputTextMultiline(label, value, width, height, readOnly? JImInputTextFlags.ReadOnly:0)) {
                        if (onChange != null) {
                            onChange.accept(this);
                        }
                    }
                }
            } else {

                if (imgui.inputText(label, value, flags)) {
                    if (onChange != null) {
                        onChange.accept(this);
                    }
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
            if (super.getWidth() > 0 && !multiline) {
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
            if (getWidth() == 0 || (getWidth() != imgui.getItemRectSizeX()) && !multiline) {
                width(imgui.getItemRectSizeX());
                reapplyAlign = true;

            }
            if (getHeight() == 0 || getHeight() != imgui.getItemRectSizeY() && !multiline) {
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

    public InputText hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public InputText showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public InputText isPassword(boolean password) {
        this.password = password;
        this.buildFlags();
        return this;
    }

    public InputText multiline(boolean v) {
        this.multiline = v;
        this.buildFlags();
        return this;
    }

    public InputText readOnly(boolean v) {
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

    public String getText() {
        return value.toString();
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

    public InputText move(Direction d) {
        super.move(d);
        return this;
    }

    public InputText text(final String value) {
        this.value.clear();

        for (char c : value.toCharArray()) {
            this.value.append(c);
        }

        return this;
    }

    public InputText width(final float value) {
        super.width(value);
        return this;
    }

    public InputText alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public InputText height(final float value) {
        super.height(value);
        return this;
    }

    public InputText hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public InputText borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public InputText borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public InputText borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public InputText color(final Color value) {

        this.color = value;
        return this;
    }

    public InputText hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public InputText activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public InputText size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public InputText width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public InputText height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public InputText onChange(SerializableConsumer<InputText> c) {
        this.onChange = c;
        return this;
    }

    public InputText onHover(SerializableConsumer<InputText> c) {
        this.onHover = c;
        return this;
    }

    @Override
    public String toString() {
        return "InputText{ id= " + id + " }";
    }

}
