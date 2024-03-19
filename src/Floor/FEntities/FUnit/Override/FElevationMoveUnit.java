package Floor.FEntities.FUnit.Override;

import Floor.FTools.FUnitUpGrade;
import Floor.FTools.LayAble;
import Floor.FTools.UnitChainAble;
import arc.math.Mathf;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.ElevationMoveUnit;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.io.TypeIO;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FElevationMoveUnit extends ElevationMoveUnit implements FUnitUpGrade, LayAble {
    private final Seq<Integer> idList = new Seq<>();
    public Seq<Unit> units = new Seq<>();
    public Map<String, Integer> unitAbilities = new HashMap<>();

    protected int damageLevel = 0;
    protected int speedLevel = 0;
    protected int healthLevel = 0;
    protected int reloadLevel = 0;
    protected int againLevel = 0;
    protected int shieldLevel = 0;
    protected ShieldRegenFieldAbility sfa = null;

    public int level = 0;
    public float exp = 0;

    protected FElevationMoveUnit() {
        this.applied = new Bits(Vars.content.getBy(ContentType.status).size);
        this.resupplyTime = Mathf.random(10.0F);
        this.statuses = new Seq<>();
    }

    public static FElevationMoveUnit create() {
        return new FElevationMoveUnit();
    }

    @Override
    public int classId() {
        return 109;
    }

    @Override
    public void read(Reads read) {
        short REV = read.s();
        if (REV == 0) {
            TypeIO.readAbilities(read, this.abilities);
            this.ammo = read.f();
            this.controller = TypeIO.readController(read, this.controller);
            this.elevation = read.f();
            this.flag = read.d();
            this.health = read.f();
            this.isShooting = read.bool();
            this.mineTile = TypeIO.readTile(read);
            TypeIO.readMounts(read, this.mounts);
            this.plans = TypeIO.readPlansQueue(read);
            this.rotation = read.f();
            this.shield = read.f();
            this.spawnedByCore = read.bool();
            this.stack = TypeIO.readItems(read, this.stack);
            int statuses_LENGTH = read.i();
            this.statuses.clear();

            for (int INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
                StatusEntry statuses_ITEM = TypeIO.readStatus(read);
                if (statuses_ITEM != null) {
                    this.statuses.add(statuses_ITEM);
                }
            }

            this.team = TypeIO.readTeam(read);
            this.type = Vars.content.getByID(ContentType.unit, read.s());
            this.updateBuilding = read.bool();
            this.vel = TypeIO.readVec2(read, this.vel);
            this.x = read.f();
            this.y = read.f();
            this.afterRead();


            int number = read.i();
            for (int i = 0; i < number; i++) {
                idList.add(read.i());
            }
            number = read.i();
            for (int i = 0; i < number; i++) {
                unitAbilities.put(read.str(), read.i());
            }
            level = read.i();
            exp = read.f();

            damageLevel = read.i();
            speedLevel = read.i();
            reloadLevel = read.i();
            healthLevel = read.i();
            againLevel = read.i();
            shieldLevel = read.i();
            if (shieldLevel > 0) {
                sfa = new ShieldRegenFieldAbility(maxHealth / 100 * shieldLevel,
                        maxHealth * shieldLevel / 10, 120, 60);
            }
        } else {
            throw new IllegalArgumentException("Unknown revision '" + REV + "' for entity type 'elude'");
        }
    }

    @Override
    public void write(Writes write) {
        write.s(0);
        TypeIO.writeAbilities(write, this.abilities);
        write.f(this.ammo);
        TypeIO.writeController(write, this.controller);
        write.f(this.elevation);
        write.d(this.flag);
        write.f(this.health);
        write.bool(this.isShooting);
        TypeIO.writeTile(write, this.mineTile);
        TypeIO.writeMounts(write, this.mounts);
        write.i(this.plans.size);

        int INDEX;
        for (INDEX = 0; INDEX < this.plans.size; ++INDEX) {
            TypeIO.writePlan(write, this.plans.get(INDEX));
        }

        write.f(this.rotation);
        write.f(this.shield);
        write.bool(this.spawnedByCore);
        TypeIO.writeItems(write, this.stack);
        write.i(this.statuses.size);

        for (INDEX = 0; INDEX < this.statuses.size; ++INDEX) {
            TypeIO.writeStatus(write, this.statuses.get(INDEX));
        }

        TypeIO.writeTeam(write, this.team);
        write.s(this.type.id);
        write.bool(this.updateBuilding);
        TypeIO.writeVec2(write, this.vel);
        write.f(this.x);
        write.f(this.y);

        write.i(units.size);
        for (Unit u : units) {
            write.i(u.id);
        }
        write.i(unitAbilities.size());
        for (String s : unitAbilities.keySet()) {
            write.str(s);
            write.i(unitAbilities.get(s));
        }
        write.i(level);
        write.f(exp);
        write.i(damageLevel);
        write.i(speedLevel);
        write.i(reloadLevel);
        write.i(healthLevel);
        write.i(againLevel);
        write.i(shieldLevel);
    }

    @Override
    public void update() {
        super.update();

        speedMultiplier += speedLevel * 0.2f;
        damageMultiplier += damageLevel * 0.2f;
        reloadMultiplier += reloadLevel * 0.2f;
        heal(maxHealth * healthLevel * 0.01f);
        if (sfa != null) {
            sfa.update(this);
        }

        if (units.size == 0 && idList.size > 0) {
            for (int i : idList) units.add(Groups.unit.getByID(i));
            idList.clear();
        }

        for (Unit u : units) {
            if (!u.within(x, y, u.speed() * 40)) {
                if (u instanceof UnitChainAble uca) {
                    uca.UnderUnit(null);
                    uca.upon(false);
                }
                units.remove(u);
                continue;
            }
            if ((u.dead || u.health() <= 0) || (u instanceof UnitChainAble uca && uca.UnderUnit() != this)) {
                units.remove(u);
                continue;
            }
            if (u instanceof UnitChainAble uca && uca.UnderUnit() == null) uca.UnderUnit(this);
        }
    }

    @Override
    public void kill() {
        if ((new Random()).nextInt(10) + 1 <= againLevel) {
            FElevationMoveUnit fu = (FElevationMoveUnit) type.create(team);
            fu.x(x);
            fu.y(y);
            fu.rotation(rotation);
            fu.setDamageLevel(damageLevel / 2);
            fu.setHealthLevel(healthLevel / 2);
            fu.setSpeedLevel(speedLevel / 2);
            fu.setShieldLevel(shieldLevel / 2);
            fu.setReloadLevel(reloadLevel / 2);
            fu.setLevel(damageLevel / 2 + healthLevel / 2 + speedLevel / 2 + shieldLevel / 2 + reloadLevel / 2);
            if(shieldLevel >= 2){
                fu.sfa = new ShieldRegenFieldAbility(maxHealth / 200 * shieldLevel,
                        maxHealth * shieldLevel / 20, 120, 60);
            }
            fu.health(maxHealth / 10 * againLevel);
            fu.add();
        }
        super.kill();
    }

    @Override
    public Map<String, Integer> getMap() {
        return unitAbilities;
    }

    @Override
    public Seq<Unit> getUit() {
        return units;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int l) {
        level = l;
    }

    @Override
    public float getExp() {
        return exp;
    }

    @Override
    public void addExp(float exp) {
        this.exp = exp + this.exp;
    }

    @Override
    public int number() {
        int number = 0;
        while (exp > (4 + level) * maxHealth / 10) {
            exp = exp - (4 + level) * maxHealth / 10;
            level++;
            number++;
        }
        return number;
    }

    public int getDamageLevel() {
        return damageLevel;
    }

    public void setDamageLevel(int damageLevel) {
        this.damageLevel = damageLevel;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public void setSpeedLevel(int speedLevel) {
        this.speedLevel = speedLevel;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(int healthLevel) {
        this.healthLevel = healthLevel;
    }

    public int getReloadLevel() {
        return reloadLevel;
    }

    public void setReloadLevel(int reloadLevel) {
        this.reloadLevel = reloadLevel;
    }

    public int getAgainLevel() {
        return againLevel;
    }

    public void setAgainLevel(int againLevel) {
        this.againLevel = againLevel;
    }

    public int getShieldLevel() {
        return shieldLevel;
    }

    public void setShieldLevel(int shieldLevel) {
        this.shieldLevel = shieldLevel;
    }
    @Override
    public void sfa(int level) {
        sfa = new ShieldRegenFieldAbility(maxHealth / 100 * shieldLevel,
                maxHealth * shieldLevel / 10, 120, 60);
    }
}
