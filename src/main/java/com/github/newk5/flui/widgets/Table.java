package com.github.newk5.flui.widgets;

import com.github.newk5.flui.util.SerializableConsumer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import com.esotericsoftware.kryo.serializers.ClosureSerializer.Closure;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Font;
import com.github.newk5.flui.util.SerializableBiConsumer;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ice1000.jimgui.JImFont;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.flag.JImSelectableFlags;
import org.ice1000.jimgui.flag.JImTableFlags;
import org.objenesis.strategy.StdInstantiatorStrategy;
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
    protected int nextBtnParentIdx = -1;
    private Button prevBtn;
    protected int prevBtnParentIdx = -1;
    private Label pagesLbl;
    protected int pagesLblParentIdx = -1;

    private int currentPage = 1;
    private int totalPages;

    private int offset = 0;
    Kryo kryo;

    private SerializableConsumer<Object> onSelect;
    private SerializableBiConsumer<Integer, Integer> pageChangeEvent;

    public Table(String id) {
        super(id, true);
        counter++;
        this.index(counter);
        idIndex.put(id, counter);
        instances.add(this);
        buildFlags();
        title = new JImStr(id);

        kryo = new Kryo();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.setRegistrationRequired(false);
        kryo.register(SerializedLambda.class);
        kryo.register(SerializableConsumer.class);
        kryo.register(Closure.class, new ClosureSerializer());
    }

    public static Table withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Table) w;
    }

    public void updateRow(Object o) {
        int idx = this.data.indexOf(o);

        if (idx > -1) {
            CellWrapper[] cellW = simpleData.get(idx);
            if (o instanceof String[]) {
                for (int i = 0; i < ((String[]) o).length; i++) {

                    String value = ((String[]) o)[i];
                    cellW[i].value(new JImStr(value));
                }

            } else {

                List<CellWrapper> cells = new ArrayList<>();
                columns.forEach(col -> {
                    String value = getValue(o, col.getField());
                    int colIdx = columns.indexOf(col);
                    CellWrapper cell = new CellWrapper("", new JImStr(value), col.getHeaderAsStr(), o);
                    cell.columnIdx(colIdx);
                    if (col.hasWidgets()) {
                        //avoid recreating the widgets and just copy them from the old row
                        cell = Arrays.stream(cellW).filter(c -> c.getColumnIdx() == colIdx).findFirst().get();
                    }
                    cells.add(cell);

                });
                CellWrapper[] row = cells.toArray(new CellWrapper[cells.size()]);
                this.sortCells(row);
                simpleData.set(idx, row);

            }
        }
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

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalRows() {
        return this.data.size();
    }

    private void nextPage() {
        if (this.currentPage < this.totalPages) {
            this.offset += this.rowsPerPage;
            if (pageChangeEvent != null) {
                pageChangeEvent.accept(currentPage, currentPage + 1);
            }
            this.currentPage++;
            this.updatePaginator();

        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            if (this.currentPage + 1 >= this.totalPages) {
                this.offset -= this.rowsPerPage;
                if (pageChangeEvent != null) {
                    pageChangeEvent.accept(currentPage, currentPage - 1);
                }
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
        super.preRender(imgui);

        if (imgui.beginTable(title, columns.size(), flags)) {
            columns.forEach(c -> imgui.tableSetupColumn(c.getHeader()));
            imgui.tableHeadersRow();

            int counter = 0;
            for (int i = offset; i < simpleData.size(); i++) {
                if (counter == rowsPerPage) {
                    break;
                }
                CellWrapper[] row = simpleData.get(i);
                imgui.tableNextRow();
                for (int cellIdx = 0; cellIdx < row.length; cellIdx++) {
                    CellWrapper cell = row[cellIdx];

                    imgui.tableSetColumnIndex(cell.getColumnIdx());

                    imgui.pushID(i);
                    if (cell.hasWidgets()) {
                        imgui.setItemAllowOverlap();
                        cell.renderWidgets(imgui);

                    } else {
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
                if (prevBtnParentIdx > -1) {
                    UI.runLater(() -> {
                        super.getParent().addAtIndex(prevBtn, prevBtnParentIdx);
                    });
                }

                prevBtn.sameLine(true);
            } else {
                prevBtn.posX = pagesLbl.posX - (pagesLbl.width / 2);
            }

            if (this.pagesLbl == null) {
                this.pagesLbl = new Label(id + ":pagesLbl").align(Alignment.CENTER_H).text("Page " + currentPage + " of " + totalPages);

                if (pagesLblParentIdx > -1) {
                    UI.runLater(() -> {
                        super.getParent().addAtIndex(pagesLbl, pagesLblParentIdx);
                    });
                }
                this.pagesLbl.sameLine(true);
            }

            if (nextBtn == null) {
                nextBtn = new Button(id + ":NextBtn").align(Alignment.CENTER_H).text(">").onClick((btn) -> {
                    this.nextPage();
                });

                if (nextBtnParentIdx > -1) {
                    UI.runLater(() -> {
                        super.getParent().addAtIndex(nextBtn, nextBtnParentIdx);

                    });
                }
            } else {
                nextBtn.posX = pagesLbl.posX + pagesLbl.width + 20;

            }

        }

        super.postRender(imgui);
    }

    public Table rowsPerPage(int rows) {
        this.rowsPerPage = rows;
        return this;
    }

    public Table onPageChange(SerializableBiConsumer<Integer, Integer> e) {
        this.pageChangeEvent = e;
        return this;
    }

    private void sortCells(CellWrapper[] cells) {
        /*
                we need to sort our cells so that we render the ones with widgets first,
                so that when we submit the selectable last, it will be the same height
                as the cell with the tallest height
            
         */
        Arrays.sort(cells, (cell1, cell2) -> (cell1.getWidgetsCount() > cell2.getWidgetsCount() ? -1 : 1));
    }

    public Table page(int page) {
        if (page != currentPage && page <= totalPages && page >= 1) {
            if (page > currentPage) {
                int diff = page - currentPage;
                offset += diff * rowsPerPage;
            } else {
                int diff = currentPage - page;
                offset -= diff * rowsPerPage;
            }

            if (pageChangeEvent != null) {
                pageChangeEvent.accept(currentPage, page);
            }
            currentPage = page;
            updatePaginator();
        }
        return this;
    }

    public Table onSelect(SerializableConsumer<Object> o) {
        onSelect = o;
        return this;
    }

    public Table font(String fontName) {
        super.font = fontName;
        super.fontObj = Application.fonts.get(fontName);
        return this;

    }

    public Table columns(Column... cols) {
        Arrays.stream(cols).forEach(c -> columns.add(c));

        return this;
    }

    public <T> T getRowData(int idx) {
        return (T) data.get(idx);
    }

    public void add(Object o) {
        List<CellWrapper> lst = new ArrayList<>();
        if (o instanceof String[]) {
            for (int i = 0; i < ((String[]) o).length; i++) {
                Column col = columns.get(i);
                String value = ((String[]) o)[i];
                lst.add(new CellWrapper("", new JImStr(value), col.getHeaderAsStr(), o));
            }

            this.simpleData.add(lst.toArray(new CellWrapper[lst.size()]));
        } else {

            columns.forEach(col -> {
                String value = getValue(o, col.getField());
                int idx = columns.indexOf(col);
                CellWrapper cell = new CellWrapper("", new JImStr(value), col.getHeaderAsStr(), o);
                cell.columnIdx(idx);
                if (col.hasWidgets()) {
                    col.getWidgets().forEach(w -> cell.addWidget(id, w, kryo));
                }

                lst.add(cell);

            });
            CellWrapper[] row = lst.toArray(new CellWrapper[lst.size()]);

            this.sortCells(row);
            this.simpleData.add(row);
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
            field = fields.get("f") == null ? obj.getClass().getDeclaredField(f) : fields.get(f);
            fields.putIfAbsent(f, field);

            field.setAccessible(true);
            if (field != null) {
                Object v = field.get(obj);
                value = v != null ? v + "" : "";
            }
        } catch (Exception ex) {
            // Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }
}
