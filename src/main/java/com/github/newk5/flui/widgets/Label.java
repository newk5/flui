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
import org.ice1000.jimgui.NativeInt;
import vlsi.utils.CompactHashMap;

public class Label extends SizedWidget {

    private static long counter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private String text;
    private JImStr jtext;
    private SerializableConsumer<Label> onClick;
    private SerializableConsumer<Label> onHover;

    private Color color;
    private Color hoverColor;

    private boolean wrap;

    public Label(String id) {
        super(id, true);
        this.init();
    }

    public Label() {
        super();
    }

    @Override
    protected Label clone() {
        Label l = new Label();
        super.copyProps(l);
        l.text = text;
        if (jtext != null) {
            l.jtext = new JImStr((jtext.toString()));
        }
        l.onClick = onClick;
        l.onHover = onHover;
        if (color != null) {
            l.color = color.clone();
        }
        if (hoverColor != null) {
            l.hoverColor = hoverColor.clone();
        }
        l.wrap = wrap;
        return l;

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

    @Override
    protected void freeColors() {
        super.freeColor(color);
        super.freeColor(hoverColor);

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
    
      public Label posX(float f) {
        super.posX(f);
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

                if (wrap) {
                    imgui.textWrapped(jtext);
                } else if (color != null) {
                    imgui.textColored(color.asVec4(), jtext);
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

    public Color getColor() {
        return color;
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
        this.color = value;
        return this;
    }

}
