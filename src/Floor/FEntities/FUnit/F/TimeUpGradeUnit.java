package Floor.FEntities.FUnit.F;

import Floor.FEntities.FUnit.Override.FUnitEntity;
import Floor.FTools.HighChange;
import Floor.FTools.UpGradeTime;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
public class TimeUpGradeUnit extends FUnitEntity implements UpGradeTime {
    private final Seq<Integer> idList = new Seq<>();
    public float boostTime;

    @Override
    public int classId() {
        return 119;
    }

    protected TimeUpGradeUnit() {
        super();
    }

    public static TimeUpGradeUnit create() {
        return new TimeUpGradeUnit();
    }

    @Override
    public void update() {
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
        if (this.type.bounded) {
            offset = 0.0F;
            range = 0.0F;
            cx = (float)Vars.world.unitHeight();
            cy = (float)Vars.world.unitWidth();
            if (Vars.state.rules.limitMapArea && !this.team.isAI()) {
                offset = (float)(Vars.state.rules.limitY * 8);
                range = (float)(Vars.state.rules.limitX * 8);
                cx = (float)(Vars.state.rules.limitHeight * 8) + offset;
                cy = (float)(Vars.state.rules.limitWidth * 8) + range;
            }

            if (!Vars.net.client() || this.isLocal()) {
                float dx = 0.0F;
                float dy = 0.0F;
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
        this.itemTime = Mathf.lerpDelta(this.itemTime, (float)Mathf.num(this.hasItem()), 0.05F);
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
                if (Mathf.chance(0.06 * (double)Time.delta)) {
                    Fx.pulverizeSmall.at(this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), 0.0F, item.color);
                }

                if (this.mineTimer >= 50.0F + (this.type.mineHardnessScaling ? (float)item.hardness * 15.0F : 15.0F)) {
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
        if (!this.statuses.isEmpty()) {
            index = 0;

            label318:
            while(true) {
                while(true) {
                    if (index >= this.statuses.size) {
                        break label318;
                    }

                    StatusEntry entry = this.statuses.get(index++);
                    entry.time = Math.max(entry.time - Time.delta, 0.0F);
                    if (entry.effect != null && (!(entry.time <= 0.0F) || entry.effect.permanent)) {
                        this.applied.set(entry.effect.id);
                        this.speedMultiplier *= entry.effect.speedMultiplier;
                        this.healthMultiplier *= entry.effect.healthMultiplier;
                        this.damageMultiplier *= entry.effect.damageMultiplier;
                        this.reloadMultiplier *= entry.effect.reloadMultiplier;
                        this.buildSpeedMultiplier *= entry.effect.buildSpeedMultiplier;
                        this.dragMultiplier *= entry.effect.dragMultiplier;
                        this.disarmed |= entry.effect.disarm;
                        entry.effect.update(this, entry.time);
                    } else {
                        Pools.free(entry);
                        --index;
                        this.statuses.remove(index);
                    }
                }
            }
        }
        speedMultiplier += speedLevel * 0.2f;
        damageMultiplier += damageLevel * 0.2f;
        reloadMultiplier += reloadLevel * 0.2f;
        heal(maxHealth * healthLevel * 0.0001f);
        if (sfa != null) {
            sfa.update(this);
        }

        float damageTo = 1;
        float speedTo = 1;
        float reloadTo = 1;
        float healthTo = 1;
        float buildTo = 1;
        float dargTo = 1;
        for (StatusEntry se : statuses) {
            if (se.effect instanceof HighChange hc) {
                damageTo = Math.min(damageTo, hc.damageTo());
                speedTo = Math.min(speedTo, hc.speedTo());
                reloadTo = Math.min(reloadTo, hc.reloadTo());
                healthTo = Math.min(healthTo, hc.healthTo());
                buildTo = Math.min(buildTo, hc.buildTo());
                dargTo = Math.min(dargTo, hc.dargTo());
            }
        }
        speedMultiplier *= speedTo;
        damageMultiplier *= damageTo;
        reloadMultiplier *= reloadTo;
        healthMultiplier *= healthTo;
        buildSpeedMultiplier *= buildTo;
        dragMultiplier *= dargTo;

        if (level > 60) {
            int boost2 = level - 60;
            speedMultiplier += boost2 * 0.01f;
            damageMultiplier += boost2 * 0.01f;
            reloadMultiplier += boost2 * 0.01f;
        }

        if (boostTime > 0) {
            speedMultiplier = speedMultiplier + boostTime / 100;
            damageMultiplier = damageMultiplier + boostTime / 100;
            reloadMultiplier = reloadMultiplier + boostTime / 100;
            healthMultiplier = healthMultiplier + boostTime / 100;
            boostTime = Math.max(0, boostTime - Time.delta / 60);
        }

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

        if (Vars.state.rules.unitAmmo && this.ammo < (float)this.type.ammoCapacity - 1.0E-4F) {
            this.resupplyTime += Time.delta;
            if (this.resupplyTime > 10.0F) {
                this.type.ammoType.resupply(this);
                this.resupplyTime = 0.0F;
            }
        }

        Ability[] var10 = this.abilities;
        index = var10.length;

        for(accepted = 0; accepted < index; ++accepted) {
            Ability a = var10[accepted];
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

        WeaponMount[] var16 = this.mounts;
        index = var16.length;

        for(accepted = 0; accepted < index; ++accepted) {
            WeaponMount mount = var16[accepted];
            mount.weapon.update(this, mount);
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
        boostTime = read.f();
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
        write.i(level);
        write.f(exp);
        write.i(damageLevel);
        write.i(speedLevel);
        write.i(reloadLevel);
        write.i(healthLevel);
        write.i(againLevel);
        write.i(shieldLevel);
        write.f(boostTime);
    }

    @Override
    public void add(float number) {
        boostTime += number;
    }

    @Override
    public void set(float number) {
        boostTime = number;
    }
}
