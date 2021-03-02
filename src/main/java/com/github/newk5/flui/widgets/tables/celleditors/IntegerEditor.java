package com.github.newk5.flui.widgets.tables.celleditors;

import static com.github.newk5.flui.util.ReflectUtil.setValue;
import com.github.newk5.flui.widgets.CellWrapper;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.tables.TableModifier;
import java.lang.reflect.Field;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeInt;

public class IntegerEditor extends TableModifier implements CellEditor {

    public IntegerEditor(Table table) {
        super(table);
    }

    @Override
    public void onClick(JImGui imgui, CellWrapper cell, Field field) {
        super.focused=false;
        cell.cellEditorVisible(true);
        NativeInt s = new NativeInt();
        int v = Integer.parseInt(cell.getValue().toString());
        s.modifyValue(v);

        cell.nativeInt(s);

    }

    @Override
    public void drawEditor(JImGui imgui, CellWrapper cell, Field field) {

        if (!super.focused) {
            imgui.setKeyboardFocusHere(0);
            super.focused = true;
        }
        imgui.inputInt(JImStr.EMPTY, cell.getNativeInt(), 0);

        if (imgui.isItemDeactivatedAfterEdit()) {
            cell.cellEditorVisible(false);
            setValue(cell.getRowObject(), field, cell.getNativeInt().accessValue());
            super.getTable().updateRow(cell.getRowObject());
        }

    }

}
