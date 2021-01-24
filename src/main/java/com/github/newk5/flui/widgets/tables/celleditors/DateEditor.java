package com.github.newk5.flui.widgets.tables.celleditors;

import static com.github.newk5.flui.util.ReflectUtil.setValue;
import com.github.newk5.flui.widgets.CellWrapper;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.tables.TableModifier;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.NativeTime;

public class DateEditor extends TableModifier implements CellEditor {

    public DateEditor(Table table) {
        super(table);
    }

    @Override
    public void onClick(JImGui imgui, CellWrapper cell, Field field) {
        if (cell.getValueType().equals(Date.class)) {

            NativeTime t = new NativeTime();
            Date d = cell.getCellValue().equals("") ? new Date() : (Date) cell.getCellValue();
            t.modifyAbsoluteSeconds(TimeUnit.MILLISECONDS.toSeconds(d.getTime()));

            cell.nativeTime(t);

        }

    }

    @Override
    public void onSubmit(JImGui imgui, CellWrapper cell, Field field) {
        if (cell.getValueType().equals(Date.class)) {
            if (imgui.dateChooser(JImStr.EMPTY, cell.getNativeTime())) {

                cell.cellEditorVisible(false);
                Date d = new Date(TimeUnit.SECONDS.toMillis(cell.getNativeTime().accessAbsoluteSeconds()));

                setValue(cell.getRowObject(), field, d);
                super.getTable().updateRow(cell.getRowObject());

            }
        }
    }

}
