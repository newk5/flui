package com.github.newk5.flui.widgets.tables.celleditors;

import com.github.newk5.flui.widgets.CellWrapper;
import java.lang.reflect.Field;
import org.ice1000.jimgui.JImGui;

public interface CellEditor {

    /**
     * Defines behaviour when the user clicks a table cell.
     *
     * @param imgui
     * @param cell
     * @param field
     */
    public void onClick(JImGui imgui, CellWrapper cell, Field field);

    /**
     * Defines behaviour that specifies how the value should be submitted after
     * being edited
     *
     * @param imgui
     * @param cell
     * @param field
     */
    public void onSubmit(JImGui imgui, CellWrapper cell, Field field);

}
