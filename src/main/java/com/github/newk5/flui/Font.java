package com.github.newk5.flui;

import java.util.Objects;
import org.ice1000.jimgui.JImFont;
import org.ice1000.jimgui.JImFontAtlas;

public class Font {

    private String name;
    private String path;
    private int size;
    private JImFont jimFont;

    public Font() {
    }

    public Font(String name, String path, int size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public Font(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void loadFont(JImFontAtlas atlas) {
        if (size == 0) {
            jimFont = atlas.addFontFromFile(path);
        } else {
            jimFont = atlas.addFontFromFile(path, size);
        }
    }

    public Font name(final String value) {
        this.name = value;
        return this;
    }

    public Font path(final String value) {
        this.path = value;
        return this;
    }

    public Font size(final int value) {
        this.size = value;
        return this;
    }

    public Font jimFont(final JImFont value) {
        this.jimFont = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    public JImFont getJimFont() {
        return jimFont;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.path);
        hash = 41 * hash + this.size;
        hash = 41 * hash + Objects.hashCode(this.jimFont);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Font other = (Font) obj;
        if (this.size != other.size) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (!Objects.equals(this.jimFont, other.jimFont)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Font{" + "name=" + name + ", path=" + path + ", size=" + size + ", jimFont=" + jimFont + '}';
    }

}
