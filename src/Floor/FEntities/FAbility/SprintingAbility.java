package Floor.FEntities.FAbility;

import Floor.FContent.FEvents;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.SortedSpriteBatch;
import arc.input.KeyCode;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.Shaders;
import mindustry.input.MobileInput;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;
import static mindustry.Vars.*;

public class SprintingAbility extends Ability {
    public float damage = 1000;
    public float maxLength = 200;
    public float reload = 180;
    public float powerReload = 60;
    public Effect maxPowerEffect = new Effect(3, e -> {
        Place p = (Place) e.data;
        Unit u = p.unit;
        float f = p.fin;
        float x = u.x, y = u.y, ro = u.rotation;
        Lines.stroke(3f * f);
        Draw.color(Color.valueOf("ff0000").a(0x33 * f));
        Vec2 v1 = new Vec2();
        Vec2 v2 = new Vec2();
        v2.trns(ro + 120 * (1 - f), maxLength * f);
        v1.trns(ro + 90, u.hitSize);
        Lines.line(x + v1.x, y + v1.y, x + v1.x + v2.x, y + v1.y + v2.y);
        v2.trns(ro - 120 * (1 - f), maxLength * f);
        v1.trns(ro - 90, u.hitSize);
        Lines.line(x + v1.x, y + v1.y, x + v1.x + v2.x, y + v1.y + v2.y);
    });

    protected float powerTimer = 0;
    protected float timer = 0;
    protected int stats = 0;
    protected static Table select;
    protected static Table signer;
    protected static Table mobileMover;
    protected static Table screenChanger;
    protected static boolean haveSigner = false;
    protected static boolean haveMover = false;
    protected static boolean haveSelect = false;

    private static Unit play = null;

