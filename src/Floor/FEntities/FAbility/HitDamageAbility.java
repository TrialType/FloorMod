package Floor.FEntities.FAbility;

import Floor.FContent.FEvents;
import arc.Core;
import arc.Events;
import arc.math.geom.Rect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Unit;

import java.util.HashMap;
import java.util.Map;

public class HitDamageAbility extends Ability {
    public float minSpeed = 5;
    public float damage, reload;
    public boolean firstPercent;
    public float percent, changeHel;
    private static final Rect rect = new Rect();
    private static Unit moveUnit;
    private static HitDamageAbility hda;
    private final Map<Unit, Long> unitMap = new HashMap<>();
    private final Map<Building, Long> buildingMap = new HashMap<>();
    private static Map<Unit, Long> UnitMap;
    private static Map<Building, Long> BuildingMap;
    @Override
    public void update(Unit unit) {
        moveUnit = unit;
        UnitMap = unitMap;
        BuildingMap = buildingMap;
        UnitMap.replaceAll((u, v) -> v + 1);
        BuildingMap.replaceAll((u, v) -> v + 1);
        hda = this;
        if (unit.speed() >= minSpeed) {
            rect.setCenter(unit.x(), unit.y()).setSize(unit.hitSize / 2F);
            Units.nearbyEnemies(unit.team, rect, u -> {
                if (!(u.team == moveUnit.team)) {
                    long time = UnitMap.computeIfAbsent(u, unit1 -> -1L);
                    if (time < 0 || time >= hda.reload) {
                        UnitMap.put(u, 0L);
                        if (moveUnit.moving()) {
                            boolean life = u.dead();
                            if (hda.firstPercent && u.health() > hda.changeHel || !hda.firstPercent && u.health() < hda.changeHel) {
                                u.health(u.health() - u.maxHealth() * hda.percent / 100.0F);
                                u.damage(0);
                                if (u.health() <= 0) u.dead(true);
                            } else {
                                u.damage(hda.damage);
                            }
                            if (!life && u.dead()) {
                                Events.fire(new FEvents.UnitDestroyOtherEvent(unit, u));
                            }
                        }
                    }
                }
            });
            Units.nearbyBuildings(unit.x, unit.y, unit.hitSize / 2F, b -> {
                if (!(b.team == moveUnit.team)) {
                    long time = BuildingMap.computeIfAbsent(b, unit1 -> -1L);
                    if (time < 0 || time >= hda.reload) {
                        BuildingMap.put(b, 0L);
                        if (moveUnit.moving()) {
                            boolean life = b.dead();
                            if (hda.firstPercent && b.health() > hda.changeHel / 2 || !hda.firstPercent && b.health() < hda.changeHel * 2) {
                                b.health(b.health() - b.maxHealth() * hda.percent / 100.0F);
                                b.damage(0);
                                if (b.health() <= 0) b.dead(true);
                            } else {
                                b.damage(hda.damage);
                            }
                            if (!life && b.dead()) {
                                Events.fire(new FEvents.UnitDestroyOtherEvent(unit, b));
                            }
                        }
                    }
                }
            });
        }
    }

    public static HitDamageAbility create(float damage, float percent, float changeHel, boolean firstPercent) {
        return new HitDamageAbility(damage, percent, changeHel, firstPercent, 30, 5);
    }

    public HitDamageAbility(float damage, float percent, float changeHel, boolean firstPercent, float reload, float minSpeed) {
        this.damage = damage;
        this.percent = percent;
        this.changeHel = changeHel;
        this.firstPercent = firstPercent;
        this.reload = reload;
        this.minSpeed = minSpeed;
    }

    @Override
    public String localized() {
        return Core.bundle.get("ability.hit_damageAbility.name");
    }
}