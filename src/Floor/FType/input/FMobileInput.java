package Floor.FType.input;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.input.Binding;
import mindustry.input.InputHandler;
import mindustry.input.MobileInput;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.lang.reflect.Field;

import static arc.Core.scene;
import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;
import static mindustry.input.PlaceMode.*;

public class FMobileInput extends MobileInput {
    public boolean zoomModel = true;
    Rect r1 = new Rect(), r2 = new Rect();

    @Override
    public void update() {
        if (logicCutscene && !renderer.isCutscene()) {
            Core.camera.position.lerpDelta(logicCamPan, logicCamSpeed);
        } else {
            logicCutsceneZoom = -1f;
        }

        commandBuildings.removeAll(b -> !b.isValid());

        if (!commandMode) {
            commandRect = false;
        }

        Field field;
        QuadTree<BuildPlan> playerPlanTree;
        try {
            field = InputHandler.class.getDeclaredField("playerPlanTree");
            field.setAccessible(true);
            playerPlanTree = (QuadTree<BuildPlan>) field.get(control.input);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        playerPlanTree.clear();
        player.unit().plans.each(playerPlanTree::insert);

        player.typing = ui.chatfrag.shown();

        if (player.dead()) {
            droppingItem = false;
        }

        if (player.isBuilder()) {
            player.unit().updateBuilding(isBuilding);
        }

        if (player.shooting && !wasShooting && player.unit().hasWeapons() && state.rules.unitAmmo && !player.team().rules().infiniteAmmo && player.unit().ammo <= 0) {
            player.unit().type.weapons.first().noAmmoSound.at(player.unit());
        }

        //you don't want selected blocks while locked, looks weird
        if (locked()) {
            block = null;

        }

        wasShooting = player.shooting;

        //only reset the controlled type and control a unit after the timer runs out
        //essentially, this means the client waits for ~1 second after controlling something before trying to control something else automatically
        if (!player.dead() && (recentRespawnTimer -= Time.delta / 70f) <= 0f && player.justSwitchFrom != player.unit()) {
            controlledType = player.unit().type;
        }

        if (controlledType != null && player.dead() && controlledType.playerControllable) {
            Unit unit = Units.closest(player.team(), player.x, player.y, u -> !u.isPlayer() && u.type == controlledType && !u.dead);

            if (unit != null) {
                //only trying controlling once a second to prevent packet spam
                if (!net.client() || controlInterval.get(0, 70f)) {
                    recentRespawnTimer = 1f;
                    Call.unitControl(player, unit);
                }
            }
        }

        boolean locked = locked();

        if (player.dead()) {
            mode = none;
            manualShooting = false;
            payloadTarget = null;
        }

        if (locked || block != null || scene.hasField() || hasSchem() || selectPlans.size > 0) {
            commandMode = false;
        }

        //validate commanding units
        selectedUnits.removeAll(u -> !u.isCommandable() || !u.isValid());

        if (!commandMode) {
            commandBuildings.clear();
            selectedUnits.clear();
        }

        //zoom camera
        if (zoomModel && !locked && Math.abs(Core.input.axisTap(Binding.zoom)) > 0 && !Core.input.keyDown(Binding.rotateplaced) && (Core.input.keyDown(Binding.diagonal_placement) || ((!player.isBuilder() || !isPlacing() || !block.rotate) && selectPlans.isEmpty()))) {
            renderer.scaleCamera(Core.input.axisTap(Binding.zoom));
        }

        if (!Core.settings.getBool("keyboard") && !locked) {
            float camSpeed = 6f;
            Core.camera.position.add(Tmp.v1.setZero().add(Core.input.axis(Binding.move_x), Core.input.axis(Binding.move_y)).nor().scl(Time.delta * camSpeed));
        }

        if (Core.settings.getBool("keyboard")) {
            if (Core.input.keyRelease(Binding.select)) {
                player.shooting = false;
            }

            if (player.shooting && !canShoot()) {
                player.shooting = false;
            }
        }

        if (!player.dead() && !state.isPaused() && !locked) {
            updateMovement(player.unit());
        }

        if (mode == none) {
            lineMode = false;
        }

        if (lineMode && mode == placing && block == null) {
            lineMode = false;
        }

        if (block != null && mode == none) {
            mode = placing;
        }

        if (block == null && mode == placing) {
            mode = none;
        }

        if (block != null) {
            schematicMode = false;
        }

        if (!schematicMode && (mode == schematicSelect || mode == rebuildSelect)) {
            mode = none;
        }

        if (!rebuildMode && mode == rebuildSelect) {
            mode = none;
        }

        if (mode == schematicSelect || mode == rebuildSelect) {
            lastLineX = rawTileX();
            lastLineY = rawTileY();
            autoPan();
        }

        if (lastBlock != block && mode == breaking && block != null) {
            mode = placing;
            lastBlock = block;
        }

        if (lineMode) {
            lineScale = Mathf.lerpDelta(lineScale, 1f, 0.1f);

            if (Core.input.isTouched(0)) {
                autoPan();
            }

            int lx = tileX(Core.input.mouseX()), ly = tileY(Core.input.mouseY());

            if ((lastLineX != lx || lastLineY != ly) && isPlacing()) {
                lastLineX = lx;
                lastLineY = ly;
                updateLine(lineStartX, lineStartY, lx, ly);
            }
        } else {
            linePlans.clear();
            lineScale = 0f;
        }

        for (int i = removals.size - 1; i >= 0; i--) {

            if (removals.get(i).animScale <= 0.0001f) {
                removals.remove(i);
                i--;
            }
        }

        if (player.shooting && (player.unit().activelyBuilding() || player.unit().mining())) {
            player.shooting = false;
        }
    }

    boolean showCancel() {
        return (player.unit().isBuilding() || block != null || mode == breaking || !selectPlans.isEmpty()) && !hasSchem();
    }

    boolean hasSchem() {
        return lastSchematic != null && !selectPlans.isEmpty();
    }

    int rawTileX() {
        return World.toTile(Core.input.mouseWorld().x);
    }

    int rawTileY() {
        return World.toTile(Core.input.mouseWorld().y);
    }

    int tileX(float cursorX) {
        Vec2 vec = Core.input.mouseWorld(cursorX, 0);
        if (selectedBlock()) {
            vec.sub(block.offset, block.offset);
        }
        return World.toTile(vec.x);
    }

    int tileY(float cursorY) {
        Vec2 vec = Core.input.mouseWorld(0, cursorY);
        if (selectedBlock()) {
            vec.sub(block.offset, block.offset);
        }
        return World.toTile(vec.y);
    }

    void checkTargets(float x, float y) {
        Unit unit = Units.closestEnemy(player.team(), x, y, 20f, u -> !u.dead);

        if (unit != null && player.unit().type.canAttack) {
            player.unit().mineTile = null;
            target = unit;
        } else {
            Building tile = world.buildWorld(x, y);

            if ((tile != null && player.team() != tile.team && (tile.team != Team.derelict || state.rules.coreCapture)) || (tile != null && player.unit().type.canHeal && tile.team == player.team() && tile.damaged())) {
                player.unit().mineTile = null;
                target = tile;
            }
        }
    }

    boolean hasPlan(Tile tile) {
        return getPlan(tile) != null;
    }

    boolean checkOverlapPlacement(int x, int y, Block block) {
        r2.setSize(block.size * tilesize);
        r2.setCenter(x * tilesize + block.offset, y * tilesize + block.offset);

        for (var plan : selectPlans) {
            Tile other = plan.tile();

            if (other == null || plan.breaking) continue;

            r1.setSize(plan.block.size * tilesize);
            r1.setCenter(other.worldx() + plan.block.offset, other.worldy() + plan.block.offset);

            if (r2.overlaps(r1)) {
                return true;
            }
        }

        for (var plan : player.unit().plans()) {
            Tile other = world.tile(plan.x, plan.y);

            if (other == null || plan.breaking) continue;

            r1.setSize(plan.block.size * tilesize);
            r1.setCenter(other.worldx() + plan.block.offset, other.worldy() + plan.block.offset);

            if (r2.overlaps(r1)) {
                return true;
            }
        }
        return false;
    }

    BuildPlan getPlan(Tile tile) {
        r2.setSize(tilesize);
        r2.setCenter(tile.worldx(), tile.worldy());

        for (var plan : selectPlans) {
            Tile other = plan.tile();

            if (other == null) continue;

            if (!plan.breaking) {
                r1.setSize(plan.block.size * tilesize);
                r1.setCenter(other.worldx() + plan.block.offset, other.worldy() + plan.block.offset);

            } else {
                r1.setSize(other.block().size * tilesize);
                r1.setCenter(other.worldx() + other.block().offset, other.worldy() + other.block().offset);
            }

            if (r2.overlaps(r1)) return plan;
        }
        return null;
    }

    void removePlan(BuildPlan plan) {
        selectPlans.remove(plan, true);
        if (!plan.breaking) {
            removals.add(plan);
        }
    }

    boolean isLinePlacing() {
        return mode == placing && lineMode && Mathf.dst(lineStartX * tilesize, lineStartY * tilesize, Core.input.mouseWorld().x, Core.input.mouseWorld().y) >= 3 * tilesize;
    }

    boolean isAreaBreaking() {
        return mode == breaking && lineMode && Mathf.dst(lineStartX * tilesize, lineStartY * tilesize, Core.input.mouseWorld().x, Core.input.mouseWorld().y) >= 2 * tilesize;
    }
}
