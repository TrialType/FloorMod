package Floor.FEntities.FBulletType;

import arc.math.Angles;
import arc.math.Rand;
import arc.util.Time;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;

public class SummonBulletType extends BasicBulletType {
    public BulletType summon = null;
    public float summonRange = 150;
    public int summonNumber = 12;
    public float summonRad = 360;
    public float summonDelay = 0;
    public float everySummonDelay = 0;

    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        if (summon != null) {
            if (summonDelay > 0) {
                Time.run(summonDelay, () -> createSummon(b.owner, b.team, x, y, summonNumber));
            } else {
                createSummon(b.owner, b.team, x, y, summonNumber);
            }
        }
    }

    public void createSummon(Entityc owner, Team team, float x, float y, int number) {
        Rand r = new Rand();
        if (everySummonDelay <= 0) {
            for (int i = 0; i < number; i++) {
                summon(owner, team, x, y, r.range(summonRad));
            }
        } else {
            summon(owner, team, x, y, r.range(summonRad));

            if (number > 1) {
                Time.run(everySummonDelay, () -> createSummon(owner, team, x, y, number - 1));
            }
        }
    }

    public void summon(Entityc owner, Team team, float x, float y, float rotate) {
        float dx = (float) (summonRange * Math.cos(rotate)), dy = (float) (summonRange * Math.sin(rotate));
        Bullet bu = summon.create(owner, team, dx + x, dy + y, 0);
        bu.vel.set(-dx, -dy).setLength(summon.speed);
        bu.rotation(Angles.angle(-dx, -dy));
    }
}
