
import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Color;
import com.github.newk5.flui.widgets.Canvas;
import com.github.newk5.flui.widgets.Label;
import com.github.newk5.flui.widgets.UI;
import com.github.newk5.flui.widgets.Window;

public class WindowWithAlignedCanvasTest {

    public static void main(String[] args) {
        UI.render(() -> {
            new Window("w")
                    .width("100%").height("100%")
                    .children(
                            new Canvas("canv2")
                                    .width("50%").height("50%")
                                    .color(new Color(50, 20, 15, 255))
                                    .align(Alignment.TOP_LEFT)
                                    .children(
                                            new Label("text")
                                                    .text("label")
                                                    .align(Alignment.CENTER)
                                    ),
                            new Canvas("canv3")
                                    .width("50%").height("50%")
                                    .color(new Color(15, 20, 100, 255))
                                    .align(Alignment.TOP_RIGHT),
                            new Canvas("canv4")
                                    .width("50%").height("50%")
                                    .color(new Color(15, 100, 100, 255))
                                    .align(Alignment.BOTTOM_LEFT),
                            new Canvas("canv5")
                                    .width("50%").height("50%")
                                    .color(new Color(150, 20, 100, 255))
                                    .align(Alignment.BOTTOM_RIGHT)
                    );
        });
    }

}
