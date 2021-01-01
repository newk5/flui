
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.widgets.Button;
import com.github.newk5.flui.widgets.Column;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.UI;
import com.github.newk5.flui.widgets.Window;
import java.util.Objects;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TablesTest {

    public static void main(String[] args) {
        Application app = new Application().title("test").height(500).width(1200);

        User us = new User("John13", 19);
        UI.render(app, () -> {

            new Window("w").fill().children(
                    new Table("tbl").rowsPerPage(5).columns(
                            new Column("Name").field("name"),
                            new Column("Age").field("age")
                    ).data(
                            Stream.of(
                                    new User("John1", 19),
                                    new User("Steve2", 29),
                                    new User("John3", 19),
                                    new User("John4", 19),
                                    new User("John5", 19),
                                    new User("John6", 19),
                                    new User("John7", 19),
                                    new User("John8", 19),
                                    new User("John9", 19),
                                    new User("John10", 19),
                                    new User("John11", 19),
                                    new User("John12", 19)
                            ).collect(Collectors.toList())
                    ).onSelect((o) -> {
                        User u = (User) o;
                        System.out.println(u.getName());
                    }), new Button("btn").text("add").onClick((btn)->{
                        Table.withID("tbl").add(us);
                    }).sameLine(true),
                    new Button("btn2").text("remove").onClick((btn)->{
                        Table.withID("tbl").remove(us);
                    }).sameLine(true),
                    new Button("btn3").text("clear").onClick((btn)->{
                        Table.withID("tbl").clear();
                    })
            );

        });

    }
}

class User {

    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User name(final String value) {
        this.name = value;
        return this;
    }

    public User age(final int value) {
        this.age = value;
        return this;
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
