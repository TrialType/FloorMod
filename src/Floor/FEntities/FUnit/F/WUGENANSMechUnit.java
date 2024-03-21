package Floor.FEntities.FUnit.F;

import Floor.FEntities.FUnit.Override.FMechUnit;
import Floor.FEntities.FUnitType.WUGENANSMechUnitType;
import Floor.FTools.PhysicsWorldChanger;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.async.AsyncProcess;
import mindustry.async.PhysicsProcess;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.EntityCollisions;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;
import mindustry.input.InputHandler;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;

import java.lang.reflect.Field;

import static mindustry.Vars.asyncCore;
import static mindustry.Vars.world;

public class WUGENANSMechUnit extends FMechUnit {
    private static final Seq<WUGENANSMechUnit> mec = new Seq<>();
    public boolean under = false;
    public float power;
    public float powerNeed;
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
                        physicsWorldChanger = new PhysicsWorldChanger(world.getQuadBounds(new Rect()));
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

        float offset;
        float range;
        if (!Vars.net.client() || this.isLocal()) {
            offset = this.x;
            range = this.y;
            this.move(this.vel.x * Time.delta, this.vel.y * Time.delta);
            if (Mathf.equal(offset, this.x)) {
                this.vel.x = 0.0F;
            }

            if (Mathf.equal(range, this.y)) {
                this.vel.y = 0.0F;
            }

            this.vel.scl(Math.max(1.0F - this.drag * Time.delta, 0.0F));
        }

        float cx;
        float cy;
        float dy;
        if (this.type.bounded) {
            offset = 0.0F;
            range = 0.0F;
            cx = (float) world.unitHeight();
            cy = (float) world.unitWidth();
            if (Vars.state.rules.limitMapArea && !this.team.isAI()) {
                offset = (float) (Vars.state.rules.limitY * 8);
                range = (float) (Vars.state.rules.limitX * 8);
                cx = (float) (Vars.state.rules.limitHeight * 8) + offset;
                cy = (float) (Vars.state.rules.limitWidth * 8) + range;
            }

            if (!Vars.net.client() || this.isLocal()) {
                float dx = 0.0F;
                dy = 0.0F;
                if (this.x < range) {
                    dx += -(this.x - range) / 30.0F;
                }

                if (this.y < offset) {
                    dy += -(this.y - offset) / 30.0F;
                }

                if (this.x > cy) {
                    dx -= (this.x - cy) / 30.0F;
                }

                if (this.y > cx) {
                    dy -= (this.y - cx) / 30.0F;
                }

                this.velAddNet(dx * Time.delta, dy * Time.delta);
            }

            if (this.isGrounded()) {
                this.x = Mathf.clamp(this.x, range, cy - 8.0F);
                this.y = Mathf.clamp(this.y, offset, cx - 8.0F);
            }

            if (this.x < -250.0F + range || this.y < -250.0F + offset || this.x >= cy + 250.0F || this.y >= cx + 250.0F) {
                this.kill();
            }
        }

        this.updateBuildLogic();
        Floor floor = this.floorOn();
        if (this.isFlying() != this.wasFlying) {
            if (this.wasFlying && this.tileOn() != null) {
                Fx.unitLand.at(this.x, this.y, this.floorOn().isLiquid ? 1.0F : 0.5F, this.tileOn().floor().mapColor);
            }

            this.wasFlying = this.isFlying();
        }

        if (!this.hovering && this.isGrounded() && (this.splashTimer += Mathf.dst(this.deltaX(), this.deltaY())) >= 7.0F + this.hitSize() / 8.0F) {
            floor.walkEffect.at(this.x, this.y, this.hitSize() / 8.0F, floor.mapColor);
            this.splashTimer = 0.0F;
            if (this.emitWalkSound()) {
                floor.walkSound.at(this.x, this.y, Mathf.random(floor.walkSoundPitchMin, floor.walkSoundPitchMax), floor.walkSoundVolume);
            }
        }

        this.updateDrowning();
        this.hitTime -= Time.delta / 9.0F;
        this.stack.amount = Mathf.clamp(this.stack.amount, 0, this.itemCapacity());
        this.itemTime = Mathf.lerpDelta(this.itemTime, (float) Mathf.num(this.hasItem()), 0.05F);
        if (this.walked || Vars.net.client()) {
            offset = this.deltaLen();
            this.baseRotation = Angles.moveToward(this.baseRotation, this.deltaAngle(), this.type().baseRotateSpeed * Mathf.clamp(offset / this.type().speed / Time.delta) * Time.delta);
            this.walkTime += offset;
            this.walked = false;
        }

