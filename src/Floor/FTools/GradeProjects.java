package Floor.FTools;

import Floor.FContent.FItems;
import Floor.FEntities.FBulletType.PercentBulletType;
import arc.struct.Seq;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.StatusEffect;
import mindustry.type.Weapon;

public class GradeProjects {
    public static Weapon weapon;
    public static BulletType bullet;
    public static Ability ability;
    public static StatusEffect statusEffect;
    public static int[] allProLev = new int[12];
    public static final Seq<active> all = new Seq<>(new active[]{healthU -> {
        healthU.maxHealth = (float) (healthU.maxHealth * (allProLev[1] * 0.45 + 1));
    }, speedU -> {

    }, copperU -> {
        bullet = new PercentBulletType(){{

        }};
    }, laserU -> {

    }, reloadU -> {

    }, shieldU -> {

    }, splashU -> {

    }, pricesU -> {

    }, slowU -> {

    }, knockU -> {

    }, percentU -> {

    }});

    private GradeProjects() {
    }

    public static void updateProjects() {
        Item[] items;
        for (int i = 0; i < FItems.allProject.length - 1; i++) {
            items = FItems.allProject[i];
            for (int j = items.length; j > 0; j--) {
                if (items[j - 1].unlocked()) {
                    allProLev[i] = j;
                    break;
                } else if (j == 1) {
                    allProLev[i] = 0;
                    break;
                }
            }
        }
    }

    public interface active {
        void get(Unit unit);
    }
}
