package Floor.FContent;

import Floor.FEntities.FBlock.ElectricFence;
import Floor.FEntities.FBlock.GradeFactory;
import Floor.FEntities.FBlock.KnockingTurret;
import Floor.FEntities.FBlock.OwnerTurret;
import Floor.FEntities.FBulletType.AroundBulletType;
import Floor.FEntities.FBulletType.WindBulletType;
import Floor.FEntities.FBulletType.ownerBulletType;
import arc.graphics.Color;
import mindustry.content.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.WaveEffect;
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

import static mindustry.type.ItemStack.with;

public class FBlocks {
    public static Block outPowerFactory, inputPowerFactory;
    public static Block kt;
    public static Block eleFence;
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
            requirements(Category.turret, ItemStack.with(Items.titanium, 170,
                    Items.copper, 240, Items.graphite, 350));

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
            range = 400;
            shootY = 35;
            reload = 5;
            recoil = 3;
            inaccuracy = 15;

            bullet = new ownerBulletType(8f, 4) {{
                lifetime = 6.4f;
                splashDamage = 3;
                splashDamageRadius = 20;
                pierce = true;
                pierceBuilding = true;
                status = FStatusEffects.burningII;
                statusDuration = 240;
            }};

            requirements(Category.turret, ItemStack.with(Items.copper, 40, Items.graphite, 30));
        }};
        stay = new PowerTurret("stay") {{
            consume(new ConsumePower(3, 0, false));

            health = 200;
            size = 2;

            recoil = 0.8f;
            range = 100;
            reload = 30;
            consumesPower = true;
            hasPower = true;
            consumeAmmoOnce = false;

            shootType = new LaserBoltBulletType(2, 8) {{
                lifetime = 30;
                rangeOverride = 60;
                lightColor = frontColor = backColor = Pal.redLight;
                status = StatusEffects.unmoving;
                statusDuration = 24;
                splashDamageRadius = 45;

                shootEffect = smokeEffect = Fx.none;
                hitEffect = new WaveEffect() {{
                    lifetime = 26;
                    sizeTo = sizeFrom = 45;
                    strokeFrom = 2;
                    strokeTo = 0;
                    colorFrom = colorTo = Pal.redLight;
                }};
                despawnEffect = hitEffect;
            }};

            requirements(Category.turret, ItemStack.with(Items.copper, 40, Items.graphite, 30));
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

            shootType = new LaserBoltBulletType(4.5f, 16) {{
                pierce = true;
                pierceCap = 2;
                rangeOverride = 200;
                lightColor = frontColor = backColor = Pal.redLight;
                status = StatusEffects.unmoving;
                statusDuration = 45;
                splashDamageRadius = 60;

                shootEffect = smokeEffect = Fx.none;
                hitEffect = new WaveEffect() {{
                    lifetime = 30;
                    sizeTo = sizeFrom = 60;
                    strokeFrom = 2;
                    strokeTo = 0;
                    colorFrom = colorTo = Pal.redLight;
                }};
                despawnEffect = hitEffect;
            }};

            requirements(Category.turret, ItemStack.with(Items.copper, 350, Items.titanium, 650, Items.graphite, 500));
        }};
        smallWindTurret = new ItemTurret("small_wind_turret") {{
            requirements(Category.turret, ItemStack.with(Items.titanium, 50,
                    Items.copper, 120, Items.graphite, 120));
            coolant = consume(new ConsumeCoolant(0.3f));

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
                trailEffect = new ExplosionEffect() {{
                    smokeColor = Pal.darkerGray;
                    sparks = 0;
                    waveLife = 0;
                    smokes = 15;
                    lifetime = 180;
                }};

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
                    collides = false;
                    absorbable = hittable = reflectable = false;
                    lifetime = 600;
                    damage = 0.5f;
                    windPower = 0.45f;
                    applyEffect = FStatusEffects.burningIII;
                }};
            }});
        }};
        middleWindTurret = new ItemTurret("middle_wind_turret") {{
            requirements(Category.turret, ItemStack.with(Items.titanium, 900,
                    Items.copper, 1000, Items.graphite, 780));

            consume(new ConsumePower(500, 1500, false));
            coolant = consume(new ConsumeCoolant(0.8f));

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

                trailEffect = new ExplosionEffect() {{
                    smokeColor = Pal.darkPyraFlame;
                    sparks = 0;
                    waveLife = 0;
                    smokes = 40;
                    smokeSize = 6;
                    lifetime = 240;
                }};

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
                    collides = false;
                    absorbable = hittable = reflectable = false;
                    lifetime = 850;
                    damage = 1f;
                    windPower = 0.65f;
                    applyEffect = FStatusEffects.burningIV;
                }};
            }});
        }};
        largeWindTurret = new ItemTurret("large_wind_turret") {{
            requirements(Category.turret, ItemStack.with(Items.titanium, 2000,
                    Items.copper, 2600, Items.graphite, 1800));

            consume(new ConsumePower(5000, 20000, false));
            coolant = consume(new ConsumeCoolant(1.5f));

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

                trailEffect = new ExplosionEffect() {{
                    smokeColor = Pal.darkPyraFlame;
                    sparks = 0;
                    waveLife = 0;
                    smokes = 60;
                    smokeSize = 12;
                    lifetime = 300;
                }};

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
                    collides = false;
                    absorbable = hittable = reflectable = false;
                    lifetime = 1000;
                    damage = 2f;
                    windPower = 0.85f;
                    windWidth = 600;
                    windLength = 300;
                    applyEffect = FStatusEffects.burningV;
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
            eleDamage = 0.3f;
            air = true;

            consume(new ConsumePower(15, 1000, true));

            requirements(Category.defense, ItemStack.with(Items.titanium, 150,
                    Items.copper, 300, Items.silicon, 150));
        }};
    }
}
