package Floor.FEntities.FUnitType;

import Floor.FAI.StrongBoostAI;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.meta.Stat;

public class ENGSWEISUnitType extends UpGradeUnitType {
    public float damage = 1;
    public float percent = 1;
    public boolean firstPercent = false;
    public float changeHel = -1;
    public float HitReload = 3600;
    public float minSpeed = Float.MAX_VALUE;
    public float defend = 1.0F / 3;
    public int power = 1;
    public long reload = 1800;
    public float delay = 90;
    public Effect boostEffect = Fx.missileTrailSmoke;
    public float Speed1 = 0.5F;
    public float Health2 = 70;
    public int exchangeTime = 120;
    public int number = 0;
    public ENGSWEISUnitType(String name) {
        super(name);
        aiController = StrongBoostAI::new;
    }
    @Override
    public void setStats() {
        super.setStats();
        stats.add(new Stat("speed1"),Speed1);
        stats.add(new Stat("health2"),Health2);
        stats.add(new Stat("change_time"),exchangeTime);
        stats.add(new Stat("min_number"),number);
        stats.add(new Stat("boost_reload"),reload);
        stats.add(new Stat("hit_damage"),damage);
        stats.add(new Stat("hit_percent"),percent);
        stats.add(new Stat("first_percent"),firstPercent);
        stats.add(new Stat("change_hel"),changeHel);
    }
}