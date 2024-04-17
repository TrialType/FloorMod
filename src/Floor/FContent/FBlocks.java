package Floor.FContent;

import Floor.FEntities.FBlock.*;
import Floor.FEntities.FBulletType.AroundBulletType;
import Floor.FEntities.FBulletType.FreeBulletType;
import Floor.FEntities.FBulletType.WindBulletType;
import Floor.FEntities.FBulletType.ownerBulletType;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.FlarePart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.consumers.ConsumePower;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static mindustry.type.ItemStack.with;

public class FBlocks {
    public static Block outPowerFactory, inputPowerFactory,
            outPowerFactoryII, inputPowerFactoryII,
            outPowerFactoryIII, inputPowerFactoryIII;

    //these twice only use on test
    public static Block kt, pu;

    public static Block slowProject;
    public static Block eleFence, eleFenceII, eleFenceIII;
    public static Block fourNet, fireBoost,
            smallWindTurret, middleWindTurret, largeWindTurret,
            stay, bind, tranquil,
            fireStream;
    public static Block primarySolidification, intermediateSolidification, advancedSolidification, ultimateSolidification;

    public static void load() {
        primarySolidification = new StackCrafter("primary-solidification") {{
            itemCapacity = 60;
            liquidCapacity = 120;
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionCopper, 14),
                    ItemStack.with(Items.copper, 6),
                    LiquidStack.empty, 120
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionLead, 14),
                    ItemStack.with(Items.lead, 6),
                    LiquidStack.empty, 120
            ));

            hasPower = false;

            requirements(Category.crafting, ItemStack.with(Items.metaglass, 35, Items.copper, 40, Items.lead, 25));
        }};
        intermediateSolidification = new StackCrafter("intermediate-solidification") {{
            itemCapacity = 150;
            liquidCapacity = 300;
            size = 2;
            health = 250;

            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionCopper, 30),
                    ItemStack.with(Items.copper, 15),
                    LiquidStack.empty, 90
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionLead, 30),
                    ItemStack.with(Items.lead, 15),
                    LiquidStack.empty, 90
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionTitanium, 30),
                    ItemStack.with(Items.titanium, 12),
                    LiquidStack.empty, 90
            ));

            consume(new ConsumePower(5, 0, false));

            requirements(Category.crafting, ItemStack.with(Items.metaglass, 125, Items.copper, 150,
                    Items.lead, 100, Items.graphite, 140));
        }};
        advancedSolidification = new StackCrafter("advanced-solidification") {{
            itemCapacity = 360;
            liquidCapacity = 600;
            size = 3;
            health = 750;

            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionCopper, 60),
                    ItemStack.with(Items.copper, 36),
                    LiquidStack.empty, 90
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionLead, 60),
                    ItemStack.with(Items.lead, 36),
                    LiquidStack.empty, 90
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionTitanium, 60),
                    ItemStack.with(Items.titanium, 30),
                    LiquidStack.empty, 90
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionThorium, 60),
                    ItemStack.with(Items.thorium, 24),
                    LiquidStack.empty, 90
            ));

            consume(new ConsumePower(20, 0, false));

            requirements(Category.crafting, ItemStack.with(Items.metaglass, 500, Items.copper, 450,
                    Items.lead, 400, Items.graphite, 350, Items.titanium, 300));
        }};
        ultimateSolidification = new StackCrafter("ultimate-solidification") {{
            itemCapacity = 360;
            liquidCapacity = 900;
            size = 4;
            health = 750;

            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionCopper, 90),
                    ItemStack.with(Items.copper, 64),
                    LiquidStack.empty, 60
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionLead, 90),
                    ItemStack.with(Items.lead, 64),
                    LiquidStack.empty, 60
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionTitanium, 90),
                    ItemStack.with(Items.titanium, 64),
                    LiquidStack.empty, 60
            ));
            switchStack.add(new ProductStack(
                    ItemStack.empty,
                    LiquidStack.with(FLiquids.fusionThorium, 90),
                    ItemStack.with(Items.thorium, 64),
                    LiquidStack.empty, 60
            ));

            consume(new ConsumePower(100, 0, false));

            requirements(Category.crafting, ItemStack.with(Items.metaglass, 1500, Items.copper, 1450,
                    Items.lead, 1400, Items.graphite, 1350, Items.titanium, 1400, Items.thorium, 1450, Items.surgeAlloy, 500));
        }};

        pu = new PureProject("pu") {{
            health = 650;

            consumePower(50f);

            requirements(Category.effect, ItemStack.with(Items.copper, 1));
        }};

        outPowerFactory = new GradeFactory("out_power_factory") {{
            requirements(Category.units, with(Items.copper, 500, Items.lead, 600, Items.silicon, 800));

            itemCapacity = 770;
            size = 3;
            consumePower(50f);

            constructTime = 60f * 10f;
            grades = new Seq<>(new UnitType[]{
                    UnitTypes.flare, UnitTypes.nova,
                    UnitTypes.pulsar, UnitTypes.dagger,
                    UnitTypes.mace, UnitTypes.crawler,
                    UnitTypes.atrax, UnitTypes.horizon,
                    UnitTypes.poly, UnitTypes.risso,
                    UnitTypes.minke, UnitTypes.retusa,
                    UnitTypes.oxynoe, UnitTypes.stell,
                    UnitTypes.locus, UnitTypes.merui,
                    UnitTypes.cleroi, UnitTypes.elude,
                    UnitTypes.avert, FUnits.barb, FUnits.hammer
            });
        }};
        inputPowerFactory = new GradeFactory("input_power_factory") {{
            requirements(Category.units, with(Items.copper, 500, Items.lead, 600, Items.silicon, 800));

            itemCapacity = 770;
            size = 3;
            consumePower(50f);

            constructTime = 60f * 10f;
            grades = new Seq<>(new UnitType[]{
                    UnitTypes.flare, UnitTypes.nova,
                    UnitTypes.pulsar, UnitTypes.dagger,
                    UnitTypes.mace, UnitTypes.crawler,
                    UnitTypes.atrax, UnitTypes.horizon,
                    UnitTypes.poly, UnitTypes.risso,
                    UnitTypes.minke, UnitTypes.retusa,
                    UnitTypes.oxynoe, UnitTypes.stell,
                    UnitTypes.locus, UnitTypes.merui,
                    UnitTypes.cleroi, UnitTypes.elude,
                    UnitTypes.avert, FUnits.barb, FUnits.hammer
            });
            out = false;
        }};
        outPowerFactoryII = new GradeFactory("out_power_factory_II") {{
            requirements(Category.units, with(Items.copper, 1000, Items.lead, 1200, Items.silicon, 2000));

            itemCapacity = 770;
            size = 7;
            consumePower(200);

            constructTime = 60f * 10f;
            grades = new Seq<>(new UnitType[]{
                    UnitTypes.flare, UnitTypes.nova,
                    UnitTypes.pulsar, UnitTypes.dagger,
                    UnitTypes.mace, UnitTypes.crawler,
                    UnitTypes.atrax, UnitTypes.horizon,
                    UnitTypes.poly, UnitTypes.risso,
                    UnitTypes.minke, UnitTypes.retusa,
                    UnitTypes.oxynoe, UnitTypes.stell,
                    UnitTypes.locus, UnitTypes.merui,
                    UnitTypes.cleroi, UnitTypes.elude,
                    UnitTypes.avert, FUnits.barb, FUnits.hammer,
                    UnitTypes.fortress, UnitTypes.scepter,
                    UnitTypes.quasar, UnitTypes.vela,
                    UnitTypes.spiroct, UnitTypes.arkyid,
                    UnitTypes.zenith, UnitTypes.antumbra,
                    UnitTypes.mega, UnitTypes.quad,
                    UnitTypes.bryde, UnitTypes.sei,
                    UnitTypes.cyerce, UnitTypes.aegires,
                    UnitTypes.precept, UnitTypes.vanquish,
                    UnitTypes.anthicus, UnitTypes.tecta,
                    UnitTypes.obviate, UnitTypes.quell,
                    FUnits.buying, FUnits.crazy,
                    FUnits.dive, FUnits.befall
            });
        }};
        inputPowerFactoryII = new GradeFactory("input_power_factory_II") {{
            requirements(Category.units, with(Items.copper, 1000, Items.lead, 1200, Items.silicon, 2000));

            itemCapacity = 770;
            size = 7;
            consumePower(200);

            constructTime = 60f * 10f;
            grades = new Seq<>(new UnitType[]{
                    UnitTypes.flare, UnitTypes.nova,
                    UnitTypes.pulsar, UnitTypes.dagger,
                    UnitTypes.mace, UnitTypes.crawler,
                    UnitTypes.atrax, UnitTypes.horizon,
                    UnitTypes.poly, UnitTypes.risso,
                    UnitTypes.minke, UnitTypes.retusa,
                    UnitTypes.oxynoe, UnitTypes.stell,
                    UnitTypes.locus, UnitTypes.merui,
                    UnitTypes.cleroi, UnitTypes.elude,
                    UnitTypes.avert, FUnits.barb, FUnits.hammer,
                    UnitTypes.fortress, UnitTypes.scepter,
                    UnitTypes.quasar, UnitTypes.vela,
                    UnitTypes.spiroct, UnitTypes.arkyid,
                    UnitTypes.zenith, UnitTypes.antumbra,
                    UnitTypes.mega, UnitTypes.quad,
                    UnitTypes.bryde, UnitTypes.sei,
                    UnitTypes.cyerce, UnitTypes.aegires,
                    UnitTypes.precept, UnitTypes.vanquish,
                    UnitTypes.anthicus, UnitTypes.tecta,
                    UnitTypes.obviate, UnitTypes.quell,
                    FUnits.buying, FUnits.crazy,
                    FUnits.dive, FUnits.befall
            });
            out = false;
        }};
        outPowerFactoryIII = new GradeFactory("out_power_factory_III") {{
            requirements(Category.units, with(Items.copper, 2000, Items.lead, 2400, Items.silicon, 2800));

            itemCapacity = 770;
            size = 11;
            consumePower(500);

            constructTime = 60f * 10f;
        }};
        inputPowerFactoryIII = new GradeFactory("input_power_factory_III") {{
            requirements(Category.units, with(Items.copper, 2000, Items.lead, 2400, Items.silicon, 2800));

            itemCapacity = 770;
            size = 11;
            consumePower(500);

            constructTime = 60f * 10f;
            out = false;
        }};
