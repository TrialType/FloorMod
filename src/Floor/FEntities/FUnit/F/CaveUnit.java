package Floor.FEntities.FUnit.F;

import Floor.FContent.FEvents;
import Floor.FContent.FStatusEffects;
import Floor.FContent.FUnits;
import Floor.FEntities.FEffect.WaterWave;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.core.World;
import mindustry.ctype.ContentType;
import mindustry.entities.Units;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Unit;
import mindustry.gen.UnitWaterMove;
import mindustry.graphics.Trail;
import mindustry.maps.Map;
import mindustry.type.Planet;
import mindustry.type.UnitType;

import java.util.Random;

import static java.lang.Math.max;
import static mindustry.Vars.world;

public class CaveUnit extends UnitWaterMove {
    private static final Seq<UnitType> u1 = new Seq<>(new UnitType[]{
            UnitTypes.dagger,
            UnitTypes.nova,
            UnitTypes.flare,
            UnitTypes.retusa,
            UnitTypes.risso,
            FUnits.barb
    });
    private static final Seq<UnitType> u2 = new Seq<>(new UnitType[]{
            UnitTypes.mace,
            UnitTypes.pulsar,
            UnitTypes.atrax,
            UnitTypes.horizon,
            UnitTypes.poly,
            UnitTypes.minke,
            UnitTypes.oxynoe,
            FUnits.hammer
    });
    private static final Seq<UnitType> u3 = new Seq<>(new UnitType[]{
            UnitTypes.fortress,
            UnitTypes.quasar,
            UnitTypes.spiroct,
            UnitTypes.zenith,
            UnitTypes.mega,
            UnitTypes.bryde,
            UnitTypes.cyerce,
            UnitTypes.cyerce,
            FUnits.buying
    });
    private static final Seq<UnitType> u4 = new Seq<>(new UnitType[]{
            UnitTypes.scepter,
            UnitTypes.vela,
            UnitTypes.arkyid,
            UnitTypes.antumbra,
            UnitTypes.quad,
            UnitTypes.sei,
            UnitTypes.aegires,
            FUnits.crazy
    });
    private static final Seq<UnitType> u5 = new Seq<>(new UnitType[]{
            UnitTypes.reign,
            UnitTypes.corvus,
            UnitTypes.toxopid,
            UnitTypes.eclipse,
            UnitTypes.omura,
            UnitTypes.navanax,
            FUnits.transition
    });
    private MultiEffect water = null;
    private float timer = -1;
    private float length = -1;
    private float allTimer = -1;
    private boolean back = false;
    private float backTimer = 0;
    private float summonTimer = 0;
    private int time = 1;

    protected CaveUnit() {
        this.applied = new Bits(Vars.content.getBy(ContentType.status).size);
        this.resupplyTime = Mathf.random(10.0F);
        this.statuses = new Seq<>();
        this.tleft = new Trail(1);
        this.trailColor = Blocks.water.mapColor.cpy().mul(1.5F);
        this.tright = new Trail(1);
    }

    public static CaveUnit create() {
        return new CaveUnit();
    }

    @Override
    public int classId() {
        return 118;
    }

    @Override
    public void update() {
        if (allTimer >= 21000) {
            Fx.unitDrop.at(x, y, 0, this);
            if (allTimer >= 21600) {
                back = true;
                time = 0;
                water = null;
                WaterWave.back = true;
                destroy();
                return;
            }
        }

        super.update();

        if (water == null) {
            updateEffect();
        } else {
            timer += Time.delta;
            allTimer += Time.delta;
            if (timer >= 676) {
                water.at(this);
                timer = timer % 676;
            }
            if (allTimer >= max(world.width(), world.height()) * 13.44f && allTimer % 1800 < Time.delta) {
                back = true;
                WaterWave.back = true;
            }
            applyDamage();
        }

        if (back) {
            backTimer += Time.delta;
            summonTimer = summonTimer + Time.delta;
            if (backTimer >= 481) {
                time++;
                back = false;
                WaterWave.back = false;
                backTimer = 0;
                summonTimer = 0;
            } else if (summonTimer >= 60) {
                summonTimer = 0;
                summonUnit();
            }
        }
    }

    public void updateEffect() {
        WaterWave[] effects = new WaterWave[28];
        for (int i = 0; i < 28; i++) {
            String a;
            if (i <= 13) {
                a = Integer.toHexString(125 + (i + 1) * 10);
            } else {
                a = Integer.toHexString(255 - (i - 13) * 10);
            }
            int finalI = i;
            effects[i] = new WaterWave() {{
                startDelay = 25 * finalI;
                sizeFrom = 0;
                sizeTo = max(world.width(), world.height()) * 11.2f;
                lifetime = max(world.width(), world.height()) * 13.44f;
                strokeTo = strokeFrom = 24f;
                colorTo = colorFrom = Color.valueOf("061726" + a);
            }};
        }

        length = max(world.width(), world.height()) * 9;
        water = new MultiEffect(effects);
        WaterWave.back = false;
        back = false;
        timer = 676;
        allTimer = 0;
    }

    public void applyDamage() {
        float boost = allTimer >= max(world.width(), world.height()) * 13.44f ? 1 : allTimer * 0.00695f / max(world.width(), world.height()) * 13.44f;
        Units.nearbyEnemies(team, x, y, length * boost, u -> {
            if (u.shield > 0.01) {
                u.damage(u.shield() * 2);
                u.damage(u.maxHealth * 0.001f);
            } else {
                u.damage(u.maxHealth * 0.001f);
            }
            u.apply(StatusEffects.slow, 2);
            u.apply(FStatusEffects.High_tension, 2);
        });
        Units.nearbyBuildings(x, y, length * boost, b -> {
            b.applySlowdown(0.8f, 3);
            if (b.team != team) {
                b.damage(b.maxHealth * 0.001f);
            }
        });
    }

    public void summonUnit() {
        Random ra = new Random();
        int power = ra.nextInt(time * 15) + 1;
        while (power > 1) {
            if (power >= 32) {
                power = power - 32;
                summonUnit(u5.get(ra.nextInt(u5.size)));
            } else if (power >= 16) {
                power = power - 16;
                summonUnit(u4.get(ra.nextInt(u4.size)));
            } else if (power >= 8) {
                power = power - 8;
                summonUnit(u3.get(ra.nextInt(u3.size)));
            } else if (power >= 4) {
                power = power - 4;
                summonUnit(u2.get(ra.nextInt(u2.size)));
            } else {
                power = power - 2;
                summonUnit(u1.get(ra.nextInt(u1.size)));
            }
        }
    }

    public void summonUnit(UnitType u) {
        Random ra = new Random();
        float x = 0, y = 0, rotate = 45;

        switch (ra.nextInt(4)) {
            case 0: {
                y = 0;
                x = ra.nextInt(world.width() * 8 + 1);
                rotate = 90;
                break;
            }
            case 1: {
                x = world.width() * 8;
                y = ra.nextInt(world.height() * 8 + 1);
                rotate = 180;
                break;
            }
            case 2: {
                y = world.height() * 8;
                x = ra.nextInt(world.width() * 8 + 1);
                rotate = 270;
                break;
            }
            case 3: {
                x = 0;
                y = ra.nextInt(world.height() * 8 + 1);
                rotate = 0;
            }
        }
        Unit unit = u.create(team);
        unit.set(x, y);
        unit.rotation(rotate);
        unit.add();
        unit.apply(FStatusEffects.High_tension);
        Events.fire(new FEvents.GetPowerEvent(unit, time * 2, false));
        Fx.unitSpawn.at(x, y, rotate);
    }
}
