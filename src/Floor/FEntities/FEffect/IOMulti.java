package Floor.FEntities.FEffect;

import arc.graphics.Color;
import mindustry.entities.effect.MultiEffect;

public class IOMulti extends MultiEffect {
    public IOEffect[] effects = new IOEffect[0];
    @Override
    public void create(float x, float y, float rotation, Color color, Object data){
        if(!shouldCreate()) return;

        for(IOEffect effect : effects){
            effect.create(x, y, rotation, color, data);
        }
    }
}
