package Floor.FEntities.FBlock;

import Floor.FContent.FStatusEffects;
import Floor.FType.FDialog.ProjectsLocated;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;

import static Floor.FType.FDialog.ProjectsLocated.*;
import static mindustry.Vars.player;

public class UnitProjectBlock extends Block {
    private static int num = 0;
    public float pointReload = 60;
    public float laserReload = 120;
    public Effect applyEffect = Fx.unitDespawn;
    public Effect laserEffect = Fx.none;
    public Effect pointEffect = new WaveEffect() {{
        lifetime = 45;
        sides = 4;
        colorFrom = Pal.redLight;
        lightColor = Pal.redLight;
        colorTo = Pal.redLight;
        sizeFrom = 10;
        sizeTo = 30;
        strokeFrom = 3;
        strokeTo = 0;
    }};

    public UnitProjectBlock(String name) {
        super(name);
        update = true;
        configurable = true;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return num == 0;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Unit u = player.unit();
        Drawf.light(u.x, u.y, u.type.region, u.rotation, Pal.lightOrange, 12);
    }

    public class UnitProjectBuild extends Building {
        float pointTimer = 0;
        float laserTimer = 0;

        @Override
        public void updateTile() {
            pointTimer++;
            laserTimer++;
            if (projects == null) {
                ProjectsLocated.create();
            }
            Unit play = player.unit();
            if (play != null && play.spawnedByCore) {
                if (!(play.mounts[play.mounts.length - 1] == sign)) {
                    play.apply(FStatusEffects.StrongStop, 180);
                    applyEffect.at(player.unit().x, player.unit().y, 0, player.unit());
                    projects.upper.get(play);
                }
            }
            if (play != null) {
                if (pointTimer >= pointReload) {
                    pointTimer = 0;
                    pointEffect.at(play.x, play.y, play.rotation, play);
                }
                if (laserTimer >= laserReload) {
                    laserTimer = 0;
                    laserEffect.at(play.x, play.y, play.rotation, new float[]{this.x, this.y, play.x, play.y});
                }
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            table.row();
            table.table(t -> t.button(Icon.units, () -> {
                if (projects == null) {
                    ProjectsLocated.create();
                }
                projects.set(player.unit());
                projects.show();
            }));
        }

        @Override
        public void add() {
            super.add();
            if (projects == null) {
                ProjectsLocated.create();
            }
            projects.setZero();
            num = 1;
        }

        @Override
        public void remove() {
            super.remove();
            num = 0;
            if (projects != null) {
                projects.setZero();
                if (player.unit() != null && player.unit().spawnedByCore) {
                    projects.upper.get(player.unit());
                    player.unit().unapply(eff);
                    WeaponMount[] mount = new WeaponMount[player.unit().type.weapons.size];
                    System.arraycopy(player.unit().mounts, 0, mount, 0, mount.length);
                    player.unit().mounts = mount;
                    Ability[] ability = new Ability[player.unit().type.abilities.size];
                    System.arraycopy(player.unit().abilities, 0, ability, 0, ability.length);
                    player.unit().abilities = ability;
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            if (player.unit() != null) {
                projects.set(player.unit());
            }
            projects.write(write);
            num = 0;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read);
            if (projects == null) {
                ProjectsLocated.create();
            }
            projects.read(read);
            num = 1;
        }
    }
}
