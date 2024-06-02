package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.entities.part.*;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class PartsDialog extends BaseDialog {
    protected ShapePart shapePart;
    protected HoverPart hoverPart;
    protected HaloPart haloPart;
    protected FlarePart flarePart;
    protected Table baseOn, typeOn;
    protected Seq<DrawPart> parts = new Seq<>();
    protected Cons<Seq<DrawPart>> apply;
    protected int index = 0;
    protected static String dia = "part";

    public PartsDialog(String title, Cons<Seq<DrawPart>> apply) {
        super(title);

        this.apply = apply;
        shown(this::rebuild);
        buttons.button("@back", Icon.left, this::hide);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            apply.get(parts);
            hide();
        });
    }

    public void rebuild() {
        cont.pane(t -> baseOn = t);
        cont.pane(t -> typeOn = t);
        rebuildBase();
        rebuildType();
    }

    public void rebuildBase() {

    }

    public void rebuildType() {

    }
}
