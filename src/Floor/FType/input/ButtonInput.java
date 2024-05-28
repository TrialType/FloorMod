package Floor.FType.input;

import arc.math.geom.Vec2;
import mindustry.input.InputHandler;

public class ButtonInput extends InputHandler {
    @Override
    public void update() {
    }

    @Override
    public void panCamera(Vec2 po) {
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return true;
    }
}
