package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import com.github.newk5.flui.widgets.Widget;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImTextureID;
import org.ice1000.jimgui.JImVec4;
import vlsi.utils.CompactHashMap;

public class Image extends SizedWidget {

    private static long imgCounter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private int flags;

    private boolean hasSetBorderSize;
    private boolean hasSetBorderRounding;
    private float borderRounding;
    private float borderSize;
    private Color borderColor;

    SerializableConsumer<Image> onHover;
    private File sourceFile;
    private byte[] sourceBytes;
    private Path sourcePath;
    private JImTextureID img;

    public Image(String id) {
        super(id, true);
        this.init();

    }

    public Image() {
        super();
    }

    @Override
    protected void init() {
        imgCounter++;
        this.index(imgCounter);
        idIndex.put(id, imgCounter);
        instances.add(this);
    }

    @Override
    protected void freeColors() {

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

    public Image sameLine(final boolean value) {
        this.sameLine = value;
        return this;
    }

    public static Image withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Image) w;
    }

    public Image move(Direction d) {
        super.move(d);
        return this;
    }

    public Image align(Alignment a) {
        return (Image) super.setAlignment(a);
    }

    @Override
    public void render(JImGui imgui) {

        if (!super.isHidden()) {
            super.preRender(imgui);

            if (hasSetBorderRounding) {
                imgui.pushStyleVar(JImStyleVars.FrameRounding, borderRounding);
            }
            if (hasSetBorderSize) {
                imgui.pushStyleVar(JImStyleVars.FrameBorderSize, borderSize);

            }
            if (borderColor != null) {
                imgui.pushStyleColor(JImStyleColors.Border, borderColor.asVec4());
            }
            if (img != null) {
                imgui.image(img, super.getWidth(), super.getHeight());

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

    public Image hideBorders() {
        hasSetBorderSize = false;
        return this;
    }

    public Image showBorders() {
        hasSetBorderSize = true;
        return this;
    }

    public Image width(final float value) {
        super.width(value);
        return this;
    }

    public Image alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public Image height(final float value) {
        super.height(value);
        return this;
    }

    public Image hidden(final boolean value) {
        super.hidden(value);
        return this;
    }

    public Image borderColor(final Color color) {
        this.borderColor = color;
        return this;
    }

    public Image source(final File f) {
        this.sourceFile = f;
        img = JImTextureID.fromFile(sourceFile);
        return this;
    }

    public Image source(final Path p) {
        this.sourcePath = p;
        img = JImTextureID.fromPath(sourcePath);
        return this;
    }

    public Image source(final byte[] b) {
        this.sourceBytes = b;
        img = JImTextureID.fromBytes(sourceBytes);
        return this;
    }

    public Image borderSize(final float value) {
        hasSetBorderSize = true;
        this.borderSize = value;
        return this;
    }

    public Image borderRounding(final float value) {
        hasSetBorderRounding = true;
        this.borderRounding = value;
        return this;
    }

    public float getBorderRounding() {
        return borderRounding;
    }

    public Image size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public Image width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public Image height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public Image onHover(SerializableConsumer<Image> c) {
        this.onHover = c;
        return this;
    }

}
