package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Application;
import com.github.newk5.flui.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.ice1000.jimgui.JImGui;
import vlsi.utils.CompactHashMap;

public class Topbar extends Widget {

    private List<Menu> menus = new ArrayList<>();
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static long inputCounter = 0;
    protected static float height;

    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();
    private Font font;

    public Topbar(String id) {
        super(id);
        this.index(0);
        idIndex.put(id, 0l);
        instances.add(this);

    }

    public Topbar menus(final Menu... menus) {
        Arrays.stream(menus).forEach(m -> this.addMenu(m));
        return this;
    }

    public Topbar font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    protected void render(JImGui imgui) {
        if (font != null) {
            imgui.pushFont(font.getJimFont());
        }
        if (imgui.beginMainMenuBar()) {
            if (height == 0) {
                height = imgui.getFrameHeight();
            }
            menus.forEach(m -> m.render(imgui));
            imgui.endMainMenuBar();
        }
        if (font != null){
            imgui.popFont();
        }
    }

    public void addMenu(Menu m) {
        this.menus.add(m);

    }

}
