package com.github.newk5.flui.widgets;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.ice1000.jimgui.JImStr;

public class Column {

    private JImStr header;
    private String field;
    private List<SizedWidget> widgets = new ArrayList<>();
    private BiFunction<Object, Object, Integer> sorter;

    public Column(String header) {
        this.header = new JImStr(header);
    }

    public Column header(final JImStr value) {
        this.header = value;
        return this;
    }

    public Column field(final String value) {
        this.field = value;
        return this;
    }

    public Column(JImStr header, String field) {
        this.header = header;
        this.field = field;
    }

    public boolean hasWidgets() {
        return !this.widgets.isEmpty();
    }

    public List<SizedWidget> getWidgets() {
        return widgets;
    }

    public Column widgets(SizedWidget... w) {
        widgets.clear();
        Arrays.stream(w).forEach(sw -> widgets.add(sw));
        return this;
    }

    public JImStr getHeader() {
        return header;
    }

    public String getHeaderAsStr() {
        return new String(header.bytes, StandardCharsets.UTF_8);
    }

    public String getField() {
        return field;
    }
    
  
    public Column sorter(final BiFunction<Object,Object,Integer> value) {
        this.sorter = value;
        return this;
    }

    public BiFunction<Object, Object, Integer> getSorter() {
        return sorter;
    }
    

}
