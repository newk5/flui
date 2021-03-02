package com.github.newk5.flui.widgets.tables.celleditors;

import static com.github.newk5.flui.util.ReflectUtil.setValue;
import com.github.newk5.flui.widgets.CellWrapper;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.tables.TableModifier;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
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
        super.focused=false;
        cell.cellEditorVisible(true);
        NativeString s = new NativeString();
        byte[] str = cell.getValueAsString().getBytes(StandardCharsets.UTF_8);
        for (byte ch : str) {
            s.append(ch);
        }

        cell.nativeString(s);

    }

    @Override
    public void drawEditor(JImGui imgui, CellWrapper cell, Field field) {

        if (!super.focused) {
            imgui.setKeyboardFocusHere(0);
            super.focused=true;
        }
        if (imgui.inputText(JImStr.EMPTY, cell.getNativeString(), JImInputTextFlags.EnterReturnsTrue)) {
            setValue(cell.getRowObject(), field, new String(cell.getNativeString().toBytes(), StandardCharsets.UTF_8));
            super.getTable().updateRow(cell.getRowObject());
            cell.cellEditorVisible(false);
        }

    }

}
