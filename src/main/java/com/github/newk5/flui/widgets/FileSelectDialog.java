package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Application;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.util.SerializableConsumer;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ice1000.jimgui.JImFileDialog;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeBool;
import org.ice1000.jimgui.NativeString;
import org.ice1000.jimgui.NativeStrings;
import org.ice1000.jimgui.flag.JImTabItemFlags;
import org.ice1000.jimgui.util.JniLoader;
import vlsi.utils.CompactHashMap;

public class FileSelectDialog extends SizedWidget {

    private static long counter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private JImStr title = new JImStr("");

    //background color
    private Color color;

    private int flags;
    private NativeBool modal;
    protected float rounding = 0;
    private JImStr filter;
    private JImStr jimStrID;
    private SerializableConsumer<List<File>> onFilesSelect;
    private SerializableConsumer<File> onFileSelect;

    JImFileDialog instance;
    private int selectCount = 1;

    public FileSelectDialog(String id) {

        super(id);

        this.init();
    }

    @Override
    protected void init() {
        counter++;
        this.index(counter);
        idIndex.put(id, counter);
        instances.add(this);

        jimStrID = new JImStr(id);
        modal = new NativeBool();
        modal.modifyValue(false);
        JImFileDialog.loadIcons(15);
        title = new JImStr(JImFileDialog.Icons.FOLDER_OPEN + " Choose any file");
        width = 400;
        height = 250;
        instance = new JImFileDialog();
    }

    public static FileSelectDialog withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (FileSelectDialog) w;
    }

    public float getWidth() {
        return super.getWidth();
    }

    public float getHeight() {
        return super.getHeight();
    }

    @Override
    protected void freeColors() {
        super.freeColor(color);

    }

    public void delete() {
        UI.runLater(() -> {

            if (deleteFlag) {
                freeColors();
                idIndex.remove(id);
                instances.remove(this);

                children.forEach(child -> {
                    ((SizedWidget) child).deleteFlag = true;
                });

                SizedWidget sw = super.getParent();
                if (sw != null) {
                    sw.deleteChild(this);
                }
                UI.components.remove(this);
            }
            deleteFlag = true;

        });

    }

    @Override
    public String toString() {
        return "FileSelectDialog{ id= " + id + " }";
    }

    public FileSelectDialog font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public void open() {
        modal.modifyValue(true);
    }

    public void close() {
        modal.modifyValue(false);
        instance.close();
    }

    @Override
    public void render(JImGui imgui) {
        if (!super.isHidden()) {
            super.preRender(imgui);

            if (color != null) {

                imgui.pushStyleColor(JImStyleColors.ChildBg, color.asVec4());
            }

            imgui.getStyle().setTabRounding(rounding);

            if (modal.accessValue()) {
                instance.openModal(jimStrID, title, filter, JImStr.EMPTY, selectCount);
            }
            if (instance.display(jimStrID, 0, width, height)) {
                if (instance.isOk()) {
                    if (selectCount == 1) {
                        try (NativeString currentPath = instance.filePathName()) {
                            if (onFileSelect != null) {
                                onFileSelect.accept(new File(currentPath.toString()));
                            }

                        }
                    } else {
                        try (NativeStrings currentPath = instance.selections()) {
                            List<File> files = new ArrayList<>();
                            String jarPath = new File(UI.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath().trim();
                            for (int i = 0; i < currentPath.size(); i++) {
                                String s = currentPath.get(i).toString().trim();
                                if (JniLoader.OsName.toLowerCase().startsWith("win")) {
                                    if (!"".equals(s)) {
                                        files.add(new File(s));
                                    }
                                } else {
                                    if (!s.equals(jarPath) && !s.equals("")) {
                                        files.add(new File(s));
                                    }
                                }

                            }

                            if (onFilesSelect != null) {
                                onFilesSelect.accept(files);
                            }

                        } catch (URISyntaxException ex) {
                            Logger.getLogger(FileSelectDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
                close();
                instance.close();

            }

            if (color != null) {
                imgui.popStyleColor();
            }

            postRender(imgui);
            if (deleteFlag) {
                this.delete();
            }
        }

    }

    protected void postRender(JImGui imgui) {
        if (super.reapplyAlign && super.getAlign() != null) {
            super.reapplyAlign = false;
            setAlignment(super.getAlign());
        }
        if (super.font != null) {
            imgui.popFont();
        }
    }

    public FileSelectDialog onFileSelect(final SerializableConsumer<File> e) {
        this.onFileSelect = e;
        return this;
    }

    public FileSelectDialog onFilesSelect(final SerializableConsumer<List<File>> e) {
        this.onFilesSelect = e;
        return this;
    }

    public FileSelectDialog selections(final int selections) {
        this.selectCount = selections;
        return this;
    }

    public FileSelectDialog rounding(final float rounding) {
        this.rounding = rounding;
        return this;
    }

    public FileSelectDialog filter(final String filter) {
        this.filter = new JImStr(filter);
        return this;
    }

    public FileSelectDialog alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public FileSelectDialog width(final float value) {
        super.width(value);
        return this;
    }

    public FileSelectDialog height(final float value) {
        super.height(value);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    public FileSelectDialog hidden(final boolean value) {
        super.hidden(value);

        return this;
    }

    public FileSelectDialog title(final String value) {
        this.title = new JImStr(value);
        return this;
    }

    public FileSelectDialog color(final Color value) {
        this.color = value;
        return this;
    }

    public Color getColor() {
        return color;
    }

}
