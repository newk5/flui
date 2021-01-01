package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
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

    private int rowsPerPage = -1;
    private boolean borders = true;
    private int flags;
    private List<Column> columns = new ArrayList<>();
    private List<CellWrapper[]> simpleData = new ArrayList<>();
    private List<Object> data = new ArrayList<>();
    private CompactHashMap<String, Field> fields = new CompactHashMap<>();
    private JImStr title;
    private CellWrapper lastSelected;
    private int selectedIdx = -1;

    private Button nextBtn;
    private Button prevBtn;
    private Label pagesLbl;

    private int currentPage = 1;
    private int totalPages;

    private int offset = 0;

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

    public static Table withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Table) w;
    }

    public void clear() {
        this.data.clear();
        this.simpleData.clear();
        updatePaginator();
    }

    private void buildFlags() {
        flags = 0;
        if (borders) {
            flags |= JImTableFlags.Borders;
        }
    }

    private void calculatePageCount() {
        totalPages = (int) Math.ceil(Float.valueOf(this.simpleData.size() + "") / Float.valueOf(rowsPerPage + ""));
        if (totalPages == 0) {
            totalPages = 1;
        }
    }

    private void nextPage() {
        if (this.currentPage < this.totalPages) {
            this.offset += this.rowsPerPage;
            this.currentPage++;
            this.updatePaginator();

        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            if (this.currentPage + 1 >= this.totalPages) {
                this.offset -= this.rowsPerPage;
                this.currentPage--;
                this.updatePaginator();
            }
        }
    }

    private void updatePaginator() {
        calculatePageCount();
        if (pagesLbl != null) {
            this.pagesLbl.text("Page " + currentPage + " of " + totalPages);
        }
    }

    @Override
    protected void render(JImGui imgui) {
        imgui.pushID(numId);
        if (imgui.beginTable(title, columns.size(), flags)) {
            columns.forEach(c -> imgui.tableSetupColumn(c.getHeader()));
            imgui.tableHeadersRow();

            int counter = 0;
            for (int i = offset; i < simpleData.size(); i++) {
                if (counter == rowsPerPage) {
                    break;
                }
                CellWrapper[] row = simpleData.get(i);

                for (CellWrapper cell : row) {
                    imgui.tableNextColumn();
                    imgui.pushID(i);
                    if (imgui.selectable0(cell.getValue(), cell.getSelected(), JImSelectableFlags.SpanAllColumns)) {
                        cell.selected(true);
                        if (lastSelected != null) {
                            lastSelected.selected(false);
                        }
                        selectedIdx = i;

                        if (onSelect != null) {
                            onSelect.accept(this.data.get(selectedIdx));
                        }
                        lastSelected = cell;
                    }
                    imgui.popID();
                }
                counter++;
            }

            imgui.endTable();
            //draw paginator widgets
            if (prevBtn == null) {
                prevBtn = new Button(id + ":PrevBtn").text("<").align(Alignment.CENTER_H).onClick((btn) -> {
                    this.prevPage();
                });
                UI.runLater(() -> {
                    super.getParent().add(prevBtn);
                });

                prevBtn.sameLine(true);
            } else {
                prevBtn.posX = pagesLbl.posX - (pagesLbl.width / 2);
            }

            if (this.pagesLbl == null) {
                this.pagesLbl = new Label(id + ":pagesLbl").align(Alignment.CENTER_H).text("Page " + currentPage + " of " + totalPages);
                UI.runLater(() -> {
                    super.getParent().add(pagesLbl);
                });
                this.pagesLbl.sameLine(true);
            }

            if (nextBtn == null) {
                nextBtn = new Button(id + ":NextBtn").align(Alignment.CENTER_H).text(">").onClick((btn) -> {
                    this.nextPage();
                });
                UI.runLater(() -> {
                    super.getParent().add(nextBtn);

                });
            } else {
                nextBtn.posX = pagesLbl.posX + pagesLbl.width + 20;

            }

        }
        imgui.popID();
    }

    public Table rowsPerPage(int rows) {
        this.rowsPerPage = rows;
        return this;
    }

    public Table onSelect(Consumer<Object> o) {
        onSelect = o;
        return this;
    }

    public Table columns(Column... cols) {
        Arrays.stream(cols).forEach(c -> columns.add(c));

        return this;
    }

    public void add(Object o) {
        List<CellWrapper> lst = new ArrayList<>();
        if (o instanceof String[]) {
            for (int i = 0; i < ((String[]) o).length; i++) {
                Column col = columns.get(i);
                String value = ((String[]) o)[i];
                lst.add(new CellWrapper("", new JImStr(value), col.getHeaderAsStr(), this.simpleData.size()));
            }

            this.simpleData.add(lst.toArray(new CellWrapper[lst.size()]));
        } else {

            columns.forEach(col -> {

                lst.add(new CellWrapper("", new JImStr(getValue(o, col.getField())), col.getHeaderAsStr(), this.simpleData.size()));

            });

            this.simpleData.add(lst.toArray(new CellWrapper[lst.size()]));
        }
        this.data.add(o);
        updatePaginator();
    }

    public void remove(Object o) {
        int idx = data.indexOf(o);

        if (idx > -1) {
            data.remove(idx);
            simpleData.remove(idx);

        }
    }

    public void remove(int rowIndex) {

        if (rowIndex > -1) {
            data.remove(rowIndex);
            simpleData.remove(rowIndex);

        }
    }

    public Table data(List<Object> data) {
        this.simpleData.clear();
        data.forEach(rowArray -> {
            this.add(rowArray);

        });
        calculatePageCount();
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
