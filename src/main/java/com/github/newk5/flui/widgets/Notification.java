package com.github.newk5.flui.widgets;

import com.github.newk5.flui.Alignment;
import com.github.newk5.flui.Application;
import com.github.newk5.flui.Direction;
import com.github.newk5.flui.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.ice1000.jimgui.JImGui;
import org.ice1000.jimgui.JImStr;
import org.ice1000.jimgui.JImStyleColors;
import org.ice1000.jimgui.JImStyleVars;
import org.ice1000.jimgui.JImVec4;
import org.ice1000.jimgui.flag.JImWindowFlags;
import vlsi.utils.CompactHashMap;

public class Notification extends SizedWidget {

    private static long counter = 0;
    private static CopyOnWriteArrayList<Widget> instances = new CopyOnWriteArrayList<>();
    private static CompactHashMap<String, Long> idIndex = new CompactHashMap<String, Long>();

    private JImStr title = new JImStr("");
    //background color
    private Color color;

    private int flags;
    private boolean moveable;

    private boolean showTitlebar;
    private boolean noBackground;

    private boolean appliedSizeOnce;
    protected static float globalXPadding = -1;
    protected static float globalYPadding = -1;

    private BiConsumer<Float, Float> onResize;
    int iterations = 1;
    private Button closeBtn;
    private Label detailLbl;
    private Label titleLbl;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public static Alignment ALIGN;

    private int timeout;

    public Notification(String id) {
        super(id);
        init();
    }

    @Override
    protected void init() {
        counter++;
        this.index(counter);
        idIndex.put(id, counter);
        instances.add(this);
        title = new JImStr(id);
        showTitlebar = false;
        moveable = false;
        hidden(true);

        width = 300;
        height = 100;
        if (ALIGN == null) {
            ALIGN = Alignment.TOP_RIGHT;
        }

        closeBtn = new Button(id + "::CloseBtn").text("X").align(Alignment.TOP_RIGHT).rounding(10).move(new Direction().left("2%").down("5%")).onClick((btn) -> {
            this.hide();
        });
        detailLbl = new Label(id + "::Detail").text("").wrap(true).align(Alignment.CENTER);
        titleLbl = new Label(id + "::Title").wrap(true).text("").align(Alignment.TOP_LEFT).move(new Direction().right("2%").down("5%"));

        align(ALIGN);
        children(titleLbl,
                closeBtn, detailLbl
        );
        applyAlignment();
        this.buildFlags();
    }
    private float totalHeight = 0f;

    private float getTotalHeight() {
        totalHeight = 0f;
        instances.stream().filter(w -> !w.id.equals(id)).forEach(w -> {
            Notification n = (Notification) w;
            totalHeight += n.getHeight();
            //System.out.println(f);

        });
        return totalHeight;
    }

    @Override
    protected void freeColors() {
        super.freeColor(color);
        closeBtn.freeColors();
        detailLbl.freeColors();
        titleLbl.freeColors();
    }

    public void delete() {
        hide();
        UI.runLater(() -> {
            freeColors();
            if (deleteFlag) {
                idIndex.remove(id);
                instances.remove(this);

                children.forEach(child -> {
                    ((SizedWidget) child).deleteFlag = true;
                });

                SizedWidget sw = super.getParent();
                if (sw != null) {
                    sw.deleteChild(this);
                }
            }
            deleteFlag = true;

        });

    }

    public Notification buttonColor(Color c) {
        this.closeBtn.color(c);
        return this;
    }

    public Notification timeout(int ms) {
        this.timeout = ms;
        return this;
    }

    public Notification titleColor(Color c) {
        this.titleLbl.color(c);
        return this;
    }

    public Notification textFont(String f) {
        this.detailLbl.font(f);
        return this;
    }

    public Notification titleFont(String f) {
        this.titleLbl.font(f);
        return this;
    }

    public Notification textColor(Color c) {
        this.detailLbl.color(c);
        return this;
    }

    public Notification buttonColor(Color c, boolean generateNeighbouringColors) {
        this.closeBtn.color(c, generateNeighbouringColors);
        return this;
    }

    protected static void reApplyAlignmenToAll() {
        instances.forEach(w -> {
            Notification n = (Notification) w;
            n.setAlignment(n.getAlign());
        });
    }

    public Notification text(String t) {
        this.detailLbl.text(t);


        return this;
    }

    public Notification title(String t) {
        this.titleLbl.text(t);


        return this;
    }

    public static Notification withID(String id) {
        Widget w = getWidget(idIndex.get(id), instances);
        if (w == null) {
            return null;

        }
        return (Notification) w;
    }

