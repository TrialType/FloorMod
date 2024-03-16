package Floor.FEntities.FUnit.F;

import Floor.FContent.FEvents;
import Floor.FContent.FUnits;
import Floor.FEntities.FUnit.Override.FUnitEntity;
import Floor.FEntities.FUnitType.ENGSWEISUnitType;
import Floor.FTools.BossList;
import Floor.FTools.PhysicsWorldChanger;
import arc.Events;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.async.AsyncProcess;
import mindustry.async.PhysicsProcess;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.entities.Units;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.*;
import mindustry.io.TypeIO;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
import static mindustry.Vars.asyncCore;

public class ENGSWEISUnitEntity extends FUnitEntity {
    public final static Seq<ENGSWEISUnitEntity> crazy = new Seq<>();
    private final Map<Unit, Float> unitMap = new HashMap<>();
    private final Map<Building, Float> buildingMap = new HashMap<>();
    public boolean first = true;
    private final Seq<Integer> idList = new Seq<>();
    public Teamc target;
    public static BeginChanger bc = new BeginChanger();
    public static PhysicsWorldChanger physicsWorldChanger;

    protected ENGSWEISUnitEntity() {
        this.applied = new Bits(Vars.content.getBy(ContentType.status).size);
        this.resupplyTime = Mathf.random(10.0F);
        this.statuses = new Seq<>();
    }

    public static ENGSWEISUnitEntity create() {
        return new ENGSWEISUnitEntity();
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
        if (crazy.indexOf(this) < 0) {
            crazy.add(this);
            change();
        }

        if (!team.isAI() || BossList.list.indexOf(type) > -1) {
            first = false;
        }

        super.update();

        unitMap.replaceAll((u, v) -> v + Time.delta);
        buildingMap.replaceAll((u, v) -> v + Time.delta);
        if (moving() && type instanceof ENGSWEISUnitType eut && target != null) {
            float damage = eut.damage;
            float changeHel = eut.changeHel;
            float percent = eut.percent;
            boolean firstPercent = eut.firstPercent;
            float reload = eut.HitReload;
            float minSpeed = eut.minSpeed;
            if (speed() >= minSpeed) {
                float length = type == FUnits.crazy ? hitSize / 2 : min(speed() * 100, 100);
                float angle = rotation + 90;
                float dx = (float) (hitSize * cos(toRadians(angle)));
                float dy = (float) (hitSize * sin(toRadians(angle)));
                float xp = dx + x;
                float yp = dy + y;
                float xj = x - dx;
                float yj = y - dy;
                Units.nearbyEnemies(team, x, y, length, u -> {
                    float timer = unitMap.computeIfAbsent(u, f -> reload);
                    if (timer >= reload) {
                        unitMap.put(u, 0F);
                        applyDamage(u, xp, yp, xj, yj, length, percent, damage, firstPercent, changeHel, type == FUnits.crazy);
                    }
                });
                Units.nearbyBuildings(x, y, length, b -> {
                    if (b.team != team) {
                        float timer = buildingMap.computeIfAbsent(b, f -> reload);
                        if (timer >= reload) {
                            buildingMap.put(b, 0F);
                            applyDamage(b, xp, yp, xj, yj, length, percent, damage, firstPercent, changeHel, type == FUnits.crazy);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int classId() {
        return 113;
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
            this.controller = TypeIO.readController(read, this.controller);
            read.bool();
            this.elevation = read.f();
            this.health = read.f();
            this.isShooting = read.bool();
            TypeIO.readMounts(read, this.mounts);
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
            this.controller = TypeIO.readController(read, this.controller);
            this.elevation = read.f();
            this.health = read.f();
            this.isShooting = read.bool();
            TypeIO.readMounts(read, this.mounts);
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
            this.controller = TypeIO.readController(read, this.controller);
            this.elevation = read.f();
            this.flag = read.d();
            this.health = read.f();
            this.isShooting = read.bool();
            TypeIO.readMounts(read, this.mounts);
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
            this.controller = TypeIO.readController(read, this.controller);
            this.elevation = read.f();
            this.flag = read.d();
            this.health = read.f();
            this.isShooting = read.bool();
            this.mineTile = TypeIO.readTile(read);
            TypeIO.readMounts(read, this.mounts);
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
        } else if (REV == 4) {
            this.ammo = read.f();
            read.f();
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
        } else if (REV == 5) {
            this.ammo = read.f();
            read.f();
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
        } else if (REV == 6) {
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
            if (REV != 7) {
                throw new IllegalArgumentException("Unknown revision '" + REV + "' for entity type 'flare'");
            }

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
            idList.add(read.i());

        }
        number = read.i();
        for (int i = 0; i < number; i++) {
            unitAbilities.put(read.str(), read.i());
        }
        first = read.bool();
        this.afterRead();
    }

    @Override
    public void write(Writes write) {
        write.s(7);
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
        write.bool(first);
    }

    @Override
    public float speed() {
        if (first) {
            if (type instanceof ENGSWEISUnitType eut) {
                return eut.Speed1;
            }
        }
        return super.speed();
    }

    private void applyDamage(Healthc u, float x1, float y1, float x2, float y2, float length, float percent, float damage, boolean firstPercent, float changeHel, boolean crazy) {

        if (crazy) {
            percentDamage(u, percent, damage, firstPercent, changeHel);
        } else {
            Fx.healWave.at(u);

            float rotate = Angles.angle(x, y, target.x(), target.y());
            float ux = u.x();
            float uy = u.y();
            float l1 = (float) sqrt((x1 - ux) * (x1 - uy) + (y1 - uy) * (y1 - uy));
            float l2 = (float) sqrt((x2 - ux) * (x2 - uy) + (y2 - uy) * (y2 - uy));
            float angle1 = Angles.angleDist(Angles.angle(x1, y1, ux, uy), rotate);
            float angle2 = Angles.angleDist(Angles.angle(x2, y2, ux, uy), rotate);
            float angle3 = Angles.angleDist(Angles.angle(ux, uy, x1, y1), Angles.angle(ux, uy, x2, y2));
            if (l1 * cos(toRadians(angle1)) <= length && l2 * cos(toRadians(angle2)) <= length && abs(angle1 + angle2 - angle3) <= 15) {
                percentDamage(u, percent, damage, firstPercent, changeHel);
            }
        }
    }

    private void percentDamage(Healthc u, float percent, float damage, boolean firstPercent, float changeHel) {

        boolean dead = u.dead();
        if (firstPercent && u.health() > changeHel || (!firstPercent && u.health() <= changeHel)) {
            u.health(u.health() - u.maxHealth() * percent / 100);
            u.hitTime(1.0F);
        } else {
            u.damage(damage);
        }
        if (!dead && u.dead()) {
            Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
        }
    }

    public static class BeginChanger implements AsyncProcess {
        @Override
        public void begin() {
            Seq<ENGSWEISUnitEntity> us = new Seq<>();
            for (ENGSWEISUnitEntity eu : crazy) {
                if (eu.dead || eu.health <= 0 || eu.target == null) {
                    us.add(eu);
                }
            }

            crazy.removeAll(us);

            for (ENGSWEISUnitEntity u : crazy) {
                if (u.target != null) {
                    u.physref.body.layer = 3;
                }
            }
        }
    }
}