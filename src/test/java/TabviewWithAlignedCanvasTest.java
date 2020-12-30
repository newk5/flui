
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.widgets.Canvas;
import com.github.newk5.flui.widgets.Tab;
import com.github.newk5.flui.widgets.Tabview;
import com.github.newk5.flui.widgets.UI;
import com.github.newk5.flui.widgets.Window;

public class TabviewWithAlignedCanvasTest {

    public static void main(String[] args) {
        Application app = new Application()
                .title("Test")
                .height(700)
                .width(1200);

        UI.render(app, () -> {
            new Window("w")
                    .showTitlebar(true)
                    .width("30%").height("70%").title("HELLO")
                    .align(Alignment.MID_CENTER).resizable(true)
                    .children(
                            new Tabview("tabs").tabs(
                                    new Tab("tab").title("Title1")
                                            .children(
                                                    new Canvas("c")
                                                            .width("50%").height("50%")
                                                            .align(Alignment.TOP_LEFT)
                                                            .color(new Color(100, 0, 0, 255)),
                                                    new Canvas("c2")
                                                            .width("50%").height("50%")
                                                            .align(Alignment.BOTTOM_LEFT)
                                                            .color(new Color(0, 100, 0, 255)),
                                                    new Canvas("c3")
                                                            .width("50%").height("50%")
                                                            .align(Alignment.TOP_RIGHT)
                                                            .color(new Color(0, 0, 100, 255)),
                                                    new Canvas("c4")
                                                            .width("50%").height("50%")
                                                            .align(Alignment.BOTTOM_RIGHT)
                                                            .color(new Color(0, 100, 100, 255))
                                            ),
                                    new Tab("tab2").title("Title2").children(
                                            new Canvas("cc")
                                                    .width("50%").height("50%")
                                                    .align(Alignment.BOTTOM_RIGHT)
                                                    .color(new Color(100, 150, 0, 255))
                                    )
                            )
                    );
        });

    }

}
