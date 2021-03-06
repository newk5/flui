package com.github.newk5.flui.widgets.tables.celleditors;

import static com.github.newk5.flui.util.ReflectUtil.setValue;
import com.github.newk5.flui.widgets.CellWrapper;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.tables.TableModifier;
import java.lang.reflect.Field;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeDouble;

public class DoubleEditor extends TableModifier implements CellEditor {

    public DoubleEditor(Table table) {
        super(table);
    }

    @Override
    public void onClick(JImGui imgui, CellWrapper cell, Field field) {
        super.focused = false;
        cell.cellEditorVisible(true);
        NativeDouble s = new NativeDouble();
        s.modifyValue(Double.valueOf(cell.getValue().toString()));

        cell.nativeDouble(s);

    }

    @Override
    public void drawEditor(JImGui imgui, CellWrapper cell, Field field) {

        if (!super.focused) {
            imgui.setKeyboardFocusHere(0);
            super.focused = true;
        }
        imgui.inputDouble(JImStr.EMPTY, cell.getNativeDouble());
        if (imgui.isItemDeactivatedAfterEdit()) {
            cell.cellEditorVisible(false);
            setValue(cell.getRowObject(), field, cell.getNativeDouble().accessValue());
            super.getTable().updateRow(cell.getRowObject());
        }

    }

}
