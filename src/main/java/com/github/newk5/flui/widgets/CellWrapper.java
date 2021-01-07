package com.github.newk5.flui.widgets;

import com.esotericsoftware.kryo.Kryo;
import java.util.ArrayList;
import java.util.List;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;

public class CellWrapper {

    private String field;
    private JImStr value;
    private String column;
    private boolean selected;
    private int columnIdx=-1;

    private Object o;
    private List<SizedWidget> widgets = new ArrayList<>();

    public CellWrapper(String field, JImStr value, String column) {
        this.field = field;
        this.value = value;
        this.column = column;
    }

    public CellWrapper(String field, JImStr value, String column, Object o) {
        this.field = field;
        this.value = value;
        this.column = column;
        this.o = o;

    }

    public int getWidgetsCount() {
        return widgets.size();
    }

    public void addWidget(String tableID, SizedWidget w, Kryo k) {
        SizedWidget sw = k.copy(w);
        sw.id = tableID + ":widget:" + column + ":" + System.currentTimeMillis();
        sw.child = true;
        sw.setData("rowData", o);
        sw.setup();
        widgets.add(sw);
    }

    public void renderWidgets(JImGui imgui) {
        widgets.forEach(w -> {
            w.render(imgui);
            if (!w.tableAddedEventFired && w.onTableAdd != null) {
                w.onTableAdd.accept(w);
                w.tableAddedEventFired = true;
            }
        });
    }

    public int getColumnIdx() {
        return columnIdx;
    }

    public CellWrapper columnIdx(int idx) {
        columnIdx = idx;
        return this;
    }

    public boolean hasWidgets() {
        return !this.widgets.isEmpty();
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
