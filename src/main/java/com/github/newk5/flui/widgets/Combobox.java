package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableBiConsumer;
import com.github.newk5.flui.util.SerializableConsumer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import vlsi.utils.CompactHashMap;

public class Combobox extends SizedWidget {

    private static long comboCounter = 0;
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
    private JImVec4 borderColorV;
    SerializableBiConsumer<Combobox, String> onChange;
    SerializableConsumer<Combobox> onHover;
    private JImStr jtext;
    private JImStr value = new JImStr("");
    private List<JImStr> items = new ArrayList<>();

    public Combobox(String id) {
        super(id, true);
        this.init();
    }

    public Combobox() {
        super();
        setup();
    }

    @Override
    protected void init() {
        comboCounter++;
        this.index(comboCounter);
        idIndex.put(id, comboCounter);
        instances.add(this);
        jtext = new JImStr("");
    }

    @Override
    protected void setup() {
        jtext = new JImStr("");
        value = new JImStr("");
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

    public boolean isDisabled() {
        return super.disabled;
    }

    public Combobox disabled(final boolean value) {
        super.disabled = value;
        if (value) {
            super.alpha(0.5f);
        } else {
            super.alpha(1);
        }
        return this;
    }

    public Combobox sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public Combobox items(String... items) {
        Arrays.stream(items).forEach(i -> this.items.add(new JImStr(i)));

        return this;
    }

    public Combobox clearItems() {
        items.clear();
        return this;
    }

    public Combobox font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public Combobox addItem(String i) {
        items.add(new JImStr(i));
        return this;
    }

    public static Combobox withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Combobox) w;
    }

    public Combobox align(Alignment a) {
        return (Combobox) super.setAlignment(a);
    }

    @Override
    public void render(JImGui imgui) {

        if (!super.isHidden()) {
            super.preRender(imgui);

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
            if (super.getWidth() > 0) {
                imgui.pushItemWidth(super.getWidth());
            }
            if (imgui.beginCombo(jtext, value, 0)) {

                for (JImStr i : items) {

                    if (imgui.selectable0(i)) {
                        value = i;
                        if (onChange != null) {
                            onChange.accept(this, new String(value.bytes, StandardCharsets.UTF_8));
                        }
                    }

                }
                if (hasSetBorderSize) {
                    imgui.popStyleVar();
                }
                if (hasSetBorderRounding) {
                    imgui.popStyleVar();
                }

                if (color != null) {
                    imgui.popStyleColor();
                }
                if (borderColor != null) {
                    imgui.popStyleColor();
                }
                if (imgui.isItemHovered()) {
                    if (onHover != null) {
                        onHover.accept(this);
                    }
                }
                imgui.endCombo();
                if (super.getWidth() > 0) {
                    imgui.popItemWidth();
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

    public Combobox hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public String getValue() {
        return new String(value.bytes);
    }

    public Combobox value(String v) {
        this.value = new JImStr(v);
        return this;
    }

    public Combobox showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public Combobox readOnly(boolean v) {
        this.readOnly = v;
        this.buildFlags();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
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

    public Combobox move(Direction d) {
        super.move(d);
        return this;
    }

    public Combobox width(final float value) {
        super.width(value);
        return this;
    }

    public Combobox alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Combobox height(final float value) {
        super.height(value);
        return this;
    }

    public Combobox hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public Combobox borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public Combobox borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public Combobox borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public Combobox color(final Color value) {

        this.color = value;
        return this;
    }

    public Combobox hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public Combobox activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public Combobox size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public Combobox width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public Combobox height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public Combobox onChange(SerializableBiConsumer<Combobox, String> c) {
        this.onChange = c;
        return this;
    }

    public Combobox onHover(SerializableConsumer<Combobox> c) {
        this.onHover = c;
        return this;
    }

    @Override
    public String toString() {
        return "Combobox{ id= " + id + " }";
    }

}
