package Floor.FAI;

import Floor.FEntities.FUnitType.ChainUnitType;
import Floor.FTools.LayAble;
import Floor.FTools.UnitChainAble;
import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.ai.Pathfinder;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.*;
import mindustry.world.Tile;

import static arc.math.Mathf.*;
import static mindustry.Vars.*;

public class ChainAI extends GroundAI {
    private Unit underUnit = null;
    private boolean upon;
    private float timer = 1500;
    private Healthc order = null;

    public void FindUnderUnit() {
        if (unit instanceof UnitChainAble uca) {
            Cons<Unit> cons = unit -> {
                if (underUnit != null) return;
                if (unit instanceof LayAble l && unit.team == this.unit.team && !unit.spawnedByCore && unit.speed() < this.unit.speed() * 3) {
                    Seq<Unit> seq = l.getUit();
                    int size = (int) (unit.hitSize / this.unit.hitSize);
                    if (size * size > seq.size && !(seq.indexOf(this.unit) >= 0) && seq.size < 1) {
                        underUnit = unit;
                        upon = false;
                        seq.add(this.unit);
                    } else if (seq.indexOf(this.unit) >= 0) {
                        underUnit = unit;
                        upon = false;
                    }
                }
            };
            Units.nearby(unit.team, unit.x(), unit.y(), unit.speed() * 300, cons);
            uca.UnderUnit(underUnit);
            uca.upon(false);
        }
    }

    @Override
    public void updateUnit() {
        if (unit instanceof UnitChainAble uca) {
            underUnit = uca.UnderUnit();
            upon = uca.upon();
            timer += 1;
            //use fallback AI when possible
            if (useFallback() && (fallback != null || (fallback = fallback()) != null)) {
                fallback.unit(unit);
                fallback.updateUnit();
                return;
            }
            updateVisuals();
            updateTargeting();
            updateMovement();
        }
    }

    @Override
    public void updateMovement() {
        if (timer >= 1500 && underUnit == null) FindUnderUnit();
        Building core = unit.closestEnemyCore();
        if (underUnit != null) {
            if (unit instanceof UnitChainAble uca) {
                if (underUnit.dead) {
                    if (underUnit instanceof LayAble la) la.getUit().remove(unit);
                    underUnit = null;
                    upon = false;
                    uca.UnderUnit(null);
                    uca.upon(false);
                    unit.elevation = 0;
                    return;
                }
                Seq<Unit> se = new Seq<>();
                if (underUnit instanceof LayAble l) {
                    se = l.getUit();
                }
                float x = 0, y = 0;
                if (se.size == 1) {
                    x = y = 0;
                }
                float mx = underUnit.x() + x - unit.x;
                float my = underUnit.y() + y - unit.y;
                float s = unit.speed();
                if (!upon) {
                    unit.updateBoosting(true);
                    float le = sqrt(my * my + mx * mx);
                    vec.set(mx * s * 2 / le, my * s * 2 / le);
                    unit.moveAt(vec);
                    if (unit.within(x + underUnit.x, y + underUnit.y, 0.1F) && unit.elevation >= 1) {
                        upon = true;
                        uca.upon(true);
                    }
                } else {
                    if (order == null) {
                        if (unit.within(underUnit.x, underUnit.y, 4.5F)) {
                            vec.set(mx, my);
                            unit.rotation = underUnit.rotation;
                        } else {
                            float le = sqrt(my * my + mx * mx);
                            vec.set(mx * s * 2 / le, my * s * 2 / le);
                        }
                    } else {
                        float oox = order.x();
                        float ooy = order.y();
                        if (unit.within(oox, ooy, 2.7F)) {
                            unit.elevation = Mathf.approachDelta(unit.elevation, 0, 0.5F);
                        }
                        float ox = oox - unit.x();
                        float oy = ooy - unit.y();
                        float ol = sqrt(ox * ox + oy * oy);
                        vec.set(ox * s * 2 / ol, oy * s * 2 / ol);
                    }
                    unit.moveAt(vec);
                    if (order != null && (order.dead() || (!unit.within(order.x(), order.y(), s * 500) && unit.elevation >= 1))) {
                        order = null;
                        uca.order(null);
                    }
                    if (order == null) {
                        Unit u = unit;
                        order = (Healthc) Units.closestTarget(unit.team, unit.x, unit.y, s * 500,
                                unit -> unit.team != u.team && unit.elevation < 0.0001F,
                                building -> building.team != u.team);
                        uca.order(unit);
                    }
                    Cons<Unit> ucons = u -> {
                        if (u.team != unit.team) {
                            if (u.elevation < 0.0001F && unit.elevation < 0.0001F) {
                                if (unit.type instanceof ChainUnitType cut) {
                                    u.damage(u.maxHealth() * cut.percent / 100.0F);
                                } else u.damage(unit.hitSize * unit.hitSize * unit.speed());
                            }
                        }
                    };
                    if (order != null) {
                        Units.nearbyEnemies(unit.team, unit.x(), unit.y(), unit.hitSize * 5F, ucons);
                    }
                    if (order != null) {
                        Tile t;
                        Building b;
                        for (int sx = (int) (unit.x() - unit.hitSize * 5F); sx < (int) (unit.x() + unit.hitSize * 5F); sx++) {
                            for (int sy = (int) (unit.y() - unit.hitSize * 5F); sy < (int) (unit.y() + unit.hitSize * 5F); sy++) {
                                t = world.tile(sx, sy);
                                if (t == null) continue;
                                b = t.build;
                                if (b == null || b.team == unit.team) continue;
                                if (unit.elevation < 0.0001F) {
                                    if (unit.type instanceof ChainUnitType cut) {
                                        b.damage(b.maxHealth() * cut.percent / 100.0F);
                                    } else b.damage(unit.hitSize * unit.hitSize * unit.speed());
                                }
                            }
                        }
                    }
                    if (unit.elevation < 0.0001F && underUnit instanceof LayAble l) {
                        Effect e = new WaveEffect();
                        e.at(unit.x, unit.y);
                        uca.upon(false);
                        uca.UnderUnit(null);
                        uca.order(null);
                        timer = 0;
                        order = null;
                        upon = false;
                        underUnit = null;
                        l.getUit().remove(unit);
                    }
                }
            }
        } else if ((unit.team.isAI() && !unit.team.rules().rtsAi)) {
            if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * tilesize / 2f)) {
                target = core;
                for (var mount : unit.mounts) {
                    if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                        mount.target = core;

                    }
                }
            }
            if ((core == null || !unit.within(core, unit.type.range * 0.5f))) {
                boolean move = true;

                if (state.rules.waves && unit.team == state.rules.defaultTeam) {
                    Tile spawner = getClosestSpawner();
                    if (spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)) move = false;
                    if (spawner == null && core == null) move = false;
                }

                //no reason to move if there's nothing there
                if (core == null && (!state.rules.waves || getClosestSpawner() == null)) {
                    move = false;
                }

                if (move) pathfind(Pathfinder.fieldCore);
            }

            if (unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid()) {
                unit.elevation = Mathf.approachDelta(unit.elevation, 0f, 0.0001F);
            }

            faceTarget();
        } else {
            CommandAI c = new CommandAI();
            c.unit(unit);
            c.command(UnitCommand.moveCommand);
            c.updateUnit();
        }
    }
}