        offset = this.walkExtend(false);
        range = this.walkExtend(true);
        cx = range % 1.0F;
        cy = this.walkExtension;
        if (!Vars.headless && cx < cy && range % 2.0F > 1.0F && !this.isFlying() && !this.inFogTo(Vars.player.team())) {
            int side = -Mathf.sign(offset);
            dy = this.hitSize / 2.0F * (float) side;
            float length = this.type.mechStride * 1.35F;
            cx = this.x + Angles.trnsx(this.baseRotation, length, dy);
            cy = this.y + Angles.trnsy(this.baseRotation, length, dy);
            if (this.type.stepShake > 0.0F) {
                Effect.shake(this.type.stepShake, this.type.stepShake, cx, cy);
            }

            if (this.type.mechStepParticles) {
                Effect.floorDust(cx, cy, this.hitSize / 8.0F);
            }
        }

        this.walkExtension = cx;
        int accepted;
        if (this.mineTile != null) {
            Building core = this.closestCore();
            Item item = this.getMineResult(this.mineTile);
            if (core != null && item != null && !this.acceptsItem(item) && this.within(core, 220.0F) && !this.offloadImmediately()) {
                accepted = core.acceptStack(this.item(), this.stack().amount, this);
                if (accepted > 0) {
                    Call.transferItemTo(this, this.item(), accepted, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), core);
                    this.clearItem();
                }
            }

            if ((!Vars.net.client() || this.isLocal()) && !this.validMine(this.mineTile)) {
                this.mineTile = null;
                this.mineTimer = 0.0F;
            } else if (this.mining() && item != null) {
                this.mineTimer += Time.delta * this.type.mineSpeed;
                if (Mathf.chance(0.06 * (double) Time.delta)) {
                    Fx.pulverizeSmall.at(this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), 0.0F, item.color);
                }

