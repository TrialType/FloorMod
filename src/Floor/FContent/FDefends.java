package Floor.FContent;

import Floor.FEntities.FBlock.KnockingTurret;
import Floor.FEntities.FBulletType.AroundBulletType;
import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.pattern.ShootPattern;
import mindustry.entities.pattern.ShootSpread;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumePower;

public class FDefends {
    public static Block kt, fourNet;

    public static void load() {
        kt = new KnockingTurret("kt") {{
            hasPower = true;
            health = 650;

            requirements(Category.effect, ItemStack.with(Items.copper, 1));
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
            inaccuracy = 0;

            hasPower = true;
            consumesPower = true;

            consumers = new Consume[]{new ConsumePower(1000, 1000, true)};

            shoot = new ShootSpread() {{
                shots = 2;
                spread = 3;
            }};
            ammoTypes.put(Liquids.water, new AroundBulletType() {{
                lifetime = 3600;
                speed = 4;
                damage = 500;
                splashDamage = 500;
                splashDamageRadius = 500;
                trailLength = 25;
                trailChance = 1;

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
