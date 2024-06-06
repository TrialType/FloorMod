package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Strings;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.util.HashMap;

import static Floor.FContent.FItems.*;
import static Floor.FContent.FItems.allTargetInterval;
import static Floor.FType.FDialog.ProjectsLocated.projects;
import static mindustry.Vars.ui;

abstract class ProjectUtils {
    public static Table colorList;
    public static float maxSize = 0;
    public static float freeSize = 0;
    public static final HashMap<String, heavyGetter> heavies = new HashMap<>();
    public static final HashMap<String, levelGetter> levels = new HashMap<>();
    public static final HashMap<String, Integer> maxLevel = new HashMap<>();

    public static void init() {
        heavies.put("none", i -> 0);

        levels.put("none", f -> 0);

        //bullet
        heavies.put("bulletBase", i -> i * 1f);
        heavies.put("splash", i -> i * 1.5f);
        heavies.put("lightning", i -> i * 1.2f);
        heavies.put("percent", i -> i * 1.2f);
        heavies.put("frags", i -> i * 1.8f);
        heavies.put("emp", i -> i);
        heavies.put("knock", i -> i * 2);

        levels.put("bulletBase", f -> f <= 0 ? 0 : f <= 36 ? 1 : f <= 72 ? 2 : f <= 144 ? 3 : f <= 288 ? 4 : f <= 576 ? 5 : 6);
        levels.put("splash", f -> f <= 0 ? 0 : f <= 3 ? 1 : f <= 4.2 ? 2 : f <= 7 ? 3 : f <= 12 ? 4 : f <= 19 ? 5 : 6);
        levels.put("lightning", f -> f <= 0 ? 0 : f <= 2 ? 1 : f <= 4 ? 2 : f <= 8 ? 3 : f <= 12 ? 4 : f <= 20 ? 5 : 6);
        levels.put("percent", f -> f <= 0 ? 0 : f <= 0.1 ? 1 : f <= 0.15 ? 2 : f <= 0.3 ? 3 : f <= 0.45 ? 4 : f <= 0.65 ? 5 : 6);
        levels.put("frags", f -> f <= 0 ? 0 : f <= 1 ? 1 : f <= 2 ? 2 : f <= 3 ? 3 : f <= 4 ? 4 : f <= 5 ? 5 : 6);
        levels.put("emp", f -> f <= 0 ? 0 : f <= 60 ? 1 : f <= 90 ? 2 : f <= 130 ? 3 : f <= 180 ? 4 : f <= 260 ? 5 : 6);
        levels.put("knock", f -> f <= 0 ? 0 : f <= 0.1 ? 1 : f <= 0.15 ? 2 : f <= 0.2 ? 3 : f <= 0.25 ? 4 : f <= 0.3 ? 5 : 6);

        //weapon
        heavies.put("number", i -> i * 1.8f);
        heavies.put("reload", i -> i * 1.5f);
        heavies.put("target", i -> i * 1.5f);

        levels.put("number", f -> f <= 1 ? 0 : f <= 2 ? 1 : f <= 3 ? 2 : f <= 4 ? 3 : f <= 5 ? 4 : f <= 6 ? 5 : 6);
        levels.put("reload", f -> f >= 150 ? 0 : f >= 120 ? 1 : f >= 90 ? 2 : f >= 60 ? 3 : f >= 30 ? 4 : f >= 15 ? 5 : 6);
        levels.put("target", f -> f >= 60 ? 0 : f >= 50 ? 1 : f >= 40 ? 2 : f >= 30 ? 3 : f >= 20 ? 4 : f >= 10 ? 5 : 6);

        updateMaxLevel();
        updateHeavy();
    }

    public static float getHeavy(String type, float val) {
        if (heavies.get(type) == null) {
            return 0;
        }
        return heavies.get(type).get(levels.get(type).get(val));
    }

    public static boolean couldUse(String type, float val) {
        if (maxLevel.isEmpty()) {
            updateMaxLevel();
        }
        return levels.get(type).get(val) <= maxLevel.computeIfAbsent(type, name -> 0);
    }

    public static void updateHeavy() {
        //test
        maxSize = 2;
        for (int i = allSize.length; i > 0; i--) {
            if (allSize[i - 1].unlocked()) {
                maxSize = 2 + (i == 1 ? 1 : i == 2 ? 2 : i == 3 ? 3 : i == 4 ? 4 : i == 5 ? 5 : i == 6 ? 6 : i == 7 ? 7 :
                        i == 8 ? 8 : i == 9 ? 9 : 10);
                break;
            }
        }
        freeSize = maxSize;
        for (ProjectsLocated.weaponPack wp : projects.weapons) {
            freeSize -= wp.heavy;
        }
        for (ProjectsLocated.abilityPack ap : projects.abilities) {
            freeSize -= ap.heavy;
        }
    }

    public static void updateMaxLevel() {
        //bulletBase
        maxLevel.put("bulletBase", 0);
        for (int i = allBulletBase.length; i > 0; i--) {
            if (allBulletBase[i - 1].unlocked()) {
                maxLevel.put("bulletBase", i);
                break;
            }
        }

        //splash
        maxLevel.put("splash", 0);
        for (int i = allSplash.length; i > 0; i--) {
            if (allSplash[i - 1].unlocked()) {
                maxLevel.put("splash", i);
                break;
            }
        }

        //knock
        maxLevel.put("knock", 0);
        for (int i = allKnock.length; i > 0; i--) {
            if (allKnock[i - 1].unlocked()) {
                maxLevel.put("knock", i);
                break;
            }
        }

        //percent
        maxLevel.put("percent", 0);
        for (int i = allPercent.length; i > 0; i--) {
            if (allPercent[i - 1].unlocked()) {
                maxLevel.put("percent", i);
                break;
            }
        }

        //frags
        maxLevel.put("frags", 0);
        for (int i = allFrag.length; i > 0; i--) {
            if (allFrag[i - 1].unlocked()) {
                maxLevel.put("frags", i);
                break;
            }
        }

        //lightning
        maxLevel.put("lightning", 0);
        for (int i = allLightning.length; i > 0; i--) {
            if (allLightning[i - 1].unlocked()) {
                maxLevel.put("lightning", i);
                break;
            }
        }

        //emp
        maxLevel.put("emp", 0);
        for (int i = allEmp.length; i > 0; i--) {
            if (allEmp[i - 1].unlocked()) {
                maxLevel.put("emp", i);
                break;
            }
        }

        //number
        maxLevel.put("number", 0);
        for (int i = allBulletNumber.length; i > 0; i--) {
            if (allBulletNumber[i - 1].unlocked()) {
                maxLevel.put("number", i);
                break;
            }
        }

        //reload
        maxLevel.put("reload", 0);
        for (int i = allReload.length; i > 0; i--) {
            if (allReload[i - 1].unlocked()) {
                maxLevel.put("reload", i);
                break;
            }
        }

        //target
        maxLevel.put("target", 0);
        for (int i = allTargetInterval.length; i > 0; i--) {
            if (allTargetInterval[i - 1].unlocked()) {
                maxLevel.put("target", i);
                break;
            }
        }
    }

