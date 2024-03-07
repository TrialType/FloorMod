package Floor.FEntities.FUnitType;

import Floor.FAI.StrongBoostAI;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Teamc;
import mindustry.type.UnitType;

public class ENGSWEISUnitType extends UnitType {
    public float damage = 1;
    public float percent = 1;
    public boolean firstPercent = false;
    public float changeHel = -1;
    public float HitReload = Float.MAX_VALUE;
    public float minSpeed = Float.MAX_VALUE;
    public float defend = 1.0F / 3;
    public int power = 1;
    public long reload = 1800;
    public float delay = 90;
    public Effect boostEffect = Fx.healWave;
    public float Speed1 = 0.5F;
    public float Health2 = 70;
    public int exchangeTime = 120;
    public ENGSWEISUnitType(String name) {
        super(name);
        aiController = StrongBoostAI::new;
    }
}