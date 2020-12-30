package com.github.newk5.flui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;

public class Menu {

    private JImStr option;
    private boolean enabled = true;
    private Consumer<Menu> onClick;

    private List<MenuOption> options = new ArrayList<>();

    public Menu(String menu) {

        option = new JImStr(menu);

    }

    protected void render(JImGui imgui) {
        if (imgui.beginMenu(option)) {

            options.forEach(opt -> opt.render(imgui));

            imgui.endMenu();
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
