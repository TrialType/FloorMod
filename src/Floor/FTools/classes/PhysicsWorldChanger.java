package Floor.FTools.classes;

import arc.math.Mathf;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.async.PhysicsProcess;

public class PhysicsWorldChanger extends PhysicsProcess.PhysicsWorld {
    public PhysicsWorldChanger(Rect bounds) {
        super(bounds);
        for (int i = 0; i < 5; i++) {
            trees[i] = new QuadTree<>(new Rect(bounds));
        }
    }
    @SuppressWarnings("unchecked")
    public final QuadTree<PhysicsBody>[] trees = new QuadTree[5];
    private static final float scl = 1.25f;
    public Seq<PhysicsBody> bodies;
    public final Seq<PhysicsBody> seq = new Seq<>(PhysicsBody.class);
    private final Rect rect = new Rect();
    private final Vec2 vec = new Vec2();

    public void add(PhysicsBody body) {
        bodies.add(body);
    }

    public void remove(PhysicsBody body) {
        bodies.remove(body);
    }

    public void update() {
        for (int i = 0; i < 5; i++) {
            trees[i].clear();
        }

        var bodyItems = bodies.items;
        int bodySize = bodies.size;

        for (int i = 0; i < bodySize; i++) {
            PhysicsBody body = bodyItems[i];
            body.collided = false;
            trees[body.layer].insert(body);
        }

        for (int i = 0; i < bodySize; i++) {
            PhysicsBody body = bodyItems[i];

            if (!body.local) continue;

            body.hitbox(rect);

            seq.size = 0;
            trees[body.layer].intersect(rect, seq);
            int size = seq.size;
            var items = seq.items;

            for (int j = 0; j < size; j++) {
                PhysicsBody other = items[j];

                if (other == body || other.collided) continue;

                float rs = body.radius + other.radius;
                float dst = Mathf.dst(body.x, body.y, other.x, other.y);

                if (dst < rs) {
                    vec.set(body.x - other.x, body.y - other.y).setLength(rs - dst);
                    float ms = body.mass + other.mass;
                    float m1 = other.mass / ms, m2 = body.mass / ms;

                    body.x += vec.x * m1 / scl;
                    body.y += vec.y * m1 / scl;

                    if (other.local) {
                        other.x -= vec.x * m2 / scl;
                        other.y -= vec.y * m2 / scl;
                    }
                }
            }
            body.collided = true;
        }
    }

}
