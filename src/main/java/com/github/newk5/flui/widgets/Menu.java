package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Application;
import com.github.newk5.flui.Font;
import com.github.newk5.flui.util.SerializableConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;

public class Menu {

    private JImStr option;
    private boolean enabled = true;
    private SerializableConsumer<Menu> onClick;

    private List<MenuOption> options = new ArrayList<>();
    private Font fontObj;
    private String font;

    public Menu(String menu) {

        option = new JImStr(menu);

    }

    public Menu font(String font) {
        this.font = font;
        fontObj = Application.fonts.get(font);
        return this;
    }

    protected void render(JImGui imgui) {
        if (fontObj != null) {
            imgui.pushFont(fontObj.getJimFont());
        }
        if (imgui.beginMenu(option, enabled)) {

            options.forEach(opt -> opt.render(imgui));

            imgui.endMenu();
        }
        if (fontObj != null) {
            imgui.popFont();
        }
    }

    public Menu option(final String value) {
        this.option = new JImStr(value);
        return this;
    }

    public Menu enabled(final boolean value) {
        this.enabled = value;
        return this;
    }

    public Menu options(final MenuOption... value) {
        Arrays.stream(value).forEach(o -> this.options.add(o));

        return this;
    }

}