                if (this.mineTimer >= 50.0F + (this.type.mineHardnessScaling ? (float) item.hardness * 15.0F : 15.0F)) {
                    this.mineTimer = 0.0F;
                    if (Vars.state.rules.sector != null && this.team() == Vars.state.rules.defaultTeam) {
                        Vars.state.rules.sector.info.handleProduction(item, 1);
                    }

                    if (core != null && this.within(core, 220.0F) && core.acceptStack(item, 1, this) == 1 && this.offloadImmediately()) {
                        if (this.item() == item && !Vars.net.client()) {
                            this.addItem(item);
                        }

                        Call.transferItemTo(this, item, 1, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), core);
                    } else if (this.acceptsItem(item)) {
                        InputHandler.transferItemToUnit(item, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), this);
                    } else {
                        this.mineTile = null;
                        this.mineTimer = 0.0F;
                    }
                }

                if (!Vars.headless) {
                    Vars.control.sound.loop(this.type.mineSound, this, this.type.mineSoundVolume);
                }
            }
        }

        this.shieldAlpha -= Time.delta / 15.0F;
        if (this.shieldAlpha < 0.0F) {
            this.shieldAlpha = 0.0F;
        }

        floor = this.floorOn();
        if (this.isGrounded() && !this.type.hovering) {
            this.apply(floor.status, floor.statusDuration);
        }

        this.applied.clear();
        this.speedMultiplier = this.damageMultiplier = this.healthMultiplier = this.reloadMultiplier = this.buildSpeedMultiplier = this.dragMultiplier = 1.0F;
        this.disarmed = false;
        int index;

        int boost = 0;
        if(!Units.canCreate(team,type)){
            for (int p = (int) power, i = (int) powerNeed; p > powerNeed; i *= 2) {
                p = p - i;
                boost++;
            }
        }
        if (!this.statuses.isEmpty()) {
            index = 0;

            label338:
            while (true) {
                while (true) {
                    if (index >= this.statuses.size) {
                        break label338;
                    }

                    StatusEntry entry = statuses.get(index++);
                    entry.time = Math.max(entry.time - Time.delta, 0.0F);
                    if (entry.effect != null && (!(entry.time <= 0.0F) || entry.effect.permanent)) {
                        applied.set(entry.effect.id);
                        speedMultiplier *= entry.effect.speedMultiplier;
                        healthMultiplier *= entry.effect.healthMultiplier;
                        damageMultiplier *= entry.effect.damageMultiplier;
                        reloadMultiplier *= entry.effect.reloadMultiplier;
                        buildSpeedMultiplier *= entry.effect.buildSpeedMultiplier;
                        dragMultiplier *= entry.effect.dragMultiplier;
                        disarmed |= entry.effect.disarm;
                        entry.effect.update(this, entry.time);
                    } else {
                        Pools.free(entry);
                        --index;
                        statuses.remove(index);
                    }
                }
            }
        }
        speedMultiplier += speedLevel * 0.2f;
        damageMultiplier += damageLevel * 0.2f;
        reloadMultiplier += reloadLevel * 0.2f;
        heal(maxHealth * healthLevel * 0.01f);
        if (sfa != null) {
            sfa.update(this);
        }
        speedMultiplier += boost;
        healthMultiplier += boost;
        damageMultiplier += boost;
        reloadMultiplier += boost;

        if (Vars.net.client() && !this.isLocal() || this.isRemote()) {
            this.interpolate();
        }

        this.type.update(this);
        if (this.wasHealed && this.healTime <= -1.0F) {
            this.healTime = 1.0F;
        }

        this.healTime -= Time.delta / 20.0F;
        this.wasHealed = false;
        if (this.team.isOnlyAI() && Vars.state.isCampaign() && Vars.state.getSector().isCaptured()) {
            this.kill();
        }

        if (!Vars.headless && this.type.loopSound != Sounds.none) {
            Vars.control.sound.loop(this.type.loopSound, this, this.type.loopSoundVolume);
        }

        if (!this.type.supportsEnv(Vars.state.rules.env) && !this.dead) {
            Call.unitEnvDeath(this);
            this.team.data().updateCount(this.type, -1);
        }

        if (Vars.state.rules.unitAmmo && this.ammo < (float) this.type.ammoCapacity - 1.0E-4F) {
            this.resupplyTime += Time.delta;
            if (this.resupplyTime > 10.0F) {
                this.type.ammoType.resupply(this);
                this.resupplyTime = 0.0F;
            }
        }

        Ability[] var14 = this.abilities;
        index = var14.length;

        for (accepted = 0; accepted < index; ++accepted) {
            Ability a = var14[accepted];
            a.update(this);
        }

        if (this.trail != null) {
            this.trail.length = this.type.trailLength;
            offset = this.type.useEngineElevation ? this.elevation : 1.0F;
            range = this.type.engineOffset / 2.0F + this.type.engineOffset / 2.0F * offset;
            cx = this.x + Angles.trnsx(this.rotation + 180.0F, range);
            cy = this.y + Angles.trnsy(this.rotation + 180.0F, range);
            this.trail.update(cx, cy);
        }

        this.drag = this.type.drag * (this.isGrounded() ? this.floorOn().dragMultiplier : 1.0F) * this.dragMultiplier * Vars.state.rules.dragMultiplier;
        if (this.team != Vars.state.rules.waveTeam && Vars.state.hasSpawns() && (!Vars.net.client() || this.isLocal()) && this.hittable()) {
            offset = Vars.state.rules.dropZoneRadius + this.hitSize / 2.0F + 1.0F;

            for (Tile spawn : Vars.spawner.getSpawns()) {
                if (this.within(spawn.worldx(), spawn.worldy(), offset)) {
                    this.velAddNet(Tmp.v1.set(this).sub(spawn.worldx(), spawn.worldy()).setLength(1.1F - this.dst(spawn) / offset).scl(0.45F * Time.delta));
                }
            }
        }

        if (this.dead || this.health <= 0.0F) {
            this.drag = 0.01F;
            if (Mathf.chanceDelta(0.1)) {
                Tmp.v1.rnd(Mathf.range(this.hitSize));
                this.type.fallEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y);
            }

            if (Mathf.chanceDelta(0.2)) {
                offset = this.type.engineOffset / 2.0F + this.type.engineOffset / 2.0F * this.elevation;
                range = Mathf.range(this.type.engineSize);
                this.type.fallEngineEffect.at(this.x + Angles.trnsx(this.rotation + 180.0F, offset) + Mathf.range(range), this.y + Angles.trnsy(this.rotation + 180.0F, offset) + Mathf.range(range), Mathf.random());
            }

            this.elevation -= this.type.fallSpeed * Time.delta;
            if (this.isGrounded() || this.health <= -this.maxHealth) {
                Call.unitDestroy(this.id);
            }
        }

        Tile tile = this.tileOn();
        floor = this.floorOn();
        if (tile != null && this.isGrounded() && !this.type.hovering) {
            if (tile.build != null) {
                tile.build.unitOn(this);
            }

            if (floor.damageTaken > 0.0F) {
                this.damageContinuous(floor.damageTaken);
            }
        }

        if (tile != null && !this.canPassOn()) {
            if (this.type.canBoost) {
                this.elevation = 1.0F;
            } else if (!Vars.net.client()) {
                this.kill();
            }
        }

        if (!Vars.net.client() && !this.dead) {
            this.controller.updateUnit();
        }

        if (!this.controller.isValidController()) {
            this.resetController();
        }

        if (this.spawnedByCore && !this.isPlayer() && !this.dead) {
            Call.unitDespawn(this);
        }

        WeaponMount[] var20 = this.mounts;
        index = var20.length;

        for (accepted = 0; accepted < index; ++accepted) {
            WeaponMount mount = var20[accepted];
            mount.weapon.update(this, mount);
        }


        if (type instanceof WUGENANSMechUnitType wut) {
            powerNeed = wut.needPower;
            if (Units.canCreate(team, type) && power >= powerNeed) {
                timerChanging = timerChanging + Time.delta;
                if (timerChanging > 120) {
                    power = power - wut.needPower;
                    timerChanging = 0;
                    WUGENANSMechUnit wu = (WUGENANSMechUnit) wut.create(team);
                    wu.x = x - hitSize / 2;
                    wu.y = y - hitSize / 2;
                    wu.rotation = rotation;
                    if (world.buildWorld(wu.x, wu.y) != null) {
                        wu.under = true;
                    } else {
                        wu.under = under;
                    }
                    wu.add();
                }
            }
            time1 = wut.outTime;
            time2 = wut.landTime;
            if (under && outTimer >= 0) {
                outTimer += Time.delta;
                elevation = Mathf.lerpDelta(elevation, 0, Time.delta / time1);
                if (outTimer >= time1) {
                    Building b = world.buildWorld(x, y);
                    if (b != null) {
                        if (b.health() <= maxHealth && !(b instanceof CoreBlock.CoreBuild)) {
                            b.kill();
                        }
                    }
                    Damage.damage(team, x, y, wut.damageRadius * 1.4F, wut.upDamage);
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
                floor = floorOn();
                if (floor != Blocks.water && floor.isDeep()) {
                    under = false;
                    elevation = 1;
                } else {
                    elevation = -1;
                }
                landTimer = outTimer = -1;
            } else {
                landTimer = outTimer = -1;
                elevation = 0;
            }
        } else if (under) {
            elevation = 0;
            under = false;
            outTimer = landTimer = -1;
        }
    }

    public EntityCollisions.SolidPred solidity() {
        return this.isFlying() || under ? null : EntityCollisions::solid;
    }

    public boolean canShoot() {
        return !under && !this.disarmed && (!this.type.canBoost || !this.isFlying());
    }

    public boolean checkTarget(boolean targetAir, boolean targetGround) {
        return this.isGrounded() && targetGround || this.isFlying() && targetAir;
    }

    public boolean collides(Hitboxc other) {
        return this.hittable();
    }

    @Override
    public boolean targetable(Team target) {
        return !under && super.targetable(target);
    }

    @Override
    public boolean isGrounded() {
        return this.elevation < 0.001F && !under;
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
            this.baseRotation = read.f();
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
            this.baseRotation = read.f();
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
            this.baseRotation = read.f();
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
        } else if (REV == 5) {
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
        } else if (REV == 6) {
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
            if (REV != 7) {
                throw new IllegalArgumentException("Unknown revision '" + REV + "' for entity type 'mace'");
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

        level = read.i();
        exp = read.f();

        under = read.bool();
        power = read.f();
        timerChanging = read.f();

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
        this.afterRead();
    }

    @Override
    public void write(Writes write) {
        write.s(7);
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

        write.i(level);
        write.f(exp);
        write.bool(under);
        write.f(power);
        write.f(timerChanging);
        write.i(level);
        write.f(exp);
        write.i(damageLevel);
        write.i(speedLevel);
        write.i(reloadLevel);
        write.i(healthLevel);
        write.i(againLevel);
        write.i(shieldLevel);
    }
}
