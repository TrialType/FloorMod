package Floor.FEntities.FBulletType;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import java.lang.reflect.Field;

import static arc.graphics.g2d.Draw.color;

public class LimitBulletType extends BulletType {
    public String type = "bullet";
    public boolean haveEmp = false;
    public boolean havePercent = false;


    public float percent = 0;
    public Color backColor = Pal.bulletYellowBack, frontColor = Pal.bulletYellow;
    public Color mixColorFrom = new Color(1f, 1f, 1f, 0f), mixColorTo = new Color(1f, 1f, 1f, 0f);
    public float width = 5f, height = 7f;
    public float shrinkX = 0f, shrinkY = 0.5f;
    public Interp shrinkInterp = Interp.linear;
    public float spin = 0, rotationOffset = 0f;
    public String sprite;
    public @Nullable String backSprite;
    public TextureRegion backRegion;
    public TextureRegion frontRegion;
    //emp
    public float radius = 100f;
    public float timeIncrease = 2.5f, timeDuration = 60f * 10f;
    public float powerDamageScl = 2f, powerSclDecrease = 0.2f;
    public Effect hitPowerEffect = Fx.hitEmpSpark, chainEffect = Fx.chainEmp, applyEffect = Fx.heal;
    public boolean hitUnits = true;
    public float unitDamageScl = 0.7f;
    //continuous
    public float shake = 0f;
    public float damageInterval = 5f;
    public boolean largeHit = false;
    public boolean continuous = true;
    //continuousF
    public float lightStroke = 40f;
    public float oscScl = 1.2f, oscMag = 0.02f;
    public int divisions = 25;
    public boolean drawFlare = true;
    public Color flareColor = Color.valueOf("e189f5");
    public float flareWidth = 3f, flareInnerScl = 0.5f, flareLength = 40f, flareInnerLenScl = 0.5f, flareLayer = Layer.bullet - 0.0001f, flareRotSpeed = 1.2f;
    public boolean rotateFlare = false;
    public Interp lengthInterp = Interp.slope;
    public float[] lengthWidthPans = {
            1.12f, 1.3f, 0.32f,
            1f, 1f, 0.3f,
            0.8f, 0.9f, 0.2f,
            0.5f, 0.8f, 0.15f,
            0.25f, 0.7f, 0.1f,
    };
    public Color[] colors = {Color.valueOf("eb7abe").a(0.55f), Color.valueOf("e189f5").a(0.7f), Color.valueOf("907ef7").a(0.8f), Color.valueOf("91a4ff"), Color.white.cpy()};
    //continuousL
    public float laserCLength = 0;
    public float fadeTime = 16f;
    public float strokeFrom = 2f, strokeTo = 0.5f, pointyScaling = 0.75f;
    public float backLength = 7f, frontLength = 35f;
    //laser
    public float laserLength = 220;
    public Effect laserEffect = Fx.lancerLaserShootSmoke;
    public float lengthFalloff = 0.5f;
    public float sideLength = 29f, sideWidth = 0.7f;
    public float sideAngle = 90f;
    public float lightningSpacing = -1, lightningDelay = 0.1f, lightningAngleRand;
    //point
    private static float cdist = 0f;
    private static Unit result;
    public float trailSpacing = 10f;
    //rail
    public float railLength = 0;
    static float furthest = 0;
    static boolean any = false;
    public Effect pierceEffect = Fx.hitBulletSmall, pointEffect = Fx.none, lineEffect = Fx.none;
    public Effect endEffect = Fx.none;
    public float pointEffectSpace = 20f;
    //lightning
    public int bulletLightningLength = 0;
    public int bulletLightningLengthRand = 0;
    public Color bulletLightningColor = Pal.lancerLaser;

    //______________________________________________________________________________________________________________________
    @Override
    public void update(Bullet b) {
        if (type.equals("continuousF") || type.equals("continuousL")) {
            if (!continuous) return;
            if (b.timer(1, damageInterval)) {
                applyDamage(b);
            }
            if (shake > 0) {
                Effect.shake(shake, shake, b);
            }
            updateBulletInterval(b);
        } else {
            super.update(b);
        }
    }

    @Override
    public void load() {
        if (type.equals("bullet")) {
            super.load();
            backRegion = Core.atlas.find(backSprite == null ? (sprite + "-back") : backSprite);
            frontRegion = Core.atlas.find(sprite);
        } else {
            super.load();
        }
    }

