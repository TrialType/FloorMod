package Floor.FEntities.FAbility;

import Floor.FContent.FEvents;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
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
import mindustry.input.MobileInput;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;
import static mindustry.Vars.*;

public class SprintingAbility extends Ability {
    public float damage = 50;
    public float maxLength = 150;
    public float reload = 180;
    public float powerReload = 60;
    public Effect powerEffect = new Effect(2, e -> {
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
    public Effect maxPowerEffect = new Effect();

    protected float powerTimer = 0;
    protected float timer = 0;
    protected int stats = 0;
    protected boolean needPress = false;
    protected static boolean screenScale = true;
    protected static float lastZoom;
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
            if (mobile) {
                MobileInput input = (MobileInput) control.input;
                lastZoom = input.lastZoom;
            }
            stats = 1;
        }

        if (stats == 1 && !screenScale && mobile) {
            renderer.setScale(lastZoom);
        } else if (screenScale && mobile) {
            MobileInput input = (MobileInput) control.input;
            lastZoom = input.lastZoom;
            screenScale = false;
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
                screenScale = true;
                Vars.renderer.scaleCamera(-1);
            }).width(50).height(50).row();
            screenChanger.button(Icon.down, () -> {
                screenScale = true;
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

        if (Vars.mobile && unit.isPlayer() && play != unit) {
            rebuild();
            play = unit;
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
                        unit.lookAt(dx + unit.x, dy + unit.y);
                        unit.lookAt(dx + unit.x, dy + unit.y);
                    }
                    unit.vel.x += (float) (unit.speed() * cos(toRadians(angle)));
                    unit.vel.y += (float) (unit.speed() * sin(toRadians(angle)));
                    break;
                }
            }
        }

        timer += Time.delta;
        if (timer >= reload) {
            if (powerTimer >= powerReload) {
                maxPowerEffect.at(unit.x, unit.y, unit.rotation, unit);
            }

            float x = unit.x;
            float y = unit.y;
            if (unit.isPlayer() && stats != 0) {
                boolean getting = Vars.mobile ? onSign() : Core.input.keyDown(KeyCode.altLeft);

                if (!(!getting && powerTimer >= powerReload) && powerTimer > 0) {
                    powerEffect.at(x, y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                }

                if (getting) {
                    if (!Vars.mobile) {
                        unit.lookAt(Angles.angle(Core.input.mouseWorldX() - x, Core.input.mouseWorldY() - y));
                        unit.lookAt(Angles.angle(Core.input.mouseWorldX() - x, Core.input.mouseWorldY() - y));
                        unit.lookAt(Angles.angle(Core.input.mouseWorldX() - x, Core.input.mouseWorldY() - y));
                        powerTimer += Time.delta;
                    } else if (stats == 1) {
                        for (int i = 0; i < Core.input.getTouches(); i++) {
                            if (Core.input.isTouched(i) && Core.input.mouseX(i) <= 1800 && Core.input.mouseX(i) >= 1500 &&
                                    Core.input.mouseY(i) <= 1000 && Core.input.mouseY(i) >= 700) {
                                powerTimer += Time.delta;
                                float dx = Core.input.mouseX(i) - 1650, dy = Core.input.mouseY(i) - 850;
                                unit.lookAt(Angles.angle(dx, dy));
                                unit.lookAt(Angles.angle(dx, dy));
                                unit.lookAt(Angles.angle(dx, dy));
                                break;
                            }
                        }
                    } else if (stats == 2) {
                        powerTimer += Time.delta;
                        Vec2 mover = new Vec2(unit.vel);
                        unit.vel.setZero();
                        unit.move(mover);
                        unit.lookAt(Core.input.mouseWorld());
                    }
                } else if (powerTimer < powerReload) {
                    powerTimer = max(0, powerTimer - Time.delta);
                }

                if (!getting && powerTimer >= powerReload && !needPress) {
                    powerTimer = 0;
                    timer = 0;
                    applyDamage(x, y, damage, unit);
                    Vec2 mo = boostMove(unit.x, unit.y,
                            (float) cos(toRadians(unit.rotation)) * maxLength,
                            (float) sin(toRadians(unit.rotation)) * maxLength);
                    unit.x = x + mo.x;
                    unit.y = y + mo.y;
                } else if (needPress && getting) {
                    needPress = false;
                }
            } else if (stats == 0) {
                powerTimer += Time.delta;
                if (powerTimer >= powerReload + 2 * Time.delta) {
                    needPress = true;
                    Teamc target = Units.closestTarget(unit.team, x, y, maxLength * 2);
                    if (target != null) {
                        if (Angles.angleDist(Angles.angle(x, y, target.x(), target.y()), unit.rotation) <= 0) {
                            powerTimer = 0;
                            timer = 0;
                            float angle = Angles.angle(x, y, target.x(), target.y());
                            applyDamage(x, y, damage, unit);
                            Vec2 mo = boostMove(unit.x, unit.y,
                                    (float) cos(toRadians(angle)) * maxLength,
                                    (float) sin(toRadians(angle)) * maxLength);
                            unit.x = x + mo.x;
                            unit.y = y + mo.y;
                        } else {
                            unit.lookAt(target);
                        }
                    } else {
                        powerEffect.at(unit.x, unit.y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                    }
                } else {
                    powerEffect.at(unit.x, unit.y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                }
            }
        }
    }

    protected Vec2 boostMove(float x, float y, float mx, float my) {
        if (x + mx > world.width() * 8 || x + mx < 0) {
            if (y + my > world.height() * 8 || y + my < 0) {
                float mxx, mxy;
                if (y + my > world.height() * 8) {
                    if (x + mx > world.width() * 8) {
                        mxx = mx + x - world.width() * 8;
                    } else {
                        mxx = mx + x;
                    }
                    mxy = my + y - world.height() * 8;
                } else {
                    if (x + mx > world.width() * 8) {
                        mxx = mx + x - world.width() * 8;
                    } else {
                        mxx = mx + x;
                    }
                    mxy = my + y;
                }
                if (mxx / mx > mxy / my) {
                    return new Vec2(mx * mxy / my, mxy);
                } else {
                    return new Vec2(mxx, my * mxx / mx);
                }
            } else {
                float mxx;
                if (x + mx > world.width() * 8) {
                    mxx = x + mx - world.width() * 8;
                } else {
                    mxx = x + mx;
                }
                return new Vec2(mxx, my * mxx / mx);
            }
        } else {
            if (y + my > world.height() * 8 || y + my < 0) {
                float mxy;
                if (y + my > world.height() * 8) {
                    mxy = y + my - world.height() * 8;
                } else {
                    mxy = y + my;
                }
                return new Vec2(mx * mxy / my, mxy);
            } else {
                return new Vec2(mx, my);
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
            if (stats == 1 && mobile) {
                MobileInput input = (MobileInput) control.input;
                lastZoom = input.lastZoom;
            }
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
                if (cos(toRadians(angle)) * len <= maxLength && sin(toRadians(angle)) * len <= killer.hitSize * 1.4f) {
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
                    if (cos(toRadians(angle)) * len <= maxLength && sin(toRadians(angle)) * len <= killer.hitSize * 1.4f) {
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
