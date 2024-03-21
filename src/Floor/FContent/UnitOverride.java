package Floor.FContent;

import Floor.FAI.MissileAI_II;
import Floor.FEntities.FAbility.TimeLargeDamageAbility;
import Floor.FEntities.FBulletType.FlyContinuousLaserBulletType;
import Floor.FEntities.FBulletType.MissileExplosionBulletType;
import Floor.FEntities.FUnit.F.TimeUpGradeUnit;
import Floor.FEntities.FUnit.Override.*;
import Floor.FEntities.FWeapon.SuctionWeapon;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Sounds;
import mindustry.gen.TimedKillUnit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class UnitOverride {
    public static void load() {
        UnitTypes.dagger.constructor = FMechUnit::create;
        UnitTypes.mace.constructor = FMechUnit::create;
        UnitTypes.fortress.constructor = FMechUnit::create;
        UnitTypes.scepter.constructor = FMechUnit::create;
        UnitTypes.reign.constructor = FMechUnit::create;

        UnitTypes.nova.constructor = FMechUnitLegacyNova::create;
        UnitTypes.pulsar.constructor = FMechUnitLegacyNova::create;
        UnitTypes.quasar.constructor = FMechUnitLegacyNova::create;
        UnitTypes.vela.constructor = FMechUnitLegacyNova::create;
        UnitTypes.corvus.constructor = FMechUnitLegacyNova::create;

        UnitTypes.crawler.constructor = FMechUnit::create;
        UnitTypes.atrax.constructor = FLegsUnit::create;
        UnitTypes.spiroct.constructor = FLegsUnit::create;
        UnitTypes.arkyid.constructor = FLegsUnit::create;
        UnitTypes.toxopid.constructor = FLegsUnit::create;

        UnitTypes.flare.constructor = FUnitEntity::create;
        UnitTypes.horizon.constructor = FUnitEntity::create;
        UnitTypes.zenith.constructor = FUnitEntity::create;
        UnitTypes.antumbra.constructor = FUnitEntity::create;
        UnitTypes.eclipse.constructor = TimeUpGradeUnit::create;

        UnitTypes.poly.constructor = FUnitEntity::create;
        UnitTypes.mega.constructor = FPayloadUnit::create;
        UnitTypes.quad.constructor = FPayloadUnit::create;
        UnitTypes.oct.constructor = FPayloadUnit::create;

        UnitTypes.risso.constructor = FUnitWaterMove::create;
        UnitTypes.minke.constructor = FUnitWaterMove::create;
        UnitTypes.bryde.constructor = FUnitWaterMove::create;
        UnitTypes.sei.constructor = FUnitWaterMove::create;
        UnitTypes.omura.constructor = FUnitWaterMove::create;

        UnitTypes.retusa.constructor = FUnitWaterMove::create;
        UnitTypes.oxynoe.constructor = FUnitWaterMove::create;
        UnitTypes.cyerce.constructor = FUnitWaterMove::create;
        UnitTypes.aegires.constructor = FUnitWaterMove::create;
        UnitTypes.navanax.constructor = FUnitWaterMove::create;

        UnitTypes.alpha.constructor = FUnitEntity::create;
        UnitTypes.beta.constructor = FUnitEntity::create;
        UnitTypes.gamma.constructor = FUnitEntity::create;

        UnitTypes.stell.constructor = FTankUnit::create;
        UnitTypes.locus.constructor = FTankUnit::create;
        UnitTypes.precept.constructor = FTankUnit::create;
        UnitTypes.vanquish.constructor = FTankUnit::create;
        UnitTypes.conquer.constructor = FTankUnit::create;

        UnitTypes.merui.constructor = FLegsUnit::create;
        UnitTypes.cleroi.constructor = FLegsUnit::create;
        UnitTypes.anthicus.constructor = FLegsUnit::create;
        UnitTypes.tecta.constructor = FLegsUnit::create;
        UnitTypes.collaris.constructor = FLegsUnit::create;

        UnitTypes.elude.constructor = FElevationMoveUnit::create;
        UnitTypes.avert.constructor = FUnitEntity::create;
        UnitTypes.obviate.constructor = FUnitEntity::create;
        UnitTypes.quell.constructor = FPayloadUnit::create;
        UnitTypes.disrupt.constructor = FPayloadUnit::create;

        UnitTypes.evoke.constructor = FPayloadUnit::create;
        UnitTypes.incite.constructor = FPayloadUnit::create;
        UnitTypes.emanate.constructor = FPayloadUnit::create;

        /*=================================================================*/
        /*=================================================================*/
        /*=================================================================*/
        /*=================================================================*/

        UnitTypes.scepter.health = 31500;
        UnitTypes.scepter.weapons.get(0).bullet.damage = 150;
        UnitTypes.scepter.weapons.get(0).bullet.lightningDamage = 60;
        UnitTypes.scepter.weapons.get(1).bullet.damage = 30;

        UnitTypes.reign.health = 84000;
        UnitTypes.reign.weapons.get(0).bullet.damage = 240;
        UnitTypes.reign.weapons.get(0).bullet.splashDamage = 54;
        UnitTypes.reign.weapons.get(0).bullet.fragBullet.damage = 60;
        UnitTypes.reign.weapons.get(0).bullet.fragBullet.splashDamage = 45;
        UnitTypes.reign.weapons.get(0).bullet.fragBullets = 6;

        /*-----------------------------------------------------------------------------*/

        UnitTypes.arkyid.health = 28000;


        UnitTypes.toxopid.health = 77000;

        /*-----------------------------------------------------------------------------*/

        UnitTypes.antumbra.health = 25200;


        UnitTypes.eclipse.health = 77000;
        UnitTypes.eclipse.weapons.add(new Weapon() {{
            reload = 480;
            bullet = new BulletType() {{
                lifetime = 0;
                spawnUnit = new UnitType("eclipse1") {{
                    lifetime = 1260;

                    constructor = TimedKillUnit::create;
                    controller = u -> new MissileAI_II();
                    hidden = true;
                    flying = true;
                    health = 38500;
                    speed = 0.1f;
                    armor = 13;
                    weapons.add(new Weapon() {{
                        alwaysShooting = true;
                        controllable = aiControllable = false;
                        reload = 1202;
                        shoot = new ShootPattern() {{
                            firstShotDelay = 1200;
                        }};
                        bullet = new MissileExplosionBulletType(0, 0) {{
                            spawnUnit = new UnitType("eclipse2") {{
                                range = maxRange = 1000;

                                lifetime = 1201;

                                constructor = TimedKillUnit::create;
                                controller = u -> new MissileAI_II();
                                hidden = true;
                                flying = true;
                                targetable = false;
                                hittable = false;
                                speed = 11;
                                weapons.add(new Weapon() {{
                                    rangeOverride = 1;
                                    bullet = new ExplosionBulletType(10000, 500);
                                }});
                            }};
                        }};
                    }});
                    weapons.add(new Weapon() {{
                        reload = 20;
                        shoot = new ShootPattern() {{
                            shots = 3;
                        }};
                        bullet = new BasicBulletType() {{
                            inaccuracy = 24;

                            homingRange = 1000;
                            homingDelay = 20;
                            homingPower = 0.07f;
                            lifetime = 120;
                            speed = 5;
                            damage = 120;
                        }};
                    }});
                }};
            }};
        }});

        /*-----------------------------------------------------------------------------*/

        UnitTypes.quad.health = 22000;


        UnitTypes.oct.health = 77000;

        /*-----------------------------------------------------------------------------*/

        UnitTypes.sei.health = 22000;

        UnitTypes.omura.health = 77000;

        /*-----------------------------------------------------------------------------*/

        UnitTypes.aegires.health = 42000;
        UnitTypes.aegires.abilities.add(new TimeLargeDamageAbility(2));

        UnitTypes.navanax.health = 70000;

        /*-----------------------------------------------------------------------------*/

        UnitTypes.precept.health = 17500;

        UnitTypes.vanquish.health = 38500;

        UnitTypes.conquer.health = 77000;

        /*-----------------------------------------------------------------------------*/

        UnitTypes.obviate.health = 8050;

        UnitTypes.quell.health = 22000;
        UnitTypes.quell.weapons.get(0).bullet.spawnUnit.weapons.get(0).bullet.splashDamage = 220f;

        UnitTypes.disrupt.health = 42000;
        UnitTypes.disrupt.weapons.get(0).bullet.spawnUnit.weapons.get(0).bullet.splashDamage = 280f;
        /*-----------------------------------------------------------------------------*/

        UnitTypes.anthicus.health = 10150;
        UnitTypes.anthicus.weapons.get(0).bullet.spawnUnit.weapons.get(0).bullet.splashDamage = 280f;

        UnitTypes.tecta.health = 26550;

        UnitTypes.collaris.health = 63000;
        BulletType b = UnitTypes.collaris.weapons.get(0).bullet;
        UnitTypes.collaris.targetAir = true;
        b.damage = 520;
        b.splashDamage = 85f;
        b.splashDamageRadius = 20f;
        b.bulletInterval = 20;
        b.intervalBullets = 3;
        b.intervalRandomSpread = 30;
        b.intervalAngle = 0;
        b.intervalBullet = new BasicBulletType() {{
            lifetime = 180;
            damage = 120;
            speed = 6;
            homingPower = 0.08F;
            homingRange = 1000;
            homingDelay = 30;
            trailChance = 1F;
            trailColor = Pal.techBlue;
            trailWidth = 2.2f;
            trailLength = 30;
        }};
        b.fragBullet.damage = 100;
        b.fragBullet.splashDamage = 92f;
        b.fragBullet.splashDamageRadius = 30f;

        /*-----------------------------------------------------------------------------*/

        UnitTypes.vela.health = 22000;
        UnitTypes.vela.weapons.get(0).bullet = new FlyContinuousLaserBulletType() {{
            damage = 35f;
            length = 180f;
            hitEffect = Fx.hitMeltHeal;
            drawSize = 420f;
            lifetime = 160f;
            shake = 1f;
            despawnEffect = Fx.smokeCloud;
            smokeEffect = Fx.none;

            chargeEffect = Fx.greenLaserChargeSmall;

            incendChance = 0.1f;
            incendSpread = 5f;
            incendAmount = 1;

            healPercent = 1f;
            collidesTeam = true;

            colors = new Color[]{Pal.heal.cpy().a(.2f), Pal.heal.cpy().a(.5f), Pal.heal.cpy().mul(1.2f), Color.white};
        }};
        UnitTypes.vela.rotateSpeed = 5.4F;

        UnitTypes.corvus.health = 77000;
        UnitTypes.corvus.weapons.remove(0);
        UnitTypes.corvus.weapons.add(new SuctionWeapon("corvus-weapon") {{
            range = 200;
            time = 600;

            shootSound = Sounds.laserblast;
            chargeSound = Sounds.lasercharge;
            soundPitchMin = 1f;
            top = false;
            mirror = false;
            shake = 14f;
            shootY = 5f;
            x = y = 0;
            reload = 350f;
            recoil = 0f;

            cooldownTime = 350f;

            shootStatusDuration = 60f * 2f;
            shootStatus = StatusEffects.unmoving;
            shoot.firstShotDelay = Fx.greenLaserCharge.lifetime;
            parentizeEffects = true;

            bullet = new LaserBulletType() {{
                length = 460f;
                damage = 1120f;
                width = 75f;

                lifetime = 65f;

                lightningSpacing = 35f;
                lightningLength = 25;
                lightningDelay = 1.1f;
                lightningLengthRand = 5;
                lightningDamage = 150;
                lightningAngleRand = 40f;
                largeHit = true;
                lightColor = lightningColor = Pal.heal;

                chargeEffect = Fx.greenLaserCharge;

                healPercent = 25f;
                collidesTeam = true;

                sideAngle = 15f;
                sideWidth = 0f;
                sideLength = 0f;
                colors = new Color[]{Pal.heal.cpy().a(0.4f), Pal.heal, Color.white};
            }};
        }});

        /*-----------------------------------------------------------------------------*/

    }
}