    public static void createNumberDialog(Table on, String dia, String tile, float def, Cons<Float> apply, Runnable rebuild) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ");
            t.label(() -> def + "").pad(3);
            t.button(Icon.pencilSmall, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog." + dia + "." + tile), "", 15, def + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    apply.get(amount);
                    rebuild.run();
                } else {
                    ui.showInfo(Core.bundle.get("@inputError"));
                }
            })).size(55);
        }).pad(10).width(250);
    }

    public static void createBooleanDialog(Table on, String dia, String tile, boolean def, Cons<Boolean> apply, Runnable rebuild) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ").pad(5);
            t.label(() -> Core.bundle.get("@" + def)).pad(5);
            t.button(Icon.rotate, () -> {
                apply.get(!def);
                rebuild.run();
            });
        }).pad(10).width(250);
    }

    public static void createNumberDialogWithLimit(Table on, String dia, String tile, float def, float max, float min, Cons<Float> apply, Runnable rebuild) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ");
            t.label(() -> def + "").pad(3);
            t.button(Icon.pencilSmall, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog." + dia + "." + tile), "", 15, def + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    if (min <= amount && max >= amount) {
                        apply.get(amount);
                        rebuild.run();
                    } else {
                        ui.showInfo(Core.bundle.format("configure.invalid", min, max));
                    }
                } else {
                    ui.showInfo(Core.bundle.get("@inputError"));
                }
            })).size(55);
        }).pad(10).width(250);
    }

    public static void createLevDialog(Table on, String dia, String type, String tile, float def, Cons<Float> apply, Runnable rebuild, Runnable updateHeavy, StrBool levUser, BoolGetter hevUser) {
        on.table(t -> {
            t.setBackground(Tex.underline2);
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ");
            t.label(() -> def + "").pad(3);
            t.button(Icon.pencilSmall, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog." + dia + "." + tile), "", 15, def + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    apply.get(amount);
                    updateHeavy.run();
                    if (!levUser.get(type)) {
                        ui.showInfo(Core.bundle.get("@levelOutOfBounds"));
                        apply.get(def);
                        return;
                    } else if (!hevUser.get()) {
                        ui.showInfo(Core.bundle.get("@tooHeavy"));
                        apply.get(def);
                        return;
                    }
                    rebuild.run();
                } else {
                    ui.showInfo(Core.bundle.get("@inputError"));
                }
            })).size(55);
        }).pad(10).width(250);
    }

    public static void createPartsDialog(Table on, String dia, String tile, Seq<DrawPart> parts) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile)).pad(5);
            t.button(Icon.pencilSmall, () -> {
                PartsDialog pd = new PartsDialog(parts);
                pd.show();
            });
        }).pad(10).width(250);
    }

    public static void createColorDialog(Table on, String dia, String tile, Color def, Cons<Color> apply, Runnable reb) {
        Color color = def == null ? new Color() : Color.valueOf(def.toString());
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile));
            t.button(b -> {
                b.setSize(25);
                b.setColor(color);

                b.clicked(() -> ui.showTextInput(Core.bundle.get("dialog.color.input"),
                        Core.bundle.get("dialog.color.input"), color.toString(), s -> {
                            Color c = Color.valueOf(s);
                            apply.get(c);
                            reb.run();
                        }));
            }, () -> {
            }).pad(5);
        }).width(250);
    }

    public static void createColorDialogList(Table on, String dia, String tile, Color[] def, Cons<Color[]> apply, Runnable reb) {
        Seq<Color> cs = new Seq<>(def);
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile)).pad(5);
            t.button(Icon.pencilSmall, () -> {
                BaseDialog bd = new BaseDialog("");
                bd.cont.pane(ta -> {
                    colorList = ta;
                    rebuildColorList(cs);
                }).grow();
                bd.buttons.button(Icon.left, bd::hide);
                bd.buttons.button(Icon.add, () -> {
                    Color color = new Color();
                    cs.add(color);
                    rebuildColorList(cs);
                });
                bd.buttons.button(Icon.right, () -> {
                    apply.get(cs.items);
                    bd.hide();
                });
                bd.show();
                bd.hidden(reb);
            }).pad(5);
        }).pad(10).width(250);
    }

    public static void createEffectList(Table on, EffectTableGetter data, String dia, String name, Effect list) {
        MultiEffect multi = (MultiEffect) list;
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + name) + "->");
            if (multi != null) {
                t.button(Icon.pencil, () -> {
                    BaseDialog bd = new BaseDialog("");
                    bd.cont.pane(li -> {
                        li.table(ta -> {
                            data.set(ta);
                            rebuildEffectList(data.get(), list);
                        }).grow();
                        li.row();
                    }).width(1400);
                    bd.buttons.button(Icon.left, bd::hide);
                    bd.buttons.button(Icon.add, () -> {
                        Effect effect = new Effect();
                        Effect[] effects = new Effect[multi.effects.length + 1];
                        System.arraycopy(multi.effects, 0, effects, 0, multi.effects.length);
                        effects[effects.length - 1] = effect;
                        multi.effects = effects;
                        rebuildEffectList(data.get(), list);
                    });
                    bd.show();
                }).pad(3).size(25);
            }
        }).pad(10).width(250);
    }

    private static void rebuildEffectList(Table on, Effect list) {
        on.clear();
        MultiEffect effect = (MultiEffect) list;
        if (effect != null) {
            for (int i = 0; i < effect.effects.length; i++) {
                int finalI = i;
                on.table(t -> {
                    t.setBackground(Tex.underline);
                    t.label(() -> finalI + "").width(100);
                    t.button(Icon.pencil, () -> createEffectDialog(effect.effects[finalI],
                            e -> effect.effects[finalI] = e,
                            () -> {
                            })).grow();
                    t.button(b -> {
                        b.image(Icon.pencilSmall);

                        b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                            tb.clear();
                            //239 for all , fuck you
                            tb.button("none", () -> {
                                effect.effects[finalI] = Fx.none;
                                hide.run();
                            }).width(100).row();
                            tb.button("blockCrash", () -> {
                                effect.effects[finalI] = Fx.blockCrash;
                                hide.run();
                            }).width(100).row();
                            tb.button("trailFade", () -> {
                                effect.effects[finalI] = Fx.trailFade;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitSpawn", () -> {
                                effect.effects[finalI] = Fx.unitSpawn;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitCapKill", () -> {
                                effect.effects[finalI] = Fx.unitCapKill;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitEnvKill", () -> {
                                effect.effects[finalI] = Fx.unitEnvKill;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitControl", () -> {
                                effect.effects[finalI] = Fx.unitControl;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitDespawn", () -> {
                                effect.effects[finalI] = Fx.unitDespawn;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitSpirit", () -> {
                                effect.effects[finalI] = Fx.unitSpirit;
                                hide.run();
                            }).width(100).row();
                            tb.button("itemTransfer", () -> {
                                effect.effects[finalI] = Fx.itemTransfer;
                                hide.run();
                            }).width(100).row();
                            tb.button("pointBeam", () -> {
                                effect.effects[finalI] = Fx.pointBeam;
                                hide.run();
                            }).width(100).row();
                            tb.button("pointHit", () -> {
                                effect.effects[finalI] = Fx.pointHit;
                                hide.run();
                            }).width(100).row();
                            tb.button("lightning", () -> {
                                effect.effects[finalI] = Fx.lightning;
                                hide.run();
                            }).width(100).row();
                            tb.button("coreBuildShockwave", () -> {
                                effect.effects[finalI] = Fx.coreBuildShockwave;
                                hide.run();
                            }).width(100).row();
                            tb.button("coreBuildBlock", () -> {
                                effect.effects[finalI] = Fx.coreBuildBlock;
                                hide.run();
                            }).width(100).row();
                            tb.button("pointShockwave", () -> {
                                effect.effects[finalI] = Fx.pointShockwave;
                                hide.run();
                            }).width(100).row();
                            tb.button("moveCommand", () -> {
                                effect.effects[finalI] = Fx.moveCommand;
                                hide.run();
                            }).width(100).row();
                            tb.button("attackCommand", () -> {
                                effect.effects[finalI] = Fx.attackCommand;
                                hide.run();
                            }).width(100).row();
                            tb.button("commandSend", () -> {
                                effect.effects[finalI] = Fx.commandSend;
                                hide.run();
                            }).width(100).row();
                            tb.button("upgradeCore", () -> {
                                effect.effects[finalI] = Fx.upgradeCore;
                                hide.run();
                            }).width(100).row();
                            tb.button("upgradeCoreBloom", () -> {
                                effect.effects[finalI] = Fx.upgradeCoreBloom;
                                hide.run();
                            }).width(100).row();
                            tb.button("placeBlock", () -> {
                                effect.effects[finalI] = Fx.placeBlock;
                                hide.run();
                            }).width(100).row();
                            tb.button("coreLaunchConstruct", () -> {
                                effect.effects[finalI] = Fx.coreLaunchConstruct;
                                hide.run();
                            }).width(100).row();
                            tb.button("tapBlock", () -> {
                                effect.effects[finalI] = Fx.tapBlock;
                                hide.run();
                            }).width(100).row();
                            tb.button("breakBlock", () -> {
                                effect.effects[finalI] = Fx.breakBlock;
                                hide.run();
                            }).width(100).row();
                            tb.button("payloadDeposit", () -> {
                                effect.effects[finalI] = Fx.payloadDeposit;
                                hide.run();
                            }).width(100).row();
                            tb.button("select", () -> {
                                effect.effects[finalI] = Fx.select;
                                hide.run();
                            }).width(100).row();
                            tb.button("smoke", () -> {
                                effect.effects[finalI] = Fx.smoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("fallSmoke", () -> {
                                effect.effects[finalI] = Fx.fallSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitWreck", () -> {
                                effect.effects[finalI] = Fx.unitWreck;
                                hide.run();
                            }).width(100).row();
                            tb.button("rocketSmoke", () -> {
                                effect.effects[finalI] = Fx.rocketSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("rocketSmokeLarge", () -> {
                                effect.effects[finalI] = Fx.rocketSmokeLarge;
                                hide.run();
                            }).width(100).row();
                            tb.button("magmasmoke", () -> {
                                effect.effects[finalI] = Fx.magmasmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("spawn", () -> {
                                effect.effects[finalI] = Fx.spawn;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitAssemble", () -> {
                                effect.effects[finalI] = Fx.unitAssemble;
                                hide.run();
                            }).width(100).row();
                            tb.button("padlaunch", () -> {
                                effect.effects[finalI] = Fx.padlaunch;
                                hide.run();
                            }).width(100).row();
                            tb.button("breakProp", () -> {
                                effect.effects[finalI] = Fx.breakProp;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitDrop", () -> {
                                effect.effects[finalI] = Fx.unitDrop;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitLand", () -> {
                                effect.effects[finalI] = Fx.unitLand;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitDust", () -> {
                                effect.effects[finalI] = Fx.unitDust;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitLandSmall", () -> {
                                effect.effects[finalI] = Fx.unitLandSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitPickup", () -> {
                                effect.effects[finalI] = Fx.unitPickup;
                                hide.run();
                            }).width(100).row();
                            tb.button("crawlDust", () -> {
                                effect.effects[finalI] = Fx.crawlDust;
                                hide.run();
                            }).width(100).row();
                            tb.button("landShock", () -> {
                                effect.effects[finalI] = Fx.landShock;
                                hide.run();
                            }).width(100).row();
                            tb.button("pickup", () -> {
                                effect.effects[finalI] = Fx.pickup;
                                hide.run();
                            }).width(100).row();
                            tb.button("sparkExplosion", () -> {
                                effect.effects[finalI] = Fx.sparkExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("titanExplosion", () -> {
                                effect.effects[finalI] = Fx.titanExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("titanSmoke", () -> {
                                effect.effects[finalI] = Fx.titanSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("missileTrailSmoke", () -> {
                                effect.effects[finalI] = Fx.missileTrailSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("neoplasmSplat", () -> {
                                effect.effects[finalI] = Fx.neoplasmSplat;
                                hide.run();
                            }).width(100).row();
                            tb.button("scatheExplosion", () -> {
                                effect.effects[finalI] = Fx.scatheExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("scatheLight", () -> {
                                effect.effects[finalI] = Fx.scatheLight;
                                hide.run();
                            }).width(100).row();
                            tb.button("scatheSlash", () -> {
                                effect.effects[finalI] = Fx.scatheSlash;
                                hide.run();
                            }).width(100).row();
                            tb.button("dynamicSpikes", () -> {
                                effect.effects[finalI] = Fx.dynamicSpikes;
                                hide.run();
                            }).width(100).row();
                            tb.button("greenBomb", () -> {
                                effect.effects[finalI] = Fx.greenBomb;
                                hide.run();
                            }).width(100).row();
                            tb.button("greenLaserCharge", () -> {
                                effect.effects[finalI] = Fx.greenLaserCharge;
                                hide.run();
                            }).width(100).row();
                            tb.button("greenLaserChargeSmall", () -> {
                                effect.effects[finalI] = Fx.greenLaserChargeSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("greenCloud", () -> {
                                effect.effects[finalI] = Fx.greenCloud;
                                hide.run();
                            }).width(100).row();
                            tb.button("healWaveDynamic", () -> {
                                effect.effects[finalI] = Fx.healWaveDynamic;
                                hide.run();
                            }).width(100).row();
                            tb.button("healWave", () -> {
                                effect.effects[finalI] = Fx.healWave;
                                hide.run();
                            }).width(100).row();
                            tb.button("heal", () -> {
                                effect.effects[finalI] = Fx.heal;
                                hide.run();
                            }).width(100).row();
                            tb.button("dynamicWave", () -> {
                                effect.effects[finalI] = Fx.dynamicWave;
                                hide.run();
                            }).width(100).row();
                            tb.button("shieldWave", () -> {
                                effect.effects[finalI] = Fx.shieldWave;
                                hide.run();
                            }).width(100).row();
                            tb.button("shieldApply", () -> {
                                effect.effects[finalI] = Fx.shieldApply;
                                hide.run();
                            }).width(100).row();
                            tb.button("disperseTrail", () -> {
                                effect.effects[finalI] = Fx.disperseTrail;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitBulletSmall", () -> {
                                effect.effects[finalI] = Fx.hitBulletSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitBulletColor", () -> {
                                effect.effects[finalI] = Fx.hitBulletColor;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitSquaresColor", () -> {
                                effect.effects[finalI] = Fx.hitSquaresColor;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitFuse", () -> {
                                effect.effects[finalI] = Fx.hitFuse;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitBulletBig", () -> {
                                effect.effects[finalI] = Fx.hitBulletBig;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitFlameSmall", () -> {
                                effect.effects[finalI] = Fx.hitFlameSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitFlamePlasma", () -> {
                                effect.effects[finalI] = Fx.hitFlamePlasma;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitLiquid", () -> {
                                effect.effects[finalI] = Fx.hitLiquid;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitLaserBlast", () -> {
                                effect.effects[finalI] = Fx.hitLaserBlast;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitEmpSpark", () -> {
                                effect.effects[finalI] = Fx.hitEmpSpark;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitLancer", () -> {
                                effect.effects[finalI] = Fx.hitLancer;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitBeam", () -> {
                                effect.effects[finalI] = Fx.hitBeam;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitFlameBeam", () -> {
                                effect.effects[finalI] = Fx.hitFlameBeam;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitMeltdown", () -> {
                                effect.effects[finalI] = Fx.hitMeltdown;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitMeltHeal", () -> {
                                effect.effects[finalI] = Fx.hitMeltHeal;
                                hide.run();
                            }).width(100).row();
                            tb.button("instBomb", () -> {
                                effect.effects[finalI] = Fx.instBomb;
                                hide.run();
                            }).width(100).row();
                            tb.button("instTrail", () -> {
                                effect.effects[finalI] = Fx.instTrail;
                                hide.run();
                            }).width(100).row();
                            tb.button("instShoot", () -> {
                                effect.effects[finalI] = Fx.instShoot;
                                hide.run();
                            }).width(100).row();
                            tb.button("instHit", () -> {
                                effect.effects[finalI] = Fx.instHit;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitLaser", () -> {
                                effect.effects[finalI] = Fx.hitLaser;
                                hide.run();
                            }).width(100).row();
                            tb.button("hitLaserColor", () -> {
                                effect.effects[finalI] = Fx.hitLaserColor;
                                hide.run();
                            }).width(100).row();
                            tb.button("despawn", () -> {
                                effect.effects[finalI] = Fx.despawn;
                                hide.run();
                            }).width(100).row();
                            tb.button("airBubble", () -> {
                                effect.effects[finalI] = Fx.airBubble;
                                hide.run();
                            }).width(100).row();
                            tb.button("flakExplosion", () -> {
                                effect.effects[finalI] = Fx.flakExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("plasticExplosion", () -> {
                                effect.effects[finalI] = Fx.plasticExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("plasticExplosionFlak", () -> {
                                effect.effects[finalI] = Fx.plasticExplosionFlak;
                                hide.run();
                            }).width(100).row();
                            tb.button("blastExplosion", () -> {
                                effect.effects[finalI] = Fx.blastExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("sapExplosion", () -> {
                                effect.effects[finalI] = Fx.sapExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("massiveExplosion", () -> {
                                effect.effects[finalI] = Fx.massiveExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("artilleryTrail", () -> {
                                effect.effects[finalI] = Fx.artilleryTrail;
                                hide.run();
                            }).width(100).row();
                            tb.button("incendTrail", () -> {
                                effect.effects[finalI] = Fx.incendTrail;
                                hide.run();
                            }).width(100).row();
                            tb.button("missileTrail", () -> {
                                effect.effects[finalI] = Fx.missileTrail;
                                hide.run();
                            }).width(100).row();
                            tb.button("missileTrailShort", () -> {
                                effect.effects[finalI] = Fx.missileTrailShort;
                                hide.run();
                            }).width(100).row();
                            tb.button("colorTrail", () -> {
                                effect.effects[finalI] = Fx.colorTrail;
                                hide.run();
                            }).width(100).row();
                            tb.button("absorb", () -> {
                                effect.effects[finalI] = Fx.absorb;
                                hide.run();
                            }).width(100).row();
                            tb.button("forceShrink", () -> {
                                effect.effects[finalI] = Fx.forceShrink;
                                hide.run();
                            }).width(100).row();
                            tb.button("flakExplosionBig", () -> {
                                effect.effects[finalI] = Fx.flakExplosionBig;
                                hide.run();
                            }).width(100).row();
                            tb.button("burning", () -> {
                                effect.effects[finalI] = Fx.burning;
                                hide.run();
                            }).width(100).row();
                            tb.button("fireRemove", () -> {
                                effect.effects[finalI] = Fx.fireRemove;
                                hide.run();
                            }).width(100).row();
                            tb.button("fire", () -> {
                                effect.effects[finalI] = Fx.fire;
                                hide.run();
                            }).width(100).row();
                            tb.button("fireHit", () -> {
                                effect.effects[finalI] = Fx.fireHit;
                                hide.run();
                            }).width(100).row();
                            tb.button("fireSmoke", () -> {
                                effect.effects[finalI] = Fx.fireSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("neoplasmHeal", () -> {
                                effect.effects[finalI] = Fx.neoplasmHeal;
                                hide.run();
                            }).width(100).row();
                            tb.button("steam", () -> {
                                effect.effects[finalI] = Fx.steam;
                                hide.run();
                            }).width(100).row();
                            tb.button("ventSteam", () -> {
                                effect.effects[finalI] = Fx.ventSteam;
                                hide.run();
                            }).width(100).row();
                            tb.button("drillSteam", () -> {
                                effect.effects[finalI] = Fx.drillSteam;
                                hide.run();
                            }).width(100).row();
                            tb.button("fluxVapor", () -> {
                                effect.effects[finalI] = Fx.fluxVapor;
                                hide.run();
                            }).width(100).row();
                            tb.button("vapor", () -> {
                                effect.effects[finalI] = Fx.vapor;
                                hide.run();
                            }).width(100).row();
                            tb.button("vaporSmall", () -> {
                                effect.effects[finalI] = Fx.vaporSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("fireballsmoke", () -> {
                                effect.effects[finalI] = Fx.fireballsmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("ballfire", () -> {
                                effect.effects[finalI] = Fx.ballfire;
                                hide.run();
                            }).width(100).row();
                            tb.button("freezing", () -> {
                                effect.effects[finalI] = Fx.freezing;
                                hide.run();
                            }).width(100).row();
                            tb.button("melting", () -> {
                                effect.effects[finalI] = Fx.melting;
                                hide.run();
                            }).width(100).row();
                            tb.button("wet", () -> {
                                effect.effects[finalI] = Fx.wet;
                                hide.run();
                            }).width(100).row();
                            tb.button("muddy", () -> {
                                effect.effects[finalI] = Fx.muddy;
                                hide.run();
                            }).width(100).row();
                            tb.button("sapped", () -> {
                                effect.effects[finalI] = Fx.sapped;
                                hide.run();
                            }).width(100).row();
                            tb.button("electrified", () -> {
                                effect.effects[finalI] = Fx.electrified;
                                hide.run();
                            }).width(100).row();
                            tb.button("sporeSlowed", () -> {
                                effect.effects[finalI] = Fx.sporeSlowed;
                                hide.run();
                            }).width(100).row();
                            tb.button("oily", () -> {
                                effect.effects[finalI] = Fx.oily;
                                hide.run();
                            }).width(100).row();
                            tb.button("overdriven", () -> {
                                effect.effects[finalI] = Fx.overdriven;
                                hide.run();
                            }).width(100).row();
                            tb.button("overclocked", () -> {
                                effect.effects[finalI] = Fx.overclocked;
                                hide.run();
                            }).width(100).row();
                            tb.button("dropItem", () -> {
                                effect.effects[finalI] = Fx.dropItem;
                                hide.run();
                            }).width(100).row();
                            tb.button("shockwave", () -> {
                                effect.effects[finalI] = Fx.shockwave;
                                hide.run();
                            }).width(100).row();
                            tb.button("bigShockwave", () -> {
                                effect.effects[finalI] = Fx.bigShockwave;
                                hide.run();
                            }).width(100).row();
                            tb.button("spawnShockwave", () -> {
                                effect.effects[finalI] = Fx.spawnShockwave;
                                hide.run();
                            }).width(100).row();
                            tb.button("explosion", () -> {
                                effect.effects[finalI] = Fx.explosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("dynamicExplosion", () -> {
                                effect.effects[finalI] = Fx.dynamicExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("reactorExplosion", () -> {
                                effect.effects[finalI] = Fx.reactorExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("impactReactorExplosion", () -> {
                                effect.effects[finalI] = Fx.impactReactorExplosion;
                                hide.run();
                            }).width(100).row();
                            tb.button("blockExplosionSmoke", () -> {
                                effect.effects[finalI] = Fx.blockExplosionSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("smokePuff", () -> {
                                effect.effects[finalI] = Fx.smokePuff;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmall", () -> {
                                effect.effects[finalI] = Fx.shootSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmallColor", () -> {
                                effect.effects[finalI] = Fx.shootSmallColor;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootHeal", () -> {
                                effect.effects[finalI] = Fx.shootHeal;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootHealYellow", () -> {
                                effect.effects[finalI] = Fx.shootHealYellow;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmallSmoke", () -> {
                                effect.effects[finalI] = Fx.shootSmallSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootBig", () -> {
                                effect.effects[finalI] = Fx.shootBig;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootBig2", () -> {
                                effect.effects[finalI] = Fx.shootBig2;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootBigColor", () -> {
                                effect.effects[finalI] = Fx.shootBigColor;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootTitan", () -> {
                                effect.effects[finalI] = Fx.shootTitan;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootBigSmoke", () -> {
                                effect.effects[finalI] = Fx.shootBigSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootBigSmoke2", () -> {
                                effect.effects[finalI] = Fx.shootBigSmoke2;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmokeDisperse", () -> {
                                effect.effects[finalI] = Fx.shootSmokeDisperse;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmokeSquare", () -> {
                                effect.effects[finalI] = Fx.shootSmokeSquare;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmokeSquareSparse", () -> {
                                effect.effects[finalI] = Fx.shootSmokeSquareSparse;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmokeSquareBig", () -> {
                                effect.effects[finalI] = Fx.shootSmokeSquareBig;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmokeTitan", () -> {
                                effect.effects[finalI] = Fx.shootSmokeTitan;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmokeSmite", () -> {
                                effect.effects[finalI] = Fx.shootSmokeSmite;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmokeMissile", () -> {
                                effect.effects[finalI] = Fx.shootSmokeMissile;
                                hide.run();
                            }).width(100).row();
                            tb.button("regenParticle", () -> {
                                effect.effects[finalI] = Fx.regenParticle;
                                hide.run();
                            }).width(100).row();
                            tb.button("regenSuppressParticle", () -> {
                                effect.effects[finalI] = Fx.regenSuppressParticle;
                                hide.run();
                            }).width(100).row();
                            tb.button("regenSuppressSeek", () -> {
                                effect.effects[finalI] = Fx.regenSuppressSeek;
                                hide.run();
                            }).width(100).row();
                            tb.button("surgeCruciSmoke", () -> {
                                effect.effects[finalI] = Fx.surgeCruciSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("neoplasiaSmoke", () -> {
                                effect.effects[finalI] = Fx.neoplasiaSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("heatReactorSmoke", () -> {
                                effect.effects[finalI] = Fx.heatReactorSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("circleColorSpark", () -> {
                                effect.effects[finalI] = Fx.circleColorSpark;
                                hide.run();
                            }).width(100).row();
                            tb.button("colorSpark", () -> {
                                effect.effects[finalI] = Fx.colorSpark;
                                hide.run();
                            }).width(100).row();
                            tb.button("colorSparkBig", () -> {
                                effect.effects[finalI] = Fx.colorSparkBig;
                                hide.run();
                            }).width(100).row();
                            tb.button("randLifeSpark", () -> {
                                effect.effects[finalI] = Fx.randLifeSpark;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootPayloadDriver", () -> {
                                effect.effects[finalI] = Fx.shootPayloadDriver;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootSmallFlame", () -> {
                                effect.effects[finalI] = Fx.shootSmallFlame;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootPyraFlame", () -> {
                                effect.effects[finalI] = Fx.shootPyraFlame;
                                hide.run();
                            }).width(100).row();
                            tb.button("shootLiquid", () -> {
                                effect.effects[finalI] = Fx.shootLiquid;
                                hide.run();
                            }).width(100).row();
                            tb.button("casing1", () -> {
                                effect.effects[finalI] = Fx.casing1;
                                hide.run();
                            }).width(100).row();
                            tb.button("casing2", () -> {
                                effect.effects[finalI] = Fx.casing2;
                                hide.run();
                            }).width(100).row();
                            tb.button("casing3", () -> {
                                effect.effects[finalI] = Fx.casing3;
                                hide.run();
                            }).width(100).row();
                            tb.button("casing4", () -> {
                                effect.effects[finalI] = Fx.casing4;
                                hide.run();
                            }).width(100).row();
                            tb.button("casing2Double", () -> {
                                effect.effects[finalI] = Fx.casing2Double;
                                hide.run();
                            }).width(100).row();
                            tb.button("casing3Double", () -> {
                                effect.effects[finalI] = Fx.casing3Double;
                                hide.run();
                            }).width(100).row();
                            tb.button("railShoot", () -> {
                                effect.effects[finalI] = Fx.railShoot;
                                hide.run();
                            }).width(100).row();
                            tb.button("railTrail", () -> {
                                effect.effects[finalI] = Fx.railTrail;
                                hide.run();
                            }).width(100).row();
                            tb.button("railHit", () -> {
                                effect.effects[finalI] = Fx.railHit;
                                hide.run();
                            }).width(100).row();
                            tb.button("lancerLaserShoot", () -> {
                                effect.effects[finalI] = Fx.lancerLaserShoot;
                                hide.run();
                            }).width(100).row();
                            tb.button("lancerLaserShootSmoke", () -> {
                                effect.effects[finalI] = Fx.lancerLaserShootSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("lancerLaserCharge", () -> {
                                effect.effects[finalI] = Fx.lancerLaserCharge;
                                hide.run();
                            }).width(100).row();
                            tb.button("lancerLaserChargeBegin", () -> {
                                effect.effects[finalI] = Fx.lancerLaserChargeBegin;
                                hide.run();
                            }).width(100).row();
                            tb.button("lightningCharge", () -> {
                                effect.effects[finalI] = Fx.lightningCharge;
                                hide.run();
                            }).width(100).row();
                            tb.button("sparkShoot", () -> {
                                effect.effects[finalI] = Fx.sparkShoot;
                                hide.run();
                            }).width(100).row();
                            tb.button("lightningShoot", () -> {
                                effect.effects[finalI] = Fx.lightningShoot;
                                hide.run();
                            }).width(100).row();
                            tb.button("thoriumShoot", () -> {
                                effect.effects[finalI] = Fx.thoriumShoot;
                                hide.run();
                            }).width(100).row();
                            tb.button("reactorsmoke", () -> {
                                effect.effects[finalI] = Fx.reactorsmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("redgeneratespark", () -> {
                                effect.effects[finalI] = Fx.redgeneratespark;
                                hide.run();
                            }).width(100).row();
                            tb.button("turbinegenerate", () -> {
                                effect.effects[finalI] = Fx.turbinegenerate;
                                hide.run();
                            }).width(100).row();
                            tb.button("generatespark", () -> {
                                effect.effects[finalI] = Fx.generatespark;
                                hide.run();
                            }).width(100).row();
                            tb.button("fuelburn", () -> {
                                effect.effects[finalI] = Fx.fuelburn;
                                hide.run();
                            }).width(100).row();
                            tb.button("incinerateSlag", () -> {
                                effect.effects[finalI] = Fx.incinerateSlag;
                                hide.run();
                            }).width(100).row();
                            tb.button("coreBurn", () -> {
                                effect.effects[finalI] = Fx.coreBurn;
                                hide.run();
                            }).width(100).row();
                            tb.button("plasticburn", () -> {
                                effect.effects[finalI] = Fx.plasticburn;
                                hide.run();
                            }).width(100).row();
                            tb.button("conveyorPoof", () -> {
                                effect.effects[finalI] = Fx.conveyorPoof;
                                hide.run();
                            }).width(100).row();
                            tb.button("pulverize", () -> {
                                effect.effects[finalI] = Fx.pulverize;
                                hide.run();
                            }).width(100).row();
                            tb.button("pulverizeRed", () -> {
                                effect.effects[finalI] = Fx.pulverizeRed;
                                hide.run();
                            }).width(100).row();
                            tb.button("pulverizeSmall", () -> {
                                effect.effects[finalI] = Fx.pulverizeSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("pulverizeMedium", () -> {
                                effect.effects[finalI] = Fx.pulverizeMedium;
                                hide.run();
                            }).width(100).row();
                            tb.button("producesmoke", () -> {
                                effect.effects[finalI] = Fx.producesmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("artilleryTrailSmoke", () -> {
                                effect.effects[finalI] = Fx.artilleryTrailSmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("smokeCloud", () -> {
                                effect.effects[finalI] = Fx.smokeCloud;
                                hide.run();
                            }).width(100).row();
                            tb.button("smeltsmoke", () -> {
                                effect.effects[finalI] = Fx.smeltsmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("coalSmeltsmoke", () -> {
                                effect.effects[finalI] = Fx.coalSmeltsmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("formsmoke", () -> {
                                effect.effects[finalI] = Fx.formsmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("blastsmoke", () -> {
                                effect.effects[finalI] = Fx.blastsmoke;
                                hide.run();
                            }).width(100).row();
                            tb.button("lava", () -> {
                                effect.effects[finalI] = Fx.lava;
                                hide.run();
                            }).width(100).row();
                            tb.button("dooropen", () -> {
                                effect.effects[finalI] = Fx.dooropen;
                                hide.run();
                            }).width(100).row();
                            tb.button("doorclose", () -> {
                                effect.effects[finalI] = Fx.doorclose;
                                hide.run();
                            }).width(100).row();
                            tb.button("dooropenlarge", () -> {
                                effect.effects[finalI] = Fx.dooropenlarge;
                                hide.run();
                            }).width(100).row();
                            tb.button("doorcloselarge", () -> {
                                effect.effects[finalI] = Fx.doorcloselarge;
                                hide.run();
                            }).width(100).row();
                            tb.button("generate", () -> {
                                effect.effects[finalI] = Fx.generate;
                                hide.run();
                            }).width(100).row();
                            tb.button("mineWallSmall", () -> {
                                effect.effects[finalI] = Fx.mineWallSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("mineSmall", () -> {
                                effect.effects[finalI] = Fx.mineSmall;
                                hide.run();
                            }).width(100).row();
                            tb.button("mine", () -> {
                                effect.effects[finalI] = Fx.mine;
                                hide.run();
                            }).width(100).row();
                            tb.button("mineBig", () -> {
                                effect.effects[finalI] = Fx.mineBig;
                                hide.run();
                            }).width(100).row();
                            tb.button("mineHuge", () -> {
                                effect.effects[finalI] = Fx.mineHuge;
                                hide.run();
                            }).width(100).row();
                            tb.button("mineImpact", () -> {
                                effect.effects[finalI] = Fx.mineImpact;
                                hide.run();
                            }).width(100).row();
                            tb.button("mineImpactWave", () -> {
                                effect.effects[finalI] = Fx.mineImpactWave;
                                hide.run();
                            }).width(100).row();
                            tb.button("payloadReceive", () -> {
                                effect.effects[finalI] = Fx.payloadReceive;
                                hide.run();
                            }).width(100).row();
                            tb.button("teleportActivate", () -> {
                                effect.effects[finalI] = Fx.teleportActivate;
                                hide.run();
                            }).width(100).row();
                            tb.button("teleport", () -> {
                                effect.effects[finalI] = Fx.teleport;
                                hide.run();
                            }).width(100).row();
                            tb.button("teleportOut", () -> {
                                effect.effects[finalI] = Fx.teleportOut;
                                hide.run();
                            }).width(100).row();
                            tb.button("ripple", () -> {
                                effect.effects[finalI] = Fx.ripple;
                                hide.run();
                            }).width(100).row();
                            tb.button("bubble", () -> {
                                effect.effects[finalI] = Fx.bubble;
                                hide.run();
                            }).width(100).row();
                            tb.button("launch", () -> {
                                effect.effects[finalI] = Fx.launch;
                                hide.run();
                            }).width(100).row();
                            tb.button("launchPod", () -> {
                                effect.effects[finalI] = Fx.launchPod;
                                hide.run();
                            }).width(100).row();
                            tb.button("healWaveMend", () -> {
                                effect.effects[finalI] = Fx.healWaveMend;
                                hide.run();
                            }).width(100).row();
                            tb.button("overdriveWave", () -> {
                                effect.effects[finalI] = Fx.overdriveWave;
                                hide.run();
                            }).width(100).row();
                            tb.button("healBlock", () -> {
                                effect.effects[finalI] = Fx.healBlock;
                                hide.run();
                            }).width(100).row();
                            tb.button("healBlockFull", () -> {
                                effect.effects[finalI] = Fx.healBlockFull;
                                hide.run();
                            }).width(100).row();
                            tb.button("rotateBlock", () -> {
                                effect.effects[finalI] = Fx.rotateBlock;
                                hide.run();
                            }).width(100).row();
                            tb.button("lightBlock", () -> {
                                effect.effects[finalI] = Fx.lightBlock;
                                hide.run();
                            }).width(100).row();
                            tb.button("overdriveBlockFull", () -> {
                                effect.effects[finalI] = Fx.overdriveBlockFull;
                                hide.run();
                            }).width(100).row();
                            tb.button("shieldBreak", () -> {
                                effect.effects[finalI] = Fx.shieldBreak;
                                hide.run();
                            }).width(100).row();
                            tb.button("coreLandDust", () -> {
                                effect.effects[finalI] = Fx.coreLandDust;
                                hide.run();
                            }).width(100).row();
                            tb.button("unitShieldBreak", () -> {
                                effect.effects[finalI] = Fx.unitShieldBreak;
                                hide.run();
                            }).width(100).row();
                            tb.button("chainLightning", () -> {
                                effect.effects[finalI] = Fx.chainLightning;
                                hide.run();
                            }).width(100).row();
                            tb.button("chainEmp", () -> {
                                effect.effects[finalI] = Fx.chainEmp;
                                hide.run();
                            }).width(100).row();
                            tb.button("legDestroy", () -> {
                                effect.effects[finalI] = Fx.legDestroy;
                                hide.run();
                            }).width(100).row();
                        }));
                    }, () -> {
                    }).grow();
                    t.button(Icon.trash, () -> {
                        Effect[] effects = new Effect[effect.effects.length - 1];
                        for (int j = 0; j < effect.effects.length; j++) {
                            if (j != finalI) {
                                effects[j] = effect.effects[j];
                            }
                        }
                        effect.effects = effects;
                        rebuildEffectList(on, effect);
                    }).grow();
                }).width(1400);
                on.row();
            }
        }
    }

    private static void createEffectDialog(Effect def, Cons<Effect> apply, Runnable hide) {
        EffectDialog ed = new EffectDialog("", apply, def);
        ed.hidden(hide);
        ed.show();
    }

    private static void rebuildColorList(Seq<Color> list) {
        colorList.clear();
        for (int i = 0; i < list.size; i++) {
            int finalI = i;
            colorList.table(c -> {
                c.setBackground(Tex.buttonDown);
                c.label(() -> finalI + 1 + "").pad(5);
                c.button(b -> {
                    b.setSize(5);
                    b.setColor(list.get(finalI));

                    b.clicked(() -> ui.showTextInput(Core.bundle.get("dialog.color.input"),
                            Core.bundle.get("dialog.color.input"), list.get(finalI).toString(), s -> {
                                Color co = Color.valueOf(s);
                                list.set(finalI, co);
                                rebuildColorList(list);
                            }));
                }, () -> {
                }).pad(5);
            }).growX();
            if (finalI % 3 == 0) {
                colorList.row();
            }
        }
    }

    public static void createSelectDialog(Button b, Cons2<Table, Runnable> table) {
        Table ta = new Table() {
            @Override
            public float getPrefHeight() {
                return Math.min(super.getPrefHeight(), Core.graphics.getHeight());
            }

            @Override
            public float getPrefWidth() {
                return Math.min(super.getPrefWidth(), Core.graphics.getWidth());
            }
        };
        ta.margin(4);

        Element hitter = new Element();

        Runnable hide = () -> {
            Core.app.post(hitter::remove);
            ta.actions(Actions.fadeOut(0.3f, Interp.fade), Actions.remove());
        };

        hitter.fillParent = true;
        hitter.tapped(hide);

        Core.scene.add(hitter);

        ta.update(() -> {
            if (b.parent == null || !b.isDescendantOf(Core.scene.root)) {
                Core.app.post(() -> {
                    hitter.remove();
                    ta.remove();
                });
                return;
            }

            b.localToStageCoordinates(Tmp.v1.set(b.getWidth() / 2f, b.getHeight() / 2f));
            ta.setPosition(Tmp.v1.x, Tmp.v1.y, Align.center);
            if (ta.getWidth() > Core.scene.getWidth()) ta.setWidth(Core.graphics.getWidth());
            if (ta.getHeight() > Core.scene.getHeight()) ta.setHeight(Core.graphics.getHeight());
            ta.keepInStage();
            ta.invalidateHierarchy();
            ta.pack();
        });

        Core.scene.add(ta);

        ta.top().pane(select -> {
            select.setBackground(Tex.buttonDown);
            table.get(select, hide);
        }).pad(0f).top().scrollX(false);
        ta.actions(Actions.alpha(0), Actions.fadeIn(0.001f));

        ta.pack();
    }

    public static void createInterpolSelect(Table on, String dia, String tile, Cons<Interp> apply) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile)).pad(5);
            t.button(b -> {
                b.image(Icon.pencilSmall);

                b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                    tb.clear();
                    tb.button("linear", () -> {
                        apply.get(Interp.linear);
                        hide.run();
                    }).width(100).row();
                    tb.button("reverse", () -> {
                        apply.get(Interp.reverse);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow2", () -> {
                        apply.get(Interp.pow2);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow2In", () -> {
                        apply.get(Interp.pow2In);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow2Out", () -> {
                        apply.get(Interp.pow2Out);
                        hide.run();
                    }).width(100).row();
                    tb.button("smooth", () -> {
                        apply.get(Interp.smooth);
                        hide.run();
                    }).width(100).row();
                    tb.button("smooth2", () -> {
                        apply.get(Interp.smooth2);
                        hide.run();
                    }).width(100).row();
                    tb.button("one", () -> {
                        apply.get(Interp.one);
                        hide.run();
                    }).width(100).row();
                    tb.button("zero", () -> {
                        apply.get(Interp.zero);
                        hide.run();
                    }).width(100).row();
                    tb.button("slope", () -> {
                        apply.get(Interp.slope);
                        hide.run();
                    }).width(100).row();
                    tb.button("smoother", () -> {
                        apply.get(Interp.smoother);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow2InInverse", () -> {
                        apply.get(Interp.pow2InInverse);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow2OutInverse", () -> {
                        apply.get(Interp.pow2OutInverse);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow3", () -> {
                        apply.get(Interp.pow3);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow3In", () -> {
                        apply.get(Interp.pow3In);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow3Out", () -> {
                        apply.get(Interp.pow3Out);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow3InInverse", () -> {
                        apply.get(Interp.pow3InInverse);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow3OutInverse", () -> {
                        apply.get(Interp.pow3OutInverse);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow4", () -> {
                        apply.get(Interp.pow4);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow4In", () -> {
                        apply.get(Interp.pow4In);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow4Out", () -> {
                        apply.get(Interp.pow4Out);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow5", () -> {
                        apply.get(Interp.pow5);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow5In", () -> {
                        apply.get(Interp.pow5In);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow10In", () -> {
                        apply.get(Interp.pow10In);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow10Out", () -> {
                        apply.get(Interp.pow10Out);
                        hide.run();
                    }).width(100).row();
                    tb.button("pow5Out", () -> {
                        apply.get(Interp.pow5Out);
                        hide.run();
                    }).width(100).row();
                    tb.button("sine", () -> {
                        apply.get(Interp.sine);
                        hide.run();
                    }).width(100).row();
                    tb.button("sineIn", () -> {
                        apply.get(Interp.sineIn);
                        hide.run();
                    }).width(100).row();
                    tb.button("sineOut", () -> {
                        apply.get(Interp.sineOut);
                        hide.run();
                    }).width(100).row();
                    tb.button("exp10", () -> {
                        apply.get(Interp.exp10);
                        hide.run();
                    }).width(100).row();
                    tb.button("exp10In", () -> {
                        apply.get(Interp.exp10In);
                        hide.run();
                    }).width(100).row();
                    tb.button("exp10Out", () -> {
                        apply.get(Interp.exp10Out);
                        hide.run();
                    }).width(100).row();
                    tb.button("exp5", () -> {
                        apply.get(Interp.exp5);
                        hide.run();
                    }).width(100).row();
                    tb.button("exp5In", () -> {
                        apply.get(Interp.exp5In);
                        hide.run();
                    }).width(100).row();
                    tb.button("exp5Out", () -> {
                        apply.get(Interp.exp5Out);
                        hide.run();
                    }).width(100).row();
                    tb.button("circle", () -> {
                        apply.get(Interp.circle);
                        hide.run();
                    }).width(100).row();
                    tb.button("circleIn", () -> {
                        apply.get(Interp.circleIn);
                        hide.run();
                    }).width(100).row();
                    tb.button("circleOut", () -> {
                        apply.get(Interp.circleOut);
                        hide.run();
                    }).width(100).row();
                    tb.button("circleOut", () -> {
                        apply.get(Interp.circleOut);
                        hide.run();
                    }).width(100).row();
                    tb.button("elastic", () -> {
                        apply.get(Interp.elastic);
                        hide.run();
                    }).width(100).row();
                    tb.button("elasticIn", () -> {
                        apply.get(Interp.elasticIn);
                        hide.run();
                    }).width(100).row();
                    tb.button("elasticOut", () -> {
                        apply.get(Interp.elasticOut);
                        hide.run();
                    }).width(100).row();
                    tb.button("swing", () -> {
                        apply.get(Interp.swing);
                        hide.run();
                    }).width(100).row();
                    tb.button("swingIn", () -> {
                        apply.get(Interp.swingIn);
                        hide.run();
                    }).width(100).row();
                    tb.button("swingOut", () -> {
                        apply.get(Interp.swingOut);
                        hide.run();
                    }).width(100).row();
                    tb.button("bounce", () -> {
                        apply.get(Interp.bounce);
                        hide.run();
                    }).width(100).row();
                    tb.button("bounceIn", () -> {
                        apply.get(Interp.bounceIn);
                        hide.run();
                    }).width(100).row();
                    tb.button("bounceOut", () -> {
                        apply.get(Interp.bounceOut);
                        hide.run();
                    }).width(100).row();
                }));
            }, () -> {
            });
        }).pad(10).width(250);
    }

    public static void createPartProgressSelect(Table on, String dia, String tile, Cons<DrawPart.PartProgress> apply) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile)).pad(5);
            t.button(b -> {
                b.image(Icon.pencilSmall);

                b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                    tb.clear();
                    tb.button("reload", () -> {
                        apply.get(DrawPart.PartProgress.reload);
                        hide.run();
                    }).width(100).row();
                    tb.button("smoothReload", () -> {
                        apply.get(DrawPart.PartProgress.smoothReload);
                        hide.run();
                    }).width(100).row();
                    tb.button("warmup", () -> {
                        apply.get(DrawPart.PartProgress.warmup);
                        hide.run();
                    }).width(100).row();
                    tb.button("charge", () -> {
                        apply.get(DrawPart.PartProgress.charge);
                        hide.run();
                    }).width(100).row();
                    tb.button("recoil", () -> {
                        apply.get(DrawPart.PartProgress.recoil);
                        hide.run();
                    }).width(100).row();
                    tb.button("heat", () -> {
                        apply.get(DrawPart.PartProgress.heat);
                        hide.run();
                    }).width(100).row();
                    tb.button("life", () -> {
                        apply.get(DrawPart.PartProgress.life);
                        hide.run();
                    }).width(100).row();
                }));
            }, () -> {
            });
        }).pad(10).width(250);
    }

    public static void createTypeLine(Table on, String dia, String type, float value) {
        on.row();
        on.table(table -> {
            table.setBackground(Tex.underline);
            table.label(() -> Core.bundle.get("dialog." + dia + "." + type)).left();
            table.row();
            table.label(() -> Core.bundle.get("@heavyUse") + ":  " + getHeavy(type, value)).left().pad(5);
            table.label(() -> Core.bundle.get("@maxLevel") + ":  " + maxLevel.get(type)).left().pad(5);
        });
        on.row();
    }

    public static void createMessageLine(Table on, String dia, String name) {
        on.row();
        on.setBackground(Tex.underline);
        on.label(() -> Core.bundle.get("dialog." + dia + "." + name)).center().width(35).pad(5);
    }

    public interface BoolGetter {
        boolean get();
    }

    public interface StrBool {
        boolean get(String str);
    }

    public interface EffectTableGetter {
        Table get();

        void set(Table table);
    }

    public interface heavyGetter {
        float get(int lev);
    }

    public interface levelGetter {
        int get(float val);
    }
}
