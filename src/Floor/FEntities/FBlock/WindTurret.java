package Floor.FEntities.FBlock;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.HashMap;
import java.util.Map;

import static mindustry.Vars.world;

public class WindTurret extends Block {
    public final Map<Liquid, StatusEffect> liquidStatus = new HashMap<>();
    public float statusTime = 240;
    public float windLength = 500;
    public float windWidth = 100;
    public float windPower = 0.2f;

    public WindTurret(String name) {
        super(name);

        update = true;
        solid = true;
        rotate = true;
        hasPower = true;
        hasLiquids = true;
    }

    public class WindBuild extends Building {
        public StatusEffect applyEffect = StatusEffects.none;
        public float boost = 0;
        public Liquid lastLiquid = null;
        public final Seq<Tile> tiles = new Seq<>();
        public final Seq<Tile> use = new Seq<>();
        private float timer = 0;
        private final Effect fireEffect = new ParticleEffect() {{
            lifetime = 500;

            particles = 1;
            sizeTo = 0;
            sizeFrom = 1;
            colorFrom = Pal.lightFlame;
            colorTo = Pal.darkFlame;
            lightColor = Pal.gray;
        }};

        @Override
        public void updateTile() {
            super.updateTile();

            if (tiles.size == 0) {
                getTile();
            }

            if (liquids.current() == null || liquids.current() != lastLiquid || liquids.currentAmount() == 0 || efficiency <= 0) {
                boost = 0;
            }
            lastLiquid = liquids.current();

            if (lastLiquid != null && liquids.currentAmount() > 0 && efficiency > 0) {
                boost = Mathf.lerpDelta(boost, 1, 0.1f);
                timer += Time.delta;

                applyEffect = liquidStatus.get(lastLiquid);
                if (applyEffect == null) {
                    applyEffect = StatusEffects.none;
                }

                use.clear();
                boolean hasFire = false;
                for (int i = 0; i < tiles.size; i++) {
                    Tile t = tiles.get(i);
                    if (inRange(t, boost)) {
                        use.add(t);
                        if (!hasFire && Fires.has(tiles.get(i).x, tiles.get(i).y)) {
                            hasFire = true;
                        }
                    }
                }

                Seq<Unit> units = new Seq<>();

                Units.nearby(x, y, windLength * 1.4f, windLength * 1.4f, u -> {
                    if (u.isGrounded() && u.physref.body.layer <= 2 && use.indexOf(u.tileOn()) >= 0) {
                        units.add(u);
                    }
                });

                for (Unit u : units) {
                    if (hasFire) {
                        u.apply(applyEffect, statusTime);
                    }
                    Vec2 vec = new Vec2();
                    vec.set(u.x - x, u.y - y);
                    vec.setLength(windPower);
                    u.moveAt(vec);
                }

                if (hasFire) {
                    for (Tile til : use) {
                        if (applyEffect.opposites.contains(StatusEffects.burning)) {
                            Fires.extinguish(til, -1);
                        } else {
                            if (timer > 600) {
                                fireEffect.at(til);
                                Sounds.flame.at(til);
                                timer = 0;
                            }
                            Fires.create(til);
                        }
                    }
                }

                liquids.remove(lastLiquid, 0.05f);
            }
        }

        public void getTile() {
            for (int wx = (int) (x + Math.sqrt(windLength * windLength + windWidth * windWidth / 4));
                 wx >= (int) (x - Math.sqrt(windLength * windLength + windWidth * windWidth / 4)); wx--) {
                for (int wy = (int) (y + Math.sqrt(windLength * windLength + windWidth * windWidth / 4));
                     wy >= (int) (y - Math.sqrt(windLength * windLength + windWidth * windWidth / 4)); wy--) {
                    Tile t = world.tileWorld(wx, wy);
                    if (t == null) {
                        continue;
                    }
                    if (inRange(t, 1f)) {
                        tiles.add(t);
                    }
                }
            }
        }

        public boolean inRange(Tile t, float boost) {
            float bx = t.worldx();
            float by = t.worldy();
            float angle = Angles.angleDist(rotation + 90, Angles.angle(x, y, bx, by));
            float len = (float) Math.sqrt((x - bx) * (x - bx) + (y - by) * (y - by));
            return angle < 90 && len * Math.cos(Math.toRadians(angle)) <= windLength * boost &&
                    windWidth / 2 / windLength - Math.tan(Math.toRadians(angle)) >= -0.01f;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquids.current() == null || (liquids.current() == liquid && liquids.currentAmount() < liquidCapacity);
        }
    }
}
