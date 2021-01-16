package com.github.newk5.flui.widgets;

import com.esotericsoftware.kryo.Kryo;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeBool;
import org.ice1000.jimgui.NativeDouble;
import org.ice1000.jimgui.NativeFloat;
import org.ice1000.jimgui.NativeInt;
import org.ice1000.jimgui.NativeString;

public class CellWrapper {

    private String field;
    private JImStr value;
    private String column;
    private boolean selected;
    private int columnIdx = -1;
    private boolean cellEditorVisible;

    private Object cellValue;
    private Object rowObject;
    private List<SizedWidget> widgets = new ArrayList<>();
    private NativeString nativeString;
    private NativeInt nativeInt;
    private NativeFloat nativeFloat;
    private NativeDouble nativeDouble;
    private NativeBool nativeBool;

    public CellWrapper(String field, JImStr value, String column) {
        this.field = field;
        this.value = value;
        this.column = column;
    }

    public CellWrapper(String field, JImStr value, String column, Object o) {
        this.field = field;
        this.value = value;
        this.column = column;
        this.rowObject = o;

    }

    public int getWidgetsCount() {
        return widgets.size();
    }

    public void addWidget(String tableID, SizedWidget w, Kryo k) {
        SizedWidget sw = k.copy(w);
        sw.id = tableID + ":widget:" + column + ":" + System.currentTimeMillis();
        sw.child = true;
        sw.setData("rowData", rowObject);
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

    public NativeInt getNativeInt() {
        return nativeInt;
    }

    public NativeFloat getNativeFloat() {
        return nativeFloat;
    }

    public NativeDouble getNativeDouble() {
        return nativeDouble;
    }

    public NativeBool getNativeBool() {
        return nativeBool;
    }

    public Object getRowObject() {
        return rowObject;
    }
    
    
    
    

    public Object getCellValue() {
        return cellValue;
    }

    public boolean isCellEditorVisible() {
        return cellEditorVisible;
    }

    public NativeString getNativeString() {
        return nativeString;
    }

    
    
    
    

    public int getColumnIdx() {
        return columnIdx;
    }

    public boolean hasWidgets() {
        return !this.widgets.isEmpty();
    }

    public String getField() {
        return field;
    }

    public boolean getSelected() {
        return selected;
    }

    public JImStr getValue() {
        return value;
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

    public CellWrapper selected(final boolean value) {
        this.selected = value;
        return this;
    }

    public CellWrapper columnIdx(final int value) {
        this.columnIdx = value;
        return this;
    }

    public CellWrapper cellEditorVisible(final boolean value) {
        this.cellEditorVisible = value;
        return this;
    }

    public CellWrapper cellValue(final Object value) {
        this.cellValue = value;
        return this;
    }

    public CellWrapper rowObject(final Object value) {
        this.rowObject = value;
        return this;
    }

    public CellWrapper widgets(final List<SizedWidget> value) {
        this.widgets = value;
        return this;
    }

    public CellWrapper nativeString(final NativeString value) {
        this.nativeString = value;
        return this;
    }

    public CellWrapper nativeInt(final NativeInt value) {
        this.nativeInt = value;
        return this;
    }

    public CellWrapper nativeFloat(final NativeFloat value) {
        this.nativeFloat = value;
        return this;
    }

    public CellWrapper nativeDouble(final NativeDouble value) {
        this.nativeDouble = value;
        return this;
    }

    public CellWrapper nativeBool(final NativeBool value) {
        this.nativeBool = value;
        return this;
    }

    public String getColumn() {
        return column;
    }

}
