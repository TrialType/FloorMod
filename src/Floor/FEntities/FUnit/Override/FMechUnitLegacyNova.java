package Floor.FEntities.FUnit.Override;

import Floor.FTools.FUnitUpGrade;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.MechUnitLegacyNova;
import mindustry.io.TypeIO;

import java.util.HashMap;
import java.util.Map;

public class FMechUnitLegacyNova extends MechUnitLegacyNova implements FUnitUpGrade {
    public Map<String, Integer> unitAbilities = new HashMap<>();
    public int level = 0;
    public float exp = 0;

    protected FMechUnitLegacyNova() {
    }

    public static FMechUnitLegacyNova create() {
        return new FMechUnitLegacyNova();
    }

    @Override
    public int classId() {
        return 111;
    }

    @Override
    public void read(Reads read) {
        short REV = read.s();
        int statuses_LENGTH;
        int INDEX;
        StatusEntry statuses_ITEM;
        if (REV == 0) {
            this.ammo = read.f();
            read.f();
            this.baseRotation = read.f();
            this.controller = TypeIO.readController(read, this.controller);
            read.bool();
            this.elevation = read.f();
            this.health = read.f();
            this.isShooting = read.bool();
            this.mineTile = TypeIO.readTile(read);
            TypeIO.readMounts(read, this.mounts);
            this.plans = TypeIO.readPlansQueue(read);
            this.rotation = read.f();
            this.shield = read.f();
            this.spawnedByCore = read.bool();
            this.stack = TypeIO.readItems(read, this.stack);
            statuses_LENGTH = read.i();
            this.statuses.clear();

            for (INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
                statuses_ITEM = TypeIO.readStatus(read);
                if (statuses_ITEM != null) {
                    this.statuses.add(statuses_ITEM);
                }
            }

            this.team = TypeIO.readTeam(read);
            this.type = Vars.content.getByID(ContentType.unit, read.s());
            this.x = read.f();
            this.y = read.f();
        } else if (REV == 1) {
            this.ammo = read.f();
            read.f();
            this.baseRotation = read.f();
            this.controller = TypeIO.readController(read, this.controller);
            this.elevation = read.f();
            this.health = read.f();
            this.isShooting = read.bool();
            this.mineTile = TypeIO.readTile(read);
            TypeIO.readMounts(read, this.mounts);
            this.plans = TypeIO.readPlansQueue(read);
            this.rotation = read.f();
            this.shield = read.f();
            this.spawnedByCore = read.bool();
            this.stack = TypeIO.readItems(read, this.stack);
            statuses_LENGTH = read.i();
            this.statuses.clear();

            for (INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
                statuses_ITEM = TypeIO.readStatus(read);
                if (statuses_ITEM != null) {
                    this.statuses.add(statuses_ITEM);
                }
            }

            this.team = TypeIO.readTeam(read);
            this.type = Vars.content.getByID(ContentType.unit, read.s());
            this.x = read.f();
            this.y = read.f();
        } else if (REV == 2) {
            this.ammo = read.f();
            read.f();
            this.baseRotation = read.f();
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
            statuses_LENGTH = read.i();
            this.statuses.clear();

            for (INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
                statuses_ITEM = TypeIO.readStatus(read);
                if (statuses_ITEM != null) {
                    this.statuses.add(statuses_ITEM);
                }
            }

            this.team = TypeIO.readTeam(read);
            this.type = Vars.content.getByID(ContentType.unit, read.s());
            this.x = read.f();
            this.y = read.f();
        } else if (REV == 3) {
            this.ammo = read.f();
            read.f();
            this.baseRotation = read.f();
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
            statuses_LENGTH = read.i();
            this.statuses.clear();

            for (INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
                statuses_ITEM = TypeIO.readStatus(read);
                if (statuses_ITEM != null) {
                    this.statuses.add(statuses_ITEM);
                }
            }

            this.team = TypeIO.readTeam(read);
            this.type = Vars.content.getByID(ContentType.unit, read.s());
            this.updateBuilding = read.bool();
            this.x = read.f();
            this.y = read.f();
        } else if (REV == 4) {
            this.ammo = read.f();
            this.baseRotation = read.f();
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
            statuses_LENGTH = read.i();
            this.statuses.clear();

            for (INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
                statuses_ITEM = TypeIO.readStatus(read);
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
        } else {
            if (REV != 5) {
                throw new IllegalArgumentException("Unknown revision '" + REV + "' for entity type 'nova'");
            }

            TypeIO.readAbilities(read, this.abilities);
            this.ammo = read.f();
            this.baseRotation = read.f();
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
            statuses_LENGTH = read.i();
            this.statuses.clear();

            for (INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
                statuses_ITEM = TypeIO.readStatus(read);
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
        }


        int number = read.i();
        for (int i = 0; i < number; i++) {
            unitAbilities.put(read.str(), read.i());
        }
        level = read.i();
        exp = read.f();
        this.afterRead();
    }

    @Override
    public void write(Writes write) {
        write.s(5);
        TypeIO.writeAbilities(write, this.abilities);
        write.f(this.ammo);
        write.f(this.baseRotation);
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


        write.i(unitAbilities.size());
        for (String s : unitAbilities.keySet()) {
            write.str(s);
            write.i(unitAbilities.get(s));
        }
        write.i(level);
        write.f(exp);
    }

    @Override
    public Map<String, Integer> getMap() {
        return unitAbilities;
    }

    @Override
    public int getLevel() {
        return level;
    }
    @Override
    public void setLevel(int l){
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
            exp = exp % (4 + level) * maxHealth / 10;
            number++;
        }
        return number;
    }
}
