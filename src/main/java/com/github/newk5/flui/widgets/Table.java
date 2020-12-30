package com.github.newk5.flui.widgets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.flag.JImSelectableFlags;
import org.ice1000.jimgui.flag.JImTableFlags;
import vlsi.utils.CompactHashMap;

public class Table extends SizedWidget {

    private static long counter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<>();

    private int rowsPerPage;
    private boolean borders = true;
    private int flags;
    private List<Column> columns = new ArrayList<>();
    private List<CellWrapper[]> simpleData = new ArrayList<>();
    private List<Object> data = new ArrayList<>();
    private CompactHashMap<String, Field> fields = new CompactHashMap<>();
    private JImStr title;
    private CellWrapper lastSelected;
    private int selectedIdx = -1;

    private Consumer<Object> onSelect;

    public Table(String id) {
        super(id, true);
        counter++;
        this.index(counter);
        idIndex.put(id, counter);
        instances.add(this);
        buildFlags();
        title = new JImStr(id);
    }

    private void buildFlags() {
        flags = 0;
        if (borders) {
            flags |= JImTableFlags.Borders;
        }
    }

    @Override
    protected void render(JImGui imgui) {
        imgui.pushID(numId);
        if (imgui.beginTable(title, columns.size(), JImTableFlags.Borders | JImTableFlags.Sortable | JImTableFlags.Hideable)) {
            columns.forEach(c -> imgui.tableSetupColumn(c.getHeader()));
            imgui.tableHeadersRow();

            simpleData.forEach(cells -> {
                Arrays.stream(cells).forEach(cell -> {
                    imgui.tableNextColumn();
                    if (imgui.selectable0(cell.getValue(), cell.getSelected(), JImSelectableFlags.SpanAllColumns)) {
                        cell.selected(true);
                        if (lastSelected != null) {
                            lastSelected.selected(false);
                        }
                        selectedIdx = simpleData.indexOf(cells);

                        if (onSelect != null) {
                            onSelect.accept(this.data.get(selectedIdx));
                        }
                        lastSelected = cell;
                    }
                });

            });

            imgui.endTable();

        }
        imgui.popID();
    }

    

    public Table onSelect(Consumer<Object> o) {
        onSelect = o;
        return this;
    }

    public Table columns(Column... cols) {
        Arrays.stream(cols).forEach(c -> columns.add(c));
        return this;
    }

    public Table data(List<Object> data) {
        this.data = data;
        this.simpleData.clear();
        data.forEach(rowArray -> {
            List<CellWrapper> lst = new ArrayList<>();
            if (rowArray instanceof String[]) {
                for (int i = 0; i < ((String[]) rowArray).length; i++) {
                    Column col = columns.get(i);
                    String value = ((String[]) rowArray)[i];
                    lst.add(new CellWrapper("", new JImStr(value), col.getHeaderAsStr()));
                }

                this.simpleData.add(lst.toArray(new CellWrapper[lst.size()]));
            } else {

                columns.forEach(col -> {

                    Object obj = (Object) rowArray;

                    lst.add(new CellWrapper("", new JImStr(getValue(obj, col.getField())), col.getHeaderAsStr()));

                });

                this.simpleData.add(lst.toArray(new CellWrapper[lst.size()]));
            }

        });
        return this;
    }

    private String getValue(Object obj, String f) {

        Field field;
        String value = "";
        try {
            field = obj.getClass().getDeclaredField(f);

            field.setAccessible(true);
            if (field != null) {
                Object v = field.get(obj);
                value = v != null ? v + "" : "";
            }
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }
}