    @Override
    public void update(Unit unit) {
        if (!unit.isPlayer()) {
            stats = 0;
        } else if (stats == 0) {
            stats = 1;
        }

        if (Vars.mobile && unit.isPlayer() && play != unit) {
            rebuild();
            play = unit;
        }

        if (mobileMover == null) {
            select = new Table();
            rebuild();
            mobileMover = new Table();
            signer = new Table();
            screenChanger = new Table();
            mobileMover.setBounds(200, 200, 300, 300);
            signer.setBounds(1500, 700, 300, 300);
            screenChanger.setBounds(10, 500, 50, 100);
            select.setBounds(1000, 50, 50, 15);
            screenChanger.button(Icon.up, () -> {
                MobileInput input = (MobileInput) control.input;
                renderer.setScale(input.lastZoom);
                Vars.renderer.scaleCamera(-1);
            }).width(50).height(50).row();
            screenChanger.button(Icon.down, () -> {
                MobileInput input = (MobileInput) control.input;
                renderer.setScale(input.lastZoom);
                Vars.renderer.scaleCamera(1);
            }).width(50).height(50);
            mobileMover.background(Tex.buttonDisabled);
            signer.background(Tex.buttonDisabled);
            screenChanger.background(Tex.buttonDisabled);
            mobileMover.update(() -> {
                if (!state.isGame()) {
                    haveSigner = false;
                    haveMover = false;
                    mobileMover.remove();
                    mobileMover.actions(Actions.fadeOut(1));
                }
            });
            signer.update(() -> {
                if (!state.isGame()) {
                    signer.remove();
                    signer.actions(Actions.fadeOut(1));
                }
            });
            screenChanger.update(() -> {
                if (!state.isGame()) {
                    screenChanger.remove();
                    screenChanger.actions(Actions.fadeOut(1));
                }
            });
            select.update(() -> {
                if (!state.isGame()) {
                    haveSelect = false;
                    select.remove();
                    select.actions(Actions.fadeOut(1));
                }
            });
            Core.scene.add(mobileMover);
            Core.scene.add(signer);
            Core.scene.add(screenChanger);
            Core.scene.add(select);
            mobileMover.actions(Actions.fadeOut(0));
            signer.actions(Actions.fadeOut(0));
            screenChanger.actions(Actions.fadeOut(0));
            select.actions(Actions.fadeOut(0));
        }

        if (Vars.mobile && stats == 1 && !haveMover) {
            mobileMover.actions(Actions.fadeIn(1));
            screenChanger.actions(Actions.fadeIn(1));
            haveMover = true;
        } else if (stats != 1) {
            mobileMover.actions(Actions.fadeOut(1));
            screenChanger.actions(Actions.fadeOut(1));
            haveMover = false;
        }

        if (Vars.mobile && unit.isPlayer() && !haveSelect) {
            select.actions(Actions.fadeIn(1));
            haveSelect = true;
        } else if (haveSelect) {
            select.actions(Actions.fadeOut(1));
            haveSelect = false;
        }

        if (Vars.mobile && stats == 1 && timer >= reload && !haveSigner) {
            signer.actions(Actions.fadeIn(1));
            haveSigner = true;
        } else if (haveSigner) {
            haveSigner = false;
            signer.actions(Actions.fadeOut(1));
        }

        if (noPlayer() && haveMover) {
            haveSigner = false;
            haveMover = false;
            haveSelect = false;
            mobileMover.actions(Actions.fadeOut(1));
            signer.actions(Actions.fadeOut(1));
            screenChanger.actions(Actions.fadeOut(1));
            select.actions(Actions.fadeOut(1));
        }

        if (stats == 1 && Vars.mobile) {
            MobileInput input = (MobileInput) (control.input);
            input.selecting = true;
            Core.camera.position.lerpDelta(unit, 0.1f);
        }

        if (stats == 1 && Vars.mobile && unit.isPlayer()) {
            for (int i = 0; i < Core.input.getTouches(); i++) {
                if (Core.input.isTouched(i) && Core.input.mouseX(i) <= 500 && Core.input.mouseX(i) >= 200 &&
                        Core.input.mouseY(i) <= 500 && Core.input.mouseY(i) >= 200) {
                    float dx = Core.input.mouseX(i) - 350, dy = Core.input.mouseY(i) - 350;
                    float angle = Angles.angle(dx, dy);
                    if (!onSign()) {
                        unit.lookAt(dx + unit.x, dy + unit.y);
                    }
                    unit.x += (float) (unit.speed() * cos(toRadians(angle)));
                    unit.y += (float) (unit.speed() * sin(toRadians(angle)));
                    break;
                }
            }
        }

        timer += Time.delta;
        if (timer >= reload) {
            float x = unit.x;
            float y = unit.y;
            if (unit.isPlayer() && stats != 0) {
                boolean getting = Vars.mobile ? onSign() : Core.input.keyDown(KeyCode.altLeft);

                if (!(!getting && powerTimer >= powerReload) && powerTimer > 0) {
                    maxPowerEffect.at(x, y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                }

                if (getting) {
                    if (!Vars.mobile) {
                        Vec2 mover = new Vec2(unit.vel);
                        mover.setLength(mover.len() * 1.8f);
                        unit.vel.setZero();
                        unit.move(mover);
                        unit.lookAt(Angles.angle(Core.input.mouseWorldX() - x, Core.input.mouseWorldY() - y));
                        powerTimer += Time.delta;
                    } else if (stats == 1) {
                        for (int i = 0; i < Core.input.getTouches(); i++) {
                            if (Core.input.isTouched(i) && Core.input.mouseX(i) <= 1800 && Core.input.mouseX(i) >= 1500 &&
                                    Core.input.mouseY(i) <= 1000 && Core.input.mouseY(i) >= 700) {
                                powerTimer += Time.delta;
                                float dx = Core.input.mouseX(i) - 1650, dy = Core.input.mouseY(i) - 850;
                                Vec2 mover = new Vec2(unit.vel);
                                mover.setLength(mover.len() * 1.8f);
                                unit.vel.setZero();
                                unit.move(mover);
                                unit.lookAt(Angles.angle(dx, dy));
                                break;
                            }
                        }
                    } else if (stats == 2) {
                        powerTimer += Time.delta;
                        Vec2 mover = new Vec2(unit.vel);
                        mover.setLength(mover.len() * 1.4f);
                        unit.vel.setZero();
                        unit.move(mover);
                        unit.lookAt(Core.input.mouseWorld());
                    }
                } else if (powerTimer < powerReload) {
                    powerTimer = max(0, powerTimer - Time.delta);
                }

                if (!getting && powerTimer >= powerReload) {
                    powerTimer = 0;
                    timer = 0;
                    applyDamage(x, y, damage, unit);
                    unit.x = (float) (x + cos(toRadians(unit.rotation)) * maxLength);
                    unit.y = (float) (y + sin(toRadians(unit.rotation)) * maxLength);
                }
            } else if (stats == 0) {
                powerTimer += Time.delta;
                if (powerTimer >= powerReload + 2 * Time.delta) {
                    Teamc target = Units.closestTarget(unit.team, x, y, maxLength * 2);
                    if (target != null) {
                        if (Angles.angleDist(Angles.angle(x, y, target.x(), target.y()), unit.rotation) <= 2) {
                            powerTimer = 0;
                            timer = 0;
                            float angle = Angles.angle(x, y, target.x(), target.y());
                            applyDamage(x, y, damage, unit);
                            unit.x = x + (float) cos(toRadians(angle)) * maxLength;
                            unit.y = y + (float) sin(toRadians(angle)) * maxLength;
                        } else {
                            unit.lookAt(target);
                        }
                    } else {
                        maxPowerEffect.at(unit.x, unit.y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                    }
                } else {
                    maxPowerEffect.at(unit.x, unit.y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                }
            }
        }
    }

    protected boolean noPlayer() {
        if (Vars.mobile && Vars.player.unit() != null && Vars.player.unit().abilities != null) {
            for (Ability ability : Vars.player.unit().abilities) {
                if (ability instanceof SprintingAbility) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean onSign() {
        if (stats == 2 && Core.input.getTouches() > 0) {
            return true;
        } else if (stats == 1) {
            for (int i = 0; i < Core.input.getTouches(); i++) {
                if (Core.input.isTouched(i) && Core.input.mouseX(i) <= 1800 && Core.input.mouseX(i) >= 1500 &&
                        Core.input.mouseY(i) <= 1000 && Core.input.mouseY(i) >= 700) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void rebuild() {
        select.clear();
        select.addListener(select.clicked(() -> {
            stats = max(1, (stats + 1) % 3);
            rebuild();
        }));
        if (stats == 1 || stats == 0) {
            select.add(Core.bundle.get("ability.handBoost"));
        } else if (stats == 2) {
            select.add(Core.bundle.get("ability.targetBoost"));
        }
    }

    protected void damage(Unit unit, Healthc u, float damage) {
        boolean dead = u.dead();
        u.damage(damage);
        if (!dead && u.dead()) {
            Events.fire(new FEvents.UnitDestroyOtherEvent(unit, u));
        }
    }

    protected void applyDamage(float x, float y, float damage, Unit killer) {
        Units.nearbyEnemies(killer.team, x, y, maxLength, u -> {
            float angel2 = Angles.angle(x, y, u.x, u.y);
            float angle = Angles.angleDist(killer.rotation, angel2);
            float len = (float) sqrt((x - u.x) * (x - u.x) + (y - u.y) * (y - u.y));

            if (angle <= 90) {
                if (cos(toRadians(angle)) * len <= maxLength && sin(toRadians(angle)) * len <= killer.hitSize) {
                    damage(killer, u, damage);
                }
            }
        });
        Units.nearbyBuildings(x, y, maxLength, b -> {
            if (b.team != killer.team) {
                float angel2 = Angles.angle(x, y, b.x, b.y);
                float angle = Angles.angleDist(killer.rotation, angel2);
                float len = (float) sqrt((x - b.x) * (x - b.x) + (y - b.y) * (y - b.y));

                if (angle <= 90) {
                    if (cos(toRadians(angle)) * len <= maxLength && sin(toRadians(angle)) * len <= killer.hitSize) {
                        damage(killer, b, damage);
                    }
                }
            }
        });
    }

    public static class Place {
        public Unit unit;
        public float fin;

        public Place(Unit unit, float fin) {
            this.unit = unit;
            this.fin = fin;
        }
    }
}
