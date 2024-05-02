package Floor.FEntities.FBulletType;

import arc.Core;
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
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.world.blocks.distribution.MassDriver;

import static arc.graphics.g2d.Draw.color;
import static mindustry.Vars.content;
import static mindustry.Vars.headless;

public class AllBulletTypes extends BulletType {
    public String type = "base";
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
    //artillery
    public float trailMult = 1f, trailSize = 4f;
    //emp
    public float radius = 100f;
    public float timeIncrease = 2.5f, timeDuration = 60f * 10f;
    public float powerDamageScl = 2f, powerSclDecrease = 0.2f;
    public Effect hitPowerEffect = Fx.hitEmpSpark, chainEffect = Fx.chainEmp, applyEffect = Fx.heal;
    public boolean hitUnits = true;
    public float unitDamageScl = 0.7f;
    //flak
    public float explodeRange = 30f, explodeDelay = 5f, flakDelay = 0f, flakInterval = 6f;
    //continuous
    public float length = 220f;
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
    public float fadeTime = 16f;
    public float strokeFrom = 2f, strokeTo = 0.5f, pointyScaling = 0.75f;
    public float backLength = 7f, frontLength = 35f;
    //fire
    public Color colorFrom = Pal.lightFlame, colorMid = Pal.darkFlame, colorTo = Color.gray;
    public float velMin = 0.6f, velMax = 2.6f;
    public float fireTrailChance = 0.04f;
    public Effect trailEffect2 = Fx.ballfire;
    public float fireEffectChance = 0.1f, fireEffectChance2 = 0.1f;
    //laser
    public Effect laserEffect = Fx.lancerLaserShootSmoke;
    public float lengthFalloff = 0.5f;
    public float sideLength = 29f, sideWidth = 0.7f;
    public float sideAngle = 90f;
    public float lightningSpacing = -1, lightningDelay = 0.1f, lightningAngleRand;
    //lightning
    public Color lightningColor = Pal.lancerLaser;
    public int lightningLength = 25, lightningLengthRand = 0;
    //point
    private static float cdist = 0f;
    private static Unit result;
    public float trailSpacing = 10f;
    //pointL
    public TextureRegion laser, laserEnd;

    public Color color = Color.white;

    public Effect beamEffect = Fx.colorTrail;
    public float beamEffectInterval = 3f, beamEffectSize = 3.5f;
    //rail
    static float furthest = 0;
    static boolean any = false;
    public Effect pierceEffect = Fx.hitBulletSmall, pointEffect = Fx.none, lineEffect = Fx.none;
    public Effect endEffect = Fx.none;
    public float pointEffectSpace = 20f;
    //

    public void bomb() {
        type = "bomb";
        collidesTiles = false;
        collides = false;
        shrinkY = 0.7f;
        drag = 0.05f;
        keepVelocity = false;
        collidesAir = false;
        hitSound = Sounds.explosion;
        sprite = "shell";
    }

    public void artillery() {
        type = "artillery";
        collidesTiles = false;
        collides = false;
        collidesAir = false;
        scaleLife = true;
        hitShake = 1f;
        hitSound = Sounds.explosion;
        hitEffect = Fx.flakExplosion;
        shootEffect = Fx.shootBig;
        trailEffect = Fx.artilleryTrail;

        shrinkX = 0.15f;
        shrinkY = 0.63f;
        shrinkInterp = Interp.slope;
        sprite = "shell";
    }

    public void emp() {
        type = "emp";
    }

    public void flak() {
        type = "flak";
        hitEffect = Fx.flakExplosionBig;
        collidesGround = false;
    }

    public void laserB() {
        type = "laserB";
        smokeEffect = Fx.hitLaser;
        hitEffect = Fx.hitLaser;
        despawnEffect = Fx.hitLaser;
        lightColor = Pal.heal;
        lightOpacity = 0.6f;
    }

    public void missile() {
        type = "missile";
        backColor = Pal.missileYellowBack;
        frontColor = Pal.missileYellow;
        homingPower = 0.08f;
        shrinkY = 0f;
        hitSound = Sounds.explosion;
        trailChance = 0.2f;
    }

