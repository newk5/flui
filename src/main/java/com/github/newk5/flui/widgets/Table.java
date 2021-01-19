package com.github.newk5.flui.widgets;

import com.github.newk5.flui.util.SerializableConsumer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import com.esotericsoftware.kryo.serializers.ClosureSerializer.Closure;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.Direction;
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
import java.util.stream.Collectors;
import org.ice1000.jimgui.JImFont;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVar;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.NativeBool;
import org.ice1000.jimgui.NativeDouble;
import org.ice1000.jimgui.NativeFloat;
import org.ice1000.jimgui.NativeInt;
import org.ice1000.jimgui.NativeString;
import org.ice1000.jimgui.flag.JImInputTextFlags;
import org.ice1000.jimgui.flag.JImSelectableFlags;
import org.ice1000.jimgui.flag.JImSortDirection;
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
    private List<CellWrapper[]> filteredData = new ArrayList<>();

    private List data = new ArrayList<>();
    private CompactHashMap<String, Field> fields = new CompactHashMap<>();
    private JImStr title;
    private CellWrapper lastSelected;
    private int selectedIdx = -1;

    private Button nextBtn;

    private boolean lastPageIsEmpty;

    private Button prevBtn;

    private Label pagesLbl;

    private Label globalFilterLbl;
    private InputText globalFilterInput;

    private int rowsDrawn;
    private int currentPage = 1;
    private int totalPages;
    private Font headerFont;
    private Font rowFont;

    private JImVec4 headerTextColor;
    private JImVec4 rowTextColor;

    private Color headerTextCol;
    private Color rowTextCol;
    private boolean globalFilter;
    private String globalExpr = "";
    private boolean selectable;
    private boolean celleditor;

    private CellWrapper lastEditedCell;
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

    }

    public static Table withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Table) w;
    }

    private void updateRow(List<CellWrapper[]> data, Object o, int idx) {
        if (idx > -1) {
            CellWrapper[] cellW = data.get(idx);
            if (o instanceof String[]) {
                for (int i = 0; i < ((String[]) o).length; i++) {

                    String value = ((String[]) o)[i];
                    cellW[i].value(new JImStr(value));
                }

            } else {

                List<CellWrapper> cells = new ArrayList<>();
                columns.forEach(col -> {
                    Object value = getValue(o, col.getField());
                    int colIdx = columns.indexOf(col);
                    CellWrapper cell = new CellWrapper(col.getField(), new JImStr(value + ""), col.getHeaderAsStr(), o);
                    cell.cellEditorVisible(false);
                    cell.columnIdx(colIdx);
                    cell.cellValue(value);
                    if (col.hasWidgets()) {
                        //avoid recreating the widgets and just copy them from the old row
                        cell = Arrays.stream(cellW).filter(c -> c.getColumnIdx() == colIdx).findFirst().get();
                    }
                    cells.add(cell);

                });
                CellWrapper[] row = cells.toArray(new CellWrapper[cells.size()]);
                this.sortCells(row);
                data.set(idx, row);

            }
        }
    }

    private int indexOfFilteredRow(Object o) {
        for (int i = 0; i < this.filteredData.size(); i++) {
            if (this.filteredData.get(i)[0].getRowObject().equals(o)) {
                return i;
            }
        }
        return -1;
    }

    public void updateAllRows() {
        data.forEach(o -> updateRow(o));
    }

    public void updateRow(Object o) {
        updateRow(simpleData, o, data.indexOf(o));
        if (!this.globalExpr.equals("")) {
            updateRow(filteredData, o, indexOfFilteredRow(o));

        }

    }

    public void clear() {
        this.data.clear();
        this.simpleData.clear();
        this.filteredData.clear();
        this.offset = 0;
        currentPage = 1;
        if (!globalExpr.equals("")) {
            clearGlobalFilter();
        }
        updatePaginator();
    }

    private void buildFlags() {
        flags = 0;
        if (borders) {
            flags |= JImTableFlags.Borders;
        }
        flags |= JImTableFlags.RowBg;
    }

    private void calculatePageCount() {
        totalPages = (int) Math.ceil(Float.valueOf(this.getData().size() + "") / Float.valueOf(rowsPerPage + ""));
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
            this.offset -= this.rowsPerPage;
            if (pageChangeEvent != null) {
                pageChangeEvent.accept(currentPage, currentPage - 1);
            }
            this.currentPage--;
            this.updatePaginator();

        }
    }

    private List<CellWrapper[]> getData() {
        return this.globalExpr.equals("") ? this.simpleData : this.filteredData;
    }

    public void applyGlobalFilter(String expr) {
        this.globalExpr = expr;
        filteredData.clear();
        currentPage = 1;
        this.offset = 0;
        if (!expr.equals("")) {

            filteredData = simpleData.stream().filter(row -> {
                List<CellWrapper> cells = Arrays.stream(row)
                        .filter(cell -> cell.getValue().toString().toLowerCase().contains(expr.toLowerCase()))
                        .collect(Collectors.toList());

                return !cells.isEmpty();
            }).collect(Collectors.toList());
        } else {
            clearGlobalFilter();
        }
        updatePaginator();
    }

    public void clearGlobalFilter() {
        filteredData.clear();
        globalExpr = "";
        globalFilterInput.text("");
        updatePaginator();
    }

    private void updatePaginator() {
        calculatePageCount();
        if (pagesLbl != null) {
            this.pagesLbl.text("Page " + currentPage + " of " + totalPages);
        }
    }

    private void drawCellEditor(JImGui imgui, CellWrapper cell, int i) {
        if (!cell.isCellEditorVisible()) {
            if (imgui.selectable0(cell.getValue(), cell.getSelected())) {
                if (lastEditedCell != null) {
                    lastEditedCell.cellEditorVisible(false);
                }
                cell.cellEditorVisible(true);

                if (cell.getCellValue() instanceof String) {

                    NativeString s = new NativeString();
                    for (char ch : cell.getValue().toString().toCharArray()) {
                        s.append(ch);
                    }

                    cell.nativeString(s);

                } else if (cell.getCellValue() instanceof Integer) {

                    NativeInt s = new NativeInt();
                    int v = Integer.parseInt(cell.getValue().toString());
                    s.modifyValue(v);

                    cell.nativeInt(s);

                } else if (cell.getCellValue() instanceof Double) {

                    NativeDouble s = new NativeDouble();
                    s.modifyValue(Double.valueOf(cell.getValue().toString()));

                    cell.nativeDouble(s);

                } else if (cell.getCellValue() instanceof Float) {

                    NativeFloat s = new NativeFloat();
                    s.modifyValue(Float.valueOf(cell.getValue().toString()));

                    cell.nativeFloat(s);

                } else if (cell.getCellValue() instanceof Boolean) {

                    NativeBool s = new NativeBool();
                    s.modifyValue(Boolean.valueOf(cell.getValue().toString()));

                    cell.nativeBool(s);

                }
                lastEditedCell = cell;
            }
        } else {

            if (cell.getCellValue() instanceof String) {

                if (imgui.inputText(JImStr.EMPTY, cell.getNativeString(), JImInputTextFlags.EnterReturnsTrue)) {
                    cell.cellEditorVisible(false);
                    setValue(cell.getRowObject(), cell.getField(), cell.getNativeString().toString());
                    updateRow(cell.getRowObject());
                }

            } else if (cell.getCellValue() instanceof Integer) {

                imgui.inputInt(JImStr.EMPTY, cell.getNativeInt(), 0);

                if (imgui.isItemDeactivatedAfterEdit()) {
                    cell.cellEditorVisible(false);
                    setValue(cell.getRowObject(), cell.getField(), cell.getNativeInt().accessValue());
                    updateRow(cell.getRowObject());
                }

            } else if (cell.getCellValue() instanceof Double) {
                imgui.inputDouble(JImStr.EMPTY, cell.getNativeDouble());
                if (imgui.isItemDeactivatedAfterEdit()) {
                    cell.cellEditorVisible(false);
                    setValue(cell.getRowObject(), cell.getField(), cell.getNativeDouble().accessValue());
                    updateRow(cell.getRowObject());
                }

            } else if (cell.getCellValue() instanceof Float) {
                imgui.inputFloat(JImStr.EMPTY, cell.getNativeFloat());
                if (imgui.isItemDeactivatedAfterEdit()) {
                    cell.cellEditorVisible(false);
                    setValue(cell.getRowObject(), cell.getField(), cell.getNativeFloat().accessValue());
                    updateRow(cell.getRowObject());
                }

            } else if (cell.getCellValue() instanceof Boolean) {
                imgui.checkbox(JImStr.EMPTY, cell.getNativeBool());
                if (imgui.isItemDeactivatedAfterEdit()) {
                    cell.cellEditorVisible(false);
                    setValue(cell.getRowObject(), cell.getField(), cell.getNativeBool().accessValue());
                    updateRow(cell.getRowObject());
                }
            }

        }
    }

    private void drawCell(JImGui imgui, CellWrapper[] row, int i) {
        for (int cellIdx = 0; cellIdx < row.length; cellIdx++) {
            CellWrapper cell = row[cellIdx];
            imgui.pushID(i);
            imgui.tableSetColumnIndex(cell.getColumnIdx());

            if (cell.hasWidgets()) {
                cell.renderWidgets(imgui);

            } else {
                if (rowFont != null) {
                    imgui.pushFont(rowFont.getJimFont());
                }
                if (rowTextCol != null) {
                    imgui.pushStyleColor(JImStyleColors.Text, rowTextCol.asVec4(rowTextColor));
                }
                if (selectable) {
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
                } else if (celleditor) {
                    drawCellEditor(imgui, cell, i);
                } else {
                    imgui.text(cell.getValue());
                }
                if (rowFont != null) {
                    imgui.popFont();
                }
                if (rowTextCol != null) {
                    imgui.popStyleColor();
                }

            }

            imgui.popID();
        }
    }

    private void applyStyles(JImGui imgui) {
        if (headerFont != null) {
            imgui.pushFont(headerFont.getJimFont());
        }
        if (headerTextCol != null) {
            imgui.pushStyleColor(JImStyleColors.Text, headerTextCol.asVec4(headerTextColor));
        }
        columns.forEach(c -> imgui.tableSetupColumn(c.getHeader()));
        imgui.tableHeadersRow();
        if (headerFont != null) {
            imgui.popFont();
        }
        if (headerTextCol != null) {
            imgui.popStyleColor();
        }
    }
    boolean appliedMpve = false;

    private void drawGlobalFilter() {
        if (globalFilterLbl == null) {
            globalFilterLbl = new Label(id + ":GlobalFilterLbl").text("Filter: ").align(Alignment.TOP_RIGHT).sameLine(true).move(new Direction().left(160));
            globalFilterLbl.applyMove = false;

            UI.runLater(() -> {
                int idx = super.getParent().getChildren().indexOf(this);
                super.getParent().addAtIndex(globalFilterLbl, idx);

            });
        }
        if (globalFilterInput == null) {
            globalFilterInput = new InputText(id + ":GlobalFilterInput").width(150).align(Alignment.TOP_RIGHT).onChange(i -> {
                this.applyGlobalFilter(i.getText());
            });

            UI.runLater(() -> {
                int idx = super.getParent().getChildren().indexOf(this);
                super.getParent().addAtIndex(globalFilterInput, idx);
            });

        } else {
            if (appliedMpve == false) {
                globalFilterLbl.applyMove = true;
                appliedMpve = true;
            }

        }
    }

    private void drawPaginator() {
        if (prevBtn == null) {
            prevBtn = new Button(id + ":PrevBtn").text("<").align(Alignment.CENTER_H).onClick((btn) -> {
                this.prevPage();
            });

            UI.runLater(() -> {
                int prevBtnParentIdx = super.getParent().getChildren().indexOf(this) + 1;
                super.getParent().addAtIndex(prevBtn, prevBtnParentIdx);
            });

            prevBtn.sameLine(true);
        } else {
            prevBtn.posX = pagesLbl.posX - (pagesLbl.width / 2);
        }

        if (this.pagesLbl == null) {
            this.pagesLbl = new Label(id + ":pagesLbl").align(Alignment.CENTER_H).text("Page " + currentPage + " of " + totalPages);

            UI.runLater(() -> {
                int pagesLblParentIdx = super.getParent().getChildren().indexOf(prevBtn) + 1;

                super.getParent().addAtIndex(pagesLbl, pagesLblParentIdx);
            });

            this.pagesLbl.sameLine(true);
        }

        if (nextBtn == null) {
            nextBtn = new Button(id + ":NextBtn").align(Alignment.CENTER_H).text(">").onClick((btn) -> {
                this.nextPage();
            });

            UI.runLater(() -> {
                int nextBtnParentIdx = super.getParent().getChildren().indexOf(pagesLbl) + 1;
                super.getParent().addAtIndex(nextBtn, nextBtnParentIdx);

            });

        } else {
            nextBtn.posX = pagesLbl.posX + pagesLbl.width + 20;

        }
    }

    @Override
    protected void render(JImGui imgui) {
        if (!super.isHidden()) {

            long start = System.currentTimeMillis();
            super.preRender(imgui);
            if (globalFilter) {
                drawGlobalFilter();
            }
            if (imgui.beginTable(title, columns.size(), JImTableFlags.Sortable)) {
                this.applyStyles(imgui);
               //  JImSortDirection dir = JImSortDirection.Type.
                rowsDrawn = 0;

                for (int i = offset; i < getData().size(); i++) {
                    if (rowsDrawn == rowsPerPage) {
                        break;
                    }
                    CellWrapper[] row = getData().get(i);
                    imgui.tableNextRow();
                    drawCell(imgui, row, i);
                    rowsDrawn++;
                }

                imgui.endTable();
                if (rowsPerPage > -1) {
                    this.drawPaginator();
                }

            }
            long end = System.currentTimeMillis();
            if (super.firstRenderLoop) {
                System.out.println(rowsDrawn + " rows drawn in " + (end - start) + "ms");
            }
            super.postRender(imgui);

        }
    }

    public Table hidden(boolean hidden) {
        super.hidden(hidden);
        return this;
    }

    public Table selectable(boolean selectable) {
        this.selectable = selectable;
        return this;
    }

    public Table cellEditor(boolean cellEditor) {
        this.celleditor = cellEditor;
        return this;
    }

    public Table rowsPerPage(int rows) {
        this.rowsPerPage = rows;
        return this;
    }

    public Table headerTextColor(Color c) {
        this.headerTextCol = c;
        return this;
    }

    public Table globalFilter(boolean value) {
        this.globalFilter = value;
        return this;
    }

    public boolean hasGlobalFilter() {
        return globalFilter;
    }

    public Table rowsTextColor(Color c) {
        this.rowTextCol = c;
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

    public Table headerFont(String fontName) {
        this.headerFont = Application.fonts.get(fontName);
        return this;

    }

    public Table rowsFont(String fontName) {
        this.rowFont = Application.fonts.get(fontName);
        return this;

    }

    public Table columns(Column... cols) {
        Arrays.stream(cols).forEach(c -> columns.add(c));

        return this;
    }

    public <T> T getRowData(int idx) {
        return (T) data.get(idx);
    }

    private void add(Object o, boolean updateData) {
        List<CellWrapper> lst = new ArrayList<>();
        if (o instanceof String[]) {
            for (int i = 0; i < ((String[]) o).length; i++) {
                Column col = columns.get(i);
                String value = ((String[]) o)[i];
                lst.add(new CellWrapper("", new JImStr(value), col.getHeaderAsStr(), o));
            }

            this.simpleData.add(lst.toArray(new CellWrapper[lst.size()]));
        } else {
            boolean matchesGlobalFilter = false;
            for (Column col : columns) {
                Object value = getValue(o, col.getField());
                //in case there is an active filter, check if the new added row matches it
                if (!globalExpr.equals("") && !matchesGlobalFilter) {
                    matchesGlobalFilter = value.toString().toLowerCase().contains(this.globalExpr.toLowerCase());
                }
                int idx = columns.indexOf(col);
                CellWrapper cell = new CellWrapper(col.getField(), new JImStr(value.toString()), col.getHeaderAsStr(), o);
                cell.cellValue(value);
                cell.columnIdx(idx);
                if (col.hasWidgets()) {
                    col.getWidgets().forEach(w -> cell.addWidget(id, w, kryo));
                }

                lst.add(cell);

            }
            CellWrapper[] row = lst.toArray(new CellWrapper[lst.size()]);

            this.sortCells(row);
            this.simpleData.add(row);
            if (matchesGlobalFilter) {
                this.filteredData.add(row);
            }
        }
        if (updateData) {
            this.data.add(o);
        }
        updatePaginator();
    }

    public void add(Object o) {
        add(o, true);
    }

    public void remove(Object o) {
        int idx = data.indexOf(o);

        if (idx > -1) {
            data.remove(idx);
            simpleData.remove(idx);

        }
        if (!globalExpr.equals("")) {
            int fIdx = indexOfFilteredRow(o);
            if (fIdx > -1) {
                filteredData.remove(fIdx);
            }
        }

        UI.runLater(() -> {
            if (currentPage == totalPages && rowsDrawn == 1) {
                calculatePageCount();
                updatePaginator();
                if (!globalExpr.equals("")) {
                    clearGlobalFilter();
                }
            }

        });

    }

    public Table data(List<?> data) {
        this.simpleData.clear();
        data.forEach(rowArray -> {
            this.add(rowArray, false);

        });
        this.data = data;
        calculatePageCount();
        System.gc();
        return this;
    }

    private Object getValue(Object obj, String f) {

        Field field;
        Object value = "";
        try {
            field = fields.get("f") == null ? obj.getClass().getDeclaredField(f) : fields.get(f);
            fields.putIfAbsent(f, field);

            field.setAccessible(true);
            if (field != null) {
                Object v = field.get(obj);
                value = v != null ? v : "";
            }
        } catch (Exception ex) {
            // Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    private void setValue(Object obj, String f, Object val) {

        Field field;
        Object value = "";
        try {
            field = fields.get("f") == null ? obj.getClass().getDeclaredField(f) : fields.get(f);

            field.setAccessible(true);
            if (field != null) {
                field.set(obj, val);
                ;
            }
        } catch (Exception ex) {
            // Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
