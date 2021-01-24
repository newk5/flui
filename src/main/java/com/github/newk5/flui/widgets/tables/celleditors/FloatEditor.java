package com.github.newk5.flui.widgets.tables.celleditors;

import static com.github.newk5.flui.util.ReflectUtil.setValue;
import com.github.newk5.flui.widgets.CellWrapper;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.tables.TableModifier;
import java.lang.reflect.Field;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeFloat;

public class FloatEditor extends TableModifier implements CellEditor {

    public FloatEditor(Table table) {
        super(table);
    }

    @Override
    public void onClick(JImGui imgui, CellWrapper cell, Field field) {
        if (cell.getValueType().equals(Float.class)) {

            NativeFloat s = new NativeFloat();
            s.modifyValue(Float.valueOf(cell.getValue().toString()));

            cell.nativeFloat(s);

        }

    }

    @Override
    public void onSubmit(JImGui imgui, CellWrapper cell, Field field) {
        if (cell.getValueType().equals(Float.class)) {
            imgui.inputFloat(JImStr.EMPTY, cell.getNativeFloat());
            if (imgui.isItemDeactivatedAfterEdit()) {
                cell.cellEditorVisible(false);
                setValue(cell.getRowObject(), field, cell.getNativeFloat().accessValue());
                super.getTable().updateRow(cell.getRowObject());
            }

        }
    }

}
