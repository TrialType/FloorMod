package Floor.FType.FPlanetGenerator;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import arc.util.noise.Noise;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Sector;

public class ENGSWEISPlanetGenerator extends SerpuloPlanetGenerator {
    @Override
    public void generateSector(Sector sector) {
        if (sector.id == sector.planet.startSector) {
            sector.generateEnemyBase = false;
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
                Sector sec = sector.planet.getSector(other);

                if (sec.id == sector.planet.startSector ||
                        sec.generateEnemyBase && poles < 0.85 ||
                        (sector.preset != null && noise < 0.11)) {
                    return;
                }
            }
        }

        sector.generateEnemyBase = any;
    }

    @Override
    public void generate() {
        super.generate();
        if (sector.preset.difficulty <= 5) {
            sector.preset.difficulty += 5;
        }
    }

    @Override
    public float getHeight(Vec3 position) {
        return super.getHeight(position);
    }

    @Override
    public Color getColor(Vec3 position) {
        return super.getColor(position);
    }
}
