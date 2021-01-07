
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.widgets.Button;
import com.github.newk5.flui.widgets.Canvas;
import com.github.newk5.flui.widgets.Combobox;
import com.github.newk5.flui.widgets.InputText;
import com.github.newk5.flui.widgets.Label;
import com.github.newk5.flui.widgets.Menu;
import com.github.newk5.flui.widgets.MenuOption;
import com.github.newk5.flui.widgets.Popup;
import com.github.newk5.flui.widgets.RadioGroup;
import com.github.newk5.flui.widgets.SliderFloat;
import com.github.newk5.flui.widgets.Tab;
import com.github.newk5.flui.widgets.Tabview;
import com.github.newk5.flui.widgets.Topbar;
import com.github.newk5.flui.widgets.UI;
import com.github.newk5.flui.widgets.Window;

public class FilledWindowWithCanvas {

    public static void main(String[] args) {
        Application app = new Application().title("test").height(500).width(1200);
        UI.render(app, () -> {
            new Topbar("top").menus(
                    new Menu("Hello").options(new MenuOption("test")).options(new MenuOption("test1").options(new MenuOption("test2"))),
                    new Menu("abc")
            );

            new Window("w").fill().children(
                    new Canvas("c").width(500).border(false).children(
                            new Tabview("tabs").move(new Direction().down(60)).tabs(
                                    new Tab("tab").title("Title1")
                                            .children(
                                                    new Button("btn")
                                                            .text("OK")
                                                            .onClick((btn) -> {
                                                               
                                                                Popup.withID("op").open();
                                                              
                                                            }),
                                                    new Label("a").text("aaaaaaa"),
                                                    new RadioGroup("radio")
                                                            .labels("option1", "option2")
                                                            .onChange((group) -> {

                                                                group.add("option" + (group.getLabels().size() + 1));
                                                            }),
                                                    new SliderFloat("sl").min(0).max(500).value(20),
                                                    new Combobox("combo")
                                                            .items("item1", "item2")
                                                            .move(new Direction().down(10f))
                                            )
                            )
                    )
            );

            new Popup("op").title("Popup").height(250).width(400).align(Alignment.CENTER).children(
                    new Label("lbl").text("Hello").align(Alignment.CENTER),
                    new Button("btn2").align(Alignment.BOTTOM_RIGHT).text("close").onClick((btn) -> {
                        Popup.withID("op").close();

                    })
            );

        });

    }
}
