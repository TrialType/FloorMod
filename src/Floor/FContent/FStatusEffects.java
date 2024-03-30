package Floor.FContent;

import arc.graphics.Color;
import arc.math.Interp;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.type.StatusEffect;

public class FStatusEffects {
    public final static Seq<StatusEffect> burnings = new Seq<>();
    public static StatusEffect StrongStop, boostSpeed, HardHit,
            suppressII,
            slowII, fastII,
            High_tension, High_tensionII, High_tensionIII, High_tensionIV, High_tensionV,
            burningII, burningIII, burningIV, burningV;

    public static void load() {
        StrongStop = new StatusEffect("StrongStop") {{
            speedMultiplier = 0;
            buildSpeedMultiplier = 0;
            disarm = true;
        }};
        boostSpeed = new StatusEffect("boostSpeed") {{
            speedMultiplier = 15;
            show = false;
            permanent = false;
        }};
        HardHit = new StatusEffect("hard_hit") {{
            damageMultiplier = 1.2F;
            speedMultiplier = 0.75F;
            healthMultiplier = 0.4F;
            reloadMultiplier = 0.5F;
            color = new Color(145, 75, 0, 255);
            effectChance = 1;
            effect = new MultiEffect(new ParticleEffect() {{
                baseLength = 0;
                length = 25;
                lifetime = 10;
                sizeFrom = 2;
                sizeTo = 0;
                colorFrom = color;
                colorTo = color;
            }}, new ParticleEffect() {{
                particles = 2;
                line = true;
                interp = Interp.slowFast;
                strokeFrom = 2;
                strokeTo = 0;
                lenFrom = 10;
                lenTo = 0;
                length = 23;
                baseLength = 0;
                lifetime = 10;
                colorFrom = color;
                colorTo = color;
            }});
        }};
        suppressII = new StatusEffect("suppressII") {{
            speedMultiplier = 0.8F;
            reloadMultiplier = 0.55F;
            color = new Color(170, 170, 153, 255);
            effectChance = 1;
            effect = new MultiEffect(new ParticleEffect() {{
                baseLength = 0;
                length = 25;
                lifetime = 10;
                sizeFrom = 2;
                sizeTo = 0;
                colorFrom = color;
                colorTo = color;
            }}, new ParticleEffect() {{
                particles = 2;
                line = true;
                interp = Interp.slowFast;
                strokeFrom = 2;
                strokeTo = 0;
                lenFrom = 10;
                lenTo = 0;
                length = 23;
                baseLength = 0;
                lifetime = 10;
                colorFrom = new Color(170, 170, 0, 255);
                colorTo = color;
            }});
        }};
        slowII = new StatusEffect("slowII") {{
            speedMultiplier = 0.55F;
            color = new Color(0, 0, 0, 0);
            effectChance = 1;
            effect = Fx.none;
        }};
        fastII = new StatusEffect("fastII") {{
            speedMultiplier = 2.4f;
            init(() -> opposite(StatusEffects.slow));
        }};
        High_tension = new StatusEffect("high_tension") {{
            speedMultiplier = 0.85f;
            reloadMultiplier = 0.85f;
            healthMultiplier = 0.95f;
            damage = 6;
        }};
        High_tensionII = new StatusEffect("high_tensionII") {{
            speedMultiplier = 0.67f;
            reloadMultiplier = 0.67f;
            healthMultiplier = 0.85f;
            damage = 13;
        }};
        High_tensionIII = new StatusEffect("high_tensionII") {{
            speedMultiplier = 0.45f;
            reloadMultiplier = 0.45f;
            healthMultiplier = 0.8f;
            damage = 20;
        }};
        High_tensionIV = new StatusEffect("high_tensionII") {{
            speedMultiplier = 0.24f;
            reloadMultiplier = 0.24f;
            healthMultiplier = 0.75f;
            damage = 34;
        }};
        High_tensionV = new StatusEffect("high_tensionIII") {{
            speedMultiplier = 0.1f;
            reloadMultiplier = 0.1f;
            healthMultiplier = 0.7f;
            damage = 90;
        }};
        burningII = new StatusEffect("burningI") {{
            damage = 0.7f;
            transitionDamage = 12;
            effect = Fx.burning;
        }};
        burningIII = new StatusEffect("burningI") {{
            damage = 1.2f;
            transitionDamage = 18;
            effect = Fx.burning;
        }};
        burningIV = new StatusEffect("burningI") {{
            damage = 2f;
            transitionDamage = 25;
            effect = Fx.burning;
        }};
        burningV = new StatusEffect("burningII") {{
            damage = 3.5f;
            transitionDamage = 34;
            effect = Fx.burning;
        }};

        burnings.addAll(StatusEffects.burning, burningII, burningIII, burningIV, burningV);
    }
}