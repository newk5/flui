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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeTime;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import vlsi.utils.CompactHashMap;

public class DatePicker extends SizedWidget {

    private static long counter = 0;
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
    SerializableBiConsumer<DatePicker, Date> onChange;
    SerializableConsumer<DatePicker> onHover;
    private JImStr jtext;
    private JImStr value = new JImStr("");
    private NativeTime time;
    private JImStr dateFormat = new JImStr("dd/MM/yyyy");

    public DatePicker(String id) {
        super(id, true);
        this.init();
    }

    public DatePicker() {
        super();
        setup();
    }

    @Override
    protected DatePicker clone() {
        DatePicker c = new DatePicker();
        super.copyProps(c);
        if (color != null) {
            c.color = color.clone();
        }
        if (hoverColor != null) {
            c.hoverColor = hoverColor.clone();
        }
        if (activeColor != null) {
            c.activeColor = activeColor.clone();
        }
        c.flags = flags;
        c.hasSetBorderSize = hasSetBorderSize;
        c.hasSetBorderRounding = hasSetBorderRounding;
        c.borderRounding = borderRounding;
        c.borderSize = borderSize;
        if (borderColor != null) {
            c.borderColor = borderColor.clone();
        }
        c.onChange = onChange;
        c.onHover = onHover;
        if (jtext != null) {
            c.jtext = new JImStr(jtext.toString());
        }
        c.value = new JImStr(value.toString());
        c.time =null; //TODO: Need NativeTime#modifyValue to clone
        c.dateFormat=new JImStr(dateFormat.toString());

        return c;

    }

    @Override
    protected void init() {
        counter++;
        this.index(counter);
        idIndex.put(id, counter);
        instances.add(this);
        time = new NativeTime();
        jtext = new JImStr("");
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
        jtext = new JImStr("");
        value = new JImStr("");
        time = new NativeTime();
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

    public boolean isDisabled() {
        return super.disabled;
    }

    public DatePicker disabled(final boolean value) {
        super.disabled = value;
        if (value) {
            super.alpha(0.5f);
        } else {
            super.alpha(1);
        }
        return this;
    }

    public DatePicker sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public DatePicker dateFormat(final String value) {
        this.dateFormat = new JImStr(value);
        return this;
    }

    public DatePicker font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public static DatePicker withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (DatePicker) w;
    }

    public DatePicker align(Alignment a) {
        return (DatePicker) super.setAlignment(a);
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
            if (imgui.dateChooser(jtext, time)) {

                if (onChange != null) {
                    Date d = new Date(TimeUnit.SECONDS.toMillis(time.toAbsoluteSeconds()));
                    onChange.accept(this, d);
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

    }

    public DatePicker hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public String getValue() {
        return new String(value.bytes);
    }

    public DatePicker value(String v) {
        this.value = new JImStr(v);
        return this;
    }

    public DatePicker showBorders() {
        hasSetBorderSize = true;
        return this;
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

    public DatePicker move(Direction d) {
        super.move(d);
        return this;
    }

    public DatePicker width(final float value) {
        super.width(value);
        return this;
    }

    public DatePicker alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public DatePicker height(final float value) {
        super.height(value);
        return this;
    }

    public DatePicker hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public DatePicker borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public DatePicker borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public DatePicker borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public DatePicker color(final Color value) {

        this.color = value;
        return this;
    }

    public DatePicker hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public DatePicker activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public DatePicker size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public DatePicker width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public DatePicker height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public DatePicker onChange(SerializableBiConsumer<DatePicker, Date> c) {
        this.onChange = c;
        return this;
    }

    public DatePicker onHover(SerializableConsumer<DatePicker> c) {
        this.onHover = c;
        return this;
    }

    @Override
    public String toString() {
        return "Combobox{ id= " + id + " }";
    }

}