    public void continuous() {
        type = "continuous";
        removeAfterPierce = false;
        despawnEffect = Fx.none;
        shootEffect = Fx.none;
        impact = true;
        keepVelocity = false;
        collides = false;
    }

    public void continuousF() {
        type = "continuousF";
        optimalLifeFract = 0.5f;
        hitEffect = Fx.hitFlameBeam;
        hitSize = 4;
        drawSize = 420f;
        hitColor = colors[1].cpy().a(1f);
        lightColor = hitColor;
        laserAbsorb = false;
        ammoMultiplier = 1f;
        pierceArmor = true;
    }

    public void continuousL() {
        type = "continuousL";
        shake = 1f;
        largeHit = true;
        hitEffect = Fx.hitBeam;
        hitSize = 4;
        drawSize = 420f;
        hitColor = colors[2];
        incendAmount = 1;
        incendSpread = 5;
        incendChance = 0.4f;
        lightColor = Color.orange;
    }

    public void explosion() {
        type = "explosion";
        hittable = false;
        lifetime = 1f;
        speed = 0f;
        rangeOverride = 20f;
        shootEffect = Fx.massiveExplosion;
        instantDisappear = true;
        scaledSplashDamage = true;
        killShooter = true;
        collides = false;
        keepVelocity = false;
    }

    public void fire() {
        type = "fire";
        collidesTiles = false;
        collides = false;
        drag = 0.03f;
        hitEffect = despawnEffect = Fx.none;
        trailEffect = Fx.fireballsmoke;
    }

    public void laser() {
        type = "laser";
        hitEffect = Fx.hitLaserBlast;
        hitColor = colors[2];
        despawnEffect = Fx.none;
        shootEffect = Fx.hitLancer;
        smokeEffect = Fx.none;
        hitSize = 4;
        impact = true;
        keepVelocity = false;
        collides = false;
    }

    public void lightning() {
        type = "lightning";
        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        keepVelocity = false;
    }

    public void mass() {
        type = "mass";
        collidesTiles = false;
        despawnEffect = Fx.smeltsmoke;
        hitEffect = Fx.hitBulletBig;
    }

    public void point() {
        type = "point";
        scaleLife = true;
        collides = false;
        keepVelocity = false;
        backMove = false;
    }

    public void pointL() {
        removeAfterPierce = false;
        despawnEffect = Fx.none;
        impact = true;
        keepVelocity = false;
        collides = false;
        optimalLifeFract = 0.5f;
        shootEffect = smokeEffect = Fx.none;
        drawSize = 1000f;
    }

    public void rail() {
        type = "rail";
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        collides = false;
        keepVelocity = false;
    }

