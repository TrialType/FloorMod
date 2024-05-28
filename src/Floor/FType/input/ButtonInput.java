package Floor.FType.input;

import arc.Core;
import arc.input.GestureDetector;
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
    public void add(){
        super.add();
        Core.input.getInputProcessors().remove(i -> i instanceof InputHandler || (i instanceof GestureDetector && ((GestureDetector)i).getListener() instanceof InputHandler));
        Core.input.addProcessor(detector = new GestureDetector(20, 0.5f, 0.3f, 0.15f, this));
        Core.input.addProcessor(this);
    }
}
