package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImVec4;
import vlsi.utils.CompactHashMap;

public class Label extends SizedWidget {

    private static long counter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private String text;
    private JImStr jtext;
    private SerializableConsumer<Label> onClick;
    private SerializableConsumer<Label> onHover;

    private JImVec4 color;
    private JImVec4 hoverColor;
    private JImVec4 activeColor;
    private Color c;
    private boolean wrap;

    public Label(String id) {
        super(id, true);
        this.init();
    }

    @Override
    protected void init() {
        counter++;
        this.index(counter);
        idIndex.put(id, counter);
        instances.add(this);

    }

    public Label font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
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

    public String getFont() {
        return super.font;
    }

    public Label sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public static Label withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Label) w;
    }

    public Label posY(float f) {
        super.posY(f);
        return this;
    }

    public Label move(Direction d) {
        super.move(d);
        return this;
    }

    public Label wrap(boolean v) {
        wrap = v;
        return this;
    }

    public boolean isWrap() {
        return wrap;
    }

    @Override
    public void render(JImGui imgui) {

        if (!super.isHidden()) {
            super.preRender(imgui);

            if (text != null) {

                if (c != null) {
                    this.color = c.asVec4(color);
                }
                if (wrap) {
                    imgui.textWrapped(jtext);
                } else if (color != null) {
                    imgui.textColored(color, jtext);
                } else {
                    imgui.text(jtext);
                }

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

    public Label alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public String getText() {
        return text;
    }

    public float getWidth() {
        return super.getWidth();
    }

    public float getHeight() {
        return super.getHeight();
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

    public Label text(final String value) {
        this.text = value;
        jtext = new JImStr(text);
        this.applyAlignment();
        return this;
    }

    public Label onHover(final SerializableConsumer<Label> value) {
        this.onHover = value;
        return this;
    }

    public Label hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public Label align(Alignment a) {
        return (Label) super.setAlignment(a);
    }

    public Label color(final Color value) {
        this.c = value;
        this.color = null;
        return this;
    }

    public Label hoverColor(final JImVec4 value) {
        this.hoverColor = value;
        return this;
    }

    public Label activeColor(final JImVec4 value) {
        this.activeColor = value;
        return this;
    }

}
