package Floor.FType.FRender;

import Floor.FType.FShaders.FireBallShader;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import mindustry.core.Renderer;
import mindustry.game.EventType;

import static arc.Core.graphics;

public class FireBallRenderer extends Renderer {
    public static boolean inited = false;
    public static FireBallShader shader;
    public final static Seq<FirePlace> places = new Seq<>();
    public static FrameBuffer buffer = new FrameBuffer();

    public static void enable() {
        inited = true;

        Events.run(EventType.Trigger.draw, FireBallRenderer::FireDraw);
    }

    public static void FireDraw() {
        Draw.draw(-1, () -> {
            buffer.resize(graphics.getWidth(), graphics.getHeight());
            buffer.begin();
        });

        Draw.draw(-1, () -> {
            buffer.end();

            float[] fires = new float[places.size * 4];
            float[] color = new float[places.size * 4];
            float[] rotations = new float[places.size];

            for (int i = 0; i < places.size; i++) {
                FirePlace place = places.get(i);
                fires[i * 4] = place.x;
                fires[i * 4 + 1] = place.y;
                fires[i * 4 + 2] = place.range;
                fires[i * 4 + 3] = place.backTrail;

                color[i * 4] = place.color.r;
                color[i * 4 + 1] = place.color.g;
                color[i * 4 + 2] = place.color.b;
                color[i * 4 + 3] = place.color.a;

                rotations[i] = place.rotation;
            }

            if (shader != null) {
                shader.dispose();
            }

            Shader.prependFragmentCode = "#define MAX_NUM " + FireBallRenderer.places.size + "\n";
            shader = new FireBallShader();
            Shader.prependFragmentCode = "";

            shader.fires = fires;
            shader.color = color;
            shader.rotations = rotations;
            buffer.blit(shader);
            places.clear();
        });
    }

    public static void addPlace(float x, float y) {
        addPlace(x, y, 4, 0, 0, Color.white);
    }

    public static void addPPlace(float x, float y, float rotation) {
        addPlace(x, y, 4, rotation, 0, Color.white);
    }

    public static void addPlace(float x, float y, float range, float backTrail) {
        addPlace(x, y, range, 0, backTrail, Color.white);
    }

    public static void addPlace(float x, float y, float range, float rotation, float backTrail, Color color) {
        if (!inited) enable();
        places.add(new FirePlace(x, y, range, rotation, backTrail, color));
    }

    public static class FirePlace {
        float x, y;
        float range;
        float rotation;
        float backTrail;
        Color color;

        public FirePlace(float x, float y, float range, float rotation, float backTrail, Color color) {
            this.x = x;
            this.y = y;
            this.range = range;
            this.rotation = rotation;
            this.backTrail = backTrail;
            this.color = color;
        }
    }
}
