package com.github.newk5.flui.widgets.tables;

import com.github.newk5.flui.widgets.Table;

public abstract class TableModifier {

    private Table table;

    public TableModifier(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

}
