package Floor.FContent;

import Floor.FType.FStatusEffect.ADH;
import arc.graphics.Color;
import mindustry.entities.effect.ParticleEffect;
import mindustry.type.StatusEffect;

//import java.util.Random;

public class FStatusEffects {
    public static StatusEffect
            ASpeed, ASpeed1, ASpeed2, ASpeed3, ASpeed4, ASpeed5, ASpeed6, ASpeed7, ASpeed8, ASpeed9,
            ADamage, ADamage1, ADamage2, ADamage3, ADamage4, ADamage5, ADamage6, ADamage7, ADamage8, ADamage9,
            AReload, AReload1, AReload2, AReload3, AReload4, AReload5, AReload6, AReload7, AReload8, AReload9,
            AHealth, AHealth1, AHealth2, AHealth3, AHealth4, AHealth5, AHealth6, AHealth7, AHealth8, AHealth9,
            AAgain, AAgain1, AAgain2, AAgain3, AAgain4, AAgain5, AAgain6, AAgain7, AAgain8, AAgain9;
    public static StatusEffect[] Speed, Damage, Reload, Health, Again;
    public static StatusEffect StrongStop , boostSpeed;

    //private static final Random r = new Random();
    public static void load() {
        ASpeed = new StatusEffect("ASpeed");
        ASpeed1 = new StatusEffect("ASpeed1");
        ASpeed2 = new StatusEffect("ASpeed2");
        ASpeed3 = new StatusEffect("ASpeed3");
        ASpeed4 = new StatusEffect("ASpeed4");
        ASpeed5 = new StatusEffect("ASpeed5");
        ASpeed6 = new StatusEffect("ASpeed6");
        ASpeed7 = new StatusEffect("ASpeed7");
        ASpeed8 = new StatusEffect("ASpeed8");
        ASpeed9 = new StatusEffect("ASpeed9");
        ADamage = new StatusEffect("ADamage");
        ADamage1 = new StatusEffect("ADamage1");
        ADamage2 = new StatusEffect("ADamage2");
        ADamage3 = new StatusEffect("ADamage3");
        ADamage4 = new StatusEffect("ADamage4");
        ADamage5 = new StatusEffect("ADamage5");
        ADamage6 = new StatusEffect("ADamage6");
        ADamage7 = new StatusEffect("ADamage7");
        ADamage8 = new StatusEffect("ADamage8");
        ADamage9 = new StatusEffect("ADamage9");
        AReload = new StatusEffect("AReload");
        AReload1 = new StatusEffect("AReload1");
        AReload2 = new StatusEffect("AReload2");
        AReload3 = new StatusEffect("AReload3");
        AReload4 = new StatusEffect("AReload4");
        AReload5 = new StatusEffect("AReload5");
        AReload6 = new StatusEffect("AReload6");
        AReload7 = new StatusEffect("AReload7");
        AReload8 = new StatusEffect("AReload8");
        AReload9 = new StatusEffect("AReload9");
        AHealth = new ADH("AHealth");
        AHealth1 = new ADH("AHealth1");
        AHealth2 = new ADH("AHealth2");
        AHealth3 = new ADH("AHealth3");
        AHealth4 = new ADH("AHealth4");
        AHealth5 = new ADH("AHealth5");
        AHealth6 = new ADH("AHealth6");
        AHealth7 = new ADH("AHealth7");
        AHealth8 = new ADH("AHealth8");
        AHealth9 = new ADH("AHealth9");
        AAgain = new StatusEffect("AAgain");
        AAgain1 = new StatusEffect("AAgain1");
        AAgain2 = new StatusEffect("AAgain2");
        AAgain3 = new StatusEffect("AAgain3");
        AAgain4 = new StatusEffect("AAgain4");
        AAgain5 = new StatusEffect("AAgain5");
        AAgain6 = new StatusEffect("AAgain6");
        AAgain7 = new StatusEffect("AAgain7");
        AAgain8 = new StatusEffect("AAgain8");
        AAgain9 = new StatusEffect("AAgain9");
        Speed = new StatusEffect[]{ASpeed, ASpeed1, ASpeed2, ASpeed3, ASpeed4, ASpeed5, ASpeed6, ASpeed7, ASpeed8, ASpeed9};
        Damage = new StatusEffect[]{ADamage, ADamage1, ADamage2, ADamage3, ADamage4, ADamage5, ADamage6, ADamage7, ADamage8, ADamage9};
        Reload = new StatusEffect[]{AReload, AReload1, AReload2, AReload3, AReload4, AReload5, AReload6, AReload7, AReload8, AReload9};
        Health = new StatusEffect[]{AHealth, AHealth1, AHealth2, AHealth3, AHealth4, AHealth5, AHealth6, AHealth7, AHealth8, AHealth9};
        Again = new StatusEffect[]{AAgain, AAgain1, AAgain2, AAgain3, AAgain4, AAgain5, AAgain6, AAgain7, AAgain8, AAgain9};
        for (int i = 0; i < 10; i++) {
            Speed[i].speedMultiplier = (/*r.nextInt(101) / 100.0F +*/ 1.2F + i * 0.2F);
            Speed[i].effectChance = 0.1F;
            Speed[i].permanent = true;
            Speed[i].effect = new ParticleEffect() {
                {
                    lifetime = 2;
                    colorFrom = colorTo = Color.blue;
                    particles = 1;
                }
            };

            Damage[i].damageMultiplier = (1.2F + i * 0.2F);
            Damage[i].effectChance = 0.1F;
            Damage[i].outline = true;
            Damage[i].permanent = true;
            Damage[i].show = true;
            Damage[i].effect = new ParticleEffect() {{
                lifetime = 2;
                colorFrom = colorTo = Color.red;
                particles = 1;
            }};

            Reload[i].reloadMultiplier = (1.2F + i * 0.2F);
            Reload[i].effectChance = 0.1F;
            Reload[i].outline = true;
            Reload[i].permanent = true;
            Reload[i].show = true;
            Reload[i].effect = new ParticleEffect() {{
                lifetime = 2;
                colorFrom = colorTo = Color.brown;
                particles = 1;
            }};

            Health[i].damage = -(0.01F + i * 0.01F);
            Health[i].effectChance = 0.1F;
            Health[i].outline = true;
            Health[i].permanent = true;
            Health[i].show = true;
            Health[i].effect = new ParticleEffect() {{
                lifetime = 2;
                colorFrom = colorTo = Color.green;
                particles = 1;
            }};

            Again[i].effectChance = 0.1F;
            Again[i].outline = true;
            Again[i].permanent = true;
            Again[i].show = true;
            Again[i].effect = new ParticleEffect() {{
                lifetime = 2;
                colorFrom = colorTo = Color.yellow;
                particles = 1;
            }};
        }
        StrongStop = new StatusEffect("StrongStop") {{
            speedMultiplier = 0;
            disarm = true;
        }};
        boostSpeed = new StatusEffect("boostSpeed") {{
            speedMultiplier = 9;
            show = false;
            permanent = false;
        }};
    }
}