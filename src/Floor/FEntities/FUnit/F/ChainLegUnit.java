package Floor.FEntities.FUnit.F;

import Floor.FTools.interfaces.FUnitUpGrade;
import Floor.FTools.interfaces.ChainAble;
import arc.Events;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.EntityCollisions;
import mindustry.entities.Leg;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.units.BuildPlan;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.InverseKinematics;
import mindustry.graphics.Pal;
import mindustry.input.InputHandler;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.HashMap;
import java.util.Map;

public class ChainLegUnit extends ENGSWEISLegsUnit implements FUnitUpGrade, ChainAble {
    private int underUnitId;
    public Unit underUnit = null;
    public Healthc order = null;
    public boolean upon;
    public Map<String, Integer> unitAbilities = new HashMap<>();
    @Override
    public int classId() {
        return 100;
    }
    protected ChainLegUnit() {
        this.applied = new Bits(Vars.content.getBy(ContentType.status).size);
        this.curMoveOffset = new Vec2();
        this.legs = new Leg[0];
        this.resupplyTime = Mathf.random(10.0F);
        this.statuses = new Seq<>();
    }
    public static ChainLegUnit create() {
        return new ChainLegUnit();
    }
    public boolean canPass(int tileX, int tileY) {
        EntityCollisions.SolidPred s = this.solidity();
        return underUnit != null || s == null || !s.solid(tileX, tileY);
    }

    public void hitboxTile(Rect rect) {
        float size;
        if (underUnit != null) size = 0F;
        else size = Math.min(this.hitSize * 0.66F, 7.9F);
        rect.setCentered(this.x, this.y, size, size);
    }

