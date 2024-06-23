package Floor.FEntities.FBlock;

import Floor.FContent.FStatusEffects;
import Floor.FType.FDialog.ProjectsLocated;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
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

public class UnitProjectBlock extends Block {
    private static int num = 0;
    public Effect applyEffect = Fx.none;

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
            if (Vars.player.unit() != null && Vars.player.unit().spawnedByCore) {
                if (!(Vars.player.unit().mounts[Vars.player.unit().mounts.length - 1] == sign)) {
                    Vars.player.unit().apply(FStatusEffects.StrongStop, 180);
                    projects.upper.get(Vars.player.unit());
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
                projects.set(Vars.player.unit());
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
                if (Vars.player.unit() != null && Vars.player.unit().spawnedByCore) {
                    projects.upper.get(Vars.player.unit());
                    applyEffect.at(Vars.player.unit());
                    Vars.player.unit().unapply(eff);
                    WeaponMount[] mount = new WeaponMount[Vars.player.unit().type.weapons.size];
                    System.arraycopy(Vars.player.unit().mounts, 0, mount, 0, mount.length);
                    Vars.player.unit().mounts = mount;
                    Ability[] ability = new Ability[Vars.player.unit().type.abilities.size];
                    System.arraycopy(Vars.player.unit().abilities, 0, ability, 0, ability.length);
                    Vars.player.unit().abilities = ability;
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            if (Vars.player.unit() != null) {
                projects.set(Vars.player.unit());
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
