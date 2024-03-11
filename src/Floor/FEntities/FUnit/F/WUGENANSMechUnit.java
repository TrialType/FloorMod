package Floor.FEntities.FUnit.F;

import Floor.FEntities.FUnit.Override.FMechUnit;
import Floor.FEntities.FUnitType.WUGENANSMechUnitType;
import Floor.FTools.PhysicsWorldChanger;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.async.AsyncProcess;
import mindustry.async.PhysicsProcess;
import mindustry.entities.Damage;

import java.lang.reflect.Field;

import static mindustry.Vars.asyncCore;

public class WUGENANSMechUnit extends FMechUnit {
    private static final Seq<WUGENANSMechUnit> mec = new Seq<>();
    public boolean under = false;
    public boolean changing;
    public float power;
    public float outTimer = -1;
    public float landTimer = -1;
    public float time1;
    public float time2;
    public float timerChanging = 0;
    public static BeginChanger bc = new BeginChanger();
    public static PhysicsWorldChanger physicsWorldChanger;

    public static WUGENANSMechUnit create() {
        return new WUGENANSMechUnit();
    }

    @Override
    public int classId() {
        return 117;
    }

    public static void change() {
        try {
            Field file1 = PhysicsProcess.class.getDeclaredField("physics");
            file1.setAccessible(true);
            for (AsyncProcess process : asyncCore.processes) {
                if (process instanceof PhysicsProcess) {
                    PhysicsProcess.PhysicsWorld pw = (PhysicsProcess.PhysicsWorld) file1.get(process);
                    if (!(pw instanceof PhysicsWorldChanger)) {
                        Field field2 = PhysicsProcess.PhysicsWorld.class.getDeclaredField("bodies");
                        field2.setAccessible(true);
                        physicsWorldChanger = new PhysicsWorldChanger(Vars.world.getQuadBounds(new Rect()));
                        //noinspection unchecked
                        physicsWorldChanger.bodies = (Seq<PhysicsProcess.PhysicsWorld.PhysicsBody>) field2.get(pw);
                        file1.set(process, physicsWorldChanger);
                    }
                }
            }
            if (bc != null) {
                asyncCore.processes.add(bc);
                bc = null;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update() {
        if (mec.indexOf(this) < 0) {
            change();
            mec.add(this);
        }

        super.update();

        if (type instanceof WUGENANSMechUnitType wut) {
            if (power >= wut.needPower) {
                changing = true;
                timerChanging = timerChanging + Time.delta;
                if (timerChanging > 120) {
                    power = power - wut.needPower;
                    timerChanging = 0;
                    changing = false;
                    WUGENANSMechUnit wu = (WUGENANSMechUnit) wut.create(team);
                    wu.x = x + 0.5F;
                    wu.y = y + 0.5F;
                    wu.rotation = rotation;
                    wu.add();
                }
            }
            time1 = wut.outTime;
            time2 = wut.landTime;
            if (under && outTimer >= 0) {
                outTimer += Time.delta;
                elevation = Mathf.lerpDelta(elevation, 0, Time.delta / time1);
                if (outTimer >= time1) {
                    Damage.damage(team, x, y, wut.damageRadius, wut.upDamage);
                    under = false;
                    outTimer = -1;
                }
            } else if (!under && landTimer >= 0) {
                landTimer += Time.delta;
                elevation = Mathf.lerpDelta(elevation, -1, Time.delta / time2);
                if (landTimer >= time2) {
                    under = true;
                    landTimer = -1;
                }
            } else if (under) {
                elevation = -1;
            } else {
                landTimer = outTimer = -1;
                elevation = 0;
            }
        } else if (under) {
            elevation = 0;
            under = false;
            outTimer = -1;
        }
    }

    public static class BeginChanger implements AsyncProcess {
        @Override
        public void begin() {
            Seq<WUGENANSMechUnit> us = new Seq<>();
            for (WUGENANSMechUnit eu : mec) {
                if (eu.dead || eu.health <= 0) {
                    us.add(eu);
                }
            }
            mec.removeAll(us);
            for (WUGENANSMechUnit u : mec) {
                if (u.under) {
                    u.physref.body.layer = 4;
                }
            }
        }
    }
}
