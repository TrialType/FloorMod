package Floor.FEntities.FBlock;

import Floor.FTools.FBuildUpGrade;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.Turret;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.state;

public class OwnerTurret extends Turret {
    public BulletType bullet = new BulletType(0, 0) {{
        lifetime = 0;
        despawnEffect = hitEffect = Fx.none;
        shootSound = Sounds.none;
        collides = absorbable = hittable = reflectable = false;
    }};
    public boolean withEffect = true;
    public boolean hitTeam = true;
    public float minPower = -1;
    public Effect defaultEffect = null;

    public OwnerTurret(String name) {

        super(name);

        breakable = false;
        hasItems = false;
        update = solid = true;
        itemCapacity = maxAmmo = 0;
    }

    public class ownerBuild extends TurretBuild implements FBuildUpGrade, ControlBlock {
        private float exp = 0;
        private float boost = 1;
        private Effect fireEffect;

        @Override
        public void updateTile() {
            boost = (1 + exp / maxHealth / 10);

            reloadCounter += exp;
            heal(exp / 100);
            if (withEffect) {
                if (defaultEffect != null && fireEffect != defaultEffect) {
                    fireEffect = defaultEffect;
                } else if (defaultEffect == null) {
                    fireEffect = new Effect(33f, 80f, e -> {
                        color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());

                        randLenVectors(e.id, 35, e.finpow() * range * boost * 1.3f, e.rotation, 10f,
                                (x, y) -> Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.6f));
                    });
                }
            }
            super.updateTile();
        }

        @Override
        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover) {
            queuedBullets--;
            exp = Math.max(0, exp - Time.delta * maxHealth / 300);

            if (dead || (!consumeAmmoOnce && !hasAmmo())) return;

            float
                    xSpread = Mathf.range(xRand),
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);

            float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) : 1f;

            handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);

            (fireEffect == null ? Fx.none : fireEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));

            ammoUseEffect.at(
                    x - Angles.trnsx(rotation, ammoEjectBack),
                    y - Angles.trnsy(rotation, ammoEjectBack),
                    rotation * Mathf.sign(xOffset)
            );

            if (shake > 0) {
                Effect.shake(shake, shake, this);
            }

            curRecoil = 1f;
            if (recoils > 0) {
                curRecoils[barrelCounter % recoils] = 1f;
            }
            heat = 1f;
            totalShots++;

            if (!consumeAmmoOnce) {
                useAmmo();
            }
        }

        @Override
        protected void handleBullet(@Nullable Bullet bullet, float offsetX, float offsetY, float angleOffset) {
            bullet.team = hitTeam ? exp > minPower ? Team.derelict : team : team;
            bullet.lifetime *= bullet.lifetime * boost;
            bullet.damage *= bullet.damage * boost;
        }

        @Override
        protected void findTarget() {
            float range = range();
            Team other;
            if (!state.rules.pvp) {
                other = team == state.rules.defaultTeam ? state.rules.waveTeam : state.rules.defaultTeam;
            } else {
                other = Team.crux;
            }

            if (targetAir && !targetGround) {
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
                if (target == null && hitTeam && exp >= minPower) {
                    target = Units.bestEnemy(other, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
                }
            } else {
                target = Units.bestTarget(team, x, y, range, e -> !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> targetGround && buildingFilter.get(b) && b != this, unitSort);
                if (target == null && hitTeam && exp >= minPower) {
                    target = Units.bestTarget(other, x, y, range, e -> !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> targetGround && buildingFilter.get(b) && b != this, unitSort);
                }
            }

            if (target == null && canHeal()) {
                target = Units.findAllyTile(team, x, y, range, b -> b.damaged() && b != this);
            }
        }

        @Override
        public float range() {
            if (peekAmmo() != null) {
                return range * boost + peekAmmo().rangeChange;
            }
            return range * boost;
        }

        protected boolean validateTarget() {
            return !Units.invalidateTarget(target, (canHeal() || (hitTeam && exp > minPower)) ? Team.derelict : team, x, y) || isControlled() || logicControlled();
        }

        @Override
        public BulletType peekAmmo() {
            return bullet;
        }

        public BulletType useAmmo() {
            return bullet;
        }

        public boolean hasAmmo() {
            return true;
        }

        public void read(Reads read, byte version) {
            super.read(read, version);

            exp = read.f();
            fireEffect = TypeIO.readEffect(read);
        }

        public void write(Writes write) {
            super.write(write);

            write.f(exp);
            TypeIO.writeEffect(write, fireEffect);
        }

        @Override
        public void addExp(float exp) {
            this.exp += exp;
        }

        @Override
        public void upgrade() {
        }

        @Override
        public boolean canControl() {
            return exp <= maxHealth * 2;
        }
    }
}
