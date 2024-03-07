package Floor.FAI;

import Floor.FEntities.FUnit.F.TileMiner;
import Floor.FTools.FLine;
import Floor.FTools.NeedPoseBridge;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.world;

public class TileMinerAI extends AIController implements NeedPoseBridge {
    protected TileMiner sm = null;
    protected Unit spawner;
    protected Item targetItem;
    protected Tile ore;
    protected boolean mining;
    protected Vec2 targetPos;

    public TileMinerAI(Unit unit) {
        this.unit = unit;
    }

    public TileMinerAI() {
    }

    @Override
    public void updateMovement() {
        if (sm != null && spawner != null) {
            if (!unit.canMine()) return;
            if (unit.mineTile != null && !unit.mineTile.within(unit, unit.hitSize * 4)) {
                unit.elevation = 1;
                FLine.tm.remove(unit.mineTile);
                unit.mineTile(null);
                targetItem = null;
                ore = null;
            }
            if (!unit.validMine(ore)) {
                ore = null;
                unit.mineTile = null;
            }
            if (sm.team.isAI()) {
                if (mining) {
                    if (sm.tiles[0] != null && sm.tiles[1] != null) {
                        mining = false;
                    } else {
                        if (timer.get(timerTarget2, 60 * 4) || targetItem == null) {
                            CoreBlock.CoreBuild core = unit.closestEnemyCore();
                            if(core != null){
                                targetItem = unit.type.mineItems.min(i -> FLine.hasOre(i) && unit.canMine(i) && FLine.allOres.get(i) > 0,
                                        i -> core.items.get(i));
                            }
                        }
                        if (targetItem != null) {
                            ore = FLine.findOre(sm, targetItem);
                        }
                        if (ore != null) {
                            moveTo(ore, unit.hitSize * 4, 20f);
                            if (ore.block() == Blocks.air && unit.within(ore, unit.hitSize * 5)) {
                                unit.mineTile = ore;
                                FLine.tm.put(ore, sm);
                            }
                            if (ore.block() != Blocks.air) {
                                mining = false;
                                targetItem = null;
                            }
                        }
                    }
                } else {
                    if (sm.tiles[0] == null || sm.tiles[1] == null) {
                        mining = true;
                    } else {
                        unit.mineTile = null;
                        Teamc target = Units.closestTarget(unit.team, unit.x, unit.y, Float.MAX_VALUE,
                                Unitc::hasWeapons, building -> building instanceof Turret.TurretBuild);
                        if (target != null) {
                            moveTo(target, 0);
                        }
                    }
                }
            } else {
                if (mining) {
                    if (sm.tiles[0] != null && sm.tiles[1] != null) {
                        mining = false;
                    } else {
                        if (targetPos != null) {
                            Tile tile = world.tileWorld((int) (targetPos.x), (int) (targetPos.y));
                            if (tile != null && unit.canMine(tile.drop())) {
                                ore = tile;
                            } else {
                                ore = null;
                                unit.mineTile = null;
                                moveTo(targetPos, unit.hitSize / 2);
                            }
                        }
                        if (ore != null) {
                            moveTo(ore, unit.hitSize * 3, 20f);
                            if (ore.block() == Blocks.air && unit.within(ore, unit.hitSize * 4)) {
                                unit.mineTile = ore;
                                FLine.tm.put(ore, sm);
                            }
                            if (ore.block() != Blocks.air) {
                                mining = false;
                                targetItem = null;
                            }
                        }
                    }
                } else {
                    if (sm.tiles[0] == null || sm.tiles[1] == null) {
                        mining = true;
                    } else {
                        moveTo(sm.spawner, 5);
                    }
                }
            }
        } else if (unit instanceof TileMiner) {
            sm = (TileMiner) unit;
            spawner = sm.spawner;
            ore = sm.mineTile;
        }
    }
    @Override
    public void init() {
        if (unit instanceof TileMiner) {
            sm = (TileMiner) unit;
            spawner = sm.spawner;
            ore = sm.mineTile;
        }
    }
    @Override
    public void setPose(Vec2 vec2) {
        targetPos = vec2;
    }
}
