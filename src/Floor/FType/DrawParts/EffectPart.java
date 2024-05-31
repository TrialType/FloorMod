package Floor.FType.DrawParts;

import mindustry.entities.Effect;
import mindustry.entities.part.DrawPart;

public class EffectPart extends DrawPart {
    public Effect effect;
    public float x = 0, y = 0, rotation = 0;

    @Override
    public void draw(PartParams params) {
        if (effect != null) {
            float angle = params.rotation + rotation - 90;
            effect.at((float) (params.x + x * Math.cos(Math.toRadians(angle + 90)) + y * Math.cos(Math.toRadians(angle))),
                    (float) (params.y + y * Math.sin(Math.toRadians(angle)) + x * Math.sin(Math.toRadians(angle + 90))),
                    angle);
        }
    }

    @Override
    public void load(String name) {

    }
}
