
import com.github.newk5.flui.Application;
import com.github.newk5.flui.widgets.Button;
import com.github.newk5.flui.widgets.Column;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.UI;
import com.github.newk5.flui.widgets.Window;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TablesTest {

    public static void main(String[] args) {
        Application app = new Application().title("test").height(500).width(1200);

        User us = new User("John13", 19, "Scotland");
        UI.render(app, () -> {

            new Window("w").fill().children(
                    new Table("tbl").globalFilter(true).cellEditor(true).sortable(true)
                            .rowsPerPage(10).columns(
                            new Column("Name").field("name"),
                            new Column("Age").field("age").witdh(50),
                            new Column("Deleted").field("deleted"),
                            new Column("Country").field("country"),
                            new Column("Dob").field("dob"),
                            //instead of binding properties from your object you can also render any widget you want inside table cells
                            new Column("Options").widgets(
                                    //when creating widgets inside tables you must not specify any ID, the ID's will be generated automatically
                                    new Button().text("OK").sameLine(true).onClick((btn) -> {

                                        //get the data binded to this row
                                        User user = (User) btn.getData("rowData");

                                        //change the btn text to the user name
                                        btn.text(user.getName());

                                        /*
                                        when changing any of the data that is binded to the table,
                                        we must update the table UI by updating the row the data
                                        is connected to, you can pass your data(object) to the .updateRow(object)
                                        function and the corresponding row will be automatically updated
                                         */
                                        user.name("Changed name");
                                        Table.withID("tbl").updateRow(user);

                                    }).onTableAdd((btn) -> { //event called when the widget is added to the table

                                        //could be useful to conditionally change something about the widget based on the row data
                                        Button b = (Button) btn;
                                        User u = b.getData("rowData");
                                        if (u.getName().equals("John4")) {
                                            b.text("Changed");

                                        }

                                    }),
                                    new Button().text("Remove").onClick((b) -> {
                                        User user = (User) b.getData("rowData");
                                        Table.withID("tbl").remove(user);
                                    })
                            )
                    ).data(
                            Stream.of(
                                    new User("John1", 19, "France", new Date()),
                                    new User("John2", 29, "Portugal", new Date()),
                                    new User("John3", 19, "USA", new Date()),
                                    new User("John4", 19, "Spain", new Date()),
                                    new User("John5", 19, "France", new Date()),
                                    new User("John6", 19, "Ireland", new Date()),
                                    new User("John7", 19, "Portugal", new Date()),
                                    new User("John8", 19, "Spain", new Date()),
                                    new User("John9", 19, "Japan", new Date()),
                                    new User("John10", 19, "China", new Date()),
                                    new User("John11", 19, "China", new Date()),
                                    new User("John12", 19, "Russia", new Date()),
                                    us
                            ).collect(Collectors.toList())
                    ).onSelect((o) -> { //when a row is selected (clicked on)
                        User u = (User) o;
                        System.out.println(u.getName());
                    }).onPageChange((oldPage, newPage) -> { //when a table page is changed (programatically or when the next/prev button is used)
                        System.out.println("Page changed from " + oldPage + " to " + newPage);
                    }),
                    new Button("btn").text("add").sameLine(true).onClick((btn) -> {
                        Table tbl = Table.withID("tbl");
                        tbl.add(new User("John" + (tbl.getTotalRows() + 1), 19, "Spain"));

                    }),
                    new Button("btn2").text("remove").sameLine(true).onClick((btn) -> {
                        Table.withID("tbl").remove(us);
                    }),
                    new Button("btn3").text("clear").sameLine(true).onClick((btn) -> {
                        Table.withID("tbl").clear();
                    }),
                    new Button("btn4").text("last page").onClick((btn) -> {
                        Table tbl = Table.withID("tbl");
                        tbl.page(tbl.getTotalPages());
                    })
            );

        });

    }
}

class User {

    private String name;
    private int age;
    private String country;
    private boolean deleted;
    private Date dob;

    public User(String name, int age, String country) {
        this.name = name;
        this.age = age;
        this.country = country;
    }

    public User(String name, int age, String country, Date d) {
        this.name = name;
        this.age = age;
        this.dob = d;
        this.country = country;
    }

    public User name(final String value) {
        this.name = value;
        return this;
    }

    public User age(final int value) {
        this.age = value;
        return this;
    }

    public User country(final String value) {
        this.country = value;
        return this;
    }

    public User deleted(final boolean value) {
        this.deleted = value;
        return this;
    }

    public User dob(final Date value) {
        this.dob = value;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Date getDob() {
        return dob;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", age=" + age + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.name);
        hash = 19 * hash + this.age;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.age != other.age) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

}
