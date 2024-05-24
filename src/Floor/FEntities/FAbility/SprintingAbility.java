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
import mindustry.gen.Healthc;
import mindustry.gen.Tex;
import mindustry.gen.Unit;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;
import static mindustry.Vars.state;

public class SprintingAbility extends Ability {
    public float damage = 1000;
    public float maxLength = 200;
    public float reload = 180;
    public float powerReload = 120;
    public Effect maxPowerEffect = new Effect(5, e -> {
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

    protected int stats = 0;
    protected Table select;
    protected Table signer;
    protected Table mobileMover;
    protected float powerTimer = 0;
    protected float timer = 0;
    protected boolean have = false;
    protected boolean moved = false, signed = false;
    protected boolean hase = false;

    @Override
    public void update(Unit unit) {
        if (mobileMover == null) {
            rebuild();
            mobileMover = new Table();
            signer = new Table();
            mobileMover.setBounds(200, 200, 300, 300);
            signer.setBounds(1500, 700, 300, 300);
            mobileMover.background(Tex.buttonDisabled);
            signer.background(Tex.buttonDisabled);
            mobileMover.update(() -> {
                if (!state.isGame()) {
                    have = false;
                    mobileMover.remove();
                    mobileMover.actions(Actions.fadeOut(0));
                }
            });
            signer.update(() -> {
                if (!state.isGame()) {
                    have = false;
                    signer.remove();
                    signer.actions(Actions.fadeOut(0));
                }
            });
            Core.scene.add(mobileMover);
            Core.scene.add(signer);
            mobileMover.actions(Actions.fadeOut(0));
            signer.actions(Actions.fadeOut(0));
        }

        if (!(Vars.mobile && stats == 1)) {
            have = false;
            mobileMover.actions(Actions.fadeOut(1));
            signer.actions(Actions.fadeOut(1));
        } else if (!have) {
            mobileMover.actions(Actions.fadeIn(1));
            signer.actions(Actions.fadeIn(1));
            have = true;
        }

        if (!unit.isPlayer()) {
            have = false;
            mobileMover.actions(Actions.fadeOut(1));
            signer.actions(Actions.fadeOut(1));
            hase = false;
            select.actions(Actions.fadeOut(1));
        }

        if (unit.dead || unit.health <= 0) {
            have = false;
            mobileMover.actions(Actions.fadeOut(1));
            signer.actions(Actions.fadeOut(1));
            hase = false;
            select.actions(Actions.fadeOut(1));
            mobileMover.remove();
            signer.remove();
            select.remove();
        }

        if (!hase && unit.isPlayer()) {
            select.actions(Actions.fadeIn(1));
            hase = true;
        }

        timer += Time.delta;
        if (timer >= reload) {
            if (unit.isPlayer() && stats != 0) {
                float x = unit.x;
                float y = unit.y;
                boolean getting = Vars.mobile ? onSign() : Core.input.keyDown(KeyCode.altLeft);

                if (!(!getting && powerTimer >= powerReload) && powerTimer > 0) {
                    maxPowerEffect.at(x, y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                }

                if (getting) {
                    if (!Vars.mobile) {
                        Vec2 mover = new Vec2(unit.vel);
                        mover.setLength(mover.len() * 1.2f);
                        unit.vel.setZero();
                        unit.move(mover);
                        unit.lookAt(Angles.mouseAngle(x, y));
                        powerTimer += Time.delta;
                    } else if (stats == 1) {
                        signed = moved = false;
                        for (int i = 0; i < Core.input.getTouches() && !(signed && moved); i++) {
                            if (!moved && Core.input.mouseX(i) <= 500 && Core.input.mouseX(i) >= 200 && Core.input.mouseY(i) <= 500 && Core.input.mouseY(i) >= 200) {
                                float dx = Core.input.mouseX(i) - 350, dy = Core.input.mouseY(i) - 350;
                                float angle = Angles.angle(dx, dy);
                                unit.x += (float) (unit.speed() * cos(toRadians(angle)));
                                unit.y += (float) (unit.speed() * sin(toRadians(angle)));
                                Core.camera.position.lerpDelta(unit, 1);
                                moved = true;
                            } else if (!signed && Core.input.mouseX(i) <= 1800 && Core.input.mouseX(i) >= 1500 && Core.input.mouseY(i) <= 1000 && Core.input.mouseY(i) >= 700) {
                                powerTimer += Time.delta;
                                float dx = Core.input.mouseX(i) - 1650, dy = Core.input.mouseY(i) - 850;
                                Vec2 mover = new Vec2(unit.vel);
                                mover.setLength(mover.len() * 1.2f);
                                unit.vel.setZero();
                                unit.move(mover);
                                unit.lookAt(Angles.angle(dx, dy));
                                signed = true;
                            }
                        }
                    } else if (stats == 2) {
                        powerTimer += Time.delta;
                        unit.lookAt(Core.input.mouseWorld());
                    }
                } else {
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
                    powerTimer = 0;
                    timer = 0;
                    applyDamage(unit.x, unit.y, damage, unit);
                    unit.x = unit.x + (float) cos(toRadians(unit.rotation)) * maxLength;
                    unit.y = unit.y + (float) sin(toRadians(unit.rotation)) * maxLength;
                } else {
                    maxPowerEffect.at(unit.x, unit.y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                }
            }
        }
    }

    public boolean onSign() {
        for (int i = 0; i < Core.input.getTouches(); i++) {
            if (stats == 1 && Core.input.mouseX(i) <= 1800 && Core.input.mouseX(i) >= 1500 && Core.input.mouseY(i) <= 1000 && Core.input.mouseY(i) >= 700) {
                return true;
            } else if (stats == 2 && Core.input.getTouches() > 0) {
                return true;
            }
        }
        return false;
    }

    protected void rebuild() {
        if (select == null) {
            select = new Table();
            select.setBounds(800, 50, 50, 15);
            select.update(() -> {
                if (!state.isGame()) {
                    hase = false;
                    select.remove();
                    select.actions(Actions.fadeOut(0));
                }
            });
            Core.scene.add(select);
        } else {
            select.clear();
        }
        if (Vars.mobile) {
            select.addListener(select.clicked(() -> {
                stats = (stats + 1) % 3;
                rebuild();
            }));
        } else {
            select.addListener(select.clicked(() -> {
                stats = (stats + 1) % 2;
                rebuild();
            }));
        }
        if (stats == 0) {
            select.add(Core.bundle.get("ability.autoBoost"));
        } else if (stats == 1) {
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