    public Notification font(String font) {
        super.font = font;
        super.fontObj = Application.fonts.get(font);
        return this;
    }

    public String getFont() {
        return super.font;
    }

    public Notification move(Direction d) {
        super.move(d);

        return this;
    }

    @Override
    public String toString() {
        return "Notification{ id= " + id + " }";
    }

    private void applyMove(JImGui imgui) {
        Direction move = super.getMove();
        if (move != null && applyMove) {
            applyMove = false;
            if (move.getLeft() > 0) {
                super.posX = super.getPosX() - move.getLeft();
                imgui.setNextWindowPos(posX, super.getPosY());
            } else if (move.getLeftRelative() > 0) {
                if (getParent() == null) {
                    super.posX = super.getPosX() - (UI.windowWidth * move.getLeftRelative());
                    imgui.setNextWindowPos(posX, super.getPosY());

                } else {
                    super.posX = super.getPosX() - (getParent().getWidth() * move.getLeftRelative());
                    imgui.setNextWindowPos(posX, super.getPosY());
                }
            }
            if (move.getRight() > 0) {
                super.posX = imgui.getCursorPosX() + move.getRight();
                imgui.setNextWindowPos(posX, super.getPosY());
            } else if (move.getRightRelative() > 0) {
                if (getParent() == null) {
                    super.posX = super.getPosX() + (UI.windowWidth * move.getRightRelative());
                    imgui.setNextWindowPos(posX, super.getPosY());
                } else {
                    super.posX = super.getPosX() + (getParent().getWidth() * move.getRightRelative());
                    imgui.setNextWindowPos(posX, super.getPosY());
                }
            }

            if (move.getUp() > 0) {
                super.posY = super.getPosY() - move.getUp();
                imgui.setNextWindowPos(super.getPosX(), posY);
            } else if (move.getUpRelative() > 0) {
                if (getParent() == null) {
                    super.posY = super.getPosY() - (UI.windowHeight * move.getUpRelative());
                    imgui.setNextWindowPos(super.getPosX(), posY);
                } else {
                    super.posY = super.getPosY() - (getParent().getHeight() * move.getUpRelative());
                    imgui.setNextWindowPos(super.getPosX(), posY);
                }
            }

            if (move.getDown() > 0) {
                super.posY = super.getPosY() + move.getDown();
                imgui.setNextWindowPos(super.getPosX(), posY);
            } else if (move.getDownRelative() > 0) {
                if (getParent() == null) {
                    super.posY = super.getPosY() + (UI.windowHeight * move.getDownRelative());
                    imgui.setNextWindowPos(super.getPosX(), posY);
                } else {
                    super.posY = super.getPosY() + (getParent().getHeight() * move.getDownRelative());
                    imgui.setNextWindowPos(super.getPosX(), posY);
                }
            }

        }
    }

    private void buildFlags() {
        flags = 0;
        flags |= JImWindowFlags.NoSavedSettings;

        flags |= JImWindowFlags.NoMove;

        flags |= JImWindowFlags.NoResize;

        flags |= JImWindowFlags.NoCollapse;

        if (!showTitlebar) {
            flags |= JImWindowFlags.NoTitleBar;
        }
        if (noBackground) {
            flags |= JImWindowFlags.NoBackground;
        }

    }

    @Override
    public void render(JImGui imgui) {
        if (!super.isHidden()) {
            if (font != null) {
                imgui.pushFont(Application.fonts.get(font).getJimFont());
            }
            imgui.pushID(numId);

            imgui.setNextWindowPos(super.getPosX(), super.getPosY());

            if (color != null) {

                imgui.pushStyleColor(JImStyleColors.WindowBg, color.asVec4());
            }

            imgui.setNextWindowSize(super.getWidth(), super.getHeight());
            imgui.pushStyleVar(JImStyleVars.WindowBorderSize, 2);
            imgui.getStyle().setWindowRounding(4);
            imgui.begin(title, flags);

            this.applyMove(imgui);

            if (color != null) {
                imgui.popStyleColor();
            }
            for (Widget w : children) {
                w.render(imgui);
            }

            if (reapplyAlign && super.getAlign() != null) {
                reapplyAlign = false;
                setAlignment(super.getAlign());
            }

            imgui.end();

            imgui.popStyleVar();

            imgui.popID();
            firstRenderLoop = false;
            if (font != null) {
                imgui.popFont();
            }
            if (deleteFlag) {
                this.delete();
            }
        }

    }

    protected void postRender(JImGui imgui) {
        if (super.reapplyAlign && super.getAlign() != null) {
            super.reapplyAlign = false;
            setAlignment(super.getAlign());
        }
    }

    public float getWidth() {
        return super.getWidth();
    }