    @Override
    public void update(Bullet b) {
        switch (type) {
            case "artillery": {
                super.update(b);
                if (b.timer(0, (3 + b.fslope() * 2f) * trailMult)) {
                    trailEffect.at(b.x, b.y, b.fslope() * trailSize, backColor);
                }
                break;
            }
            case "flak": {
                super.update(b);
                if (b.time >= flakDelay && b.fdata >= 0 && b.timer(2, flakInterval)) {
                    Units.nearbyEnemies(b.team, Tmp.r1.setSize(explodeRange * 2f).setCenter(b.x, b.y), unit -> {
                        if (b.fdata < 0f || !unit.checkTarget(collidesAir, collidesGround) || !unit.targetable(b.team))
                            return;

                        if (unit.within(b, explodeRange + unit.hitSize / 2f)) {
                            b.fdata = -1f;
                            Time.run(explodeDelay, () -> {
                                if (b.fdata < 0) {
                                    b.time = b.lifetime;
                                }
                            });
                        }
                    });
                }
                break;
            }
            case "continuous": {
                if (!continuous) return;
                if (b.timer(1, damageInterval)) {
                    applyDamage(b);
                }
                if (shake > 0) {
                    Effect.shake(shake, shake, b);
                }
                updateBulletInterval(b);
                break;
            }
            case "fire": {
                super.update(b);
                if (Mathf.chanceDelta(fireTrailChance)) {
                    Fires.create(b.tileOn());
                }
                if (Mathf.chanceDelta(fireEffectChance)) {
                    trailEffect.at(b.x, b.y);
                }
                if (Mathf.chanceDelta(fireEffectChance2)) {
                    trailEffect2.at(b.x, b.y);
                }
                break;
            }
            case "mass": {
                if (!(b.data() instanceof MassDriver.DriverBulletData data)) {
                    hit(b);
                    return;
                }
                float hitDst = 7f;
                if (data.to.dead()) {
                    return;
                }
                float baseDst = data.from.dst(data.to);
                float dst1 = b.dst(data.from);
                float dst2 = b.dst(data.to);
                boolean intersect = false;
                if (dst1 > baseDst) {
                    float angleTo = b.angleTo(data.to);
                    float baseAngle = data.to.angleTo(data.from);
                    if (Angles.near(angleTo, baseAngle, 2f)) {
                        intersect = true;
                        b.set(data.to.x + Angles.trnsx(baseAngle, hitDst), data.to.y + Angles.trnsy(baseAngle, hitDst));
                    }
                }
                if (Math.abs(dst1 + dst2 - baseDst) < 4f && dst2 <= hitDst) {
                    intersect = true;
                }
                if (intersect) {
                    data.to.handlePayload(b, data);
                }
                break;
            }
            case "pointL": {
                updateTrail(b);
                updateTrailEffects(b);
                updateBulletInterval(b);
                if (b.timer.get(0, damageInterval)) {
                    Damage.collidePoint(b, b.team, hitEffect, b.aimX, b.aimY);
                }
                if (b.timer.get(1, beamEffectInterval)) {
                    beamEffect.at(b.aimX, b.aimY, beamEffectSize * b.fslope(), hitColor);
                }
                if (shake > 0) {
                    Effect.shake(shake, shake, b);
                }
                break;
            }
            default: {
                super.update(b);
            }
        }
    }

