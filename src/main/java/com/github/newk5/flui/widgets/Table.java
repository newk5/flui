package com.github.newk5.flui.widgets;

import com.github.newk5.flui.util.SerializableConsumer;
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Font;
import static com.github.newk5.flui.util.ReflectUtil.getValue;
import com.github.newk5.flui.util.SerializableBiConsumer;
import com.github.newk5.flui.widgets.tables.celleditors.BooleanEditor;
import com.github.newk5.flui.widgets.tables.celleditors.CellEditor;
import com.github.newk5.flui.widgets.tables.celleditors.DateEditor;
import com.github.newk5.flui.widgets.tables.celleditors.DoubleEditor;
import com.github.newk5.flui.widgets.tables.celleditors.FloatEditor;
import com.github.newk5.flui.widgets.tables.celleditors.IntegerEditor;
import com.github.newk5.flui.widgets.tables.celleditors.StringEditor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImSortSpecs;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
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
    private List<CellWrapper[]> filteredData = new ArrayList<>();

    private List data = new ArrayList<>();
    private CompactHashMap<String, Field> fields = new CompactHashMap<>();
    private JImStr title;
    private CellWrapper lastSelected;
    private int selectedIdx = -1;

    private Button nextBtn;

    private Button prevBtn;

    private Label pagesLbl;

    private Label globalFilterLbl;
    private InputText globalFilterInput;

    private int rowsDrawn;
    private int currentPage = 1;
    private int totalPages;
    private Font headerFont;
    private Font rowFont;

    private Color headerTextColor;
    private Color rowTextColor;

    private boolean globalFilter;
    private String globalExpr = "";
    private boolean selectable;
    private boolean celleditor;
    private boolean sortable;

    private CellWrapper lastEditedCell;
    private int offset = 0;

    private SerializableConsumer<Object> onSelect;
    private SerializableBiConsumer<Integer, Integer> pageChangeEvent;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private CompactHashMap<Class, CellEditor> cellEditors = new CompactHashMap<>();
    private CompactHashMap<Class, BiFunction<Object, Object, Integer>> cellComparators = new CompactHashMap<>();

    public Table(String id) {
        super(id, true);
        counter++;
        this.index(counter);
        idIndex.put(id, counter);
        instances.add(this);
        buildFlags();
        title = new JImStr(id);

        this.addCellEditor(String.class, new StringEditor(this));
        this.addCellEditor(Boolean.class, new BooleanEditor(this));
        this.addCellEditor(boolean.class, new BooleanEditor(this));
        this.addCellEditor(Date.class, new DateEditor(this));
        this.addCellEditor(Double.class, new DoubleEditor(this));
        this.addCellEditor(double.class, new DoubleEditor(this));
        this.addCellEditor(Float.class, new FloatEditor(this));
        this.addCellEditor(float.class, new FloatEditor(this));
        this.addCellEditor(Integer.class, new IntegerEditor(this));
        this.addCellEditor(int.class, new IntegerEditor(this));

    }

    public void addTypeSorter(Class c, BiFunction<Object, Object, Integer> function) {
        cellComparators.put(c, function);
    }

    public void addCellEditor(Class c, CellEditor ce) {
        cellEditors.put(c, ce);
    }

    @Override
    protected void freeColors() {
        super.freeColor(headerTextColor);
        super.freeColor(rowTextColor);
        if (nextBtn != null) {
            nextBtn.freeColors();
            prevBtn.freeColors();
            pagesLbl.freeColors();
        }
        if (globalFilterInput != null) {
            globalFilterInput.freeColors();
            globalFilterLbl.freeColors();
        }

    }

    public void delete() {
        UI.runLater(() -> {

            freeColors();
            idIndex.remove(id);
            instances.remove(this);

            children.forEach(child -> {
                ((SizedWidget) child).deleteFlag = true;
            });

            SizedWidget sw = super.getParent();
            if (sw != null) {
                sw.deleteChild(this);
            }

        });

    }

    public static Table withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Table) w;
    }

    private CellWrapper buildCellWrapper(Column col, boolean isDate, Object value, Object o) {
        int colIdx = columns.indexOf(col);
        CellWrapper cell = new CellWrapper(col.getField(), new JImStr(isDate ? sdf.format((Date) value) : value + ""), col.getHeaderAsStr(), o);
        cell.cellEditorVisible(false);
        cell.columnIdx(colIdx);
        cell.cellValue(value);

        return cell;
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
                for (Column col : columns) {
                    Field field = getField(col.getField(), o.getClass());
                    Object value = getValue(o, field);

                    boolean isDate = value instanceof Date;

                    int colIdx = columns.indexOf(col);
                    CellWrapper cell = buildCellWrapper(col, isDate, value, o);
                    if (field != null) {
                        cell.valueType(field.getType());
                    }
                    if (col.hasWidgets()) {
                        //avoid recreating the widgets and just copy them from the old row
                        cell = Arrays.stream(cellW).filter(c -> c.getColumnIdx() == colIdx).findFirst().get();
                    }
                    cells.add(cell);

                }
                CellWrapper[] row = cells.toArray(new CellWrapper[cells.size()]);
                this.sortCells(row);
                data.set(idx, row);

            }
        }
    }

    private int indexOfRowInCellWrapper(List<CellWrapper[]> list, Object o) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i)[0].getRowObject().equals(o)) {
                return i;
            }
        }
        return -1;
    }

    public void updateAllRows() {
        data.forEach(o -> updateRow(o));
    }

    public void updateRow(Object o) {
        int idx = data.indexOf(o);
        updateRow(simpleData, o, sortable ? indexOfRowInCellWrapper(this.simpleData, o) : idx);
        if (!this.globalExpr.equals("")) {
            updateRow(filteredData, o, indexOfRowInCellWrapper(this.filteredData, o));

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
        if (sortable) {
            flags |= JImTableFlags.Sortable;
        }
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

    public Field getField(String name, Class klass) {
        try {
            if (name == null) {
                return null;
            }
            Field field = fields.get(name) == null ? klass.getDeclaredField(name) : fields.get(name);
            fields.putIfAbsent(name, field);
            return field;
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void drawCellEditor(JImGui imgui, CellWrapper cell) {
        if (!cell.isCellEditorVisible()) {

            if (imgui.selectable0(cell.getValue(), cell.getSelected())) {

                if (lastEditedCell != null) {
                    lastEditedCell.cellEditorVisible(false);
                }

                Field field = getField(cell.getField(), cell.getRowObject().getClass());

                CellEditor editor = cellEditors.get(cell.getValueType());
                if (editor != null) {

                    editor.onClick(imgui, cell, field);
                }

                lastEditedCell = cell;

            }

        } else {

            Field field = getField(cell.getField(), cell.getRowObject().getClass());

            CellEditor editor = cellEditors.get(cell.getValueType());
            if (editor != null) {
                editor.drawEditor(imgui, cell, field);
            }

        }
    }

    private void drawCell(JImGui imgui, CellWrapper[] row, int i) {
        for (int cellIdx = 0; cellIdx < row.length; cellIdx++) {
            CellWrapper cell = row[cellIdx];
            int id = Integer.valueOf(cellIdx + "0" + i);
            imgui.pushID(id);
            imgui.tableSetColumnIndex(cell.getColumnIdx());

            if (cell.hasWidgets()) {
                cell.renderWidgets(imgui);

            } else {
                if (rowFont != null) {
                    imgui.pushFont(rowFont.getJimFont());
                }
                if (rowTextColor != null) {
                    imgui.pushStyleColor(JImStyleColors.Text, rowTextColor.asVec4());
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
                    drawCellEditor(imgui, cell);
                } else {
                    imgui.text(cell.getValue());
                }
                if (rowFont != null) {
                    imgui.popFont();
                }
                if (rowTextColor != null) {
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
        if (headerTextColor != null) {
            imgui.pushStyleColor(JImStyleColors.Text, headerTextColor.asVec4());
        }
        columns.forEach(c -> imgui.tableSetupColumn(c.getHeader()));
        imgui.tableHeadersRow();
        if (headerFont != null) {
            imgui.popFont();
        }
        if (headerTextColor != null) {
            imgui.popStyleColor();
        }
    }
    boolean appliedMove = false;

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
            if (appliedMove == false) {
                globalFilterLbl.applyMove = true;
                appliedMove = true;
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

    protected void sortData(int column, int direction) {

        Column c = columns.get(column);

        if (c.getSorter() != null) {
            getData().sort((o1, o2) -> {
                CellWrapper cell1 = Arrays.stream(o1).filter(ce -> ce.getColumn().equals(c.getHeaderAsStr())).findFirst().get();
                CellWrapper cell2 = Arrays.stream(o2).filter(ce -> ce.getColumn().equals(c.getHeaderAsStr())).findFirst().get();
                int v = c.getSorter().apply(cell1.getRowObject(), cell2.getRowObject());
                if (direction != 1) {
                    v *= -1;
                }
                return v;
            });

        } else {

            Comparator<CellWrapper[]> comp = (o1, o2) -> {
                CellWrapper cell1 = Arrays.stream(o1).filter(ce -> ce.getColumn().equals(c.getHeaderAsStr())).findFirst().get();
                CellWrapper cell2 = Arrays.stream(o2).filter(ce -> ce.getColumn().equals(c.getHeaderAsStr())).findFirst().get();

                String value1 = cell1.getValue().toString();
                String value2 = cell2.getValue().toString();

                if (value1.equals("")) {
                    return -1;
                } else if (value2.equals("")) {
                    return 1;
                }
                int returnValue = 0;
                if (cell1.getValueType() == Integer.class || cell1.getValueType() == int.class) {

                    if (Integer.valueOf(value1) > Integer.valueOf(value2)) {
                        returnValue = 1;
                    } else {
                        returnValue = -1;
                    }
                } else if (cell1.getValueType() == Double.class || cell1.getValueType() == double.class) {

                    if (Double.valueOf(value1) > Double.valueOf(value2)) {
                        returnValue = 1;
                    } else {
                        returnValue = -1;
                    }
                } else if (cell1.getValueType() == Boolean.class || cell1.getValueType() == boolean.class) {

                    returnValue = Boolean.valueOf(value1).compareTo(Boolean.valueOf(value2));
                } else if (cell1.getValueType() == Float.class || cell1.getValueType() == float.class) {

                    if (Float.valueOf(value1) > Float.valueOf(value2)) {
                        returnValue = 1;
                    } else {
                        returnValue = -1;
                    }
                } else if (cell1.getValueType() == Long.class || cell1.getValueType() == long.class) {

                    if (Long.valueOf(value1) > Long.valueOf(value2)) {
                        returnValue = 1;
                    } else {
                        returnValue = -1;
                    }
                } else if (cell1.getValueType() == String.class) {

                    returnValue = cell1.getValue().toString().compareTo(cell2.getValue().toString());
                } else if (cell1.getValueType() == Date.class) {

                    try {
                        Date d1 = sdf.parse(cell1.getValue().toString());
                        Date d2 = sdf.parse(cell2.getValue().toString());

                        if (d1.after(d2)) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    BiFunction<Object, Object, Integer> f = cellComparators.get(cell1.getValueType());

                    if (f != null) {
                        returnValue = f.apply(cell1.getCellValue(), cell2.getCellValue());
                    }
                }
                if (direction != 1) {
                    returnValue *= -1;
                }
                return returnValue;

            };
            getData().sort(comp);
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

            if (imgui.beginTable(title, columns.size(), flags)) {

                if (sortable) {
                    JImSortSpecs specs = imgui.tableGetSortSpecs();

                    if (specs.isSpecsDirty() && !firstRenderLoop) {

                        sortData(specs.columnSortSpecs(0).getColumnIndex(), specs.columnSortSpecs(0).getSortDirection());

                        specs.setSpecsDirty(false);
                    }
                }

                this.applyStyles(imgui);

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

    public Table sortable(boolean sortable) {
        this.sortable = sortable;
        buildFlags();
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
        this.headerTextColor = c;
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
        this.rowTextColor = c;
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

                Field field = getField(col.getField(), o.getClass());
                Object value = field == null ? "" : getValue(o, field);

                boolean isDate = value instanceof Date;

                //in case there is an active filter, check if the new added row matches it
                if (!globalExpr.equals("") && !matchesGlobalFilter) {
                    matchesGlobalFilter = value.toString().toLowerCase().contains(this.globalExpr.toLowerCase());
                }
                CellWrapper cell = buildCellWrapper(col, isDate, value, o);
                if (field != null) {
                    cell.valueType(field.getType());
                }
                if (col.hasWidgets()) {
                    col.getWidgets().forEach(w -> cell.addWidget(id, w));
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
            int fIdx = indexOfRowInCellWrapper(filteredData, o);
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
                } else {
                    prevPage();
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

}
