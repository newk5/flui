package com.github.newk5.flui.widgets;

import org.ice1000.jimgui.JImStr;

public class CellWrapper {

    private String field;
    private JImStr value;
    private String column;
    private boolean selected;

    public CellWrapper(String field, JImStr value, String column) {
        this.field = field;
        this.value = value;
        this.column = column;
    }

    public String getField() {
        return field;
    }

    public CellWrapper field(final String value) {
        this.field = value;
        return this;
    }

    public CellWrapper value(final JImStr value) {
        this.value = value;
        return this;
    }

    public CellWrapper column(final String value) {
        this.column = value;
        return this;
    }

    public boolean getSelected() {
        return selected;
    }

    public CellWrapper selected(final boolean value) {
        this.selected = value;
        return this;
    }

    public JImStr getValue() {
        return value;
    }

    public String getColumn() {
        return column;
    }

}