    @Override
    public void load() {
        switch (type) {
            case "base", "bomb": {
                super.load();
                backRegion = Core.atlas.find(backSprite == null ? (sprite + "-back") : backSprite);
                frontRegion = Core.atlas.find(sprite);
                break;
            }
            case "pointL": {
                super.load();
                laser = Core.atlas.find(sprite);
                laserEnd = Core.atlas.find(sprite + "-end");
            }
            default: {
                super.load();
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        switch (type) {
            case "base", "bomb": {
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
            case "laserB": {
                super.draw(b);
                Draw.color(backColor);
                Lines.stroke(width);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), height);
                Draw.color(frontColor);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), height / 2f);
                Draw.reset();
                break;
            }
            case "continuousF": {
                float mult = b.fin(lengthInterp);
                float realLength = Damage.findLength(b, length * mult, laserAbsorb, pierceCap);
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
                float realLength = Damage.findLength(b, length * fout, laserAbsorb, pierceCap);
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
            case "fire": {
                Draw.color(colorFrom, colorMid, colorTo, b.fin());
                Fill.circle(b.x, b.y, radius * b.fout());
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
            case "mass": {
                float w = 11f, h = 13f;
                Draw.color(Pal.bulletYellowBack);
                Draw.rect("shell-back", b.x, b.y, w, h, b.rotation() + 90);
                Draw.color(Pal.bulletYellow);
                Draw.rect("shell", b.x, b.y, w, h, b.rotation() + 90);
                Draw.reset();
            }
            case "pointL": {
                super.draw(b);
                Draw.color(color);
                Drawf.laser(laser, laserEnd, b.x, b.y, b.aimX, b.aimY, b.fslope() * (1f - oscMag + Mathf.absin(Time.time, oscScl, oscMag)));
                Draw.reset();
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

    @Override
    public void hit(Bullet b, float x, float y) {
        switch (type) {
            case "emp": {
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
                break;
            }
            case "mass": {
                super.hit(b, x, y);
                despawned(b);
            }
            default: {
                super.hit(b, x, y);
            }
        }
    }

    @Override
    public float continuousDamage() {
        if (!continuous) return -1f;
        switch (type) {
            case "continuous", "pointL": {
                return damage / damageInterval * 60f;
            }
        }
        return -1;
    }

    @Override
    public float estimateDPS() {
        if (!continuous) return super.estimateDPS();
        return switch (type) {
            case "continuous", " pointL" -> damage * 100f / damageInterval * 3f;
            case "laser" -> super.estimateDPS() * 3f;
            case "lightning" -> super.estimateDPS() * Math.max(lightningLength / 10f, 1);
            default -> 0;
        };
    }

    @Override
    protected float calculateRange() {
        return switch (type) {
            case "continuous", "laser" -> Math.max(length, maxRange);
            case "lightning" -> (lightningLength + lightningLengthRand / 2f) * 6f;
            case "rail" -> length;
            default -> 0;
        };
    }

    @Override
    public void init() {
        switch (type) {
            case "continuous", "laser": {
                super.init();
                drawSize = Math.max(drawSize, length * 2f);
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
            case "continuous": {
                super.init(b);
                if (!continuous) {
                    applyDamage(b);
                }
                break;
            }
            case "fire": {
                super.init(b);
                b.vel.setLength(Mathf.random(velMin, velMax));
                break;
            }
            case "laser": {
                float resultLength = Damage.collideLaser(b, length, largeHit, laserAbsorb, pierceCap), rot = b.rotation();
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
                Lightning.create(b, lightningColor, damage, b.x, b.y, b.rotation(), lightningLength + Mathf.random(lightningLengthRand));
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
                b.fdata = length;
                furthest = length;
                any = false;
                Damage.collideLine(b, b.team, b.type.hitEffect, b.x, b.y, b.rotation(), length, false, false);
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

    @Override
    public void despawned(Bullet b) {
        switch (type) {
            case "mass": {
                super.despawned(b);

                if (!(b.data() instanceof MassDriver.DriverBulletData data)) return;

                for (int i = 0; i < data.items.length; i++) {
                    int amountDropped = Mathf.random(0, data.items[i]);
                    if (amountDropped > 0) {
                        float angle = b.rotation() + Mathf.range(100f);
                        Fx.dropItem.at(b.x, b.y, angle, Color.white, content.item(i));
                    }
                }
            }
            default: {
                super.despawned(b);
            }
        }
    }

    @Override
    public void updateTrailEffects(Bullet b) {
        if (type.equals("pointL")) {
            if (trailChance > 0) {
                if (Mathf.chanceDelta(trailChance)) {
                    trailEffect.at(b.aimX, b.aimY, trailRotation ? b.angleTo(b.aimX, b.aimY) : (trailParam * b.fslope()), trailColor);
                }
            }

            if (trailInterval > 0f) {
                if (b.timer(0, trailInterval)) {
                    trailEffect.at(b.aimX, b.aimY, trailRotation ? b.angleTo(b.aimX, b.aimY) : (trailParam * b.fslope()), trailColor);
                }
            }
        } else {
            super.updateTrailEffects(b);
        }
    }

    @Override
    public void updateTrail(Bullet b) {
        if (type.equals("pointL")) {
            if (!headless && trailLength > 0) {
                if (b.trail == null) {
                    b.trail = new Trail(trailLength);
                }
                b.trail.length = trailLength;
                b.trail.update(b.aimX, b.aimY, b.fslope() * (1f - (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
            }
        } else {
            super.updateTrailEffects(b);
        }
    }

    public void updateBulletInterval(Bullet b) {
        if (type.equals("pointL")) {
            if (intervalBullet != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)) {
                float ang = b.rotation();
                for (int i = 0; i < intervalBullets; i++) {
                    intervalBullet.create(b, b.aimX, b.aimY, ang + Mathf.range(intervalRandomSpread) + intervalAngle + ((i - (intervalBullets - 1f) / 2f) * intervalSpread));
                }
            }
        } else {
            super.updateBulletInterval(b);
        }
    }

    public void applyDamage(Bullet b) {
        switch (type) {
            case "continuous": {
                if (!continuous) {
                    Damage.collideLine(b, b.team, hitEffect, b.x, b.y, b.rotation(), currentLength(b), largeHit, laserAbsorb, pierceCap);
                }
            }
        }
    }

    public float currentLength(Bullet b) {
        switch (type) {
            case "continuous": {
                if (!continuous) {
                    return length;
                }
            }
            case "continuousF": {
                return length * b.fin(lengthInterp);
            }
            case "continuousL": {
                float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
                return length * fout;
            }
        }
        return 0;
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
    }
}
