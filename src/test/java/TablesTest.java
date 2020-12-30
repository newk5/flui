
import com.github.newk5.flui.Application;
import com.github.newk5.flui.widgets.Column;
import com.github.newk5.flui.widgets.Table;
import com.github.newk5.flui.widgets.UI;
import com.github.newk5.flui.widgets.Window;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TablesTest {

    public static void main(String[] args) {
        Application app = new Application().title("test").height(500).width(1200);

        UI.render(app, () -> {

            new Window("w").fill().children(
                    new Table("tbl").columns(
                            new Column("Name").field("name"),
                            new Column("Age").field("age")
                    ).data(
                            Stream.of(
                                    new User("John", 19),
                                    new User("Steve", 29)
                            ).collect(Collectors.toList())
                    ).onSelect((o) -> {
                        User u = (User) o;
                        System.out.println(u.getName());
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

}
