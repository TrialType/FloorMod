package Floor.FEntities.FEffect;

import Floor.FEntities.FEffectState.WaterEffectState;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;

public class WaterWave extends WaveEffect {
    public static boolean back = false;
    protected void add(float x, float y, float rotation, Color color, Object data) {
        var entity = WaterEffectState.create();
        entity.effect = this;
        entity.rotation = baseRotation + rotation;
        entity.data = data;
        entity.lifetime = lifetime;
        entity.set(x, y);
        entity.color.set(color);
        if (followParent && data instanceof Posc p) {
            entity.parent = p;
            entity.rotWithParent = rotWithParent;
        }
        entity.add();
    }
    @Override
    public void render(EffectContainer e){
        float fin = e.fin();
        float iFin = e.fin(interp);
        float ox = e.x + Angles.trnsx(e.rotation, offsetX, offsetY), oy = e.y + Angles.trnsy(e.rotation, offsetX, offsetY);

        if(!back){
            Draw.color(colorFrom, colorTo, iFin);
            Lines.stroke(interp.apply(strokeFrom, strokeTo, fin));

            float rad = interp.apply(sizeFrom, sizeTo, fin);
            Lines.poly(ox, oy, sides <= 0 ? Lines.circleVertices(rad) : sides, rad, rotation + e.rotation);

            Drawf.light(ox, oy, rad * lightScl, lightColor == null ? Draw.getColor() : lightColor, lightOpacity * e.fin(lightInterp));
        } else {
            Draw.color(colorTo, colorFrom, iFin);
            Lines.stroke(interp.apply(strokeTo, strokeFrom, fin));

            float rad = interp.apply(sizeTo, sizeFrom, fin);
            Lines.poly(ox, oy, sides <= 0 ? Lines.circleVertices(rad) : sides, rad, rotation + e.rotation);

            Drawf.light(ox, oy, rad * lightScl, lightColor == null ? Draw.getColor() : lightColor, lightOpacity * e.fin(lightInterp));
        }
    }
}
