package com.github.newk5.flui.widgets;

import java.nio.charset.StandardCharsets;
import org.ice1000.jimgui.JImStr;

public class Column {

    private JImStr header;
    private String field;

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

    public JImStr getHeader() {
        return header;
    }
    
       public String getHeaderAsStr() {
        return new String(header.bytes, StandardCharsets.UTF_8);
    }

    public String getField() {
        return field;
    }

}
