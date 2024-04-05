package Floor.FContent;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.type.StatusEffect;

public class FStatusEffects {
    public final static Seq<StatusEffect> burnings = new Seq<>();
    public static StatusEffect StrongStop, boostSpeed, HardHit,
            suppressI, suppressII, suppressIII, suppressIV,
            slowII, fastII,
            High_tension, High_tensionII, High_tensionIII, High_tensionIV, High_tensionV,
            burningII, burningIII, burningIV, burningV,
            breakHel, breakHelII, breakHelIII, breakHelIV, breakHelV;

    public static void load() {
        StrongStop = new StatusEffect("strong_stop") {{
            speedMultiplier = 0;
            buildSpeedMultiplier = 0;
            disarm = true;
        }};
        boostSpeed = new StatusEffect("boost_speed") {{
            speedMultiplier = 15;
            show = false;
            permanent = false;
        }};
        HardHit = new StatusEffect("hard_hit") {{
            damageMultiplier = 0.8F;
            speedMultiplier = 0.75F;
            healthMultiplier = 0.4F;
            reloadMultiplier = 0.5F;
            color = new Color(145, 75, 0, 255);
            effectChance = 0.3f;
        }};

        suppressI = new StatusEffect("suppress_I") {{
            speedMultiplier = 0.9F;
            reloadMultiplier = 0.9F;
            color = new Color(170, 170, 153, 255);
            effectChance = 1;
        }};
        suppressII = new StatusEffect("suppress_II") {{
            speedMultiplier = 0.8F;
            reloadMultiplier = 0.8F;
            color = new Color(170, 170, 153, 255);
            effectChance = 1;
        }};
        suppressIII = new StatusEffect("suppress_III") {{
            speedMultiplier = 0.7F;
            reloadMultiplier = 0.7F;
            color = new Color(170, 170, 153, 255);
            effectChance = 1;
        }};
        suppressIV = new StatusEffect("suppress_IV") {{
            speedMultiplier = 0.6F;
            reloadMultiplier = 0.6F;
            color = new Color(170, 170, 153, 255);
            effectChance = 1;
        }};

        slowII = new StatusEffect("slow_II") {{
            speedMultiplier = 0.55F;
            color = new Color(0, 0, 0, 0);
            effectChance = 1;
            effect = Fx.none;
        }};
        fastII = new StatusEffect("fast_II") {{
            speedMultiplier = 2.4f;
            init(() -> {
                opposite(StatusEffects.slow);
                opposite(slowII);
            });
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
        High_tensionIII = new StatusEffect("High_tensionIII") {{
            speedMultiplier = 0.45f;
            reloadMultiplier = 0.45f;
            healthMultiplier = 0.8f;
            damage = 20;
        }};
        High_tensionIV = new StatusEffect("High_tensionIV") {{
            speedMultiplier = 0.24f;
            reloadMultiplier = 0.24f;
            healthMultiplier = 0.75f;
            damage = 34;
        }};
        High_tensionV = new StatusEffect("High_tensionV") {{
            speedMultiplier = 0.1f;
            reloadMultiplier = 0.1f;
            healthMultiplier = 0.7f;
            damage = 54;
        }};

        StatusEffects.burning.transitionDamage = 54;
        StatusEffects.burning.damage = 0.7f;
        burningII = new StatusEffect("burningII") {{
            damage = 1.2f;
            transitionDamage = 130;
            effect = Fx.burning;
        }};
        burningIII = new StatusEffect("burningIII") {{
            damage = 3.4f;
            transitionDamage = 340;
            effect = Fx.burning;
        }};
        burningIV = new StatusEffect("burningIV") {{
            damage = 5.6f;
            transitionDamage = 480;
            effect = Fx.burning;

            init(() -> {
                opposite(StatusEffects.wet, StatusEffects.freezing);
                affinity(StatusEffects.tarred, (unit, result, time) -> {
                    unit.damagePierce(transitionDamage);
                    Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                    result.set(burningIV, Math.min(time + result.time, 300f));
                });
            });
        }};
        burningV = new StatusEffect("burningV") {{
            damage = 8f;
            transitionDamage = 650;
            effect = Fx.burning;
        }};
        breakHel = new StatusEffect("break_hel") {{
            healthMultiplier = 0.9f;
            transitionDamage = 12;
        }};
        breakHelII = new StatusEffect("break_helII") {{
            healthMultiplier = 0.8f;
            transitionDamage = 24;
        }};
        breakHelIII = new StatusEffect("break_helIII") {{
            healthMultiplier = 0.65f;
            transitionDamage = 48;
        }};
        breakHelIV = new StatusEffect("break_helIV") {{
            healthMultiplier = 0.5f;
            transitionDamage = 96;
        }};
        breakHelV = new StatusEffect("break_helV") {{
            healthMultiplier = 0.3f;
            transitionDamage = 192;
        }};

        burnings.addAll(StatusEffects.burning, burningII, burningIII, burningIV, burningV);
    }
}