package com.github.newk5.flui.widgets;

import com.github.newk5.flui.util.SerializableConsumer;
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVar;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.flag.JImItemFlags;
import vlsi.utils.CompactHashMap;

public class Button extends SizedWidget {

    private static long btnCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private JImStr text = new JImStr("");

    private Color color;
    private Color hoverColor;
    private Color activeColor;

    SerializableConsumer<Button> onHover;
    SerializableConsumer<Button> actionClick;

    private float rounding;

    public Button() {
        super();
    }

    public Button(String id) {
        super(id, true);
        this.init();
    }

    @Override
    protected void init() {
        btnCounter++;
        this.index(btnCounter);
        idIndex.put(id, btnCounter);
        instances.add(this);
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

    public static Button withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Button) w;
    }

    public Button sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public Button font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public Button posX(float x) {
        super.posX(x);
        return this;
    }

    public Button move(Direction d) {
        super.move(d);
        return this;
    }

    public Button align(Alignment a) {
        super.setAlignment(a);
        return this;
    }

    public boolean isDisabled() {
        return super.disabled;
    }

    public Button disabled(final boolean value) {
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
            imgui.pushStyleVar(JImStyleVars.FrameRounding, rounding);
            if (color != null) {

                imgui.pushStyleColor(JImStyleColors.Button, color.asVec4());
            }

            if (activeColor != null) {

                imgui.pushStyleColor(JImStyleColors.ButtonActive, activeColor.asVec4());
            }
            if (hoverColor != null) {

                imgui.pushStyleColor(JImStyleColors.ButtonHovered, hoverColor.asVec4());
            }

            if (imgui.button(text)) {
                if (actionClick != null) {
                    actionClick.accept(this);
                }

            }

            if (color != null) {
                imgui.popStyleColor();
            }
            if (activeColor != null) {
                imgui.popStyleColor();
            }
            if (activeColor != null) {
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
            imgui.popStyleVar();
            super.postRender(imgui);
            if (deleteFlag) {
                this.delete();
            }
        }

    }

    public Button alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public String getText() {
        return new String(text.bytes);
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

    public Button width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public Button height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public Button rounding(final float rounding) {
        this.rounding = rounding;

        return this;
    }

    public Button text(final String value) {
        this.text = new JImStr(value);

        return this;
    }

    public Button onClick(final SerializableConsumer<Button> value) {
        this.actionClick = value;
        return this;
    }

    public Button onHover(final SerializableConsumer<Button> value) {
        this.onHover = value;
        return this;
    }

    public Button width(final float value) {
        super.width(value);
        return this;
    }

    public Button height(final float value) {
        super.height(value);
        return this;
    }

    public Button hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public Button color(final Color value) {

        this.color = value;
        return this;
    }

    public Button color(final Color value, boolean generateNeighbouringColors) {
        this.color = value;
        if (generateNeighbouringColors) {
            hoverColor(color.getNeighborColor1());
            activeColor(color.getNeighborColor2());
        }
        return this;
    }

    public Button hoverColor(final Color value) {

        this.hoverColor = value;
        return this;
    }

    public Button activeColor(final Color value) {

        this.activeColor = value;
        return this;
    }

    public Button size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

}
