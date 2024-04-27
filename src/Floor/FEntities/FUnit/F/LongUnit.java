package Floor.FEntities.FUnit.F;

import Floor.FEntities.FUnit.Override.FCrawlUnit;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;

import java.util.Random;

public class LongUnit extends FCrawlUnit {
    public boolean killed;
    public int frontId = -1;
    public int nextId = -1;
    public LongUnit front = null;
    public LongUnit next = null;
    public boolean together = true;
    public boolean flying = false;

    @Override
    public int classId() {
        return 120;
    }

    protected LongUnit() {
        super();
        killed = false;
    }

    public static LongUnit create() {
        return new LongUnit();
    }

    public void update() {
        if (frontId >= 0) {
            front = (LongUnit) Groups.unit.getByID(frontId);
            frontId = -1;
        }
        if (nextId >= 0) {
            next = (LongUnit) Groups.unit.getByID(nextId);
            nextId = -1;
        }

        super.update();
    }

    public void kill() {
        killed = true;
        if (together) {
            Seq<LongUnit> all = all();
            for (LongUnit lu : all) {
                if (!lu.killed) {
                    lu.kill();
                }
            }
        } else {
            if ((new Random()).nextInt(10) + 1 <= againLevel) {
                LongUnit fu = (LongUnit) type.create(team);
                fu.x(x);
                fu.y(y);
                fu.rotation(rotation);
                fu.together = false;
                fu.front = front;
                if (fu.front != null) {
                    fu.front.next = fu;
                }
                fu.next = next;
                if (fu.next != null) {
                    fu.next.front = fu;
                }
                fu.setDamageLevel(damageLevel / 2);
                fu.setHealthLevel(healthLevel / 2);
                fu.setSpeedLevel(speedLevel / 2);
                fu.setShieldLevel(shieldLevel / 2);
                fu.setReloadLevel(reloadLevel / 2);
                fu.setLevel(damageLevel / 2 + healthLevel / 2 + speedLevel / 2 + shieldLevel / 2 + reloadLevel / 2);
                if (shieldLevel >= 2) {
                    fu.sfa(shieldLevel / 2);
                }
                fu.health(maxHealth / 10 * againLevel);
                fu.add();
            }
        }

        if (!this.dead && !Vars.net.client() && this.type.killable) {
            Call.unitDeath(this.id);
        }
    }

    public void again() {
        if ((new Random()).nextInt(10) + 1 <= againLevel) {
            LongUnit fu = (LongUnit) type.create(team);
            fu.x(x);
            fu.y(y);
            fu.rotation(rotation);
            fu.setDamageLevel(damageLevel / 2);
            fu.setHealthLevel(healthLevel / 2);
            fu.setSpeedLevel(speedLevel / 2);
            fu.setShieldLevel(shieldLevel / 2);
            fu.setReloadLevel(reloadLevel / 2);
            fu.setLevel(damageLevel / 2 + healthLevel / 2 + speedLevel / 2 + shieldLevel / 2 + reloadLevel / 2);
            if (shieldLevel >= 2) {
                fu.sfa(shieldLevel / 2);
            }
            fu.health(maxHealth / 10 * againLevel);
            fu.add();
        }
    }


    @Override
    public void rawDamage(float amount) {
        if (together) {
            Seq<LongUnit> all = all();
            float damage = amount * (front == null ? 0.5f : 1) / all.size;
            for (LongUnit lu : all) {
                lu.allDamage(damage);
            }
        } else {
            allDamage(amount * (front == null ? 0.5f : 1));
        }
    }

    public void allDamage(float amount) {
        super.rawDamage(amount);
    }

    public Seq<LongUnit> all() {
        Seq<LongUnit> all = new Seq<>();
        LongUnit t = this;
        all.add(this);
        while (t.next != null) {
            all.add(t.next);
            t = t.next;
        }
        t = this;
        while (t.front != null) {
            all.add(t.front);
            t = t.front;
        }
        return all;
    }
}
