package Floor.FEntities.FBlock;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.core.World;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Liquid;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.HashMap;
import java.util.Map;

import static mindustry.Vars.*;

public class WindTurret extends Block {
    public final Map<Liquid, StatusEffect> liquidStatus = new HashMap<>();
    public float statusTime = 240;
    public float windLength = 200;
    public float windWidth = 100;
    public float windPower = 0.3f;

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

        @Override
        public void updateTile() {
            super.updateTile();

            if (added && tiles.size == 0) {
                getTile();
            } else if (!added) {
                tiles.clear();
            }

            if (liquids.current() == null || liquids.current() != lastLiquid || liquids.currentAmount() == 0 || efficiency <= 0) {
                boost = 0;
            }
            lastLiquid = liquids.current();

            if (lastLiquid != null && liquids.currentAmount() > 0 && efficiency > 0) {
                boost = Mathf.lerpDelta(boost, 1, 0.01f);

                applyEffect = liquidStatus.get(lastLiquid);
                if (applyEffect == null) {
                    applyEffect = StatusEffects.none;
                }

                use.clear();
                for (int i = 0; i < tiles.size; i++) {
                    Tile t = tiles.get(i);
                    if (inRange(t.worldx(), t.worldy(), boost)) {
                        use.add(t);
                    }
                }

                Seq<Unit> units = new Seq<>();

                Units.nearby(x, y, Math.max(windWidth / 2, windLength) * 1.4f,
                        Math.max(windWidth / 2, windLength) * 1.4f, u -> {
                            if (u.isGrounded() && use.indexOf(u.tileOn()) >= 0) {
                                units.add(u);
                            } else if (u.isFlying() && inRange(u.x, u.y, boost)) {
                                units.add(u);
                            }
                        }
                );

                for (Unit u : units) {
                    if (Fires.has(u.tileX(), u.tileY())) {
                        u.apply(applyEffect, statusTime);
                    }
                    Vec2 vec = new Vec2();
                    vec.set(u.x - x, u.y - y);
                    vec.setLength(windPower);
                    u.moveAt(vec);
                }

                for (Tile til : use) {
                    float tx = til.worldx();
                    float ty = til.worldy();
                    if (applyEffect.opposites.contains(StatusEffects.burning)) {
                        Fires.extinguish(til, 0.5f);
                    } else if (Fires.has(World.toTile(tx), World.toTile(ty))) {
                        for (float x = tx - 1; x <= tx + 1; x += 1) {
                            for (float y = ty - 1; y <= ty + 1; y += 1) {
                                Tile t = world.tileWorld(x, y);
                                if (t != null) {
                                    Fires.create(t);
                                }
                            }
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
                    if (inRange(t.worldx(), t.worldy(), 1f)) {
                        tiles.add(t);
                    }
                }
            }
        }

        public boolean inRange(float bx, float by, float boost) {
            float angle = Angles.angleDist(rotation * 90, Angles.angle(x, y, bx, by));
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
