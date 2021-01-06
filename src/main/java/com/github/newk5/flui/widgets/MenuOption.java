package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Application;
import com.github.newk5.flui.Font;
import com.github.newk5.flui.util.SerializableConsumer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeBool;
import vlsi.utils.CompactHashMap;

public class MenuOption {

    private JImStr option;

    private SerializableConsumer<MenuOption> onClick;
    private List<MenuOption> options = new ArrayList<>();

    private NativeBool selected = new NativeBool();
    private boolean enabled = true;
    private JImStr shortcut = new JImStr("");
    private Font fontObj;
    private String font;

    public MenuOption(String option) {
        this.option = new JImStr(option);
        selected.modifyValue(false);
    }

    protected void render(JImGui imgui) {
        if (fontObj != null) {
            imgui.pushFont(fontObj.getJimFont());
        }
        if (options.isEmpty()) {
            if (imgui.menuItem(option, shortcut, selected, enabled)) {
                selected.modifyValue(false);
                if (onClick != null) {
                    onClick.accept(this);
                }

            }
        } else {

            options.forEach(o -> {
                if (imgui.beginMenu(option, enabled)) {
                    o.render(imgui);

                    imgui.endMenu();
                }
            });
        }
        if (fontObj != null) {
            imgui.popFont();
        }
    }

    public MenuOption font(String font) {
        this.font = font;
        fontObj = Application.fonts.get(font);
        return this;
    }

    public String getOption() {
        if (option == null) {
            return "";
        }
        return new String(option.bytes, StandardCharsets.UTF_8);
    }

    public boolean isSelected() {
        return selected.accessValue();
    }

    public List<MenuOption> getOptions() {
        return options;
    }

    public MenuOption option(final JImStr value) {
        this.option = value;
        return this;
    }

    public MenuOption enabled(final boolean value) {
        this.selected.modifyValue(value);
        return this;
    }

    public MenuOption onClick(final SerializableConsumer<MenuOption> value) {
        this.onClick = value;
        return this;
    }

    public MenuOption options(final MenuOption... value) {
        Arrays.stream(value).forEach(o -> this.options.add(o));
        return this;
    }

}
