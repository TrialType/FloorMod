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
import arc.scene.Element;
import arc.scene.event.DragListener;
import arc.scene.event.EventListener;
import arc.scene.ui.Label;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

public class SprintingAbility extends Ability {
    public boolean autoSprinting = true;
    public float damage = 1000;
    public float maxLength = 200;
    public float reload = 180;
    public float powerReload = 120;
    public Effect maxPowerEffect = new Effect(2, e -> {
        Place p = (Place) e.data;
        Unit u = p.unit;
        float f = p.fin;
        float x = u.x, y = u.y, ro = u.rotation;
        Lines.stroke(2f * f);
        Draw.color(Color.valueOf("ff0000").a(0xaa * f));
        Vec2 v1 = new Vec2();
        Vec2 v2 = new Vec2();
        v2.trns(ro + 120 * (1 - f), maxLength * f);
        v1.trns(ro + 90, u.hitSize);
        Lines.line(x + v1.x, y + v1.y, x + v1.x + v2.x, y + v1.y + v2.y);
        v2.trns(ro - 120 * (1 - f), maxLength * f);
        v1.trns(ro - 90, u.hitSize);
        Lines.line(x + v1.x, y + v1.y, x + v1.x + v2.x, y + v1.y + v2.y);
    });
    protected static Element mobileMover = new Label(() -> "") {{
        mobileMover.setBounds(20, 20, 10, 10);
//        mobileMover.addListener(new DragListener());
    }};
    protected float powerTimer = 0;
    protected float timer = 0;

    @Override
    public void update(Unit unit) {
        timer += Time.delta;
        if (timer >= reload) {
            if (unit.isPlayer()) {
                float x = unit.x;
                float y = unit.y;
                float ro = unit.rotation;
                if (!Vars.mobile && Core.input.keyDown(KeyCode.altLeft)) {
                    powerTimer += Time.delta;
                } else if (Vars.mobile && Core.input.mouseX() <= 30 && Core.input.mouseX() >= 10 &&
                        Core.input.mouseY() <= 30 && Core.input.mouseY() >= 10) {
                    Core.scene.add(mobileMover);
                    powerTimer += Time.delta;
                    float dx = Core.input.mouseX() - x, dy = Core.input.mouseY() - y;
                    unit.lookAt(unit.x + dx, unit.y + dy);

//                    DragListener dl = null;
//                    for (EventListener listener : mobileMover.getListeners()) {
//                        if (listener instanceof DragListener d) {
//                            dl = d;
//                            break;
//                        }
//                    }
//                    if (dl != null && dl.getTouchDownX() <= 30 && dl.getTouchDownX() >= 10 &&
//                            dl.getTouchDownY() <= 30 && dl.getTouchDownY() >= 10) {
//                        powerTimer += Time.delta;
//                    }
                } else if (powerTimer >= powerReload) {
                    powerTimer = 0;
                    Units.nearbyEnemies(unit.team, x, y, maxLength, u -> {
                        float angel2 = Angles.angle(x, y, u.x, u.y);
                        float angle = Angles.angleDist(ro, angel2);
                        float len = (float) sqrt((x - u.x) * (x - u.x) + (y - u.y) * (y - u.y));

                        if (angle <= 90) {
                            if (cos(toRadians(angle)) * len <= maxLength && sin(toRadians(angle)) * len <= unit.hitSize) {
                                percentDamage(unit, u, damage);
                            }
                        }
                    });
                    Units.nearbyBuildings(x, y, maxLength, b -> {
                        if (b.team != unit.team) {
                            float angel2 = Angles.angle(x, y, b.x, b.y);
                            float angle = Angles.angleDist(ro, angel2);
                            float len = (float) sqrt((x - b.x) * (x - b.x) + (y - b.y) * (y - b.y));

                            if (angle <= 90) {
                                if (cos(toRadians(angle)) * len <= maxLength && sin(toRadians(angle)) * len <= unit.hitSize) {
                                    percentDamage(unit, b, damage);
                                }
                            }
                        }
                    });
                    unit.x = (float) (x + cos(toRadians(unit.rotation)) * maxLength);
                    unit.y = (float) (y + sin(toRadians(unit.rotation)) * maxLength);
                } else {
                    powerTimer = Math.max(0, powerTimer - Time.delta);
                }

                if (powerTimer > 0) {
                    maxPowerEffect.at(x, y, 0, new Place(unit, Math.min(1, powerTimer / powerReload)));
                }
            } else if (autoSprinting) {
                Core.app.post(mobileMover::remove);
            }
        }
    }

    protected void percentDamage(Unit unit, Healthc u, float damage) {
        boolean dead = u.dead();
        u.damage(damage);
        if (!dead && u.dead()) {
            Events.fire(new FEvents.UnitDestroyOtherEvent(unit, u));
        }
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