    @Override
    public void draw(Bullet b) {
        switch (type) {
            case "continuousF": {
                float mult = b.fin(lengthInterp);
                float realLength = Damage.findLength(b, flareLength * mult, laserAbsorb, pierceCap);
                float sin = Mathf.sin(Time.time, oscScl, oscMag);
                for (int i = 0; i < colors.length; i++) {
                    Draw.color(colors[i].write(Tmp.c1).mul(0.9f).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
                    Drawf.flame(b.x, b.y, divisions, b.rotation(),
                            realLength * lengthWidthPans[i * 3] * (1f - sin),
                            width * lengthWidthPans[i * 3 + 1] * mult * (1f + sin),
                            lengthWidthPans[i * 3 + 2]
                    );
                }
                if (drawFlare) {
                    color(flareColor);
                    Draw.z(flareLayer);
                    float angle = Time.time * flareRotSpeed + (rotateFlare ? b.rotation() : 0f);
                    for (int i = 0; i < 4; i++) {
                        Drawf.tri(b.x, b.y, flareWidth, flareLength * (mult + sin), i * 90 + 45 + angle);
                    }
                    color();
                    for (int i = 0; i < 4; i++) {
                        Drawf.tri(b.x, b.y, flareWidth * flareInnerScl, flareLength * flareInnerLenScl * (mult + sin), i * 90 + 45 + angle);
                    }
                }
                Tmp.v1.trns(b.rotation(), realLength * 1.1f);
                Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
                Draw.reset();
                break;
            }
            case "continuousL": {
                float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
                float realLength = Damage.findLength(b, laserCLength * fout, laserAbsorb, pierceCap);
                float rot = b.rotation();
                for (int i = 0; i < colors.length; i++) {
                    Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
                    float colorFin = i / (float) (colors.length - 1);
                    float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
                    float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * baseStroke;
                    float ellipseLenScl = Mathf.lerp(1 - i / (float) (colors.length), 1f, pointyScaling);
                    Lines.stroke(stroke);
                    Lines.lineAngle(b.x, b.y, rot, realLength - frontLength, false);
                    Drawf.flameFront(b.x, b.y, divisions, rot + 180f, backLength, stroke / 2f);
                    Tmp.v1.trnsExact(rot, realLength - frontLength);
                    Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, divisions, rot, frontLength * ellipseLenScl, stroke / 2f);
                }
                Tmp.v1.trns(b.rotation(), realLength * 1.1f);
                Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
                Draw.reset();
                break;
            }
            case "laser": {
                float realLength = b.fdata;
                float f = Mathf.curve(b.fin(), 0f, 0.2f);
                float baseLen = realLength * f;
                float cwidth = width;
                float compound = 1f;
                Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
                for (Color color : colors) {
                    Draw.color(color);
                    Lines.stroke((cwidth *= lengthFalloff) * b.fout());
                    Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
                    Tmp.v1.trns(b.rotation(), baseLen);
                    Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Lines.getStroke(), cwidth * 2f + width / 2f, b.rotation());
                    Fill.circle(b.x, b.y, 1f * cwidth * b.fout());
                    for (int i : Mathf.signs) {
                        Drawf.tri(b.x, b.y, sideWidth * b.fout() * cwidth, sideLength * compound, b.rotation() + sideAngle * i);
                    }
                    compound *= lengthFalloff;
                }
                Draw.reset();
                Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
                Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, width * 1.4f * b.fout(), colors[0], 0.6f);
                break;
            }
            case "lightning": {
                break;
            }
            case "bullet": {
                super.draw(b);
                float shrink = shrinkInterp.apply(b.fout());
                float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
                float width = this.width * ((1f - shrinkX) + shrinkX * shrink);
                float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;
                Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());
                Draw.mixcol(mix, mix.a);
                if (backRegion.found()) {
                    Draw.color(backColor);
                    Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() + offset);
                }
                Draw.color(frontColor);
                Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset);
                break;
            }
            default: {
                super.draw(b);
            }
        }
        Draw.reset();
    }

    @Override
    public void drawLight(Bullet b) {
        switch (type) {
            case "continuousF", "continuousL", "laser": {
            }
            default: {
                super.drawLight(b);
            }
        }
    }

    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        boolean wasDead = entity instanceof Unit u && u.dead;

        if (entity instanceof Healthc h) {
            if (pierceArmor) {
                h.damagePierce(b.damage);
            } else {
                h.damage(b.damage);
            }
            if (havePercent) {
                h.damage(h.maxHealth() * percent);
            }
        }

        if (entity instanceof Unit unit) {
            Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
            if (impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
            unit.impulse(Tmp.v3);
            unit.apply(status, statusDuration);

            Events.fire(new EventType.UnitDamageEvent().set(unit, b));
        }

        if (!wasDead && entity instanceof Unit unit && unit.dead) {
            Events.fire(new EventType.UnitBulletDestroyEvent(unit, b));
        }

        handlePierce(b, health, entity.x(), entity.y());
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        if (haveEmp) {
            super.hit(b, x, y);
            if (!b.absorbed) {
                Vars.indexer.allBuildings(x, y, radius, other -> {
                    if (other.team == b.team) {
                        if (other.block.hasPower && other.block.canOverdrive && other.timeScale() < timeIncrease) {
                            other.applyBoost(timeIncrease, timeDuration);
                            chainEffect.at(x, y, 0, hitColor, other);
                            applyEffect.at(other, other.block.size * 7f);
                        }

                        if (other.block.hasPower && other.damaged()) {
                            other.heal(healPercent / 100f * other.maxHealth() + healAmount);
                            Fx.healBlockFull.at(other.x, other.y, other.block.size, hitColor, other.block);
                            applyEffect.at(other, other.block.size * 7f);
                        }
                    } else if (other.power != null) {
                        var absorber = Damage.findAbsorber(b.team, x, y, other.x, other.y);
                        if (absorber != null) {
                            other = absorber;
                        }

                        if (other.power != null && other.power.graph.getLastPowerProduced() > 0f) {
                            other.applySlowdown(powerSclDecrease, timeDuration);
                            other.damage(damage * powerDamageScl);
                            hitPowerEffect.at(other.x, other.y, b.angleTo(other), hitColor);
                            chainEffect.at(x, y, 0, hitColor, other);
                        }
                    }
                });

                if (hitUnits) {
                    Units.nearbyEnemies(b.team, x, y, radius, other -> {
                        if (other.team != b.team && other.hittable()) {
                            var absorber = Damage.findAbsorber(b.team, x, y, other.x, other.y);
                            if (absorber != null) {
                                return;
                            }

                            hitPowerEffect.at(other.x, other.y, b.angleTo(other), hitColor);
                            chainEffect.at(x, y, 0, hitColor, other);
                            other.damage(damage * unitDamageScl);
                            other.apply(status, statusDuration);
                        }
                    });
                }
            }
        } else {
            super.hit(b, x, y);
        }
    }

    @Override
    public float continuousDamage() {
        return switch (type) {
            case "continuousF", "continuousL" -> damage / damageInterval * 60f;
            default -> super.continuousDamage();
        };
    }

    @Override
    public float estimateDPS() {
        return switch (type) {
            case "continuousF", "continuousL" -> damage * 100f / damageInterval * 3f;
            case "laser" -> super.estimateDPS() * 3f;
            case "lightning" -> super.estimateDPS() * Math.max(bulletLightningLength / 10f, 1);
            default -> super.estimateDPS();
        };
    }

    @Override
    public float calculateRange() {
        return switch (type) {
            case "continuousF" -> Math.max(flareLength, maxRange);
            case "continuousL" -> Math.max(laserCLength, maxRange);
            case "laser" -> Math.max(laserLength, maxRange);
            case "lightning" -> (bulletLightningLength + bulletLightningLengthRand / 2f) * 6f;
            case "rail" -> railLength;
            default -> super.calculateRange();
        };
    }

    @Override
    public void init() {
        switch (type) {
            case "laser": {
                super.init();
                drawSize = Math.max(drawSize, laserLength * 2f);
                break;
            }
            case "continuousF": {
                super.init();
                drawSize = Math.max(drawSize, flareLength * 2f);
                break;
            }
            case "continuousL": {
                super.init();
                drawSize = Math.max(drawSize, laserCLength * 2f);
                break;
            }
            default: {
                super.init();
            }
        }
    }

    @Override
    public void init(Bullet b) {
        switch (type) {
            case "continuousF", "continuousL": {
                super.init(b);
                if (!continuous) {
                    applyDamage(b);
                }
                break;
            }
            case "laser": {
                float resultLength = Damage.collideLaser(b, laserLength, largeHit, laserAbsorb, pierceCap), rot = b.rotation();
                laserEffect.at(b.x, b.y, rot, resultLength * 0.75f);
                if (lightningSpacing > 0) {
                    int idx = 0;
                    for (float i = 0; i <= resultLength; i += lightningSpacing) {
                        float cx = b.x + Angles.trnsx(rot, i),
                                cy = b.y + Angles.trnsy(rot, i);
                        int f = idx++;
                        for (int s : Mathf.signs) {
                            Time.run(f * lightningDelay, () -> {
                                if (b.isAdded() && b.type == this) {
                                    Lightning.create(b, lightningColor,
                                            lightningDamage < 0 ? damage : lightningDamage,
                                            cx, cy, rot + 90 * s + Mathf.range(lightningAngleRand),
                                            lightningLength + Mathf.random(lightningLengthRand));
                                }
                            });
                        }
                    }
                }
                break;
            }
            case "lightning": {
                super.init(b);
                Lightning.create(b, bulletLightningColor, damage, b.x, b.y, b.rotation(),
                        bulletLightningLength + Mathf.random(bulletLightningLengthRand));
                break;
            }
            case "point": {
                super.init(b);
                float px = b.x + b.lifetime * b.vel.x,
                        py = b.y + b.lifetime * b.vel.y,
                        rot = b.rotation();
                Geometry.iterateLine(0f, b.x, b.y, px, py, trailSpacing, (x, y) -> {
                    trailEffect.at(x, y, rot);
                });
                b.time = b.lifetime;
                b.set(px, py);
                cdist = 0f;
                result = null;
                float range = 1f;
                Units.nearbyEnemies(b.team, px - range, py - range, range * 2f, range * 2f, e -> {
                    if (e.dead() || !e.checkTarget(collidesAir, collidesGround) || !e.hittable()) return;
                    e.hitbox(Tmp.r1);
                    if (!Tmp.r1.contains(px, py)) return;
                    float dst = e.dst(px, py) - e.hitSize;
                    if ((result == null || dst < cdist)) {
                        result = e;
                        cdist = dst;
                    }
                });
                if (result != null) {
                    b.collision(result, px, py);
                } else if (collidesTiles) {
                    Building build = Vars.world.buildWorld(px, py);
                    if (build != null && build.team != b.team) {
                        build.collision(b);
                    }
                }
                b.remove();
                b.vel.setZero();
                break;
            }
            case "rail": {
                super.init(b);
                b.fdata = railLength;
                furthest = railLength;
                any = false;
                Damage.collideLine(b, b.team, b.type.hitEffect, b.x, b.y, b.rotation(), railLength, false, false);
                float resultLen = furthest;
                Vec2 nor = Tmp.v1.trns(b.rotation(), 1f).nor();
                if (pointEffect != Fx.none) {
                    for (float i = 0; i <= resultLen; i += pointEffectSpace) {
                        pointEffect.at(b.x + nor.x * i, b.y + nor.y * i, b.rotation(), trailColor);
                    }
                }
                if (!any && endEffect != Fx.none) {
                    endEffect.at(b.x + nor.x * resultLen, b.y + nor.y * resultLen, b.rotation(), hitColor);
                }
                if (lineEffect != Fx.none) {
                    lineEffect.at(b.x, b.y, b.rotation(), hitColor, new Vec2(b.x, b.y).mulAdd(nor, resultLen));
                }
                break;
            }
            default: {
                super.init(b);
            }
        }
    }

    public void applyDamage(Bullet b) {
        if (type.equals("continuousF") || type.equals("continuousL")) {
            if (!continuous) {
                Damage.collideLine(b, b.team, hitEffect, b.x, b.y, b.rotation(), currentLength(b), largeHit, laserAbsorb, pierceCap);
            }
        }
    }

    public float currentLength(Bullet b) {
        if (type.equals("continuousF")) {
            return flareLength * b.fin(lengthInterp);
        } else if (type.equals("continuousL")) {
            float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
            return laserCLength * fout;
        }
        return -1;
    }

    @Override
    public void handlePierce(Bullet b, float initialHealth, float x, float y) {
        if (type.equals("rail")) {
            float sub = Math.max(initialHealth * pierceDamageFactor, 0);
            if (b.damage <= 0) {
                b.fdata = Math.min(b.fdata, b.dst(x, y));
                return;
            }
            if (b.damage > 0) {
                pierceEffect.at(x, y, b.rotation());

                hitEffect.at(x, y);
            }
            b.damage -= Math.min(b.damage, sub);
            if (b.damage <= 0f) {
                furthest = Math.min(furthest, b.dst(x, y));
            }
            any = true;
        } else {
            super.handlePierce(b, initialHealth, x, y);
        }
    }

    @Override
    public boolean testCollision(Bullet bullet, Building tile) {
        if (type.equals("rail")) {
            return bullet.team != tile.team;
        }
        return super.testCollision(bullet, tile);
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
        if (type.equals("rail")) {
            handlePierce(b, initialHealth, x, y);
        } else {
            super.hitTile(b, build, x, y, initialHealth, direct);
        }
        if (!build.dead) {
            if (havePercent && build.team != b.team) {
                build.damage(build.maxHealth * percent);
            }
            if (build.dead) {
                Events.fire(new EventType.BuildingBulletDestroyEvent(build, b));
            } else {
                Events.fire(new EventType.BuildDamageEvent().set(build, b));
            }
        }
    }

    public void copyTo(LimitBulletType other) {
        try {
            Field[] fields = LimitBulletType.class.getFields();
            for (Field field : fields) {
                field.set(other, field.get(this));
            }
            fields = BulletType.class.getFields();
            for (Field field : fields) {
                field.set(other, field.get(this));
            }
        } catch (RuntimeException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(int id) {

    }

    public LimitBulletType read(int id) {
        return this;
    }
}
