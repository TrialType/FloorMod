package Floor.FContent;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.Liquid;

public class FLiquids {
    public static Liquid fusionCopper, fusionLead, fusionTitanium, fusionThorium;

    public static void load() {
        fusionCopper = new Liquid("fusion-copper", Color.valueOf("d99d73")) {{
            temperature = 1.5f;
            viscosity = 0.7f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("ca4d33").a(0.4f);
        }};
        fusionLead = new Liquid("fusion-lead", Color.valueOf("8c7fa9")) {{
            temperature = 1.5f;
            viscosity = 0.7f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("7d4f79").a(0.4f);
        }};
        fusionTitanium = new Liquid("fusion-titanium", Color.valueOf("8da1e3")) {{
            temperature = 1.5f;
            viscosity = 0.7f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("9e51b3").a(0.4f);
        }};
        fusionThorium = new Liquid("fusion-thorium", Color.valueOf("f9a3c7")) {{
            temperature = 1.5f;
            viscosity = 0.7f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("f05397").a(0.4f);
        }};
    }
}
