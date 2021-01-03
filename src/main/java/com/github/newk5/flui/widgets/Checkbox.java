package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import com.github.newk5.flui.widgets.Widget;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeBool;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import vlsi.utils.CompactHashMap;

public class Checkbox extends SizedWidget {

    private static long chkCounter = 0;
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
    SerializableConsumer<Checkbox> onChange;
    SerializableConsumer<Checkbox> onHover;
    NativeBool value;
    private JImStr label = new JImStr("");

    public Checkbox(String id) {
        super(id,true);
        this.init();

    }

    @Override
    protected void init() {
        chkCounter++;
        this.index(chkCounter);
        idIndex.put(id, chkCounter);
        instances.add(this);
        value = new NativeBool();
        value.modifyValue(false);
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


    public static Checkbox withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Checkbox) w;
    }

    public Checkbox font(String font) {
        super.font = font;
        return this;
    }

    public String getFont() {
        return super.font;
    }
    
     public Checkbox sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public Checkbox align(Alignment a) {
        return (Checkbox) super.setAlignment(a);
    }

    public Checkbox move(Direction d) {
        super.move(d);
        return this;
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
            if (imgui.checkbox(label, value)) {
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

    public Checkbox hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public Checkbox showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public Checkbox readOnly(boolean v) {
        this.readOnly = v;
        this.buildFlags();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean getValue() {
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

    public Checkbox width(final float value) {
        super.width(value);
        return this;
    }

    public Checkbox alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Checkbox height(final float value) {
        super.height(value);
        return this;
    }

    public Checkbox hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public Checkbox borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public Checkbox value(final boolean v) {
        this.value.modifyValue(v);
        return this;
    }

    public Checkbox borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public Checkbox borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public Checkbox color(final Color value) {
        this.c = value;
        this.color = null;
        return this;
    }

    public Checkbox hoverColor(final Color value) {
        this.hC = value;
        this.hoverColor = null;
        return this;
    }

    public Checkbox activeColor(final Color value) {
        this.activeC = value;
        this.activeColor = null;
        return this;
    }

    public Checkbox size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public Checkbox width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public Checkbox height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public Checkbox onChange(SerializableConsumer<Checkbox> c) {
        this.onChange = c;
        return this;
    }

    public Checkbox onHover(SerializableConsumer<Checkbox> c) {
        this.onHover = c;
        return this;
    }

}