//======================================================================================================================
        kt = new KnockingTurret("kt") {{
            health = 650;

            requirements(Category.effect, ItemStack.with(Items.copper, 1));
        }};
//======================================================================================================================
        fireStream = new ItemTurret("fire_stream") {{
            requirements(Category.turret, ItemStack.with(
                    Items.titanium, 340,
                    Items.copper, 300,
                    Items.graphite, 350
            ));
            coolant = consume(new ConsumeCoolant(0.2f));
            coolantMultiplier = 2f;

            hasItems = true;
            itemCapacity = 32;
            maxAmmo = 32;
            consumeAmmoOnce = false;
            shootX = shootY = 0;
            inaccuracy = 15;
            shootCone = 360;

            reload = 5;
            size = 2;
            health = 900;

            shootSound = Sounds.flame;
            shoot = new ShootBarrel() {{
                shots = 16;
                barrels = new float[]{
                        0, 0, 0,
                        0, 0, 22.5f,
                        0, 0, 45,
                        0, 0, 67.5f,
                        0, 0, 90,
                        0, 0, 112.5f,
                        0, 0, 135,
                        0, 0, 157.5f,
                        0, 0, 180,
                        0, 0, 202.5f,
                        0, 0, 225,
                        0, 0, 247.5f,
                        0, 0, 270,
                        0, 0, 292.5f,
                        0, 0, 315,
                        0, 0, 337.5f,
                };
            }};
            ammoTypes.put(Items.coal, new BasicBulletType() {{
                absorbable = reflectable = hittable = false;

                damage = 38;
                lifetime = 20;
                speed = 2.3f;
                width = height = 0;
                ammoMultiplier = 8;
                shootEffect = Fx.shootPyraFlame;
                status = StatusEffects.burning;
                statusDuration = 180;
                hitEffect = despawnEffect = Fx.none;
            }});
            ammoTypes.put(Items.pyratite, new BasicBulletType() {{
                absorbable = reflectable = hittable = false;

                damage = 64;
                lifetime = 30;
                speed = 2.5f;
                width = height = 0;
                ammoMultiplier = 16;
                shootEffect = Fx.shootPyraFlame;
                status = FStatusEffects.burningII;
                statusDuration = 300;
                hitEffect = despawnEffect = Fx.none;
            }});
        }};
        fireBoost = new OwnerTurret("fire_boost") {{
            targetAir = targetGround = true;

            health = 3000;
            size = 4;
            range = 260;
            shootY = 35;
            reload = 5;
            recoil = 3;
            inaccuracy = 15;

            bullet = new ownerBulletType(8f, 8) {{
                absorbable = hittable = reflectable = false;

                lifetime = 6.7f;
                splashDamage = 6;
                splashDamageRadius = 20;

                despawnEffect = hitEffect = Fx.none;

                pierce = true;
                pierceBuilding = true;
                status = FStatusEffects.burningII;
                statusDuration = 240;
            }};

            requirements(Category.turret, ItemStack.with(
                    Items.titanium, 1500,
                    Items.graphite, 1500,
                    Items.graphite, 2000,
                    Items.silicon, 1500,
                    Items.phaseFabric, 1500,
                    Items.plastanium, 900
            ));
        }};
        stay = new PowerTurret("stay") {{
            consume(new ConsumePower(2, 0, false));

            health = 200;
            size = 2;

            recoil = 0.8f;
            range = 200;
            reload = 30;
            consumesPower = true;
            hasPower = true;
            consumeAmmoOnce = false;
            canOverdrive = false;

            shootType = new PointBulletType() {{
                speed = 100;
                damage = 8;
                lifetime = 180;

                lightColor = Pal.redLight;
                status = StatusEffects.unmoving;
                statusDuration = 24;
                splashDamageRadius = 36;

                shootEffect = smokeEffect = Fx.none;
                hitEffect = Fx.smokeCloud;
                despawnEffect = hitEffect;
            }};

            requirements(Category.turret, ItemStack.with(
                    Items.copper, 60,
                    Items.graphite, 60
            ));
        }};
        bind = new PowerTurret("bind") {{
            consume(new ConsumePower(6, 0, false));

            health = 1000;
            size = 3;

            recoil = 1.3f;
            range = 280;
            reload = 60;
            consumesPower = true;
            hasPower = true;
            consumeAmmoOnce = false;
            canOverdrive = false;

            shootType = new BulletType() {{
                speed = 4.8f;
                damage = 12;

                trailLength = 14;
                trailColor = Pal.redLight;
                trailChance = 1;
                rangeOverride = 200;
                lightColor = Pal.redLight;
                status = StatusEffects.unmoving;
                statusDuration = 45;
                splashDamageRadius = 60;

                shootEffect = smokeEffect = Fx.none;
                hitEffect = new ExplosionEffect() {{
                    lifetime = 45;
                    waveColor = Pal.redLight;
                    waveRadBase = 60;
                    waveRad = 61;
                    waveStroke = 4;
                    waveLife = 45;
                    smokes = 28;
                    smokeRad = 60;
                    smokeColor = Pal.redLight;
                    smokeSizeBase = 4;
                }};
                despawnEffect = hitEffect;

                fragAngle = 360;
                fragBullets = 6;
                fragLifeMax = 3.5f;
                fragLifeMin = 3f;
                fragVelocityMin = 0.2f;
                fragVelocityMax = 0.5f;
                fragOnAbsorb = false;
                fragOnHit = true;
                fragBullet = new BulletType() {{
                    damage = 3;
                    speed = 2.5f;
                    splashDamageRadius = 6;
                    status = FStatusEffects.suppressI;
                    statusDuration = 240;

                    hitEffect = new ExplosionEffect() {{
                        lifetime = 240;

                        waveColor = Pal.redLight;
                        waveRadBase = 6;
                        waveRad = 6;
                        waveStroke = 1;
                        waveLife = 240;

                        smokes = 4;
                        smokeRad = 6;
                        smokeColor = Pal.redLight;
                        smokeSizeBase = 1;
                    }};
                    despawnEffect = hitEffect;
                }};
            }};

            requirements(Category.turret, ItemStack.with(
                    Items.copper, 500,
                    Items.titanium, 350,
                    Items.graphite, 400
            ));
        }};
        tranquil = new PowerTurret("tranquil") {{
            consume(new ConsumePower(20, 0, false));

            health = 2000;
            size = 4;

            recoil = 2f;
            range = 400;
            reload = 90;
            consumesPower = true;
            hasPower = true;
            consumeAmmoOnce = false;
            canOverdrive = false;

            shootType = new BulletType() {{
                hittable = reflectable = absorbable = false;

                speed = 4.5f;
                lifetime = 90;

                trailChance = 1f;
                trailInterp = Interp.slope;
                trailWidth = 4.5f;
                trailLength = 19;
                trailEffect = new MultiEffect(Fx.artilleryTrail, Fx.artilleryTrailSmoke);
                rangeOverride = 200;

                status = StatusEffects.unmoving;
                statusDuration = 45;
                splashDamageRadius = 60;
                splashDamage = 32;

                shootEffect = smokeEffect = Fx.none;
                hitEffect = new ExplosionEffect() {{
                    lifetime = 360;
                    waveColor = Pal.redLight;
                    waveRadBase = 60;
                    waveRad = 61;
                    waveStroke = 4;
                    waveLife = 45;
                    smokes = 13;
                    smokeRad = 60;
                    smokeColor = Pal.redLight;
                    smokeSizeBase = 0;
                    smokeSize = 5;
                }};
                despawnEffect = hitEffect;

                parts.add(new ShapePart() {{
                    radius = 5.5f;
                    radiusTo = 5.5f;
                    circle = true;
                    color = colorTo = Pal.redLight;
                }});

                fragRandomSpread = 360;
                fragBullets = 6;
                fragLifeMax = 2.5f;
                fragLifeMin = 2f;
                fragVelocityMin = 0.4f;
                fragVelocityMax = 0.5f;
                fragOnAbsorb = true;
                fragOnHit = true;
                fragBullet = new BulletType() {{
                    damage = 12;
                    splashDamageRadius = 24;
                    status = FStatusEffects.suppressIII;
                    statusDuration = 240;

                    hitEffect = new ExplosionEffect() {{
                        lifetime = 300;

                        waveColor = Pal.redLight;
                        waveRadBase = 28;
                        waveRad = 24;
                        waveStroke = 1;
                        waveLife = 240;

                        smokes = 14;
                        smokeRad = 16;
                        smokeColor = Pal.redLight;
                        smokeSizeBase = 0;
                        smokeSize = 3;
                    }};
                    despawnEffect = hitEffect;
                }};

                intervalDelay = 3;
                bulletInterval = 4;
                intervalSpread = 22;
                intervalAngle = -11;
                intervalBullets = 2;
                intervalBullet = new BasicBulletType() {{
                    speed = 1.5f;
                    lifetime = 180;

                    frontColor = backColor = trailColor = Pal.redLight;
                    trailLength = 7;

                    status = FStatusEffects.suppressII;
                    statusDuration = 180;

                    splashDamageRadius = 16;
                    splashDamage = 12;

                    hitEffect = new ExplosionEffect() {{
                        lifetime = 240;

                        waveColor = Pal.redLight;
                        waveRadBase = 16;
                        waveRad = 18;
                        waveStroke = 1;
                        waveLife = 240;

                        smokes = 12;
                        smokeRad = 16;
                        smokeColor = Pal.redLight;
                        smokeSizeBase = 0;
                        smokeSize = 3;
                    }};
                    despawnEffect = hitEffect;
                }};
            }};

            requirements(Category.turret, ItemStack.with(
                    Items.copper, 1000,
                    Items.titanium, 700,
                    Items.graphite, 800
            ));
        }};
        smallWindTurret = new ItemTurret("small_wind_turret") {{
            requirements(Category.turret, ItemStack.with(
                    Items.titanium, 300,
                    Items.copper, 240,
                    Items.silicon, 185
            ));
            consume(new ConsumePower(8.4f, 0, false));
            coolant = consume(new ConsumeCoolant(0.3f));
            coolantMultiplier = 1.05f;

            hasItems = true;
            itemCapacity = 15;
            maxAmmo = 15;
            ammoPerShot = 5;
            consumeAmmoOnce = false;
            shootX = shootY = 0;

            range = 200;
            reload = 600;
            size = 3;
            clipSize = 3;
            health = 1300;

            ammoTypes.put(Items.blastCompound, new BasicBulletType() {{
                trailEffect = Fx.none;

                damage = 0;
                lifetime = 1800;
                speed = 0.7f;
                width = height = 36;
                shrinkX = shrinkY = 0;
                backColor = frontColor = Pal.darkPyraFlame;

                reflectable = absorbable = false;

                fragAngle = 0;
                fragRandomSpread = 0;
                fragSpread = 0;
                fragOnAbsorb = fragOnHit = true;
                fragBullets = 1;

                fragBullet = new WindBulletType() {{
                    lifetime = 400;
                    damage = 0.5f;
                    windPower = 0.3f;
                    applyEffect = FStatusEffects.burningIII;

                    fillRange = false;
                    windEffect = new Effect(120, 80f, e -> {
                        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                        randLenVectors(e.id, 3, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 50,
                                (x, y) -> Fill.circle(e.x + x, e.y + y, 1 * (1 - e.fin()))
                        );
                    });
                    everyHit = new Effect(120, e -> {
                        if (e.data instanceof Unit u) {
                            float size = u.hitSize;
                            float angle = Angles.angle(e.x, e.y, u.x, u.y);
                            float x = u.x - Angles.trnsx(angle, size / 1.2f);
                            float y = u.y - Angles.trnsy(angle, size / 1.2f);
                            for (float i = -size / 1.5f; i <= size / 1.5f; i += 1) {
                                if (i + size / 1.5f <= 1.1 || i - size / 1.5f >= -1.9) {
                                    Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                                } else {
                                    Draw.color(Pal.darkPyraFlame, Color.valueOf("00000099"), Color.valueOf("00000044"), e.fin());
                                }
                                float len = (float) (size / 1.2f - Math.sqrt(size * size / 1.2f / 1.2f - i * i));
                                float lx = x + Angles.trnsx(angle + 90, i) + Angles.trnsx(angle, len);
                                float ly = y + Angles.trnsy(angle + 90, i) + Angles.trnsy(angle, len);
                                randLenVectors((long) (e.id + i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                                randLenVectors((long) (e.id - i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                            }
                        }
                        if (e.data instanceof Building b) {
                            float size = b.hitSize();
                            float angle = Angles.angle(e.x, e.y, b.x, b.y);
                            float x = b.x - Angles.trnsx(angle, size / 1.2f);
                            float y = b.y - Angles.trnsy(angle, size / 1.2f);
                            for (float i = -size / 1.5f; i <= size / 1.5f; i += 1) {
                                if (i + size / 1.5f <= 1.1 || i - size / 1.5f >= -1.9) {
                                    Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                                } else {
                                    Draw.color(Pal.darkPyraFlame, Color.valueOf("00000099"), Color.valueOf("00000044"), e.fin());
                                }
                                float len = (float) (size / 1.2f - Math.sqrt(size * size / 1.2f / 1.2f - i * i));
                                float lx = x + Angles.trnsx(angle + 90, i) + Angles.trnsx(angle, len);
                                float ly = y + Angles.trnsy(angle + 90, i) + Angles.trnsy(angle, len);
                                randLenVectors((long) (e.id + i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                                randLenVectors((long) (e.id - i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                            }
                        }
                    });
                }};
            }});
            ammoTypes.put(Items.metaglass, new PointBulletType() {{
                trailEffect = Fx.none;

                damage = 0;
                lifetime = 600;
                speed = 500;
                trailSpacing = 20f;

                fragAngle = 0;
                fragRandomSpread = 0;
                fragSpread = 0;
                fragOnAbsorb = fragOnHit = true;
                fragBullets = 1;

                fragBullet = new FreeBulletType() {{
                    lifetime = 400;
                    damage = 0;
                    speed = 0;
                    hittable = absorbable = reflectable = collides = false;
                    despawnEffect = hitEffect = Fx.none;

                    parts.add(new FlarePart() {{
                        sides = 3;
                        radius = 36;
                        radiusTo = 0;
                        rotMove = 1000;
                        stroke = 24;
                        innerScl = 0.2f;
                        color1 = Color.valueOf("EBEEF588");
                        color2 = Color.valueOf("EBEEF5");
                    }});

                    intervalBullets = 1;
                    intervalRandomSpread = 360;
                    bulletInterval = 1;
                    intervalDelay = 0;
                    intervalBullet = new BulletType(0, 0) {{
                        rangeOverride = 60;
                        lifetime = 0;
                        splashDamageRadius = 40;
                        splashDamage = 26;
                        status = FStatusEffects.breakHelII;
                        statusDuration = 240;

                        collides = hittable = absorbable = reflectable = false;
                    }};

                    intervalHitEffect = new ExplosionEffect() {{
                        lifetime = 45;
                        sparks = 0;
                        waveLife = 0;
                        smokes = 20;
                        smokeRad = 35;
                        smokeColor = Color.valueOf("EBEEF522");
                        smokeSizeBase = 2.5f;

                        renderer = f -> {
                            Draw.color(this.waveColor);
                            f.scaled(this.waveLife, (i) -> {
                                Lines.stroke(this.waveStroke * i.fout());
                                Lines.circle(f.x, f.y, this.waveRadBase + i.fin() * this.waveRad);
                            });
                            Draw.color(this.smokeColor);
                            if (this.smokeSize > 0.0F) {
                                Angles.randLenVectors(f.id, this.smokes, 2.0F + this.smokeRad * f.finpow(), (x, y) -> Fill.circle(f.x + x, f.y + y, f.fout() * this.smokeSize + this.smokeSizeBase));
                            }

                            Draw.color(this.sparkColor);
                            Lines.stroke(f.fout() * this.sparkStroke);
                            Angles.randLenVectors(f.id + 1, this.sparks, 1.0F + this.sparkRad * f.finpow(), (x, y) -> {
                                Lines.lineAngle(f.x + x, f.y + y, Mathf.angle(x, y), 1.0F + f.fout() * this.sparkLen);
                                Drawf.light(f.x + x, f.y + y, f.fout() * this.sparkLen * 4.0F, this.sparkColor, 0.7F);
                            });

                            if (f.data instanceof Vec2 v) {
                                color(Color.valueOf("EBEEF522"));
                                Lines.stroke(7.5f * (1 - f.fin()));
                                Lines.line(f.x, f.y, v.x, v.y);
                            }
                        };
                    }};
                }};
            }});
        }};
        middleWindTurret = new ItemTurret("middle_wind_turret") {{
            requirements(Category.turret, ItemStack.with(
                    Items.titanium, 700,
                    Items.copper, 800,
                    Items.graphite, 780,
                    Items.silicon, 750
            ));

            consume(new ConsumePower(100, 1500, false));
            coolant = consume(new ConsumeCoolant(0.8f));
            coolantMultiplier = 1.05f;

            hasItems = true;
            itemCapacity = 32;
            maxAmmo = 32;
            ammoPerShot = 10;
            consumeAmmoOnce = false;
            shootX = shootY = 0;

            range = 500;
            reload = 900;
            size = 4;
            clipSize = 4;
            health = 2000;

            ammoTypes.put(Items.blastCompound, new BasicBulletType() {{
                ammoMultiplier = 1f;

                trailEffect = Fx.none;

                damage = 0;
                lifetime = 1800;
                speed = 1.5f;
                width = height = 77;
                shrinkX = shrinkY = 0;
                backColor = frontColor = Pal.darkPyraFlame;

                fragAngle = 0;
                fragRandomSpread = 0;
                fragSpread = 0;
                fragOnAbsorb = fragOnHit = true;
                fragBullets = 1;

                fragBullet = new WindBulletType() {{
                    lifetime = 850;
                    damage = 1f;
                    windPower = 0.45f;
                    applyEffect = FStatusEffects.burningIV;

                    fillRange = false;
                    windEffect = new Effect(120, 80f, e -> {
                        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                        randLenVectors(e.id, 5, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 50,
                                (x, y) -> Fill.circle(e.x + x, e.y + y, 1 * (1 - e.fin()))
                        );
                    });
                    everyHit = new Effect(120, e -> {
                        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                        if (e.data instanceof Unit u) {
                            float size = u.hitSize;
                            float angle = Angles.angle(e.x, e.y, u.x, u.y);
                            float x = u.x - Angles.trnsx(angle, size / 1.2f);
                            float y = u.y - Angles.trnsy(angle, size / 1.2f);
                            for (float i = -size / 1.5f; i <= size / 1.5f; i += 1) {
                                if (i + size / 1.5f <= 1.1 || i - size / 1.5f >= -1.9) {
                                    Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                                } else {
                                    Draw.color(Pal.darkPyraFlame, Color.valueOf("00000099"), Color.valueOf("00000044"), e.fin());
                                }
                                float len = (float) (size / 1.2f - Math.sqrt(size * size / 1.2f / 1.2f - i * i));
                                float lx = x + Angles.trnsx(angle + 90, i) + Angles.trnsx(angle, len);
                                float ly = y + Angles.trnsy(angle + 90, i) + Angles.trnsy(angle, len);
                                randLenVectors((long) (e.id + i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                                randLenVectors((long) (e.id - i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                            }
                        }
                        if (e.data instanceof Building b) {
                            float size = b.hitSize();
                            float angle = Angles.angle(e.x, e.y, b.x, b.y);
                            float x = b.x - Angles.trnsx(angle, size / 1.2f);
                            float y = b.y - Angles.trnsy(angle, size / 1.2f);
                            for (float i = -size / 1.5f; i <= size / 1.5f; i += 1) {
                                float len = (float) (size / 1.2f - Math.sqrt(size * size / 1.2f / 1.2f - i * i));
                                float lx = x + Angles.trnsx(angle + 90, i) + Angles.trnsx(angle, len);
                                float ly = y + Angles.trnsy(angle + 90, i) + Angles.trnsy(angle, len);
                                if (i + size / 1.5f <= 1.1 || i - size / 1.5f >= -1.9) {
                                    Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                                } else {
                                    Draw.color(Pal.darkPyraFlame, Color.valueOf("00000099"), Color.valueOf("00000044"), e.fin());
                                }
                                randLenVectors((long) (e.id + i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                                randLenVectors((long) (e.id - i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                            }
                        }
                    });
                }};
            }});
            ammoTypes.put(Items.metaglass, new PointBulletType() {{
                trailEffect = Fx.none;

                damage = 0;
                lifetime = 600;
                speed = 500;
                trailSpacing = 20f;

                fragAngle = 0;
                fragRandomSpread = 0;
                fragSpread = 0;
                fragOnAbsorb = fragOnHit = true;
                fragBullets = 1;

                fragBullet = new FreeBulletType() {{
                    lifetime = 700;
                    damage = 36;
                    speed = 0;
                    hittable = absorbable = reflectable = collides = false;
                    despawnEffect = hitEffect = Fx.none;

                    parts.add(new FlarePart() {{
                        sides = 3;
                        radius = 45;
                        radiusTo = 0;
                        rotMove = 1000;
                        stroke = 30;
                        innerScl = 0.3f;
                        color1 = Color.valueOf("EBEEF5");
                        color2 = Color.valueOf("EBEEF5");
                    }});

                    intervalBullets = 3;
                    intervalRandomSpread = 360;
                    bulletInterval = 1;
                    intervalDelay = 0;
                    intervalBullet = new BulletType(0, 0) {{
                        rangeOverride = 120;
                        lifetime = 0;
                        splashDamageRadius = 60;
                        splashDamage = 45;
                        status = FStatusEffects.breakHelIII;
                        statusDuration = 300;

                        collides = hittable = absorbable = reflectable = false;
                    }};

                    intervalHitEffect = new ExplosionEffect() {{
                        lifetime = 45;
                        sparks = 0;
                        waveLife = 0;
                        smokes = 20;
                        smokeRad = 45;
                        smokeColor = Color.valueOf("EBEEF522");
                        smokeSizeBase = 3;

                        renderer = f -> {
                            Draw.color(this.waveColor);
                            f.scaled(this.waveLife, (i) -> {
                                Lines.stroke(this.waveStroke * i.fout());
                                Lines.circle(f.x, f.y, this.waveRadBase + i.fin() * this.waveRad);
                            });
                            Draw.color(this.smokeColor);
                            if (this.smokeSize > 0.0F) {
                                Angles.randLenVectors(f.id, this.smokes, 2.0F + this.smokeRad * f.finpow(), (x, y) -> Fill.circle(f.x + x, f.y + y, f.fout() * this.smokeSize + this.smokeSizeBase));
                            }

                            Draw.color(this.sparkColor);
                            Lines.stroke(f.fout() * this.sparkStroke);
                            Angles.randLenVectors(f.id + 1, this.sparks, 1.0F + this.sparkRad * f.finpow(), (x, y) -> {
                                Lines.lineAngle(f.x + x, f.y + y, Mathf.angle(x, y), 1.0F + f.fout() * this.sparkLen);
                                Drawf.light(f.x + x, f.y + y, f.fout() * this.sparkLen * 4.0F, this.sparkColor, 0.7F);
                            });

                            if (f.data instanceof Vec2 v) {
                                color(Color.valueOf("EBEEF522"));
                                Lines.stroke(7.5f * (1 - f.fin()));
                                Lines.line(f.x, f.y, v.x, v.y);
                            }
                        };
                    }};
                }};
            }});
        }};
        largeWindTurret = new ItemTurret("large_wind_turret") {{
            requirements(Category.turret, ItemStack.with(
                    Items.titanium, 2000,
                    Items.copper, 2600,
                    Items.graphite, 1800,
                    Items.silicon, 1650,
                    Items.surgeAlloy, 300
            ));

            consume(new ConsumePower(1000, 20000, false));
            coolant = consume(new ConsumeCoolant(1.3f));
            coolantMultiplier = 1.01f;

            hasItems = true;
            itemCapacity = 90;
            maxAmmo = 90;
            ammoPerShot = 15;
            consumeAmmoOnce = false;
            shootX = shootY = 0;

            range = 1000;
            reload = 1200;
            size = 5;
            clipSize = 5;
            health = 8000;

            ammoTypes.put(Items.blastCompound, new BasicBulletType() {{
                ammoMultiplier = 1f;

                trailEffect = Fx.none;

                damage = 0;
                lifetime = 1800;
                speed = 2.4f;
                width = height = 104;
                shrinkX = shrinkY = 0;
                backColor = frontColor = Pal.darkPyraFlame;

                fragAngle = 0;
                fragRandomSpread = 0;
                fragSpread = 0;
                fragOnAbsorb = fragOnHit = true;
                fragBullets = 1;

                fragBullet = new WindBulletType() {{
                    lifetime = 1000;
                    damage = 2f;
                    windPower = 0.65f;
                    windWidth = 600;
                    windLength = 300;
                    applyEffect = FStatusEffects.burningV;

                    fillRange = false;
                    windEffect = new Effect(120, 80f, e -> {
                        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                        randLenVectors(e.id, 9, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 50,
                                (x, y) -> Fill.circle(e.x + x, e.y + y, 1 * (1 - e.fin()))
                        );
                    });
                    everyHit = new Effect(120, e -> {
                        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                        if (e.data instanceof Unit u) {
                            float size = u.hitSize;
                            float angle = Angles.angle(e.x, e.y, u.x, u.y);
                            float x = u.x - Angles.trnsx(angle, size / 1.2f);
                            float y = u.y - Angles.trnsy(angle, size / 1.2f);
                            for (float i = -size / 1.5f; i <= size / 1.5f; i += 1) {
                                if (i + size / 1.5f <= 1.1 || i - size / 1.5f >= -1.9) {
                                    Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                                } else {
                                    Draw.color(Pal.darkPyraFlame, Color.valueOf("00000099"), Color.valueOf("00000044"), e.fin());
                                }
                                float len = (float) (size / 1.2f - Math.sqrt(size * size / 1.2f / 1.2f - i * i));
                                float lx = x + Angles.trnsx(angle + 90, i) + Angles.trnsx(angle, len);
                                float ly = y + Angles.trnsy(angle + 90, i) + Angles.trnsy(angle, len);
                                randLenVectors((long) (e.id + i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                                randLenVectors((long) (e.id - i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                            }
                        }
                        if (e.data instanceof Building b) {
                            float size = b.hitSize();
                            float angle = Angles.angle(e.x, e.y, b.x, b.y);
                            float x = b.x - Angles.trnsx(angle, size / 1.2f);
                            float y = b.y - Angles.trnsy(angle, size / 1.2f);
                            for (float i = -size / 1.5f; i <= size / 1.5f; i += 1) {
                                if (i + size / 1.5f <= 1.1 || i - size / 1.5f >= -1.9) {
                                    Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
                                } else {
                                    Draw.color(Pal.darkPyraFlame, Color.valueOf("00000099"), Color.valueOf("00000044"), e.fin());
                                }
                                float len = (float) (size / 1.2f - Math.sqrt(size * size / 1.2f / 1.2f - i * i));
                                float lx = x + Angles.trnsx(angle + 90, i) + Angles.trnsx(angle, len);
                                float ly = y + Angles.trnsy(angle + 90, i) + Angles.trnsy(angle, len);
                                randLenVectors((long) (e.id + i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                                randLenVectors((long) (e.id - i), 1, e.finpow() * 200, angle, 24,
                                        (cx, cy) -> Fill.circle(lx + cx, ly + cy, 2 * (1 - e.fin()))
                                );
                            }
                        }
                    });

                }};
            }});
            ammoTypes.put(Items.metaglass, new PointBulletType() {{
                ammoMultiplier = 1f;

                trailEffect = Fx.none;

                damage = 0;
                lifetime = 600;
                speed = 500;
                trailSpacing = 20f;

                fragAngle = 0;
                fragRandomSpread = 0;
                fragSpread = 0;
                fragOnAbsorb = fragOnHit = true;
                fragBullets = 1;

                fragBullet = new FreeBulletType() {{
                    lifetime = 1000;
                    damage = 0;
                    speed = 0;
                    hittable = absorbable = reflectable = collides = false;
                    despawnEffect = hitEffect = Fx.none;

                    parts.add(new FlarePart() {{
                        sides = 3;
                        radius = 55;
                        radiusTo = 0;
                        rotMove = 1000;
                        stroke = 40;
                        innerScl = 0.2f;
                        color1 = Color.valueOf("EBEEF5");
                        color2 = Color.valueOf("EBEEF5");
                    }});

                    intervalBullets = 5;
                    intervalRandomSpread = 360;
                    bulletInterval = 1;
                    intervalDelay = 0;
                    intervalBullet = new BulletType(0, 0) {{
                        rangeOverride = 160;
                        lifetime = 0;
                        splashDamageRadius = 80;
                        splashDamage = 75;
                        status = FStatusEffects.breakHelIV;
                        statusDuration = 360;

                        collides = hittable = absorbable = reflectable = false;
                    }};

                    intervalHitEffect = new ExplosionEffect() {{
                        lifetime = 24;
                        sparks = 0;
                        waveLife = 0;
                        smokes = 20;
                        smokeRad = 60;
                        smokeColor = Color.valueOf("EBEEF522");
                        smokeSizeBase = 3.5f;

                        renderer = f -> {
                            Draw.color(this.waveColor);
                            f.scaled(this.waveLife, (i) -> {
                                Lines.stroke(this.waveStroke * i.fout());
                                Lines.circle(f.x, f.y, this.waveRadBase + i.fin() * this.waveRad);
                            });
                            Draw.color(this.smokeColor);
                            if (this.smokeSize > 0.0F) {
                                Angles.randLenVectors(f.id, this.smokes, 2.0F + this.smokeRad * f.finpow(), (x, y) -> Fill.circle(f.x + x, f.y + y, f.fout() * this.smokeSize + this.smokeSizeBase));
                            }

                            Draw.color(this.sparkColor);
                            Lines.stroke(f.fout() * this.sparkStroke);
                            Angles.randLenVectors(f.id + 1, this.sparks, 1.0F + this.sparkRad * f.finpow(), (x, y) -> {
                                Lines.lineAngle(f.x + x, f.y + y, Mathf.angle(x, y), 1.0F + f.fout() * this.sparkLen);
                                Drawf.light(f.x + x, f.y + y, f.fout() * this.sparkLen * 4.0F, this.sparkColor, 0.7F);
                            });

                            if (f.data instanceof Vec2 v) {
                                color(Color.valueOf("EBEEF522"));
                                Lines.stroke(7.5f * (1 - f.fin()));
                                Lines.line(f.x, f.y, v.x, v.y);
                            }
                        };
                    }};
                }};
            }});
        }};
        fourNet = new LiquidTurret("four_net") {{
            scaledHealth = 10000;
            armor = 55;

            clipSize = 4;
            size = 4;
            reload = 180;
            range = 360;
            rotateSpeed = 12;
            liquidCapacity = 12;
            consumeAmmoOnce = false;
            inaccuracy = 0;

            hasPower = true;
            consumesPower = true;

            consume(new ConsumePower(1000, 100000, false));

            shoot = new ShootSpread() {{
                shots = 2;
                spread = 3;
            }};
            ammoTypes.putAll(
                    Liquids.water, new AroundBulletType() {{
                        ammoMultiplier = 5;

                        lifetime = 3600;
                        speed = 4;
                        damage = 500;
                        splashDamage = 500;
                        splashDamageRadius = 300;
                        trailLength = 25;
                        trailChance = 1;
                        status = StatusEffects.wet;
                        statusDuration = 240;

                        targetRange = 1000;
                        circleRange = 160;

                        statusTime = 35;
                        statusEffect = FStatusEffects.High_tensionV;
                        frontColor = backColor = lightColor = trailColor = Color.valueOf("01066FAA");
                        applyEffect = new WaveEffect() {{
                            colorFrom = colorTo = Color.valueOf("01066FAA");
                            lifetime = 240;
                        }};
                    }},
                    Liquids.slag, new BulletType() {{
                        ammoMultiplier = 1;
                        lifetime = 0;
                        damage = 0;
                        absorbable = hittable = reflectable = collides = false;

                        fragRandomSpread = 30;
                        fragBullets = 36;
                        fragLifeMax = fragLifeMin = 3;
                        fragVelocityMin = 1.5f;
                        fragVelocityMax = 1.8f;
                        fragBullet = new LiquidBulletType() {{
                            liquid = Liquids.slag;

                            homingDelay = 35;
                            homingPower = 0.1f;
                            homingRange = 400;

                            damage = 700;
                            splashDamage = 700;
                            splashDamageRadius = 300;

                            status = FStatusEffects.burningIV;
                            statusDuration = 300;
                            lightColor = Pal.darkFlame;
                        }};
                    }});

            requirements(Category.turret, ItemStack.with(Items.titanium, 4999,
                    Items.copper, 4999, Items.thorium, 4999, Items.silicon, 4999, Items.phaseFabric, 4999));
        }};
//======================================================================================================================
        eleFence = new ElectricFence("ele_fence") {{
            health = 300;
            size = 2;
            clipSize = 2;
            hasPower = true;

            maxLength = 200;
            maxConnect = 5;
            maxFenceSize = 80;
            eleDamage = 0.6f;
            air = true;

            consume(new ConsumePower(10, 1000, false));

            requirements(Category.defense, ItemStack.with(
                    Items.titanium, 150,
                    Items.copper, 300,
                    Items.silicon, 150
            ));
        }};
        eleFenceII = new ElectricFence("ele_fenceII") {{
            health = 1500;
            size = 3;
            clipSize = 3;
            hasPower = true;

            maxLength = 400;
            maxConnect = 15;
            maxFenceSize = 150;
            eleDamage = 1.2f;
            air = true;
            statusEffect = FStatusEffects.burningIV;
            statusTime = 300;

            consume(new ConsumePower(25, 5000, false));

            requirements(Category.defense, ItemStack.with(
                    Items.titanium, 350,
                    Items.copper, 600,
                    Items.silicon, 300
            ));
        }};
        eleFenceIII = new ElectricFence("ele_fenceIII") {{
            health = 3000;
            size = 4;
            clipSize = 4;
            hasPower = true;

            maxLength = 650;
            maxConnect = 25;
            maxFenceSize = 300;
            eleDamage = 2.5f;
            air = true;
            statusEffect = FStatusEffects.burningV;
            statusTime = 420;

            consume(new ConsumePower(75, 15000, false));

            requirements(Category.defense, ItemStack.with(
                    Items.titanium, 450,
                    Items.copper, 1000,
                    Items.silicon, 500
            ));
        }};
//======================================================================================================================
        slowProject = new DownProject("slow_project") {{
            requirements(Category.effect, with(Items.lead, 100, Items.titanium, 75, Items.silicon, 75, Items.plastanium, 30));
            range = 16;
            downSpeed = 0.9f;

            consumePower(3.50f);
            size = 2;
            consumeItem(Items.phaseFabric).boost();
        }};
    }
}