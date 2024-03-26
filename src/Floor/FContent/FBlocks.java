package Floor.FContent;

import Floor.FEntities.FBlock.GradeFactory;
import Floor.FEntities.FBlock.KnockingTurret;
import Floor.FEntities.FBulletType.AroundBulletType;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;

import static mindustry.type.ItemStack.with;

public class FBlocks {
    public static Block outPowerFactory, inputPowerFactory;
    public static Block kt;
    public static Block fourNet, filariasis;

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

        filariasis = new ItemTurret("filariasis") {{
            requirements(Category.turret, ItemStack.with(Items.titanium, 50,
                    Items.copper, 120, Items.graphite, 120));

            hasItems = true;
            itemCapacity = 32;
            maxAmmo = 32;
            consumeAmmoOnce = false;
            shootX = shootY = 0;
            inaccuracy = 15;

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
                status = StatusEffects.burning;
                statusDuration = 300;
                hitEffect = despawnEffect = Fx.none;
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
            liquidCapacity = 5;
            consumeAmmoOnce = false;
            inaccuracy = 0;

            hasPower = true;
            consumesPower = true;

            consumers = new Consume[]{new ConsumePower(1000, 1000, true)};

            shoot = new ShootSpread() {{
                shots = 2;
                spread = 3;
            }};
            ammoTypes.put(Liquids.water, new AroundBulletType() {{
                ammoMultiplier = 100;

                lifetime = 3600;
                speed = 4;
                damage = 500;
                splashDamage = 500;
                splashDamageRadius = 500;
                trailLength = 25;
                trailChance = 1;
                status = StatusEffects.wet;
                statusDuration = 240;

                targetRange = 1000;
                circleRange = 160;

                statusTime = 240;
                statusEffect = FStatusEffects.High_tensionIII;
                frontColor = backColor = lightColor = trailColor = Color.valueOf("01066FAA");
                applyEffect = new WaveEffect() {{
                    colorFrom = colorTo = Color.valueOf("01066FAA");
                    lifetime = 180;
                }};
            }});

            requirements(Category.turret, ItemStack.with(Items.titanium, 4999,
                    Items.copper, 4999, Items.thorium, 4999, Items.silicon, 4999, Items.phaseFabric, 4999));
        }};
    }
}
