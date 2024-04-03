package Floor.FContent;

import Floor.FEntities.FBlock.ElectricFence;
import Floor.FEntities.FBlock.GradeFactory;
import Floor.FEntities.FBlock.KnockingTurret;
import Floor.FEntities.FBlock.OwnerTurret;
import Floor.FEntities.FBulletType.AroundBulletType;
import Floor.FEntities.FBulletType.WindBulletType;
import Floor.FEntities.FBulletType.ownerBulletType;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Rand;
import arc.util.Time;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.FlarePart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
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
    public static Block outPowerFactory, inputPowerFactory;
    public static Block kt;
    public static Block eleFence, eleFenceII, eleFenceIII;
    public static Block fourNet, fireStream, smallWindTurret, middleWindTurret, largeWindTurret, stay, bind, fireBoost;

    public static void load() {
        outPowerFactory = new GradeFactory("out_power_factory") {{
            requirements(Category.units, with(Items.copper, 2000, Items.lead, 2000, Items.silicon, 2000));

            itemCapacity = 100 * 10;
            size = 9;
            consumePower(500f);

            constructTime = 60f * 10f;
        }};
        inputPowerFactory = new GradeFactory("input_power_factory") {{
            requirements(Category.units, with(Items.copper, 2000, Items.lead, 2000, Items.silicon, 2000));

            itemCapacity = 100 * 10;
            size = 9;
            consumePower(500f);

            constructTime = 60f * 10f;
            out = false;
        }};

//======================================================================================================================

        kt = new KnockingTurret("kt") {{
            hasPower = true;
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
            clipSize = 2;
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
                    Items.titanium, 1540,
                    Items.graphite, 1500,
                    Items.graphite, 2000,
                    Items.silicon, 1500,
                    Items.phaseFabric, 1500
            ));
        }};
        stay = new PowerTurret("stay") {{
            consume(new ConsumePower(2, 0, false));

            health = 200;
            size = 2;

            recoil = 0.8f;
            range = 100;
            reload = 30;
            consumesPower = true;
            hasPower = true;
            consumeAmmoOnce = false;
            canOverdrive = false;

            shootType = new EmpBulletType() {{
                speed = 2;
                damage = 8;

                lifetime = 30;
                rangeOverride = 60;
                lightColor = frontColor = backColor = Pal.redLight;
                status = StatusEffects.unmoving;
                statusDuration = 24;
                splashDamageRadius = 36;

                shootEffect = smokeEffect = Fx.none;
                hitEffect = Fx.none;
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
            range = 200;
            reload = 60;
            consumesPower = true;
            hasPower = true;
            consumeAmmoOnce = false;
            canOverdrive = false;

            shootType = new EmpBulletType() {{
                speed = 4.5f;
                damage = 12;

                rangeOverride = 200;
                lightColor = frontColor = backColor = Pal.redLight;
                status = StatusEffects.unmoving;
                statusDuration = 45;
                splashDamageRadius = 60;

                shootEffect = smokeEffect = Fx.none;
                hitEffect = Fx.none;
                despawnEffect = hitEffect;

                fragAngle = 360;
                fragBullets = 6;
                fragLifeMax = 4;
                fragLifeMin = 3.5f;
                fragOnAbsorb = false;
                fragOnHit = true;
                fragBullet = new EmpBulletType() {{
                    damage = 3;
                    speed = 2.5f;
                    splashDamageRadius = 6;
                    hitEffect = Fx.none;
                    despawnEffect = hitEffect;

                    status = FStatusEffects.suppressI;
                    statusDuration = 240;
                }};
            }};

            requirements(Category.turret, ItemStack.with(
                    Items.copper, 500,
                    Items.titanium, 350,
                    Items.graphite, 400
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

            ammoTypes.put(Items.blastCompound, new PointBulletType() {{
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

                fragBullet = new WindBulletType() {{
                    lifetime = 400;
                    damage = 0.5f;
                    windPower = 0.3f;
                    applyEffect = FStatusEffects.burningIII;

                    fillRange = false;
                    windEffect = new Effect(20, 80f, e -> {
                        FlarePart fp = new FlarePart() {{
                            sides = 3;
                            color1 = color2 = Pal.darkPyraFlame;
                            radius = radiusTo = 3;
                            innerRadScl = 0.4f;
                        }};
                        Effect ef = new ExplosionEffect() {{
                            lifetime = 90;
                            sparks = 0;
                            waveLife = 90;
                            waveColor = Pal.darkPyraFlame;
                            waveRadBase = 24;
                            smokes = 15;
                            smokeRad = 18;
                            smokeColor = Pal.darkPyraFlame;
                            smokeSizeBase = 1.5f;
                        }};

                        color(Pal.darkPyraFlame, Pal.darkPyraFlame, Pal.darkPyraFlame, e.fin());

                        randLenVectors(e.id, 1, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 40,
                                (x, y) -> {
                                    float angle = Angles.angle(x, y);
                                    float x1 = e.x + x;
                                    float y1 = e.y + y;
                                    DrawPart.params.set(e.fin(), 0f, 0f, 0f, 0f, 0f, x1, y1, angle);
                                    DrawPart.params.life = e.fin();
                                    fp.draw(DrawPart.params);
                                    if (e.lifetime - e.time <= Time.delta) {
                                        ef.at(x1, y1, angle, Pal.darkPyraFlame);
                                    }
                                }
                        );
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

                fragBullet = new WindBulletType() {{
                    lifetime = 400;
                    damage = 0.5f;
                    windPower = 0.3f;
                    applyEffect = FStatusEffects.breakHelII;

                    fillRange = false;
                    windEffect = new Effect(20, 80f, e -> {
                        FlarePart fp = new FlarePart() {{
                            sides = 3;
                            color1 = color2 = Color.valueOf("ebeef5");
                            radius = radiusTo = 3;
                            innerRadScl = 0.4f;
                        }};
                        Effect ef = new ExplosionEffect() {{
                            lifetime = 90;
                            sparks = 0;
                            waveLife = 90;
                            waveColor = Color.valueOf("ebeef5");
                            waveRadBase = 24;
                            smokes = 15;
                            smokeRad = 18;
                            smokeColor = Color.valueOf("ebeef5");
                            smokeSizeBase = 1.5f;
                        }};

                        color(Color.valueOf("ebeef5"), Color.valueOf("ebeef5"), Color.valueOf("ebeef5"), e.fin());

                        randLenVectors(e.id, 1, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 40,
                                (x, y) -> {
                                    float angle = Angles.angle(x, y);
                                    float x1 = e.x + x;
                                    float y1 = e.y + y;
                                    DrawPart.params.set(e.fin(), 0f, 0f, 0f, 0f, 0f, x1, y1, angle);
                                    DrawPart.params.life = e.fin();
                                    fp.draw(DrawPart.params);
                                    if (e.lifetime - e.time <= Time.delta) {
                                        ef.at(x1, y1, angle, Color.valueOf("ebeef5"));
                                    }
                                }
                        );
                    });
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

            ammoTypes.put(Items.blastCompound, new PointBulletType() {{
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

                fragBullet = new WindBulletType() {{
                    lifetime = 850;
                    damage = 1f;
                    windPower = 0.45f;
                    applyEffect = FStatusEffects.burningIV;

                    fillRange = false;
                    windEffect = new Effect(20, 80f, e -> {
                        FlarePart fp = new FlarePart() {{
                            sides = 3;
                            color1 = color2 = Pal.darkPyraFlame;
                            radius = radiusTo = 3;
                            innerRadScl = 0.4f;
                        }};
                        Effect ef = new ExplosionEffect() {{
                            lifetime = 90;
                            sparks = 0;
                            waveLife = 90;
                            waveColor = Pal.darkPyraFlame;
                            waveRadBase = 26;
                            smokes = 20;
                            smokeRad = 22;
                            smokeColor = Pal.darkPyraFlame;
                            smokeSizeBase = 1.75f;
                        }};

                        color(Pal.darkPyraFlame, Pal.darkPyraFlame, Pal.darkPyraFlame, e.fin());

                        randLenVectors(e.id, 2, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 40,
                                (x, y) -> {
                                    float angle = Angles.angle(x, y);
                                    float x1 = e.x + x;
                                    float y1 = e.y + y;
                                    DrawPart.params.set(e.fin(), 0f, 0f, 0f, 0f, 0f, x1, y1, angle);
                                    DrawPart.params.life = e.fin();
                                    fp.draw(DrawPart.params);
                                    if (e.lifetime - e.time <= Time.delta) {
                                        ef.at(x1, y1, angle, Pal.darkPyraFlame);
                                    }
                                }
                        );
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

                fragBullet = new WindBulletType() {{
                    lifetime = 850;
                    damage = 1f;
                    windPower = 0.45f;
                    applyEffect = FStatusEffects.breakHelIII;

                    fillRange = false;
                    windEffect = new Effect(20, 80f, e -> {
                        FlarePart fp = new FlarePart() {{
                            sides = 3;
                            color1 = color2 = Color.valueOf("ebeef5");
                            radius = radiusTo = 3;
                            innerRadScl = 0.4f;
                        }};
                        Effect ef = new ExplosionEffect() {{
                            lifetime = 90;
                            sparks = 0;
                            waveLife = 90;
                            waveColor = Color.valueOf("ebeef5");
                            waveRadBase = 26;
                            smokes = 20;
                            smokeRad = 22;
                            smokeColor = Color.valueOf("ebeef5");
                            smokeSizeBase = 1.75f;
                        }};

                        color(Color.valueOf("ebeef5"), Color.valueOf("ebeef5"), Color.valueOf("ebeef5"), e.fin());

                        randLenVectors(e.id, 2, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 40,
                                (x, y) -> {
                                    float angle = Angles.angle(x, y);
                                    float x1 = e.x + x;
                                    float y1 = e.y + y;
                                    DrawPart.params.set(e.fin(), 0f, 0f, 0f, 0f, 0f, x1, y1, angle);
                                    DrawPart.params.life = e.fin();
                                    fp.draw(DrawPart.params);
                                    if (e.lifetime - e.time <= Time.delta) {
                                        ef.at(x1, y1, angle, Color.valueOf("ebeef5"));
                                    }
                                }
                        );
                    });
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

            ammoTypes.put(Items.blastCompound, new PointBulletType() {{
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

                fragBullet = new WindBulletType() {{
                    lifetime = 1000;
                    damage = 2f;
                    windPower = 0.65f;
                    windWidth = 600;
                    windLength = 300;
                    applyEffect = FStatusEffects.burningV;

                    fillRange = false;
                    windEffect = new Effect(20, 80f, e -> {
                        FlarePart fp = new FlarePart() {{
                            sides = 3;
                            color1 = color2 = Pal.darkPyraFlame;
                            radius = radiusTo = 3;
                            innerRadScl = 0.4f;
                        }};
                        Effect ef = new ExplosionEffect() {{
                            lifetime = 90;
                            sparks = 0;
                            waveLife = 90;
                            waveColor = Pal.darkPyraFlame;
                            waveRadBase = 30;
                            smokes = 24;
                            smokeRad = 30;
                            smokeColor = Pal.darkPyraFlame;
                            smokeSizeBase = 2;
                        }};

                        color(Pal.darkPyraFlame, Pal.darkPyraFlame, Pal.darkPyraFlame, e.fin());

                        randLenVectors(e.id, 3, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 40,
                                (x, y) -> {
                                    float angle = Angles.angle(x, y);
                                    float x1 = e.x + x;
                                    float y1 = e.y + y;
                                    DrawPart.params.set(e.fin(), 0f, 0f, 0f, 0f, 0f, x1, y1, angle);
                                    DrawPart.params.life = e.fin();
                                    fp.draw(DrawPart.params);
                                    if (e.lifetime - e.time <= Time.delta) {
                                        ef.at(x1, y1, angle, Pal.darkPyraFlame);
                                    }
                                }
                        );
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

                fragBullet = new WindBulletType() {{
                    lifetime = 1000;
                    damage = 2f;
                    windPower = 0.65f;
                    windWidth = 600;
                    windLength = 300;
                    applyEffect = FStatusEffects.breakHelIV;

                    fillRange = false;
                    windEffect = new Effect(20, 80f, e -> {
                        FlarePart fp = new FlarePart() {{
                            sides = 3;
                            color1 = color2 = Color.valueOf("ebeef5");
                            radius = radiusTo = 3;
                            innerRadScl = 0.4f;
                        }};
                        Effect ef = new ExplosionEffect() {{
                            lifetime = 90;
                            sparks = 0;
                            waveLife = 90;
                            waveColor = Color.valueOf("ebeef5");
                            waveRadBase = 30;
                            smokes = 24;
                            smokeRad = 30;
                            smokeColor = Color.valueOf("ebeef5");
                            smokeSizeBase = 2;
                        }};

                        color(Color.valueOf("ebeef5"), Color.valueOf("ebeef5"), Color.valueOf("ebeef5"), e.fin());

                        randLenVectors(e.id, 3, e.finpow() * Math.max(windLength, windWidth / 2) * 1.4f, e.rotation, 40,
                                (x, y) -> {
                                    float angle = Angles.angle(x, y);
                                    float x1 = e.x + x;
                                    float y1 = e.y + y;
                                    DrawPart.params.set(e.fin(), 0f, 0f, 0f, 0f, 0f, x1, y1, angle);
                                    DrawPart.params.life = e.fin();
                                    fp.draw(DrawPart.params);
                                    if (e.lifetime - e.time <= Time.delta) {
                                        ef.at(x1, y1, angle, Color.valueOf("ebeef5"));
                                    }
                                }
                        );
                    });
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
            ammoTypes.putAll(Liquids.water, new AroundBulletType() {{
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

                statusTime = 240;
                statusEffect = FStatusEffects.High_tensionV;
                frontColor = backColor = lightColor = trailColor = Color.valueOf("01066FAA");
                applyEffect = new WaveEffect() {{
                    colorFrom = colorTo = Color.valueOf("01066FAA");
                    lifetime = 240;
                }};
            }}, Liquids.slag, new AroundBulletType() {{
                ammoMultiplier = 5;

                lifetime = 3600;
                speed = 4;
                damage = 700;
                splashDamage = 700;
                splashDamageRadius = 300;
                trailLength = 25;
                trailChance = 1;
                status = FStatusEffects.burningIV;
                statusDuration = 240;

                targetRange = 1000;
                circleRange = 160;

                statusTime = 240;
                statusEffect = FStatusEffects.burningV;
                frontColor = backColor = lightColor = trailColor = Pal.darkFlame;
                applyEffect = new WaveEffect() {{
                    colorFrom = colorTo = Pal.darkFlame;
                    lifetime = 240;
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
        eleFenceII = new ElectricFence("eleFenceII") {{
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
        eleFenceIII = new ElectricFence("eleFenceIII") {{
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
    }
}