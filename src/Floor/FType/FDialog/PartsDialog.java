package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.entities.part.*;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectDialogUtils.*;

public class PartsDialog extends BaseDialog {
    protected Table listOn;
    protected Seq<DrawPart> parts = new Seq<>();
    protected Cons<Seq<DrawPart>> apply;

    protected static String dia = "part";

    public PartsDialog(String title, Cons<Seq<DrawPart>> apply) {
        super(title);

        this.apply = apply;
        shown(this::rebuild);
        buttons.button("@back", Icon.left, this::hide);
        buttons.button(Core.bundle.get("@add"), Icon.add, () -> {
            parts.add(new ShapePart());
            rebuildList();
        });
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            apply.get(parts);
            hide();
        });
    }

    public PartsDialog(String title, Cons<Seq<DrawPart>> apply, Seq<DrawPart> parts) {
        this(title, apply);
        setParts(parts);
    }

    public void setParts(Seq<DrawPart> parts) {
        this.parts = parts == null ? new Seq<>() : parts;
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> listOn = t);
        rebuildList();
    }

    public void rebuildList() {
        listOn.clear();
        for (int i = 0; i < parts.size; i++) {
            int finI = i;
            DrawPart part = parts.get(finI);
            String type = findType(part);
            listOn.row();
            listOn.table(t -> rebuildPart(t, finI, type, part)).grow();
        }
    }

    public void rebuildPart(Table t, int index, String type, DrawPart part) {
        t.clear();
        t.label(() -> Core.bundle.get("dialog.part." + type)).pad(5);
        t.button(b -> {
            b.image(Icon.pencilSmall);

            b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                tb.top();
                tb.button(Core.bundle.get("dialog.part.shape"), () -> {
                    if (type.equals("shape")) {
                        hide.run();
                        return;
                    }
                    ShapePart shapePart = new ShapePart();
                    shapePart.turretShading = part.turretShading;
                    shapePart.under = part.under;
                    shapePart.weaponIndex = part.weaponIndex;
                    shapePart.recoilIndex = part.recoilIndex;
                    parts.set(index, shapePart);
                    rebuildPart(t, index, "shape", part);
                    hide.run();
                });
                tb.row();
                tb.button(Core.bundle.get("dialog.part.hover"), () -> {
                    if (type.equals("hover")) {
                        hide.run();
                        return;
                    }
                    HoverPart hoverPart = new HoverPart();
                    hoverPart.turretShading = part.turretShading;
                    hoverPart.under = part.under;
                    hoverPart.weaponIndex = part.weaponIndex;
                    hoverPart.recoilIndex = part.recoilIndex;
                    parts.set(index, hoverPart);
                    rebuildPart(t, index, "hover", part);
                    hide.run();
                });
                tb.row();
                tb.button(Core.bundle.get("dialog.part.halo"), () -> {
                    if (type.equals("halo")) {
                        hide.run();
                        return;
                    }
                    HaloPart haloPart = new HaloPart();
                    haloPart.turretShading = part.turretShading;
                    haloPart.under = part.under;
                    haloPart.weaponIndex = part.weaponIndex;
                    haloPart.recoilIndex = part.recoilIndex;
                    parts.set(index, haloPart);
                    rebuildPart(t, index, "halo", part);
                    hide.run();
                });
                tb.row();
                tb.button(Core.bundle.get("dialog.part.flare"), () -> {
                    if (type.equals("flare")) {
                        hide.run();
                        return;
                    }
                    FlarePart flarePart = new FlarePart();
                    flarePart.turretShading = part.turretShading;
                    flarePart.under = part.under;
                    flarePart.weaponIndex = part.weaponIndex;
                    flarePart.recoilIndex = part.recoilIndex;
                    parts.set(index, flarePart);
                    rebuildPart(t, index, "flare", part);
                    hide.run();
                });
            }));
        }, () -> {
        });
        t.button(Icon.trash, () -> {
            parts.remove(index);
            rebuildList();
        }).pad(5);
        t.row();
        t.table(b -> rebuildPartBase(b, part));
        t.table(y -> rebuildPartType(y, type, part));
    }

    public void rebuildPartBase(Table t, DrawPart part) {
        t.clear();
        createMessageLine(t, dia, "default");
        t.row();
        createBooleanDialog(t, dia, "turretShading", part.turretShading,
                b -> part.turretShading = b, () -> rebuildPartBase(t, part));
        createBooleanDialog(t, dia, "under", part.under,
                b -> part.under = b, () -> rebuildPartBase(t, part));
        t.row();
        createNumberDialog(t, dia, "weaponIndex", part.weaponIndex,
                f -> part.weaponIndex = (int) (f + 0), () -> rebuildPartBase(t, part));
        createNumberDialog(t, dia, "recoilIndex", part.recoilIndex,
                f -> part.recoilIndex = (int) (f + 0), () -> rebuildPartBase(t, part));
    }

    public void rebuildPartType(Table t, String type, DrawPart part) {
        Runnable reb = () -> rebuildPartType(t, type, part);
        t.clear();
        createMessageLine(t, dia, "type");
        t.row();
        switch (type) {
            case "shape": {
                ShapePart shapePart = (ShapePart) part;
                createBooleanDialog(t, dia, "circle", shapePart.circle,
                        b -> shapePart.circle = b, reb);
                createBooleanDialog(t, dia, "hollow", shapePart.hollow,
                        b -> shapePart.hollow = b, reb);
                createBooleanDialog(t, dia, "mirror", shapePart.mirror,
                        b -> shapePart.mirror = b, reb);
                t.row();
                createNumberDialog(t, dia, "x", shapePart.x,
                        f -> shapePart.x = f, reb);
                createNumberDialog(t, dia, "y", shapePart.y,
                        f -> shapePart.y = f, reb);
                createNumberDialog(t, dia, "rotation", shapePart.rotation,
                        f -> shapePart.rotation = f, reb);
                t.row();
                createNumberDialog(t, dia, "moveX", shapePart.moveX,
                        f -> shapePart.moveX = f, reb);
                createNumberDialog(t, dia, "moveY", shapePart.moveY,
                        f -> shapePart.moveY = f, reb);
                createNumberDialog(t, dia, "moveRot", shapePart.moveRot,
                        f -> shapePart.moveRot = f, reb);
                t.row();
                createNumberDialog(t, dia, "sides", shapePart.sides,
                        f -> shapePart.sides = (int) (f + 0), reb);
                createNumberDialog(t, dia, "radius", shapePart.radius,
                        f -> shapePart.radius = f, reb);
                createNumberDialog(t, dia, "radiusTo", shapePart.radiusTo,
                        f -> shapePart.radiusTo = f, reb);
                t.row();
                createNumberDialog(t, dia, "stroke", shapePart.stroke,
                        f -> shapePart.stroke = f, reb);
                createNumberDialog(t, dia, "strokeTo", shapePart.strokeTo,
                        f -> shapePart.strokeTo = f, reb);
                createNumberDialog(t, dia, "rotateSpeed", shapePart.rotateSpeed,
                        f -> shapePart.rotateSpeed = f, reb);
                t.row();
                createNumberDialog(t, dia, "layer", shapePart.layer,
                        f -> shapePart.layer = f, reb);
                createNumberDialog(t, dia, "layerOffset", shapePart.layerOffset,
                        f -> shapePart.layerOffset = f, reb);
                createPartProgressSelect(t, dia, "progress", p -> shapePart.progress = p);
                t.row();
                createColorDialog(t, dia, "color", shapePart.color,
                        c -> shapePart.color = c, reb);
                createColorDialog(t, dia, "colorTo", shapePart.colorTo,
                        c -> shapePart.colorTo = c, reb);
            }
            case "hover": {
                HoverPart hoverPart = (HoverPart) part;
                createNumberDialog(t, dia, "x", hoverPart.x,
                        f -> hoverPart.x = f, reb);
                createNumberDialog(t, dia, "y", hoverPart.y,
                        f -> hoverPart.y = f, reb);
                createNumberDialog(t, dia, "rotation", hoverPart.rotation,
                        f -> hoverPart.rotation = f, reb);
                t.row();
                createNumberDialog(t, dia, "phase", hoverPart.phase,
                        f -> hoverPart.phase = f, reb);
                createNumberDialog(t, dia, "stroke", hoverPart.stroke,
                        f -> hoverPart.stroke = f, reb);
                createNumberDialog(t, dia, "minStroke", hoverPart.minStroke,
                        f -> hoverPart.minStroke = f, reb);
                t.row();
                createNumberDialog(t, dia, "radius", hoverPart.radius,
                        f -> hoverPart.radius = f, reb);
                createNumberDialog(t, dia, "circles", hoverPart.circles,
                        f -> hoverPart.circles = (int) (f + 0), reb);
                createNumberDialog(t, dia, "sides", hoverPart.sides,
                        f -> hoverPart.sides = (int) (f + 0), reb);
                t.row();
                createBooleanDialog(t, dia, "mirror", hoverPart.mirror,
                        b -> hoverPart.mirror = b, reb);
                createNumberDialog(t, dia, "layer", hoverPart.layer,
                        f -> hoverPart.layer = f, reb);
                createNumberDialog(t, dia, "layerOffset", hoverPart.layerOffset,
                        f -> hoverPart.layerOffset = f, reb);
                t.row();
                createColorDialog(t, dia, "color", hoverPart.color,
                        c -> hoverPart.color = c, reb);
            }
            case "halo": {
                HaloPart haloPart = (HaloPart) part;
                createBooleanDialog(t, dia, "tri", haloPart.tri,
                        b -> haloPart.tri = b, reb);
                createBooleanDialog(t, dia, "hollow", haloPart.hollow,
                        b -> haloPart.hollow = b, reb);
                createBooleanDialog(t, dia, "mirror", haloPart.mirror,
                        b -> haloPart.mirror = b, reb);
                t.row();
                createNumberDialog(t, dia, "radius", haloPart.radius,
                        f -> haloPart.radius = f, reb);
                createNumberDialog(t, dia, "sides", haloPart.sides,
                        f -> haloPart.sides = (int) (f + 0), reb);
                createNumberDialog(t, dia, "radiusTo", haloPart.radiusTo,
                        f -> haloPart.radiusTo = f, reb);
                t.row();
                createNumberDialog(t, dia, "shapes", haloPart.shapes,
                        f -> haloPart.shapes = (int) (f + 0), reb);
                createNumberDialog(t, dia, "stroke", haloPart.stroke,
                        f -> haloPart.stroke = f, reb);
                createNumberDialog(t, dia, "strokeTo", haloPart.strokeTo,
                        f -> haloPart.strokeTo = f, reb);
                t.row();
                createNumberDialog(t, dia, "x", haloPart.x,
                        f -> haloPart.x = f, reb);
                createNumberDialog(t, dia, "y", haloPart.y,
                        f -> haloPart.y = f, reb);
                createNumberDialog(t, dia, "shapeRotation", haloPart.shapeRotation,
                        f -> haloPart.shapeRotation = f, reb);
                t.row();
                createNumberDialog(t, dia, "moveX", haloPart.moveX,
                        f -> haloPart.moveX = f, reb);
                createNumberDialog(t, dia, "moveY", haloPart.moveY,
                        f -> haloPart.moveY = f, reb);
                createNumberDialog(t, dia, "shapeMoveRot", haloPart.shapeMoveRot,
                        f -> haloPart.shapeMoveRot = f, reb);
                t.row();
                createNumberDialog(t, dia, "haloRotateSpeed", haloPart.haloRotateSpeed,
                        f -> haloPart.haloRotateSpeed = f, reb);
                createNumberDialog(t, dia, "haloRotation", haloPart.haloRotation,
                        f -> haloPart.haloRotation = f, reb);
                createNumberDialog(t, dia, "rotateSpeed", haloPart.rotateSpeed,
                        f -> haloPart.rotateSpeed = f, reb);
                t.row();
                createNumberDialog(t, dia, "triLength", haloPart.triLength,
                        f -> haloPart.triLength = f, reb);
                createNumberDialog(t, dia, "triLengthTo", haloPart.triLengthTo,
                        f -> haloPart.triLengthTo = f, reb);
                createNumberDialog(t, dia, "haloRadius", haloPart.haloRadius,
                        f -> haloPart.haloRadius = f, reb);
                t.row();
                createNumberDialog(t, dia, "haloRadiusTo", haloPart.haloRadiusTo,
                        f -> haloPart.haloRadiusTo = f, reb);
                createNumberDialog(t, dia, "layer", haloPart.layer,
                        f -> haloPart.layer = f, reb);
                createNumberDialog(t, dia, "layerOffset", haloPart.layerOffset,
                        f -> haloPart.layerOffset = f, reb);
                t.row();
                createPartProgressSelect(t, dia, "progress", p -> haloPart.progress = p);
                createColorDialog(t, dia, "color", haloPart.color,
                        c -> haloPart.color = c, reb);
                createColorDialog(t, dia, "colorTo", haloPart.colorTo,
                        c -> haloPart.colorTo = c, reb);
            }
            case "flare": {
                FlarePart flarePart = (FlarePart) part;
                createNumberDialog(t, dia, "sides", flarePart.sides,
                        f -> flarePart.sides = (int) (f + 0), reb);
                createNumberDialog(t, dia, "radius", flarePart.radius,
                        f -> flarePart.radius = f, reb);
                createNumberDialog(t, dia, "radiusTo", flarePart.radiusTo,
                        f -> flarePart.radiusTo = f, reb);
                t.row();
                createNumberDialog(t, dia, "stroke", flarePart.stroke,
                        f -> flarePart.stroke = f, reb);
                createNumberDialog(t, dia, "innerScl", flarePart.innerScl,
                        f -> flarePart.innerScl = f, reb);
                createNumberDialog(t, dia, "innerRadScl", flarePart.innerRadScl,
                        f -> flarePart.innerRadScl = f, reb);
                t.row();
                createNumberDialog(t, dia, "x", flarePart.x,
                        f -> flarePart.x = f, reb);
                createNumberDialog(t, dia, "y", flarePart.y,
                        f -> flarePart.y = f, reb);
                createNumberDialog(t, dia, "rotation", flarePart.rotation,
                        f -> flarePart.rotation = f, reb);
                t.row();
                createNumberDialog(t, dia, "rotMove", flarePart.rotMove,
                        f -> flarePart.rotMove = f, reb);
                createNumberDialog(t, dia, "spinSpeed", flarePart.spinSpeed,
                        f -> flarePart.spinSpeed = f, reb);
                createNumberDialog(t, dia, "layer", flarePart.layer,
                        f -> flarePart.layer = f, reb);
                t.row();
                createBooleanDialog(t, dia, "followRotation", flarePart.followRotation,
                        b -> flarePart.followRotation = b, reb);
                createPartProgressSelect(t, dia, "progress", p -> flarePart.progress = p);
                createColorDialog(t, dia, "color1", flarePart.color1,
                        c -> flarePart.color1 = c, reb);
                t.row();
                createColorDialog(t, dia, "color2", flarePart.color2,
                        c -> flarePart.color2 = c, reb);
            }
        }
    }

    public String findType(DrawPart part) {
        if (part instanceof ShapePart) {
            return "shape";
        } else if (part instanceof HoverPart) {
            return "hover";
        } else if (part instanceof HaloPart) {
            return "halo";
        } else if (part instanceof FlarePart) {
            return "flare";
        }
        return "shape";
    }
}