    public float getHeight() {
        return super.getHeight();
    }

    public Notification position(float x, float y) {
        this.position(x, y);
        return this;
    }

    public Notification alpha(final float alpha) {
        super.alpha(alpha);
        return this;
    }

    public float getAlpha() {
        return super.getAlpha();
    }

    private Notification align(Alignment a) {

        if (null != a) {
            applyMove = true;
            switch (a) {
                case TOP_LEFT:
                    this.move(new Direction().right(10).down(15));
                    break;
                case TOP_CENTER:
                    this.move(new Direction().down(15));
                    break;
                case TOP_RIGHT:
                    this.move(new Direction().left(10).down(15));
                    break;
                case MID_LEFT:
                    this.move(new Direction().right(15));
                    break;
                case CENTER:

                    break;
                case MID_RIGHT:
                    this.move(new Direction().left(10));
                    break;
                case BOTTOM_LEFT:
                    this.move(new Direction().right(10).up(45));

                    break;
                case BOTTOM_CENTER:
                    this.move(new Direction().up(45));

                    break;
                case BOTTOM_RIGHT:
                    this.move(new Direction().left(10).up(45));
                    break;
                case CENTER_H:

                    break;
                case CENTER_V:

                    break;
                default:
                    break;
            }
        }

        return (Notification) super.setAlignment(a);
    }

    public Notification children(Widget... widgets) {
        for (Widget w : widgets) {

            add(w);
        }
        return this;
    }

    @Override
    public void add(Widget w) {
        w.parent(this);
        if (w instanceof SizedWidget) {
            SizedWidget sw = ((SizedWidget) w);
            sw.applyRelativeSize();
            sw.applyAlignment();

        }
        this.children.add(w);
    }

    @Override
    protected void addAtIndex(Widget w, int idx) {
        w.parent(this);
        if (w instanceof SizedWidget) {
            SizedWidget sw = ((SizedWidget) w);
            sw.applyRelativeSize();
            sw.applyAlignment();
        }
        this.children.add(idx, w);
    }

    public Notification width(final float value) {
        super.width(value);
        return this;
    }

    public Notification height(final float value) {
        super.height(value);
        return this;
    }

    public Notification width(final String widthPercent) {
        super.width(widthPercent);
        return this;
    }

    public Notification height(final String heightPercent) {
        super.height(heightPercent);
        return this;
    }

    public Notification show() {
        super.hidden(false);

        instances.stream().filter(w -> !w.id.equals(id)).forEach(w -> {
            Notification n = (Notification) w;
            if (n.getMove() != null && !n.isHidden()) {
                if (ALIGN == Alignment.TOP_CENTER || ALIGN == Alignment.TOP_LEFT || ALIGN == Alignment.TOP_RIGHT) {
                    n.getMove().down(n.getMove().getDown() + n.height + 15);
                } else if (ALIGN == Alignment.BOTTOM_CENTER || ALIGN == Alignment.BOTTOM_LEFT || ALIGN == Alignment.BOTTOM_RIGHT) {
                    n.getMove().up(n.getMove().getUp() + n.height + 15);
                }
                n.applyMove = true;
            }

        });
        if (timeout > 0) {

            executor.schedule(() -> {
                if (!isHidden()) {
                    hidden(true);
                }
            }, timeout, TimeUnit.MILLISECONDS);

        }

        return this;
    }

    public Notification hide() {
        super.hidden(true);

        instances.stream()
                .filter(w -> !w.id.equals(id))
                .filter(w -> w.numId < numId)
                .forEach(w -> {

                    Notification n = (Notification) w;
                    if (n.getMove() != null && !n.isHidden()) {
                        if (ALIGN == Alignment.TOP_CENTER || ALIGN == Alignment.TOP_LEFT || ALIGN == Alignment.TOP_RIGHT) {
                            n.getMove().down(n.getMove().getDown() - (n.height + 15));

                        } else if (ALIGN == Alignment.BOTTOM_CENTER || ALIGN == Alignment.BOTTOM_LEFT || ALIGN == Alignment.BOTTOM_RIGHT) {
                            n.getMove().down(n.getMove().getDown() + (n.height - 15));
                        }
                        n.reapplyAlign = true;
                        n.applyMove = true;
                    }

                });

        return this;
    }

    public Notification color(final Color value) {
        this.color = value;
        return this;
    }

    public List<Widget> getChildren() {
        return children;
    }

    public Color getColor() {
        return color;
    }

    public Notification sizeAndRealign(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }

    public Notification size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Notification noBackground(final boolean value) {
        this.noBackground = value;
        this.buildFlags();
        return this;
    }

}
