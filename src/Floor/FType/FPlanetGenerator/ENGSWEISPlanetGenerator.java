package Floor.FType.FPlanetGenerator;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import arc.util.noise.Noise;
import mindustry.graphics.g3d.PlanetGrid;
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
        float poles = Math.abs(tile.v.z);
        float noise = Noise.snoise3(tile.v.x, tile.v.y, tile.v.z, 0.001f, 0.58f);

        if (noise + poles / 7.1 > 0.12 && poles > 0.1 && tile.v.z > 0) {
            any = true;
        }

        if (noise < 0.16) {
            for (PlanetGrid.Ptile other : tile.tiles) {
                Sector sec = sector.planet.getSector(other);

                if (sec.id == sector.planet.startSector || (sec.generateEnemyBase && poles < 0.85) || sector.preset != null) {
                    return;
                }
            }
        }

        sector.generateEnemyBase = any;
    }

    @Override
    public void generate() {
        if(sector.threat <= 0.7){
            sector.threat = 0.8f;
        }
        super.generate();
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