    @Override
    public EntityCollisions.SolidPred solidity() {
        if (elevation > 0.09) {
            return null;
        } else {
            return this.type.allowLegStep ? EntityCollisions::legsSolid : EntityCollisions::solid;
        }
    }
    public boolean canMine() {
        return underUnit == null && this.type.mineSpeed > 0.0F && this.type.mineTier >= 0;
    }
    public void drawBuildPlans() {
        if (underUnit != null) return;
        Boolf<BuildPlan> skip = plan ->
                plan.progress > 0.01F || this.buildPlan() == plan && plan.initialized && (this.within((float) (plan.x * 8), (float) (plan.y * 8), this.type.buildRange) || Vars.state.isEditor());
        for (int i = 0; i < 2; ++i) {
            for (BuildPlan plan : this.plans) {
                if (!skip.get(plan)) {
                    if (i == 0) {
                        this.drawPlan(plan, 1.0F);
                    } else {
                        this.drawPlanTop(plan, 1.0F);
                    }
                }
            }
        }
        Draw.reset();
    }
    public void drawBuilding() {
        if (underUnit != null) return;
        boolean active = this.activelyBuilding();
        if (active || this.lastActive != null) {
            Draw.z(115.0F);
            BuildPlan plan = active ? this.buildPlan() : this.lastActive;
            Tile tile = plan.tile();
            CoreBlock.CoreBuild core = this.team.core();
            if (tile != null && this.within(plan, Vars.state.rules.infiniteResources ? Float.MAX_VALUE : this.type.buildRange)) {
                if (core != null && active && !this.isLocal() && !(tile.block() instanceof ConstructBlock)) {
                    Draw.z(84.0F);
                    this.drawPlan(plan, 0.5F);
                    this.drawPlanTop(plan, 0.5F);
                    Draw.z(115.0F);
                }

                if (this.type.drawBuildBeam) {
                    float focusLen = this.type.buildBeamOffset + Mathf.absin(Time.time, 3.0F, 0.6F);
                    float px = this.x + Angles.trnsx(this.rotation, focusLen);
                    float py = this.y + Angles.trnsy(this.rotation, focusLen);
                    this.drawBuildingBeam(px, py);
                }

            }
        }
    }
    public void drawBuildingBeam(float px, float py) {
        if (underUnit != null) return;
        boolean active = this.activelyBuilding();
        if (active || this.lastActive != null) {
            Draw.z(115.0F);
            BuildPlan plan = active ? this.buildPlan() : this.lastActive;
            Tile tile = Vars.world.tile(plan.x, plan.y);
            if (tile != null && this.within(plan, Vars.state.rules.infiniteResources ? Float.MAX_VALUE : this.type.buildRange)) {
                int size = plan.breaking ? (active ? tile.block().size : this.lastSize) : plan.block.size;
                float tx = plan.drawx();
                float ty = plan.drawy();
                Lines.stroke(1.0F, plan.breaking ? Pal.remove : Pal.accent);
                Draw.z(122.0F);
                Draw.alpha(this.buildAlpha);
                if (!active && !(tile.build instanceof ConstructBlock.ConstructBuild)) {
                    Fill.square(plan.drawx(), plan.drawy(), (float) (size * 8) / 2.0F);
                }

                Drawf.buildBeam(px, py, tx, ty, (float) (8 * size) / 2.0F);
                Fill.square(px, py, 1.8F + Mathf.absin(Time.time, 2.2F, 1.1F), this.rotation + 45.0F);
                Draw.reset();
                Draw.z(115.0F);
            }
        }
    }
    public void drawPlan(BuildPlan plan, float alpha) {
        if (underUnit != null) return;
        plan.animScale = 1.0F;
        if (plan.breaking) {
            Vars.control.input.drawBreaking(plan);
        } else {
            plan.block.drawPlan(plan, Vars.control.input.allPlans(), Build.validPlace(plan.block, this.team, plan.x, plan.y, plan.rotation) || Vars.control.input.planMatches(plan), alpha);
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
                throw new IllegalArgumentException("Unknown revision '" + REV + "' for entity type 'corvus'");
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
            unitAbilities.put(read.str(), read.i());
        }
        underUnitId = read.i();
        this.upon = read.bool();

        level = read.i();
        exp = read.f();

        damageLevel = read.i();
        speedLevel = read.i();
        reloadLevel = read.i();
        healthLevel = read.i();
        againLevel = read.i();
        shieldLevel = read.i();
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

        //Floor Mod Unit
        write.i(unitAbilities.size());
        for (String s : unitAbilities.keySet()) {
            write.str(s);
            write.i(unitAbilities.get(s));
        }

        write.i(underUnit == null ? -1 : underUnit.id);
        write.bool(upon);

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
        if (shieldLevel > 0 && sfa == null) {
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
        if (Mathf.dst(this.deltaX(), this.deltaY()) > 0.001F) {
            this.baseRotation = Angles.moveToward(this.baseRotation, Mathf.angle(this.deltaX(), this.deltaY()), this.type.rotateSpeed);
        }

        if (this.type.lockLegBase) {
            this.baseRotation = this.rotation;
        }

        offset = this.type.legLength;
        if (this.legs.length != this.type.legCount) {
            this.resetLegs();
        }

        range = this.type.legSpeed;
        int div = Math.max(this.legs.length / this.type.legGroupSize, 2);
        this.moveSpace = offset / 1.6F / ((float) div / 2.0F) * this.type.legMoveSpace;
        this.totalLength += this.type.legContinuousMove ? this.type.speed * this.speedMultiplier * Time.delta : Mathf.dst(this.deltaX(), this.deltaY());
        cy = this.moveSpace * 0.85F * this.type.legForwardScl;
        boolean moving = this.moving();
        Vec2 moveOffset = !moving ? Tmp.v4.setZero() : Tmp.v4.trns(Angles.angle(this.deltaX(), this.deltaY()), cy);
        moveOffset = this.curMoveOffset.lerpDelta(moveOffset, 0.1F);
        this.lastDeepFloor = null;
        int deeps = 0;

        for (int i = 0; i < this.legs.length; ++i) {
            float dstRot = this.legAngle(i);
            Vec2 baseOffset = this.legOffset(Tmp.v5, i).add(this.x, this.y);
            Leg l = this.legs[i];
            l.joint.sub(baseOffset).clampLength(this.type.legMinLength * offset / 2.0F, this.type.legMaxLength * offset / 2.0F).add(baseOffset);
            l.base.sub(baseOffset).clampLength(this.type.legMinLength * offset, this.type.legMaxLength * offset).add(baseOffset);
            float stageF = (this.totalLength + (float) i * this.type.legPairOffset) / this.moveSpace;
            int stage = (int) stageF;
            int group = stage % div;
            boolean move = i % div == group;
            boolean side = i < this.legs.length / 2;
            boolean backLeg = Math.abs((float) i + 0.5F - (float) this.legs.length / 2.0F) <= 0.501F;
            if (backLeg && this.type.flipBackLegs) {
                side = !side;
            }

            if (this.type.flipLegSide) {
                side = !side;
            }

            l.moving = move;
            l.stage = moving ? stageF % 1.0F : Mathf.lerpDelta(l.stage, 0.0F, 0.1F);
            Floor f1 = Vars.world.floorWorld(l.base.x, l.base.y);
            if (f1.isDeep()) {
                ++deeps;
                this.lastDeepFloor = f1;
            }

            if (l.group != group) {
                if (!move && (moving || !this.type.legContinuousMove) && i % div == l.group) {
                    if (!Vars.headless && !this.inFogTo(Vars.player.team()) && elevation < 0.0001F) {
                        if (f1.isLiquid) {
                            f1.walkEffect.at(l.base.x, l.base.y, this.type.rippleScale, f1.mapColor);
                            f1.walkSound.at(this.x, this.y, 1.0F, f1.walkSoundVolume);
                        } else {
                            Fx.unitLandSmall.at(l.base.x, l.base.y, this.type.rippleScale, f1.mapColor);
                        }

                        if (this.type.stepShake > 0.0F) {
                            Effect.shake(this.type.stepShake, this.type.stepShake, l.base);
                        }
                    }

                    if (this.type.legSplashDamage > 0.0F) {
                        Damage.damage(this.team, l.base.x, l.base.y, this.type.legSplashRange, this.type.legSplashDamage * Vars.state.rules.unitDamage(this.team), false, true);
                    }
                }

                l.group = group;
            }

            Vec2 legDest = Tmp.v1.trns(dstRot, offset * this.type.legLengthScl).add(baseOffset).add(moveOffset);
            Vec2 jointDest = Tmp.v2;
            InverseKinematics.solve(offset / 2.0F, offset / 2.0F, Tmp.v6.set(l.base).sub(baseOffset), side, jointDest);
            jointDest.add(baseOffset);
            Tmp.v6.set(baseOffset).lerp(l.base, 0.5F);
            if (move) {
                float moveFract = stageF % 1.0F;
                l.base.lerpDelta(legDest, moveFract);
                l.joint.lerpDelta(jointDest, moveFract / 2.0F);
            }

            l.joint.lerpDelta(jointDest, range / 4.0F);
            l.joint.sub(baseOffset).clampLength(this.type.legMinLength * offset / 2.0F, this.type.legMaxLength * offset / 2.0F).add(baseOffset);
            l.base.sub(baseOffset).clampLength(this.type.legMinLength * offset, this.type.legMaxLength * offset).add(baseOffset);
        }

        if (deeps != this.legs.length || !this.floorOn().isDeep()) {
            this.lastDeepFloor = null;
        }

        if (this.mineTile != null) {
            Building core = this.closestCore();
            Item item = this.getMineResult(this.mineTile);
            if (core != null && item != null && !this.acceptsItem(item) && this.within(core, 220.0F) && !this.offloadImmediately()) {
                div = core.acceptStack(this.item(), this.stack().amount, this);
                if (div > 0) {
                    Call.transferItemTo(this, this.item(), div, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), core);
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
        if (!this.statuses.isEmpty()) {
            index = 0;

            label416:
            while (true) {
                while (true) {
                    if (index >= this.statuses.size) {
                        break label416;
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

        Ability[] var25 = this.abilities;
        index = var25.length;

        for (div = 0; div < index; ++div) {
            Ability a = var25[div];
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
        Floor f2 = this.floorOn();
        if (tile != null && this.isGrounded() && !this.type.hovering) {
            if (tile.build != null) {
                tile.build.unitOn(this);
            }

            if (f2.damageTaken > 0.0F) {
                this.damageContinuous(f2.damageTaken);
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

        WeaponMount[] var31 = this.mounts;
        index = var31.length;

        for (div = 0; div < index; ++div) {
            WeaponMount mount = var31[div];
            mount.weapon.update(this, mount);
        }
        if (underUnit == null && underUnitId > 0) {
            underUnit = Groups.unit.getByID(underUnitId);
        }
        underUnitId = -1;
    }
    @Override
    public void updateDrowning() {
        Floor floor = this.drownFloor();
        if (floor != null && floor.isLiquid && floor.drownTime > 0.0F && elevation < 0.0001) {
            this.lastDrownFloor = floor;
            this.drownTime += Time.delta / floor.drownTime / this.type.drownTimeMultiplier;
            if (Mathf.chanceDelta(0.05000000074505806)) {
                floor.drownUpdateEffect.at(this.x, this.y, this.hitSize, floor.mapColor);
            }

            if (this.drownTime >= 0.999F && !Vars.net.client()) {
                this.kill();
                Events.fire(new EventType.UnitDrownEvent(this));
            }
        } else {
            this.drownTime -= Time.delta / 50.0F;
        }

        this.drownTime = Mathf.clamp(this.drownTime);
    }
    @Override
    public void UnderUnit(Unit unit) {
        underUnit = unit;
    }
    @Override
    public Unit UnderUnit() {
        return underUnit;
    }
    @Override
    public void upon(boolean b) {
        upon = b;
    }
    @Override
    public void order(Healthc order) {
        this.order = order;
    }
    @Override
    public Healthc order() {
        return order;
    }
    @Override
    public boolean upon() {
        return upon;
    }
}
