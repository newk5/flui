package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Application;
import com.github.newk5.flui.events.WindowResizeEvent;
import com.github.newk5.flui.util.SerializableConsumer;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.LongAdder;

import org.ice1000.jimgui.JImFontAtlas;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.util.JImGuiUtil;
import org.ice1000.jimgui.util.JniLoader;

public class UI {

    public static float windowWidth;
    public static float windowHeight;

    private static float newWindowHeight = -1;
    private static float newWindowWidth = -1;

    private static CopyOnWriteArrayList<Widget> components = new CopyOnWriteArrayList<>();

    private static Queue<Runnable> imguiThread = new ConcurrentLinkedQueue<>();

    private static boolean async;
    private static WindowResizeEvent resizeEvent;
    protected static LongAdder globalCounter = new LongAdder();
    protected static float tabBottomBorderOffset = 7.1f;
    private static Application app;
    protected static JImFontAtlas fonts;

    private static ConcurrentLinkedQueue<String> ids = new ConcurrentLinkedQueue<>();

    private static SerializableConsumer<WindowResizeEvent> resizeEventSerializableConsumer;
 
    private static boolean readyCalledFlag;

    private static void init(Application app, Runnable r) {
        JniLoader.load();
       // JImGuiUtil.cacheStringToBytes();
        try (JImGui jimgui = new JImGui(app.getWidth(), app.getHeight(), app.getTitle())) {

            UI.windowHeight = app.getHeight();
            UI.windowWidth = app.getWidth();

            resizeEvent = new WindowResizeEvent(windowWidth, windowHeight, windowWidth, windowHeight);

            app.setupTheme(jimgui);
            Window.globalXPadding = jimgui.getStyle().getWindowPaddingX();
            Window.globalYPadding = jimgui.getStyle().getWindowPaddingY();

            fonts = jimgui.getIO().getFonts();
            fonts.addDefaultFont();
            app.loadFonts(fonts);
            r.run();

            while (!jimgui.windowShouldClose()) {

                float newY = jimgui.getPlatformWindowSizeY();
                float newX = jimgui.getPlatformWindowSizeX();

                //window resize event
                if (newY != windowHeight || newX != windowWidth) {

                    resizeEvent.setOldHeight(windowHeight);
                    resizeEvent.setOldWidth(windowWidth);
                    resizeEvent.setNewHeight(newY);
                    resizeEvent.setNewWidth(newX);

                    UI.windowHeight = jimgui.getPlatformWindowSizeY();
                    UI.windowWidth = jimgui.getPlatformWindowSizeX();
                    Window.reApplyRelativeSize();
                    Popup.reApplyRelativeSize();
                    if (resizeEventSerializableConsumer != null) {

                        resizeEventSerializableConsumer.accept(resizeEvent);
                    }
                }
                //window size programatically changed
                if (newWindowHeight > -1 || newWindowWidth > -1) {

                    jimgui.setPlatformWindowSize(newWindowWidth, newWindowHeight);
                    newWindowHeight = -1;
                    newWindowWidth = -1;
                }
                jimgui.initNewFrame();

                process(jimgui);

                jimgui.render();
                if (!readyCalledFlag) {
                    if (app.getReady() != null) {
                        app.getReady().run();
                    }
                    readyCalledFlag = true;
                }
            }
        }
    }

    public static void render(Runnable r) {
        app = new Application("flui", 1200, 700);
        init(app, r);
    }

    public static void render(Application app, Runnable r) {
        init(app, r);
    }

    public static void renderAsync(Runnable r) {
        async = true;
        app = new Application("flui", 1200, 700);
        new Thread(() -> {
            init(app, r);
        }).start();

    }

    public static void renderAsync(Application app, Runnable r) {
        async = true;
        new Thread(() -> {
            init(app, r);
        }).start();

    }

    public static void setWindowSize(float w, float h) {
        if (async) {
            runLater(() -> {
                newWindowHeight = h;
                newWindowWidth = w;
            });
        } else {
            newWindowHeight = h;
            newWindowWidth = w;
        }
    }

    public static void setWindowWidth(float w) {
        if (async) {
            runLater(() -> {
                setWindowSize(w, windowHeight);
            });
        } else {
            setWindowSize(w, windowHeight);
        }
    }

    public static void setWindowHeight(float h) {
        if (async) {
            runLater(() -> {
                setWindowSize(windowWidth, h);
            });
        } else {
            setWindowSize(windowWidth, h);
        }
    }

    public static void onWindowResize(SerializableConsumer<WindowResizeEvent> e) {
        resizeEventSerializableConsumer = e;

    }

    protected static void addID(String id) {
        ids.add(id);
    }

    protected static boolean idExists(String id) {
        return (ids.contains(id));
    }

    protected static void add(Widget w) {
        if (idExists(w.id)) {
            System.err.println("ERROR: '" + w.id + "' has already been used as an identifier, ID's must be unique!");
            throw new RuntimeException("Duplicate ID : " + w.id);
        }
        addID(w.id);
        components.add(w);
    }

    public static void runLater(Runnable r) {
        imguiThread.add(r);
    }

    protected static void process(JImGui imgui) {

        for (int i = 0; i < components.size(); i++) {
            components.get(i).render(imgui);
        }
        Iterator<Runnable> it = imguiThread.iterator();
        while (it.hasNext()) {
            Runnable r = it.next();

            try {
                r.run();
            } catch (Exception e) {
            }
            it.remove();
        }
    }
}
