package Floor.FContent;

import Floor.FAI.*;
import Floor.FEntities.FAbility.*;
import Floor.FEntities.FBulletType.*;
import Floor.FEntities.FUnit.F.*;
import Floor.FEntities.FUnit.Override.FLegsUnit;
import Floor.FEntities.FUnit.Override.FUnitEntity;
import Floor.FEntities.FUnitType.*;
import Floor.FTools.classes.BossList;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.ai.UnitCommand;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.HoverPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.type.weapons.RepairBeamWeapon;

import static arc.graphics.g2d.Lines.lineAngle;
import static arc.math.Angles.randLenVectors;

public class FUnits {
    public static UnitType transfer, shuttlev_I, bulletInterception_a;

    ////ENGSWEISBoss
    public static UnitType velocity, velocity_d, velocity_s, hidden, cave;

    //ENGSWEISFlying
    public static UnitType barb, hammer, buying, crazy, transition, shuttle;

    //ENGSWEISLand
    public static UnitType dive, befall;

    //WUGENANSMechUnit
    public static UnitType recluse;

    //special
    public static UnitType bulletInterception, rejuvenate, vibrate;

    //Test
    public static UnitType e;

    public static void load() {
        vibrate = new UnitType("vibrate") {{
            constructor = FLegsUnit::create;

            speed = 1;
            health = 20000;
            armor = 18;
            hitSize = 35;
            stepShake = 0;
            mechLandShake = 0;
            faceTarget = false;

            abilities.add(new StatusFieldAbility(StatusEffects.overclock, 180, 90, 80));
            abilities.add(new StatusFieldAbility(StatusEffects.shielded, 300, 420, 40));

            weapons.add(new Weapon() {{
                shootCone = 360;
                reload = 120;
                mirror = false;
                rotate = false;
                x = y = 0;
                shootX = shootY = 0;
                shoot = new ShootSummon(0, 0, 12, 15) {{
                    shots = 3;
                    shotDelay = 6;
                }};
                bullet = new EMPLarge() {{
                    reflectable = false;
                    damage = 140;
                    speed = 8;
                    lifetime = 240;
                    trailColor = Pal.lightishGray;
                    trailLength = 8;
                    trailWidth = 3;
                    homingPower = 0.1f;
                    homingDelay = 120;
                    homingRange = 1000;

                    maxLife = 300;
                    downRange = 200;
                    downDamage = 120;
                }};
            }});

            weapons.add(new Weapon() {{
                shootCone = 360;
                reload = 75;
                mirror = true;
                rotate = false;
                y = 0;
                x = 5;
                shootX = 0;
                shootY = -4;
                shoot = new ShootBarrel() {{
                    shots = 4;
                    shotDelay = 6;
                    barrels = new float[]{
                            0, 0, 95,
                            0, 0, 100,
                            0, 0, 105,
                            0, 0, 110
                    };
                }};
                bullet = new SummonBulletType() {{
                    width = height = 12;
                    damage = 120;
                    speed = 8;
                    lifetime = 240;
                    homingDelay = 0.1f;
                    homingRange = 1000;
                    homingPower = 0.1f;
                    trailColor = Pal.redDust;
                    trailLength = 16;
                    trailWidth = 2;
                    hittable = false;

                    summon = new LightningBulletType() {{
                        lightningLength = 25;
                        damage = 90;
                        speed = 80;

                        lightningColor = Pal.redDust;
                    }};
                    summonRange = 80;
                    summonNumber = 8;
                }};
            }});

            weapons.add(new Weapon() {{
                shootCone = 360;
                reload = 75;
                mirror = true;
                rotate = false;
                y = 0;
                x = 15;
                shootX = 0;
                shootY = -4;
                shoot = new ShootBarrel() {{
                    shots = 4;
                    shotDelay = 6;
                    barrels = new float[]{
                            0, 0, -5,
                            0, 0, 0,
                            0, 0, 5,
                            0, 0, 10
                    };
                }};
                bullet = new SummonBulletType() {{
                    width = height = 12;
                    damage = 100;
                    speed = 8;
                    lifetime = 240;
                    homingDelay = 0.1f;
                    homingRange = 1000;
                    homingPower = 0.1f;
                    trailColor = Pal.techBlue;
                    trailLength = 16;
                    trailWidth = 2;
                    absorbable = false;

                    summon = new MissileBulletType(5, 260) {{
                        lifetime = 60;
                        frontColor = backColor = trailColor = Pal.techBlue;
                    }};
                    summonRange = 120;
                    summonNumber = 8;
                    everySummonDelay = 9;
                }};
            }});
        }};
        rejuvenate = new UnitType("rejuvenate") {{
            constructor = LegsUnit::create;
            aiController = HealthOnlyAI::new;

            health = 30000;
            armor = 100;
            speed = 1.8f;
            accel = 0.9f;
            drag = 0.9f;
            hitSize = 35;
            range = maxRange = 1000;
            isEnemy = false;

            abilities.add(new StatusFieldAbility(FStatusEffects.healthIV, 2400, 1200, 160));
            abilities.add(new ShieldRegenFieldAbility(5000, 100000, 600, 160));

            weapons.add(new RepairBeamWeapon() {{
                range = maxRange = 1000;
                rotateSpeed = 12;
                reload = 1200;

                repairSpeed = 10;
                fractionRepairSpeed = 0.4f;
            }});
        }};
        bulletInterception_a = new UnitType("bulletInterception_a") {{
            constructor = FollowUnit::create;
            controller = u -> new FollowAI();

            health = 3000;
            speed = 2.5f;
            armor = 20;
            rotateSpeed = 5;
            drag = 0.2f;
            accel = 1;
            maxRange = range = 120;
            isEnemy = false;
            flying = true;
            logicControllable = false;
            faceTarget = false;
            playerControllable = false;
            allowedInPayloads = false;
            hittable = false;
            targetable = false;
            pickupUnits = false;
            useUnitCap = false;
            createWreck = false;
            hidden = true;
            alwaysUnlocked = true;
            weapons.add(new PointDefenseWeapon() {{
                mirror = false;
                reload = 0.1F;
                rotateSpeed = 360;
                recoil = 0;
                x = 0;
                shootOnDeath = true;
                targetInterval = 0;
                targetSwitchInterval = 0;
                bullet = new BasicBulletType() {{
                    damage = 500;
                    rangeOverride = 100;
                }};
            }});
        }};
        bulletInterception = new UpGradeUnitType("bulletInterception") {{
            constructor = SpawnerUnit::create;

            range = 1000;
            health = 10000;
            armor = 24;
            speed = 1;
            legCount = 6;
            legGroupSize = 2;
            legLength = 14;
            legContinuousMove = false;
            abilities.add(new ShieldArcAbility() {{
                regen = 2.5F;
                max = 50000;
                cooldown = 300;
                angle = 360;
                whenShooting = false;
            }});
            abilities.add(new OwnerUnitSpawnAbility(bulletInterception_a, 600, 0, 0) {{
                maxNum = 4;
            }});


            weapons.add(new PointDefenseWeapon() {{
                mirror = false;
                rotateSpeed = 360;
                reload = 0.1F;
                targetInterval = 0;
                targetSwitchInterval = 0;
                bullet = new BasicBulletType() {{
                    damage = 300;
                    maxRange = 100;
                    hitEffect = Fx.none;
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 120;
                mirror = false;
                x = 0;
                y = -10;
                shoot = new ShootBarrel() {{
                    shots = 2;
                    barrels = new float[]{0, 0, -90};
                }};
                shootSound = Sounds.artillery;
                bullet = new BasicBulletType() {{
                    intervalSpread = 30;
                    shootEffect = Fx.artilleryTrailSmoke;
                    status = FStatusEffects.slowII;
                    statusDuration = 90;
                    splashDamageRadius = 158;
                    speed = 7;
                    lifetime = 600;
                    fragOnHit = true;
                    fragOnAbsorb = true;
                    width = 0;
                    height = 0;
                    homingPower = 0.9F;
                    homingRange = 1000;
                    homingDelay = 120;
                    despawnEffect = new WaveEffect() {{
                        lifetime = 90;
                        colorFrom = new Color(170, 113, 113, 85);
                        colorTo = new Color(170, 113, 113, 102);
                        sizeTo = 0;
                        strokeFrom = 60;
                        strokeTo = 30;
                    }};
                    hitEffect = despawnEffect;
                    fragBullets = 1;
                    fragVelocityMin = 0;
                    fragVelocityMax = 0;
                    fragLifeMin = 1.5F;
                    fragLifeMax = 1.5F;
                    fragBullet = new BasicBulletType() {{
                        fragOnHit = true;
                        fragOnAbsorb = true;
                        collides = false;
                        status = FStatusEffects.suppressII;
                        statusDuration = 150;
                        splashDamage = 20;
                        splashDamageRadius = 158;
                        despawnEffect = new MultiEffect(new WaveEffect() {{
                            lifetime = 10;
                            colorFrom = new Color(170, 113, 113, 102);
                            colorTo = new Color(170, 113, 113, 51);
                            sizeTo = 0;
                            strokeFrom = 30;
                            strokeTo = 180;
                            sides = 120;

                        }}, new WaveEffect() {{
                            startDelay = 10;
                            lifetime = 20;
                            colorFrom = new Color(170, 113, 113, 102);
                            colorTo = new Color(170, 113, 113, 51);
                            sizeTo = 0;
                            strokeFrom = 180;
                            strokeTo = 180;
                            sides = 120;

                        }}, new WaveEffect() {{
                            startDelay = 10;
                            lifetime = 50;
                            colorFrom = new Color(170, 113, 113, 102);
                            colorTo = new Color(170, 113, 113, 51);
                            sizeFrom = 10;
                            sizeTo = 160;
                            strokeFrom = 10;
                            strokeTo = 0;
                            sides = 120;
                        }}, new ExplosionEffect() {{
                            lifetime = 60;
                            startDelay = 10;
                            waveLife = 0;
                            sparkRad = 158;
                            smokeRad = 158;
                            smokes = 200;
                            sparkStroke = 2;
                            sparkLen = 5;
                            sparks = 300;

                        }});
                        hitEffect = Fx.none;
                    }};
                }};
            }});
        }};
        shuttlev_I = new UnitType("shuttlev_I") {{
            constructor = UnitEntity::create;

            flying = true;
            health = 1000;
            armor = 47;
            speed = 9;
            drag = 0;
            accel = 0;
            logicControllable = false;
            playerControllable = false;
            useUnitCap = false;
            isEnemy = false;
            physics = false;
            faceTarget = false;
            allowedInPayloads = false;
            rotateSpeed = 0;
            alwaysUnlocked = true;
            hidden = true;
            engines.add(new UnitEngine(0, -2.8F, 0.3F, -90));
            engines.add(new UnitEngine(3, -2.8F, 1, -90));
            engines.add(new UnitEngine(-3, -2.8F, 1, -90));
            weapons.add(new Weapon() {{
                x = 2;
                reload = 0.125F;
                controllable = false;
                autoTarget = true;
                alwaysShooting = true;
                aiControllable = false;
                shoot = new ShootBarrel() {{
                    shots = 1;
                    shotDelay = 0;
                }};
                bullet = new BasicBulletType() {
                    {
                        absorbable = true;
                        speed = 1;
                        width = 4;
                        height = 4;
                        damage = 13;
                        lifetime = 750;
                        weaveScale = 1540;
                        weaveMag = 0.8F;
                        pierce = true;
                        pierceBuilding = true;
                        lightning = 3;
                        lightningDamage = 13;
                        lightningLength = 3;
                    }
                };
            }});
            weapons.add(new Weapon() {{
                alwaysShooting = true;
                shoot = new ShootBarrel() {{
                    firstShotDelay = 150;
                }};
                bullet = new ExplosionBulletType() {{
                    damage = 0;
                }};
            }});
        }};
        shuttle = new ENGSWEISUnitType("shuttle") {{
            constructor = ENGSWEISUnitEntity::create;
            aiController = StrongBoostAI::new;
            commands = new UnitCommand[]{UnitCommand.moveCommand, FCommands.STB};

            Speed1 = 3;
            Health2 = 57750;
            percent = 95;
            firstPercent = true;
            HitReload = 3600;
            reload = 3600;
            minSpeed = 10;
            defend = 75;
            power = 30;

            flying = true;
            health = 57750;
            armor = 20;
            speed = 3;
            hitSize = 49;
            drag = 0.1F;
            faceTarget = true;
            rotateSpeed = 3;
            maxRange = range = 40;
            strafePenalty = 0;
            engines.add(new UnitEngine(0, -25, 5, -90));
            engines.add(new UnitEngine(13.7F, -24, 4, -90));
            engines.add(new UnitEngine(-13.7F, -24, 4, -90));
            weapons.add(new Weapon() {{
                reload = 480;
                mirror = false;
                rotate = false;
                alternate = true;
                bullet = new BasicBulletType() {
                    {
                        keepVelocity = true;
                        spawnUnit = shuttlev_I;
                    }
                };
            }});
            weapons.add(new Weapon() {{
                reload = 37;
                mirror = true;
                x = 13.2F;
                y = 1.4F;
                rotate = false;
                bullet = new BasicBulletType() {{
                    scaleLife = false;
                    shrinkY = 3;
                    lightOpacity = 0.5F;
                    lightColor = new Color(243, 0, 0, 255);
                    shootEffect = Fx.none;
                    hitEffect = shootEffect;
                    despawnEffect = shootEffect;
                    drawSize = 2;
                    hitSize = 2;
                    lifetime = 20;
                    speed = 7;
                    damage = 25;
                    width = 2;
                    height = 2;
                    lightning = 12;
                    lightningColor = lightColor;
                    lightningDamage = 100;
                    lightningLength = 12;
                    lightningCone = 360;
                    fragBullets = 3;
                    fragBullet = new BasicBulletType() {{
                        width = 0;
                        height = 0;
                        speed = 0;
                        lifetime = 60;
                        lightning = 6;
                        lightningColor = lightColor;
                        lightningDamage = 25;
                        lightningLength = 15;
                    }};
                }};
            }});
        }};
        velocity_s = new ENGSWEISUnitType("velocity_s") {{
            constructor = ENGSWEISUnitEntity::create;
            speed = 6;
            hidden = true;
        }};
        velocity_d = new UnitType("velocity_d") {{
            constructor = PayloadUnit::create;
            health = 50000;
            armor = 12;
            speed = 0;
            rotateSpeed = 0;
            baseRotateSpeed = 0;
            hitSize = 20;
            hidden = true;
            createScorch = false;
            createWreck = false;

            weapons.add(new Weapon() {
                {
                    controllable = false;
                    autoTarget = true;
                    alwaysShooting = true;
                    aiControllable = false;
                    reload = 2000;
                    bullet = new BasicBulletType() {{
                        hitEffect = Fx.massiveExplosion;
                        lifetime = 0;
                        damage = 0;
                        splashDamage = 5000;
                        splashDamageRadius = 160;
                    }};
                }
            });

            weapons.add(new Weapon() {{
                controllable = false;
                autoTarget = true;
                alwaysShooting = true;
                aiControllable = false;
                shootSound = Sounds.explosion;
                shoot = new ShootBarrel() {{
                    firstShotDelay = 2000;
                }};
                reload = 2000;
                bullet = new BasicBulletType() {{
                    hitEffect = Fx.massiveExplosion;
                    killShooter = true;
                    lifetime = 0;
                    damage = 0;
                    splashDamage = 5000;
                    splashDamageRadius = 160;
                    spawnUnit = velocity_s;
                }};
            }});
        }};
        dive = new ChainUnitType("dive") {{
            constructor = ChainLegUnit::create;

            flying = false;
            speed = 0.8F;
            health = 900;
            armor = 18;
            hitSize = 13;
            percent = 13;

            weapons.add(new Weapon("a-weapon") {{
                top = false;
                mirror = false;
                shootY = 0f;
                reload = 4;
                ejectEffect = Fx.none;
                recoil = 1f;
                x = 0f;
                shootSound = Sounds.flame;
                shoot = new ShootSpread() {{
                    shots = 16;
                    spread = 11.25F;
                }};
                bullet = new BasicBulletType() {{
                    shootEffect = Fx.shootSmallFlame;
                    lifetime = 20;
                    width = 0;
                    height = 0;
                    speed = 2.3F;
                    damage = 20;
                    despawnEffect = hitEffect = Fx.none;

                    status = StatusEffects.burning;
                }};
            }});

            engines.add(
                    new UnitEngine(
                            (float) (hitSize * Math.sin(Math.toRadians(90)) / 2.0F),
                            (float) (hitSize * Math.cos(Math.toRadians(90)) / 2.0F),
                            4, 0
                    )
            );
            engines.add(
                    new UnitEngine(
                            (float) (hitSize * Math.sin(Math.toRadians(180)) / 2.0F),
                            (float) (hitSize * Math.cos(Math.toRadians(180)) / 2.0F),
                            4, -90
                    )
            );
            engines.add(
                    new UnitEngine(
                            (float) (hitSize * Math.sin(Math.toRadians(270)) / 2.0F),
                            (float) (hitSize * Math.cos(Math.toRadians(270)) / 2.0F),
                            4, 180
                    )
            );
        }};
        befall = new ChainUnitType("befall") {{
            constructor = ChainLegUnit::create;

            health = 6000;
            armor = 30;
            speed = 1;
            range = maxRange = 56;

            abilities.add(new ShieldRegenFieldAbility(200, 1000, 120, 0.01F));
            abilities.add(new TimeLargeDamageAbility());

            engines.add(
                    new UnitEngine(
                            (float) (hitSize * Math.sin(Math.toRadians(90)) / 2.0F),
                            (float) (hitSize * Math.cos(Math.toRadians(90)) / 2.0F),
                            5, 0
                    )
            );
            engines.add(
                    new UnitEngine(
                            (float) (hitSize * Math.sin(Math.toRadians(180)) / 2.0F),
                            (float) (hitSize * Math.cos(Math.toRadians(180)) / 2.0F),
                            5, -90
                    )
            );
            engines.add(
                    new UnitEngine(
                            (float) (hitSize * Math.sin(Math.toRadians(270)) / 2.0F),
                            (float) (hitSize * Math.cos(Math.toRadians(270)) / 2.0F),
                            5, 180
                    )
            );
        }};
        velocity = new ENGSWEISUnitType("velocity") {{
            constructor = ENGSWEISUnitEntity::create;

            damage = 50;
            HitReload = 15;
            Speed1 = 2.5F;
            minSpeed = 2;

            defend = 60;
            power = 50;
            flying = true;
            health = 100000;
            armor = 30;
            speed = 2.5F;
            hitSize = 35;
            createScorch = false;
            createWreck = false;

            abilities.add(new SpawnDeathAbility(velocity_d, 1, 0));

            weapons.add(new Weapon() {{
                reload = 300;
                controllable = false;
                mirror = false;
                alwaysShooting = true;
                shootSound = Sounds.none;
                bullet = new BulletType() {{
                    despawnEffect = Fx.none;
                    hitEffect = Fx.none;
                    speed = 0;
                    damage = 0;
                    lifetime = 0;
                    splashDamage = 20000;
                    splashDamageRadius = 56;
                    splashDamagePierce = true;
                    shootEffect = new WaveEffect() {{
                        lifetime = 120;
                        colorTo = Color.white;
                        colorFrom = Pal.darkPyraFlame;
                        sizeTo = 56;
                    }};
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 50;
                mirror = true;
                x = 0;
                y = 0;
                shoot = new ShootBarrel() {{
                    barrels = new float[]{0, -20, 70};
                }};
                bullet = new BasicBulletType() {{
                    rangeOverride = 500;
                    sprite = "jamming_bomb";
                    width = 24;
                    height = 55;
                    trailLength = 23;
                    trailWidth = 5;
                    speed = 11;
                    damage = 300;
                    lifetime = 210;
                    homingRange = 1000;
                    homingDelay = 30;
                    homingPower = 0.4F;
                    hitEffect = Fx.none;
                    despawnEffect = Fx.none;
                    absorbable = false;
                    reflectable = false;
                    keepVelocity = false;

                    intervalBullets = 5;
                    intervalDelay = 100;
                    bulletInterval = 15;
                    intervalBullet = new BasicBulletType(11, 2) {{
                        lifetime = 120;
                        trailChance = 1;
                        trailColor = Pal.lightTrail;
                        trailInterp = Interp.swingOut;

                        weaveMag = 3;
                        weaveScale = 15;

                        homingPower = 0.3f;
                        homingRange = 1000;
                        homingDelay = 20;
                    }};
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 50;
                mirror = true;
                x = 0;
                y = 0;
                shoot = new ShootBarrel() {{
                    barrels = new float[]{0, -25, 50};
                }};
                bullet = new BasicBulletType() {{
                    sprite = "fire_bomb";
                    absorbable = false;
                    reflectable = false;
                    width = 15;
                    height = 40;
                    trailLength = 20;
                    trailWidth = 4;
                    speed = 7;
                    damage = 300;
                    lifetime = 210;
                    homingRange = 1000;
                    homingDelay = 40;
                    homingPower = 0.3F;
                    hitEffect = Fx.none;
                    splashDamage = 0;
                    splashDamageRadius = 156;
                    splashDamagePierce = true;
                    statusDuration = 300;
                    keepVelocity = false;
                    status = FStatusEffects.burningV;
                    incendSpread = 360;
                    incendChance = 1;
                    incendAmount = 15;

                    fragBullets = 1;
                    fragLifeMin = 6;
                    fragLifeMax = 6;
                    fragVelocityMin = 0;
                    fragVelocityMax = 0;
                    fragBullet = new FreeBulletType() {{
                        collides = absorbable = hittable = reflectable = false;

                        point = false;
                        intervalRandomSpread = 360;
                        intervalBullets = 2;
                        intervalDelay = 10;
                        bulletInterval = 10;
                        //intervalHitEffect = ;
                        intervalBullet = new BulletType() {{
                            speed = 1.2f;
                            damage = 75;

                            incendSpread = 360;
                            incendChance = 1;
                            incendAmount = 6;

                            status = FStatusEffects.breakHelIII;
                            statusDuration = 240;
                        }};
                    }};
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 50;
                mirror = true;
                x = 0;
                y = 0;
                shoot = new ShootBarrel() {{
                    barrels = new float[]{0, -30, 30};
                }};
                bullet = new BulletType() {{
                    damage = 0;
                    speed = 1f;
                    lifetime = 120;
                    hittable = absorbable = reflectable = collides = false;
                    hitEffect = despawnEffect = new ExplosionEffect() {{
                        lifetime = 180;

                        smokeColor = Color.valueOf("110000DD");
                        sparks = 40;
                        smokes = 60;
                        smokeRad = 100;
                        smokeSize = 24;
                        smokeSizeBase = 2;
                    }};
                    hitSound = despawnSound = Sounds.artillery;

                    fragBullets = 15;
                    fragRandomSpread = 360;
                    fragLifeMin = 300;
                    fragLifeMax = 300;
                    fragVelocityMax = fragVelocityMin = 1.5f;
                    fragBullet = new BasicBulletType() {{
                        sprite = "high_explosive_projectile";
                        absorbable = false;
                        reflectable = false;
                        splashDamagePierce = true;

                        trailEffect = Fx.missileTrailSmoke;
                        trailChance = 0.4f;

                        width = 5;
                        height = 10;
                        speed = 6;
                        damage = 0;
                        lifetime = 300;

                        homingRange = 1000;
                        homingDelay = 0;
                        homingPower = 0.2F;
                        splashDamage = 80;
                        splashDamageRadius = 156;

                        status = FStatusEffects.breakHelIII;
                        statusDuration = 180;
                        keepVelocity = false;
                        despawnEffect = new WaveEffect() {{
                            strokeFrom = 14;
                            strokeTo = 10;
                            lifetime = 160;
                            sizeTo = 168;
                            colorFrom = Color.valueOf("110000DD");
                            colorTo = colorFrom;
                        }};
                        hitEffect = despawnEffect;
                    }};
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 1200;
                bullet = new PercentBulletType() {{
                    width = 0;
                    height = 0;
                    speed = 0.5F;
                    damage = 20000;
                    lifetime = 1200;
                    splashDamageRadius = 0;
                    shootEffect = Fx.none;
                    homingRange = 1000;
                    homingDelay = 50;
                    homingPower = 0.2F;
                    keepVelocity = false;
                    lightColor = Color.valueOf("8D0000FF");
                    scaleLife = false;
                    shrinkY = 0;
                    trailLength = 0;
                    trailChance = 1;
                    WL = true;
                    lightningPercent = 12;
                    lightning = 12;
                    lightningLength = 10;
                    trailEffect = new ParticleEffect() {{
                        particles = 80;
                        cone = 360;
                        lifetime = 4;
                        line = true;
                        lenFrom = 7;
                        lenTo = 7;
                        strokeFrom = 1;
                        strokeTo = 1;
                        cap = false;
                        colorFrom = Color.valueOf("914B00FF");
                        colorTo = colorFrom;
                    }};
                    parts.add(new ShapePart() {{
                        circle = true;
                        radius = 11.5F;
                        radiusTo = 11.5F;
                        color = Color.valueOf("914B00FF");
                    }});
                    parts.add(new HoverPart() {{
                        radius = 56;
                        color = Color.valueOf("914B00FF");
                        phase = 150;
                        sides = 18;
                    }});
                }};
            }});
        }};
        crazy = new ENGSWEISUnitType("crazy") {{
            constructor = ENGSWEISUnitEntity::create;
            aiController = StrongBoostAI::new;
            defaultCommand = new UnitCommand("crazy", "crazy", u -> new StrongBoostAI());
            commands = new UnitCommand[]{UnitCommand.moveCommand, defaultCommand};
            immunities.add(StatusEffects.slow);
            immunities.add(StatusEffects.unmoving);
            immunities.add(StatusEffects.wet);
            immunities.add(StatusEffects.sporeSlowed);

            Health2 = 10000;
            Speed1 = 3;

            HitReload = 18;
            percent = 1.5F;
            changeHel = 1000;
            damage = 200 * 1.4F;
            minSpeed = 4;
            firstPercent = true;

            defend = 100;
            power = 2;
            circleTarget = true;
            flying = true;
            targetAir = targetGround = true;
            maxRange = range = 40;
            rotateSpeed = 360;
            speed = 7;
            drag = 1F;
            accel = 0.5F;
            health = 6600;
            armor = 7;
        }};
        barb = new ENGSWEISUnitType("barb") {{
            constructor = ENGSWEISUnitEntity::create;
            aiController = StrongBoostAI::new;
            commands = new UnitCommand[]{UnitCommand.moveCommand, FCommands.STB};
            flying = true;
            targetAir = targetGround = true;

            Speed1 = 0.8F;
            damage = 0;
            percent = 10;
            changeHel = -1;
            firstPercent = true;
            reload = 3600;
            minSpeed = 7;
            delay = 90;
            HitReload = 90;

            range = 40;
            maxRange = range;
            speed = 1.3F;
            rotateSpeed = 5;
            faceTarget = true;
            health = 140;
            armor = 2;
            engineOffset = 7;
            engineSize = 2.5F;
            itemCapacity = 30;
            hitSize = 5;

            weapons.add(new Weapon("barb-weapon") {{
                reload = 30;
                y = 3.8F;
                x = 2.7F;
                top = false;
                bullet = new BasicBulletType() {{
                    speed = 3.7F;
                    damage = 15;
                    lifetime = 60;
                }};
            }});
        }};
        transfer = new TileMinerUnitType("transfer") {{
            constructor = TileMiner::create;
            defaultCommand = new UnitCommand("TileMine", "TileMine", TileMinerAI::new);
            aiController = TileMinerAI::new;
            commands = new UnitCommand[]{defaultCommand, new UnitCommand("TilePut", "TilePut", TilePutAI::new)};
            controller = u -> !playerControllable || (u.team.isAI() && !u.team.rules().rtsAi) ? aiController.get() : new PoseBridgeCommand();
            hidden = true;
            alwaysUnlocked = true;
            isEnemy = false;
            useUnitCap = false;

            flying = true;
            health = 100;
            speed = 1.7F;
            armor = 2;
            drag = 0.8F;
            accel = 0.8F;
            rotateSpeed = 4;

            mineFloor = true;
            mineWalls = true;
            mineRange = 0;
            mineSpeed = 1;
            mineTier = 5;
        }};
        buying = new ENGSWEISUnitType("buying") {{
            constructor = TileSpawnerUnit::create;
            aiController = StrongBoostAI::new;
            commands = new UnitCommand[]{UnitCommand.moveCommand, FCommands.STB};

            minSpeed = 5;
            reload = 3600;
            HitReload = 3600;
            percent = 30;
            firstPercent = true;
            Speed1 = 0.3F;
            Health2 = 2000;
            number = 3;

            defend = 15;
            power = 5;

            rotateSpeed = 5.4f;
            buildSpeed = 3f;

            drownTimeMultiplier = 4f;

            hitSize = 24f;
            flying = true;
            speed = 0.4f;
            engineOffset = 9;
            engineSize = 3;
            accel = 0.9F;
            drag = 0.9F;
            maxRange = range = 40;

            health = 2000;
            armor = 9f;

            abilities.add(new OwnerUnitSpawnAbility(transfer, 2400, 1, 1));
            abilities.add(new ShieldRegenFieldAbility(25, 900, 90, range));

            weapons.add(new Weapon() {{
                range = 150;
                reload = 180;
                y = 3.8F;
                x = 2.7F;
                top = false;
                alternate = false;
                shoot = new ShootBarrel() {{
                    shots = 30;
                    firstShotDelay = 60;
                    shotDelay = 1;
                }};
                bullet = new BasicBulletType() {{
                    homingDelay = 45;
                    homingPower = 0.1F;
                    homingRange = 1000;
                    inaccuracy = 30;
                    weaveRandom = true;
                    speed = 3.7F;
                    damage = 1;
                    lifetime = 240;
                    splashDamage = 150;
                    splashDamageRadius = 25.5F;
                    trailChance = 1F;
                }};
            }});
        }};
        hammer = new ENGSWEISUnitType("hammer") {{
            constructor = ENGSWEISUnitEntity::create;
            aiController = StrongBoostAI::new;
            commands = new UnitCommand[]{UnitCommand.moveCommand, FCommands.STB};

            health = 250;
            speed = 1.2F;
            range = maxRange = 36;
            armor = 4;
            accel = 0.9F;
            drag = 0.9F;
            flying = true;

            percent = 20;
            firstPercent = true;
            Health2 = 350;
            minSpeed = 8;
            Speed1 = 1.0F;
            HitReload = 3600;
            reload = 3600;

            defend = 100;
            power = 2;

            abilities.add(new ForceFieldAbility(hitSize * 2, 1, 100, 600));
            abilities.add(new EMPAbility());

            weapons.add(new Weapon() {{
                reload = 42;
                x = y = 0;
                shoot = new ShootSpread() {{
                    shots = 8;
                    spread = 22.5F;
                }};
                bullet = new BasicBulletType() {{
                    damage = 28;
                    lifetime = 90;
                    speed = 4;
                }};
            }});
        }};
        transition = new ENGSWEISUnitType("transition") {{
            constructor = ENGSWEISUnitEntity::create;
            aiController = StrongBoostAI::new;
            commands = new UnitCommand[]{UnitCommand.moveCommand, FCommands.STB};

            health = 22000;
            speed = 2F;
            range = maxRange = 40;
            armor = 14;
            accel = 0.9F;
            drag = 0.9F;
            flying = true;
            hitSize = 40;

            percent = 75;
            firstPercent = true;
            Health2 = 18000;
            minSpeed = 8;
            Speed1 = 1.3F;
            HitReload = 3600;
            reload = 3600;

            defend = 20;
            power = 20;

            weapons.add(new Weapon() {{
                range = 320;
                reload = 780;
                mirror = false;
                shoot = new ShootBarrel() {{
                    firstShotDelay = 120;
                }};
                bullet = new SqrtDamageBullet() {{
                    damage = 600;
                    speed = 0;
                    lifetime = 1200;
                    halfWidth = 120;
                    sqrtLength = 320;
                    chargeEffect = new ParticleEffect() {{
                        lifetime = 120;
                        sizeFrom = 3;
                        sizeTo = 0;
                        interp = Interp.zero;
                        colorFrom = Pal.redLight;
                        colorTo = Pal.redDust;
                        particles = 15;
                        cone = 60;
                    }};
                }};
            }});

            weapons.add(new Weapon() {{
                reload = 5;
                alternate = false;
                bullet = new BasicBulletType() {{
                    damage = 35;
                    speed = 7;
                    lifetime = 90;
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 900;
                alternate = false;
                y = 10;

                bullet = new PercentBulletType() {{
                    homingRange = 1000;
                    homingPower = 0.1F;
                    percent = 3.5F;
                    firstPercent = true;
                    speed = 5.5F;
                    lifetime = 120;
                    trailChance = 1;
                    trailColor = Pal.redLight;
                    hitEffect = Fx.dynamicWave;
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 900;
                alternate = false;
                y = -10;
                bullet = new PercentBulletType() {{
                    homingRange = 1000;
                    homingPower = 0.1F;
                    percent = 7.5F;
                    firstPercent = true;
                    speed = 5.5F;
                    lifetime = 120;
                    trailChance = 1;
                    trailColor = Pal.redLight;
                    hitEffect = Fx.dynamicWave;
                }};
            }});
        }};
        hidden = new UnitType("hidden") {{
            constructor = HiddenUnit::create;
            aiController = HiddenAI::new;
            commands = new UnitCommand[]{UnitCommand.moveCommand, new UnitCommand("findAuto", "findAuto", u -> new HiddenAI())};

            targetAir = targetGround = true;
            range = maxRange = 200;
            hitSize = 40;
            flying = true;
            health = 3000000;
            armor = 150;
            speed = 0.1F;
            rotateSpeed = 12;
            drag = 0.9F;
            accel = 0.9F;
            faceTarget = false;

            immunities.addAll(
                    FStatusEffects.fastII,
                    StatusEffects.slow, StatusEffects.wet,
                    StatusEffects.unmoving, FStatusEffects.StrongStop,
                    StatusEffects.fast, StatusEffects.freezing
            );

            abilities.add(new StatusFieldAbility(FStatusEffects.fastII, 600, 610, 100));
            abilities.add(new ShieldRegenFieldAbility(10000, 100000, 610, 100));
            abilities.add(new UnitSpawnAbility(hammer, 600, 1, 1));
            abilities.add(new UnitSpawnAbility(hammer, 600, -1, 1));
            abilities.add(new UnitSpawnAbility(hammer, 600, -1, -1));
            abilities.add(new UnitSpawnAbility(hammer, 600, 1, -1));

            weapons.add(new Weapon() {{
                reload = 45f;
                mirror = false;
                y = x = 0;
                shoot = new ShootPattern() {{
                    shots = 5;
                }};
                bullet = new BasicBulletType() {{
                    range = maxRange = 1000;
                    inaccuracy = 60;
                    width = height = 0;
                    damage = 0;
                    lifetime = 1800;
                    speed = 0.9f;
                    lightning = 24;
                    lightningDamage = 800;
                    lightningColor = Color.valueOf("436E2D");
                    lightningLength = 35;
                    lightningLengthRand = 15;
                }};
            }});
        }};
        recluse = new WUGENANSMechUnitType("recluse") {{
            constructor = WUGENANSMechUnit::create;
            aiController = LandMoveAI::new;
            commands = new UnitCommand[]{UnitCommand.moveCommand, new UnitCommand("lm", "lm", u -> new LandMoveAI())};

            health = 1200;
            upDamage = 13;
            speed = 1.2f;

            weapons.add(new Weapon() {{
                reload = 180;
                bullet = new EMPLarge() {{
                    trailEffect = new MultiEffect(new Effect(90, e -> {
                        Draw.color(Color.valueOf("990000"));
                        Lines.stroke(3 * (1 - e.fin()));
                        Lines.ellipse(e.x, e.y, 36, 0.75f * e.fin(), 0.15f * e.fin(), e.rotation + 90);
                        Drawf.light(e.x, e.y, e.fout() * 27, Color.valueOf("990000"), 0.7f);
                    }), new Effect(15, e -> randLenVectors(e.id + 1, 12,
                            27 * e.fin(), e.rotation, 60, (x, y) -> {
                                Draw.color(Color.valueOf("990000"));
                                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin());
                                Drawf.light(e.x + x, e.y + y, e.fout() * 4f, Color.valueOf("990000"), 0.7f);
                            })));
                    trailRotation = true;
                    trailWidth = 1;
                    trailLength = 19;
                    trailChance = 0.4f;
                    trailColor = Color.valueOf("990000");
                    shootEffect = Fx.shootBigColor;
                    smokeEffect = Fx.shootSmokeSquareBig;

                    width = height = 4;
                    frontColor = backColor = Color.valueOf("990000aa");
                    lifetime = 120;
                    speed = 13;
                    damage = 1;
                }};
            }});
        }};
        cave = new UnitType("cave") {{
            constructor = CaveUnit::create;

            hittable = false;
            targetable = false;
            playerControllable = logicControllable = false;
            faceTarget = false;
            physics = false;

            rotateSpeed = 0;
            hitSize = 45;
            health = 1;
            armor = 0;
            speed = 0F;
            drag = 1;
            range = maxRange = 1000;
            targetAir = targetGround = true;

            for (int i = 0; i < 2; i++) {
                int finalI = i;
                weapons.add(new Weapon() {{
                    rotate = true;
                    rotateSpeed = 12;
                    reload = 20;
                    x = 25;
                    y = finalI % 2 == 0 ? 15 : -15;
                    shootSound = Sounds.none;

                    shoot = new ShootBarrel() {{
                        shots = 24;
                        firstShotDelay = 30;
                        shotDelay = 10;
                    }};

                    bullet = new LiquidBulletType() {{
                        liquid = Liquids.water;

                        range = maxRange = 1000;
                        lifetime = 600;
                        speed = 3;
                        damage = 80;
                        buildingDamageMultiplier = 0.75f;
                        splashDamage = 10;
                        splashDamageRadius = 40;
                        splashDamagePierce = true;

                        lightColor = trailColor = Color.valueOf("01066FAA");
                        hitEffect = despawnEffect = Fx.none;
                        shootEffect = Fx.none;

                        status = FStatusEffects.High_tensionII;
                        statusDuration = 240;
                    }};
                }});
            }
            weapons.add(new Weapon() {{
                reload = 3;
                x = y = shootY = shootX = 0;
                mirror = false;
                inaccuracy = 360;
                rotate = false;
                alwaysShooting = true;

                bullet = new BasicBulletType() {{
                    width = height = 12;
                    shrinkX = shrinkY = 0;
                    damage = 14;
                    speed = 5;
                    lifetime = 1200;
                    status = FStatusEffects.High_tension;
                    statusDuration = 90;
                    spin = 12;
                    frontColor = backColor = Pal.techBlue;
                    trailChance = 0.05f;
                    trailEffect = new Effect(90, e -> {
                        Draw.color(Pal.techBlue);
                        Fill.circle(e.x, e.y, 3);
                    });
                    weaveMag = 0.1f;
                    weaveScale = 25;
                    shootEffect = Fx.none;
                    hitEffect = despawnEffect = Fx.smoke;
                }};
            }});

            weapons.add(new Weapon() {{
                rotate = true;
                shootCone = 360;
                rotateSpeed = 120;
                reload = 20;
                shootSound = Sounds.laser;

                bullet = new LaserBulletType(135) {{
                    rangeOverride = range = 2000;
                    length = 2000;
                    width = 45;
                    sideLength = 20;

                    status = FStatusEffects.slowII;
                    statusDuration = 240;

                    laserAbsorb = true;
                    colors = new Color[]{Pal.techBlue, Pal.techBlue, Pal.techBlue};
                }};
            }});
        }};

        BossList.list.add(velocity);
        BossList.list.add(velocity_d);
        BossList.list.add(velocity_s);
        BossList.list.add(hidden);
        BossList.list.add(cave);
    }
}