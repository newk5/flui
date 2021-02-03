package com.github.newk5.flui.widgets.tables.celleditors;

import static com.github.newk5.flui.util.ReflectUtil.setValue;
import com.github.newk5.flui.widgets.CellWrapper;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.tables.TableModifier;
import java.lang.reflect.Field;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeBool;

public class BooleanEditor extends TableModifier implements CellEditor {

    public BooleanEditor(Table table) {
        super(table);
    }

    @Override
    public void onClick(JImGui imgui, CellWrapper cell, Field field) {
        cell.cellEditorVisible(true);
        NativeBool s = new NativeBool();
        s.modifyValue(Boolean.valueOf(cell.getValue().toString()));

        cell.nativeBool(s);

    }

    @Override
    public void onSubmit(JImGui imgui, CellWrapper cell, Field field) {
        imgui.checkbox(JImStr.EMPTY, cell.getNativeBool());
        if (imgui.isItemDeactivatedAfterEdit()) {
            cell.cellEditorVisible(false);
            setValue(cell.getRowObject(), field, cell.getNativeBool().accessValue());
            super.getTable().updateRow(cell.getRowObject());
        }

    }

}
