package Floor.FEntities.FUnit.F;

import Floor.FEntities.FUnit.Override.FCrawlUnit;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.input.InputHandler;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import java.util.Random;

public class LongUnitTogether extends FCrawlUnit {
    public float lastX = -1, lastY = -1;
    public int lastLen = -1;
    public boolean toThis = false;
    public boolean killed;
    public int frontId = -1;
    public int nextId = -1;
    public LongUnitTogether front = null;
    public LongUnitTogether next = null;

    @Override
    public int classId() {
        return 120;
    }

    protected LongUnitTogether() {
        super();
        killed = false;
    }

    public static LongUnitTogether create() {
        return new LongUnitTogether();
    }

    public void update() {
        if (lastLen > 0 && toThis && within(x, y, hitSize / 2)) {
            LongUnitTogether n = (LongUnitTogether) type.create(team);
            n.front = this;
            next = n;
            n.lastLen = lastLen - 1;
            n.toThis = true;
            n.set(x, y);
            n.rotation = 0;
        }
        if (frontId >= 0) {
            front = (LongUnitTogether) Groups.unit.getByID(frontId);
            frontId = -1;
        }
        if (nextId >= 0) {
            next = (LongUnitTogether) Groups.unit.getByID(nextId);
            nextId = -1;
        }
        float allShield = 0;
        float allHealthy = 0;
        Seq<LongUnitTogether> all = all();
        for (LongUnitTogether lut : all) {
            allShield += lut.shield;
            allHealthy += lut.health;
        }
        allShield = allShield / all.size;
        allHealthy = allHealthy / all.size;
        for (LongUnitTogether lut : all()) {
            lut.shield = allShield;
            lut.health = allHealthy;
        }

        if (front == null && shieldLevel > 0 && sfa == null) {
            sfa = new ShieldRegenFieldAbility(maxHealth / 100 * shieldLevel,
                    maxHealth * shieldLevel / 10, 120, 60);
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
        if (this.type.bounded) {
            offset = 0.0F;
            range = 0.0F;
            cx = (float) Vars.world.unitHeight();
            cy = (float) Vars.world.unitWidth();
            if (Vars.state.rules.limitMapArea && !this.team.isAI()) {
                offset = (float) (Vars.state.rules.limitY * 8);
                range = (float) (Vars.state.rules.limitX * 8);
                cx = (float) (Vars.state.rules.limitHeight * 8) + offset;
                cy = (float) (Vars.state.rules.limitWidth * 8) + range;
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
        int index;
        int accepted;
        if (this.moving()) {
            this.segmentRot = Angles.moveToward(this.segmentRot, this.rotation, this.type.segmentRotSpeed);
            int radius = (int) Math.max(0.0F, this.hitSize / 8.0F * 2.0F);
            index = 0;
            accepted = 0;
            int deeps = 0;
            this.lastDeepFloor = null;

            for (cx = -radius; cx <= radius; ++cx) {
                for (cy = -radius; cy <= radius; ++cy) {
                    if (cx * cx + cy * cy <= radius) {
                        ++index;
                        Tile t = Vars.world.tileWorld(this.x + cx * 8, this.y + cy * 8);
                        if (t != null) {
                            if (t.solid()) {
                                ++accepted;
                            }

                            if (t.floor().isDeep()) {
                                ++deeps;
                                this.lastDeepFloor = t.floor();
                            }

                            if (t.build != null && t.build.team != this.team) {
                                t.build.damage(this.team, this.type.crushDamage * Time.delta * Vars.state.rules.unitDamage(this.team));
                            }

                            if (Mathf.chanceDelta(0.025)) {
                                Fx.crawlDust.at(t.worldx(), t.worldy(), t.floor().mapColor);
                            }
                        } else {
                            ++accepted;
                        }
                    }
                }
            }

            if ((float) deeps / (float) index < 0.75F) {
                this.lastDeepFloor = null;
            }

            this.lastCrawlSlowdown = Mathf.lerp(1.0F, this.type.crawlSlowdown, Mathf.clamp((float) accepted / (float) index / this.type.crawlSlowdownFrac));
        }

        this.segmentRot = Angles.clampRange(this.segmentRot, this.rotation, this.type.segmentMaxRot);
        this.crawlTime += this.vel.len();
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
        if (!this.statuses.isEmpty()) {
            index = 0;

            label353:
            while (true) {
                while (true) {
                    if (index >= this.statuses.size) {
                        break label353;
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

        if (front == null) {
            speedMultiplier *= (1 + speedLevel * 0.2f);
            damageMultiplier *= (1 + damageLevel * 0.2f);
            reloadMultiplier *= (1 + reloadLevel * 0.2f);
            heal(maxHealth * healthLevel * 0.0001f);
            if (sfa != null) {
                sfa.update(this);
            }

            if (level > 60) {
                int boost2 = level - 60;
                float lBoost = (float) Math.pow(1.01f, boost2);
                healthMultiplier *= lBoost;
                if (lBoost >= 9) {
                    speedMultiplier *= 9;
                    healthMultiplier *= (lBoost - 8);
                } else {
                    speedMultiplier *= lBoost;
                }
                damageMultiplier *= lBoost;
                reloadMultiplier *= lBoost;
            }
        } else {
            speedMultiplier = first().speedMultiplier;
            damageMultiplier = first().damageMultiplier;
            healthMultiplier = first().healthMultiplier;
            reloadMultiplier = first().reloadMultiplier;
            dragMultiplier = first().dragMultiplier;
            buildSpeedMultiplier = first().buildSpeedMultiplier;
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

        if (Vars.state.rules.unitAmmo && this.ammo < (float) this.type.ammoCapacity - 1.0E-4F) {
            this.resupplyTime += Time.delta;
            if (this.resupplyTime > 10.0F) {
                this.type.ammoType.resupply(this);
                this.resupplyTime = 0.0F;
            }
        }

        Ability[] var12 = this.abilities;
        index = var12.length;

        for (accepted = 0; accepted < index; ++accepted) {
            Ability a = var12[accepted];
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

        WeaponMount[] var18 = this.mounts;
        index = var18.length;

        for (accepted = 0; accepted < index; ++accepted) {
            WeaponMount mount = var18[accepted];
            mount.weapon.update(this, mount);
        }
    }

    public void kill() {
        if (killed) {
            return;
        }
        killed = true;
        Seq<LongUnitTogether> all = all();
        boolean again = (new Random()).nextInt(10) + 1 <= all.first().getAgainLevel();
        for (LongUnitTogether lu : all) {
            if (!lu.killed) {
                lu.kill();
                if (again) {
                    lu.again();
                }
            }
        }

        if (!this.dead && !Vars.net.client() && this.type.killable) {
            Call.unitDeath(this.id);
        }
    }

    public void again() {
        if ((new Random()).nextInt(10) + 1 <= againLevel) {
            LongUnitTogether fu = (LongUnitTogether) type.create(team);
            fu.x(x);
            fu.y(y);
            fu.rotation(rotation);
            fu.front = this.front;
            fu.next = this.next;
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
        Seq<LongUnitTogether> all = all();
        float damage = amount * (front == null ? 0.5f : 1) / all.size;
        for (LongUnitTogether lu : all) {
            lu.allDamage(damage);
        }
    }

    public void allDamage(float amount) {
        super.rawDamage(amount);
    }

    public Seq<LongUnitTogether> all() {
        Seq<LongUnitTogether> all = new Seq<>();
        Seq<LongUnitTogether> half = new Seq<>();
        LongUnitTogether t = this;
        while (t.front != null) {
            half.add(t.front);
            t = t.front;
        }
        for (int i = half.size - 1; i >= 0; i--) {
            all.add(half.get(i));
        }
        t = this;
        all.add(t);
        while (t.next != null) {
            all.add(t.next);
            t = t.next;
        }
        return all;
    }

    public LongUnitTogether first() {
        return this.front == null ? this : this.front.first();
    }

    @Override
    public void addExp(float exp) {
        first().addExp(exp);
    }

    @Override
    public int number() {
        if (front == null) {
            return super.number();
        } else {
            return -1;
        }
    }

    @Override
    public int getAgainLevel() {
        return front == null ? this.againLevel : front.getAgainLevel();
    }

    @Override
    public int getDamageLevel() {
        return front == null ? this.damageLevel : front.getDamageLevel();
    }

    @Override
    public int getSpeedLevel() {
        return front == null ? this.speedLevel : front.getSpeedLevel();
    }

    @Override
    public int getShieldLevel() {
        return front == null ? this.shieldLevel : front.getShieldLevel();
    }

    @Override
    public int getHealthLevel() {
        return front == null ? this.healthLevel : front.getHealthLevel();
    }

    @Override
    public int getReloadLevel() {
        return front == null ? this.reloadLevel : front.getReloadLevel();
    }

}
