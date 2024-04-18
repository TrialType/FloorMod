package Floor.FType.FPlanetGenerator;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import arc.util.noise.Noise;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;

public class ENGSWEISPlanetGenerator extends PlanetGenerator {
    @Override
    public void generateSector(Sector sector) {
        if (sector.id == 10) {
            sector.generateEnemyBase = false;
            sector.preset.difficulty = 7;
            return;
        }

        PlanetGrid.Ptile tile = sector.tile;

        boolean any = false;
        float poles = Math.abs(tile.v.y);
        float noise = Noise.snoise3(tile.v.x, tile.v.y, tile.v.z, 0.001f, 0.58f);

        if (noise + poles / 7.1 > 0.12 && poles > 0.23) {
            any = true;
        }

        if (noise < 0.16) {
            for (PlanetGrid.Ptile other : tile.tiles) {
                Sector osec = sector.planet.getSector(other);

                if (osec.id == sector.planet.startSector ||
                        osec.generateEnemyBase && poles < 0.85 ||
                        (sector.preset != null && noise < 0.11)) {
                    return;
                }
            }
        }

        sector.generateEnemyBase = any;
    }

    @Override
    public void generate() {

    }

    @Override
    public float getHeight(Vec3 position) {
        return 10;
    }

    @Override
    public Color getColor(Vec3 position) {
        return Pal.accent;
    }
}
