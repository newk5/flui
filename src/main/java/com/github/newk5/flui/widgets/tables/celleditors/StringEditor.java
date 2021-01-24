package com.github.newk5.flui.widgets.tables.celleditors;

import static com.github.newk5.flui.util.ReflectUtil.setValue;
import com.github.newk5.flui.widgets.CellWrapper;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.tables.TableModifier;
import java.lang.reflect.Field;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeString;
import org.ice1000.jimgui.flag.JImInputTextFlags;

public class StringEditor extends TableModifier implements CellEditor {

    public StringEditor(Table table) {
        super(table);
    }

    @Override
    public void onClick(JImGui imgui, CellWrapper cell, Field field) {
        if (cell.getValueType().equals(String.class)) {

            NativeString s = new NativeString();
            for (char ch : cell.getValue().toString().toCharArray()) {
                s.append(ch);
            }

            cell.nativeString(s);

        }

    }

    @Override
    public void onSubmit(JImGui imgui, CellWrapper cell, Field field) {
        if (cell.getValueType().equals(String.class)) {

            if (imgui.inputText(JImStr.EMPTY, cell.getNativeString(), JImInputTextFlags.EnterReturnsTrue)) {
                setValue(cell.getRowObject(), field, cell.getNativeString().toString());
                super.getTable().updateRow(cell.getRowObject());
                cell.cellEditorVisible(false);
            }

        }
    }

}
