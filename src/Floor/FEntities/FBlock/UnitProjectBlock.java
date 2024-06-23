package Floor.FEntities.FBlock;

import Floor.FContent.FStatusEffects;
import Floor.FType.FDialog.ProjectsLocated;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.world.Block;
import mindustry.world.Tile;

import static Floor.FType.FDialog.ProjectsLocated.*;
import static mindustry.Vars.player;

public class UnitProjectBlock extends Block {
    private static int num = 0;
    public Effect applyEffect = Fx.unitDespawn;

    public UnitProjectBlock(String name) {
        super(name);
        update = true;
        configurable = true;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return num == 0;
    }

    public class UnitProjectBuild extends Building {

        @Override
        public void updateTile() {
            if (projects == null) {
                ProjectsLocated.create();
            }
            if (player.unit() != null && player.unit().spawnedByCore) {
                if (!(player.unit().mounts[player.unit().mounts.length - 1] == sign)) {
                    player.unit().apply(FStatusEffects.StrongStop, 180);
                    projects.upper.get(player.unit());
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
                    applyEffect.at(player.unit().x, player.unit().y, 0, player.unit());
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
