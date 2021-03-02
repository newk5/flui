package com.github.newk5.flui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ice1000.jimgui.JImFontAtlas;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImVec4;
import vlsi.utils.CompactHashMap;

public class Application {

    private String title;
    private int width;
    private int height;
    public static CompactHashMap<String, Font> fonts = new CompactHashMap<>();
    private Runnable ready;

    public Application() {
    }

    public Application(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void loadFonts(JImFontAtlas atlas) {
        fonts.forEach((fontName, font) -> {
            font.loadFont(atlas);
        });
    }

    public Application title(final String value) {
        this.title = value;
        return this;
    }

    public Application width(final int value) {
        this.width = value;
        return this;
    }

    public Application height(final int value) {
        this.height = value;
        return this;
    }

    public Application ready(final Runnable value) {
        this.ready = value;
        return this;
    }

    public Runnable getReady() {
        return ready;
    }

    public void setupTheme(JImGui imgui) {
        buildTheme(imgui,
                new JImVec4(236f / 255f, 240.f / 255.f, 241.f / 255.f, 1),
                new JImVec4(41.f / 255.f, 128.f / 255.f, 185.f / 255.f, 1),
                new JImVec4(57.f / 255.f, 79.f / 255.f, 105.f / 255.f, 1),
                new JImVec4(44.f / 255.f, 62.f / 255.f, 80.f / 255.f, 1),
                new JImVec4(33.f / 255.f, 46.f / 255.f, 60.f / 255.f, 1));
    }

    //from https://github.com/ocornut/imgui/issues/707#issuecomment-254424199 with some changes
    private void buildTheme(JImGui imgui, JImVec4 textColor, JImVec4 headColor, JImVec4 areaColor, JImVec4 bodyColor, JImVec4 popColor) {

        JImVec4 textDisabledCol = new JImVec4(textColor.getX(), textColor.getY(), textColor.getZ(), 0.58f);

        JImVec4 childBgCol = new JImVec4(bodyColor.getX(), bodyColor.getY(), bodyColor.getZ(), 0.58f);
        JImVec4 windowBgCol = new JImVec4(bodyColor.getX(), bodyColor.getY(), bodyColor.getZ(), 95f);

        imgui.pushStyleColor(JImStyleColors.Text, textColor);
        imgui.pushStyleColor(JImStyleColors.TextDisabled, textDisabledCol);

        imgui.pushStyleColor(JImStyleColors.WindowBg, windowBgCol);
        imgui.pushStyleColor(JImStyleColors.ChildBg, childBgCol);

        imgui.pushStyleColor(JImStyleColors.Border, new JImVec4(popColor.getX(), popColor.getY(), popColor.getZ(), 1f));
        imgui.pushStyleColor(JImStyleColors.BorderShadow, new JImVec4(popColor.getX(), popColor.getY(), popColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.FrameBg, new JImVec4(areaColor.getX(), areaColor.getY(), areaColor.getZ(), 1f));
        imgui.pushStyleColor(JImStyleColors.FrameBgHovered, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.78f));
        imgui.pushStyleColor(JImStyleColors.FrameBgActive, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.TitleBg, new JImVec4(areaColor.getX(), areaColor.getY(), areaColor.getZ(), 1f));
        imgui.pushStyleColor(JImStyleColors.TitleBgCollapsed, new JImVec4(areaColor.getX(), areaColor.getY(), areaColor.getZ(), 0.75f));
        imgui.pushStyleColor(JImStyleColors.TitleBgActive, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.MenuBarBg, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.47f));

        imgui.pushStyleColor(JImStyleColors.ScrollbarBg, new JImVec4(areaColor.getX(), areaColor.getY(), areaColor.getZ(), 1f));
        imgui.pushStyleColor(JImStyleColors.ScrollbarGrab, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.21f));
        imgui.pushStyleColor(JImStyleColors.ScrollbarGrabHovered, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.78f));
        imgui.pushStyleColor(JImStyleColors.ScrollbarGrabActive, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.PopupBg, new JImVec4(areaColor.getX(), areaColor.getY(), areaColor.getZ(), 1f));
        imgui.pushStyleColor(JImStyleColors.CheckMark, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.80f));
        imgui.pushStyleColor(JImStyleColors.SliderGrab, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.90f));
        imgui.pushStyleColor(JImStyleColors.SliderGrabActive, new JImVec4(popColor.getX(), popColor.getY(), popColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.Button, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.50f));
        imgui.pushStyleColor(JImStyleColors.ButtonHovered, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.86f));
        imgui.pushStyleColor(JImStyleColors.ButtonActive, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.Header, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.76f));
        imgui.pushStyleColor(JImStyleColors.HeaderHovered, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.86f));

        imgui.pushStyleColor(JImStyleColors.ResizeGrip, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.15f));
        imgui.pushStyleColor(JImStyleColors.ResizeGripHovered, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.78f));
        imgui.pushStyleColor(JImStyleColors.ResizeGripActive, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.PlotLines, new JImVec4(textColor.getX(), textColor.getY(), textColor.getZ(), 0.63f));
        imgui.pushStyleColor(JImStyleColors.PlotLinesHovered, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.PlotHistogram, new JImVec4(textColor.getX(), textColor.getY(), textColor.getZ(), 0.63f));
        imgui.pushStyleColor(JImStyleColors.PlotHistogramHovered, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 1f));

        imgui.pushStyleColor(JImStyleColors.TextSelectedBg, new JImVec4(headColor.getX(), headColor.getY(), headColor.getZ(), 0.43f));
        imgui.pushStyleColor(JImStyleColors.PopupBg, new JImVec4(popColor.getX(), popColor.getY(), popColor.getZ(), 0.92f));

        imgui.pushStyleColor(JImStyleColors.ModalWindowDimBg, new JImVec4(areaColor.getX(), areaColor.getY(), areaColor.getZ(), 0.73f));

        imgui.pushStyleColor(JImStyleColors.TableHeaderBg, new JImVec4(43 / 255.0f, 90 / 255f, 125 / 255f, 1));
        
    }

    public Application addFont(String fontName, String pathToFile, int size) {
        fonts.put(fontName, new Font(fontName, pathToFile, size));
        return this;
    }

    public Application addFont(String fontName, String pathToFile) {
        fonts.put(fontName, new Font(fontName, pathToFile));
        return this;
    }

    public Application addFont(String fontName, String extension, InputStream is) {
        try {
            String dir = File.createTempFile(fontName, extension).getParentFile().getPath();
            Files.copy(is, Paths.get(dir + File.separator + fontName + "." + extension), StandardCopyOption.REPLACE_EXISTING);
            fonts.put(fontName, new Font(fontName, dir + File.separator + fontName + "." + extension));

        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    public Application addFont(String fontName, String extension, InputStream is, int size) {
        try {
            String dir = File.createTempFile(fontName, extension).getParentFile().getPath();
            Files.copy(is, Paths.get(dir + File.separator + fontName + "." + extension), StandardCopyOption.REPLACE_EXISTING);
            fonts.put(fontName, new Font(fontName, dir + File.separator + fontName + "." + extension, size));

        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    public Application addTTFFont(String fontName, InputStream is) {
        try {
            String dir = File.createTempFile(fontName, "ttf").getParentFile().getPath();
            Files.copy(is, Paths.get(dir + File.separator + fontName + ".ttf"), StandardCopyOption.REPLACE_EXISTING);
            fonts.put(fontName, new Font(fontName, dir + File.separator + fontName + ".ttf"));

        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    public Application addTTFFont(String fontName, InputStream is, int size) {
        try {
            String dir = File.createTempFile(fontName, "ttf").getParentFile().getPath();
            Files.copy(is, Paths.get(dir + File.separator + fontName + ".ttf"), StandardCopyOption.REPLACE_EXISTING);
            fonts.put(fontName, new Font(fontName, dir + File.separator + fontName + ".ttf", size));

        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
