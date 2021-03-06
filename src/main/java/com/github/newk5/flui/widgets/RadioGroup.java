package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import com.github.newk5.flui.widgets.Widget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeInt;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import vlsi.utils.CompactHashMap;

public class RadioGroup extends SizedWidget {

    private static long radioGroupCounter = 0;
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
    SerializableConsumer<RadioGroup> onChange;
    SerializableConsumer<RadioGroup> onHover;
    NativeInt value;
    List<JImStr> labels = new ArrayList<>();
    private boolean[] values;

    public RadioGroup(String id) {
        super(id, true);
        this.init();

    }

    public RadioGroup() {
        super();
    }

    @Override
    public RadioGroup clone() {
        RadioGroup r = new RadioGroup();
        super.copyProps(r);

        if (color != null) {
            r.color = color.clone();
        }
        if (hoverColor != null) {
            r.hoverColor = hoverColor.clone();
        }
        if (activeColor != null) {
            r.activeColor = activeColor.clone();
        }
        r.flags = flags;
        r.readOnly = readOnly;
        r.hasSetBorderSize = hasSetBorderSize;
        r.hasSetBorderRounding = hasSetBorderRounding;
        r.borderRounding = borderSize;
        if (borderColor != null) {
            r.borderColor = borderColor.clone();
        }
        r.onChange = onChange;
        r.onHover = onHover;
        if (value != null) {
            r.value = new NativeInt();
            r.value.modifyValue(value.accessValue());
        }
        List<JImStr> labels = new ArrayList<>();
        this.labels.forEach(l -> {
            labels.add(new JImStr(l.toString()));
        });
        r.labels=labels;
        r.values= Arrays.copyOf(values, values.length);

        return r;
    }

    public String getSelectedLabel() {

        int i = getIndexOfSelectedValue();
        if (i == -1) {
            return "";
        }
        return new String(labels.get(i).bytes);
    }

    public RadioGroup sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public RadioGroup font(String font) {
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

    public List<String> getLabels() {
        List<String> list = new ArrayList<>();
        for (JImStr js : labels) {
            list.add(new String(js.bytes));
        }
        return list;
    }

    public RadioGroup selectedValue(String v) {
        int idx = this.labels.indexOf(new JImStr(v));
        if (idx > -1) {
            Arrays.fill(values, false);
            values[idx] = true;
        }
        return this;
    }

    private int getIndexOfSelectedValue() {
        for (int i = 0; i < values.length; i++) {
            if (values[i]) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void init() {
        radioGroupCounter++;
        this.index(radioGroupCounter);
        idIndex.put(id, radioGroupCounter);
        instances.add(this);
        value = new NativeInt();

    }

    public static RadioGroup withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (RadioGroup) w;
    }

    public RadioGroup align(Alignment a) {
        return (RadioGroup) super.setAlignment(a);
    }

    public RadioGroup move(Direction d) {
        super.move(d);
        return this;
    }

    public RadioGroup add(String l) {
        this.labels.add(new JImStr(l));
        int idx = getIndexOfSelectedValue();

        values = new boolean[labels.size()];
        Arrays.fill(values, false);
        if (idx > -1) {
            values[idx] = true;
        }

        return this;
    }

    public RadioGroup labels(String... labels) {
        this.labels.clear();
        values = new boolean[labels.length];
        for (int i = 0; i < labels.length; i++) {
            this.labels.add(new JImStr(labels[i]));
            values[i] = false;

        }

        return this;
    }

    public boolean isDisabled() {
        return super.disabled;
    }

    public RadioGroup disabled(final boolean value) {
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
            for (int i = 0; i < labels.size(); i++) {
                if (imgui.radioButton0(labels.get(i), values[i])) {
                    Arrays.fill(values, false);
                    values[i] = true;
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
            if (super.getWidth() > 0) {
                imgui.popItemWidth();
            }
            if (borderColor != null) {
                imgui.popStyleColor();
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

    public RadioGroup hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public RadioGroup showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public RadioGroup readOnly(boolean v) {
        this.readOnly = v;
        this.buildFlags();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public NativeInt getValue() {
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

    public RadioGroup width(final float value) {
        super.width(value);
        return this;
    }

    public RadioGroup alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public RadioGroup height(final float value) {
        super.height(value);
        return this;
    }

    public RadioGroup hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public RadioGroup borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public RadioGroup value(final int v) {
        this.value.modifyValue(v);
        return this;
    }

    public RadioGroup borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public RadioGroup borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public RadioGroup color(final Color value) {

        this.color = value;
        return this;
    }

    public RadioGroup hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public RadioGroup activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public RadioGroup size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public RadioGroup width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public RadioGroup height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public RadioGroup onChange(SerializableConsumer<RadioGroup> c) {
        this.onChange = c;
        return this;
    }

    public RadioGroup onHover(SerializableConsumer<RadioGroup> c) {
        this.onHover = c;
        return this;
    }

}
