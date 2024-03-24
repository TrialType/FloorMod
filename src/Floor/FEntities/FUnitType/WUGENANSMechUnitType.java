package Floor.FEntities.FUnitType;

import Floor.FEntities.FUnit.F.WUGENANSMechUnit;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import static mindustry.Vars.player;


public class WUGENANSMechUnitType extends UpGradeUnitType {
    private final static Vec2 legOffset = new Vec2();
    public float landTime = 60;
    public float outTime = 60;
    public float upDamage = -1;
    public float damageRadius = 60;
    public float landReload = 3600;
    public float needPower = 10000;
    public float powerRange = 1000;
    public float getRange = 100;

    public WUGENANSMechUnitType(String name) {
        super(name);
    }

    @Override
    public void draw(Unit unit) {
        if (unit.inFogTo(Vars.player.team())) return;

        boolean isPayload = !unit.isAdded();

        Mechc mech = unit instanceof Mechc ? (Mechc) unit : null;
        float z = isPayload ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        if (unit.controller().isBeingControlled(player.unit())) {
            drawControl(unit);
        }

        if (!isPayload && (unit.isFlying() || shadowElevation > 0)) {
            Draw.z(Math.min(Layer.darkness, z - 1f));
            drawShadow(unit);
        }

        Draw.z(z - 0.02f);

        if (mech != null && !(unit instanceof WUGENANSMechUnit wu && wu.under)) {
            drawMech(mech);
        }
        //side
        if (mech != null) {
            legOffset.trns(mech.baseRotation(), 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f / Mathf.PI, 1) * mechSideSway, 0f, unit.elevation));
        }

        //front
        if (mech != null) {
            legOffset.add(Tmp.v1.trns(mech.baseRotation() + 90, 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 1f / Mathf.PI, 1) * mechFrontSway, 0f, unit.elevation)));
        }

        unit.trns(legOffset.x, legOffset.y);


        if (unit instanceof Tankc) {
            drawTank((Unit & Tankc) unit);
        }

        if (unit instanceof Legsc && !isPayload) {
            drawLegs((Unit & Legsc) unit);
        }

        Draw.z(Math.min(z - 0.01f, Layer.bullet - 1f));

        if (unit instanceof Payloadc) {
            drawPayload((Unit & Payloadc) unit);
        }

        if (!(unit instanceof WUGENANSMechUnit wu && wu.under)) {
            drawSoftShadow(unit);
        }

        Draw.z(z);

        if (unit instanceof Crawlc c) {
            drawCrawl(c);
        }

        if (!(unit instanceof WUGENANSMechUnit wu && wu.under)) {
            if (drawBody) drawOutline(unit);
            drawWeaponOutlines(unit);
            if (engineLayer > 0) Draw.z(engineLayer);
            if (trailLength > 0 && !naval && (unit.isFlying() || !useEngineElevation)) {
                drawTrail(unit);
            }
            if (engines.size > 0) drawEngines(unit);
            Draw.z(z);
            if (drawBody) drawBody(unit);
            if (drawCell) drawCell(unit);
            drawWeapons(unit);
            if (drawItems) drawItems(unit);
            drawLight(unit);

            if (unit.shieldAlpha > 0 && drawShields) {
                drawShield(unit);
            }

            if (parts.size > 0) {
                for (int i = 0; i < parts.size; i++) {
                    var part = parts.get(i);

                    WeaponMount first = unit.mounts.length > part.weaponIndex ? unit.mounts[part.weaponIndex] : null;
                    if (first != null) {
                        DrawPart.params.set(first.warmup, first.reload / weapons.first().reload, first.smoothReload, first.heat, first.recoil, first.charge, unit.x, unit.y, unit.rotation);
                    } else {
                        DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation);
                    }

                    if (unit instanceof Scaled s) {
                        DrawPart.params.life = s.fin();
                    }

                    part.draw(DrawPart.params);
                }
            }

            if (!isPayload) {
                for (Ability a : unit.abilities) {
                    Draw.reset();
                    a.draw(unit);
                }
            }
        }

        unit.trns(-legOffset.x, -legOffset.y);

        Draw.reset();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(new Stat("land_time"),landTime);
        stats.add(new Stat("out_time"),outTime);
        stats.add(new Stat("up_damage"),upDamage);
        stats.add(new Stat("up_range"),damageRadius);
        stats.add(new Stat("land_reload"),landReload);
        stats.add(new Stat("power_need"),needPower);
        stats.add(new Stat("power_range"),powerRange);
        stats.add(new Stat("get_range"),getRange);
    }
}
