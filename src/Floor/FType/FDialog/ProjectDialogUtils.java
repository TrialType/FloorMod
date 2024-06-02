package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Strings;
import arc.util.Tmp;
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
import static Floor.FType.FDialog.ProjectsLocated.abilities;
import static Floor.FType.FDialog.ProjectsLocated.weapons;
import static mindustry.Vars.ui;

abstract class ProjectDialogUtils {
    public static float maxSize = 0;
    public static float freeSize = 0;
    public static final HashMap<String, heavyGetter> heavies = new HashMap<>();
    public static final HashMap<String, levelGetter> levels = new HashMap<>();
    public static final HashMap<String, Integer> maxLevel = new HashMap<>();

    public static void init() {
        //just for effect
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
        for (ProjectsLocated.weaponPack wp : weapons) {
            freeSize -= wp.heavy;
        }
        for (ProjectsLocated.abilityPack ap : abilities) {
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
        }).pad(10).fillX();
    }

    public static void createBooleanDialog(Table on, String dia, String tile, boolean def, Cons<Boolean> apply, Runnable rebuild) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ").pad(5);
            t.label(() -> Core.bundle.get("@" + def)).pad(5);
            t.button(Icon.rotate, () -> {
                apply.get(!def);
                rebuild.run();
            });
        });
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
        });
    }

    public static void createLevDialog(Table on, String dia, String type, String tile, float def, Cons<Float> apply, Runnable rebuild, Runnable updateHeavy, StrBool levUser, BoolGetter hevUser) {
        on.table(t -> {
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
        }).pad(10).fillX();
    }

    public static void createEffectList(Table on, EffectTableGetter data, String dia, String name, Effect list) {
        MultiEffect multi = (MultiEffect) list;
        if (multi != null) {
            on.label(() -> Core.bundle.get("dialog." + dia + "." + name) + "->");
            on.button(Icon.pencil, () -> {
                BaseDialog bd = new BaseDialog("");
                bd.cont.pane(li -> {
                    li.table(ta -> {
                        data.set(ta);
                        rebuildEffectList(data.get(), list);
                    }).grow();
                    li.row();
                    li.button(Icon.add, () -> {
                        Effect effect = new Effect();
                        Effect[] effects = new Effect[multi.effects.length + 1];
                        System.arraycopy(multi.effects, 0, effects, 0, multi.effects.length);
                        effects[effects.length - 1] = effect;
                        multi.effects = effects;
                        rebuildEffectList(data.get(), list);
                    });
                }).growX().growY();
                bd.row();
                bd.buttons.button(Icon.left, bd::hide);
                bd.show();
            });
        }
    }

    private static void rebuildEffectList(Table on, Effect list) {
        on.clear();
        MultiEffect effect = (MultiEffect) list;
        if (effect != null) {
            for (int i = 0; i < effect.effects.length; i++) {
                int finalI = i;
                on.table(t -> {
                    t.label(() -> finalI + "").growX();
                    t.button(Icon.pencil, () -> createEffectDialog(effect.effects[finalI],
                            e -> effect.effects[finalI] = e,
                            () -> rebuildEffectList(on, list))).growX();
                    t.button(Icon.trash, () -> {
                        Effect[] effects = new Effect[effect.effects.length - 1];
                        for (int j = 0; j < effect.effects.length; j++) {
                            if (j != finalI) {
                                effects[j] = effect.effects[j];
                            }
                        }
                        effect.effects = effects;
                        rebuildEffectList(on, list);
                    }).growX();
                }).growX();
                on.row();
            }
        }
    }

    private static void createEffectDialog(Effect def, Cons<Effect> apply, Runnable hide) {
        EffectDialog ed = new EffectDialog("", apply, def);
        ed.hidden(hide);
        ed.show();
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

        ta.top().pane(select -> table.get(select, hide)).pad(0f).top().scrollX(false);
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
                    }).row();
                    tb.button("reverse", () -> {
                        apply.get(Interp.reverse);
                        hide.run();
                    }).row();
                    tb.button("pow2", () -> {
                        apply.get(Interp.pow2);
                        hide.run();
                    }).row();
                    tb.button("pow2In", () -> {
                        apply.get(Interp.pow2In);
                        hide.run();
                    }).row();
                    tb.button("pow2Out", () -> {
                        apply.get(Interp.pow2Out);
                        hide.run();
                    }).row();
                    tb.button("smooth", () -> {
                        apply.get(Interp.smooth);
                        hide.run();
                    }).row();
                    tb.button("smooth2", () -> {
                        apply.get(Interp.smooth2);
                        hide.run();
                    }).row();
                    tb.button("one", () -> {
                        apply.get(Interp.one);
                        hide.run();
                    }).row();
                    tb.button("zero", () -> {
                        apply.get(Interp.zero);
                        hide.run();
                    }).row();
                    tb.button("slope", () -> {
                        apply.get(Interp.slope);
                        hide.run();
                    }).row();
                    tb.button("smoother", () -> {
                        apply.get(Interp.smoother);
                        hide.run();
                    }).row();
                    tb.button("pow2InInverse", () -> {
                        apply.get(Interp.pow2InInverse);
                        hide.run();
                    }).row();
                    tb.button("pow2OutInverse", () -> {
                        apply.get(Interp.pow2OutInverse);
                        hide.run();
                    }).row();
                    tb.button("pow3", () -> {
                        apply.get(Interp.pow3);
                        hide.run();
                    }).row();
                    tb.button("pow3In", () -> {
                        apply.get(Interp.pow3In);
                        hide.run();
                    }).row();
                    tb.button("pow3Out", () -> {
                        apply.get(Interp.pow3Out);
                        hide.run();
                    }).row();
                    tb.button("pow3InInverse", () -> {
                        apply.get(Interp.pow3InInverse);
                        hide.run();
                    }).row();
                    tb.button("pow3OutInverse", () -> {
                        apply.get(Interp.pow3OutInverse);
                        hide.run();
                    }).row();
                    tb.button("pow4", () -> {
                        apply.get(Interp.pow4);
                        hide.run();
                    }).row();
                    tb.button("pow4In", () -> {
                        apply.get(Interp.pow4In);
                        hide.run();
                    }).row();
                    tb.button("pow4Out", () -> {
                        apply.get(Interp.pow4Out);
                        hide.run();
                    }).row();
                    tb.button("pow5", () -> {
                        apply.get(Interp.pow5);
                        hide.run();
                    }).row();
                    tb.button("pow5In", () -> {
                        apply.get(Interp.pow5In);
                        hide.run();
                    }).row();
                    tb.button("pow10In", () -> {
                        apply.get(Interp.pow10In);
                        hide.run();
                    }).row();
                    tb.button("pow10Out", () -> {
                        apply.get(Interp.pow10Out);
                        hide.run();
                    }).row();
                    tb.button("pow5Out", () -> {
                        apply.get(Interp.pow5Out);
                        hide.run();
                    }).row();
                    tb.button("sine", () -> {
                        apply.get(Interp.sine);
                        hide.run();
                    }).row();
                    tb.button("sineIn", () -> {
                        apply.get(Interp.sineIn);
                        hide.run();
                    }).row();
                    tb.button("sineOut", () -> {
                        apply.get(Interp.sineOut);
                        hide.run();
                    }).row();
                    tb.button("exp10", () -> {
                        apply.get(Interp.exp10);
                        hide.run();
                    }).row();
                    tb.button("exp10In", () -> {
                        apply.get(Interp.exp10In);
                        hide.run();
                    }).row();
                    tb.button("exp10Out", () -> {
                        apply.get(Interp.exp10Out);
                        hide.run();
                    }).row();
                    tb.button("exp5", () -> {
                        apply.get(Interp.exp5);
                        hide.run();
                    }).row();
                    tb.button("exp5In", () -> {
                        apply.get(Interp.exp5In);
                        hide.run();
                    }).row();
                    tb.button("exp5Out", () -> {
                        apply.get(Interp.exp5Out);
                        hide.run();
                    }).row();
                    tb.button("circle", () -> {
                        apply.get(Interp.circle);
                        hide.run();
                    }).row();
                    tb.button("circleIn", () -> {
                        apply.get(Interp.circleIn);
                        hide.run();
                    }).row();
                    tb.button("circleOut", () -> {
                        apply.get(Interp.circleOut);
                        hide.run();
                    }).row();
                    tb.button("circleOut", () -> {
                        apply.get(Interp.circleOut);
                        hide.run();
                    }).row();
                    tb.button("elastic", () -> {
                        apply.get(Interp.elastic);
                        hide.run();
                    }).row();
                    tb.button("elasticIn", () -> {
                        apply.get(Interp.elasticIn);
                        hide.run();
                    }).row();
                    tb.button("elasticOut", () -> {
                        apply.get(Interp.elasticOut);
                        hide.run();
                    }).row();
                    tb.button("swing", () -> {
                        apply.get(Interp.swing);
                        hide.run();
                    }).row();
                    tb.button("swingIn", () -> {
                        apply.get(Interp.swingIn);
                        hide.run();
                    }).row();
                    tb.button("swingOut", () -> {
                        apply.get(Interp.swingOut);
                        hide.run();
                    }).row();
                    tb.button("bounce", () -> {
                        apply.get(Interp.bounce);
                        hide.run();
                    }).row();
                    tb.button("bounceIn", () -> {
                        apply.get(Interp.bounceIn);
                        hide.run();
                    }).row();
                    tb.button("bounceOut", () -> {
                        apply.get(Interp.bounceOut);
                        hide.run();
                    }).row();
                }));
            }, () -> {
            });
        }).growX();
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
                    }).row();
                    tb.button("smoothReload", () -> {
                        apply.get(DrawPart.PartProgress.smoothReload);
                        hide.run();
                    }).row();
                    tb.button("warmup", () -> {
                        apply.get(DrawPart.PartProgress.warmup);
                        hide.run();
                    }).row();
                    tb.button("charge", () -> {
                        apply.get(DrawPart.PartProgress.charge);
                        hide.run();
                    }).row();
                    tb.button("recoil", () -> {
                        apply.get(DrawPart.PartProgress.recoil);
                        hide.run();
                    }).row();
                    tb.button("heat", () -> {
                        apply.get(DrawPart.PartProgress.heat);
                        hide.run();
                    }).row();
                    tb.button("life", () -> {
                        apply.get(DrawPart.PartProgress.life);
                        hide.run();
                    }).row();
                }));
            }, () -> {
            });
        }).growX();
    }

    public static void createTypeLine(Table on, String dia, String type, float value) {
        on.row();
        on.table(table -> {
            table.background(Tex.scroll);
            table.label(() -> Core.bundle.get("dialog." + dia + "." + type)).left();
            table.row();
            table.label(() -> Core.bundle.get("@heavyUse") + ":  " + getHeavy(type, value)).left().pad(5);
            table.label(() -> Core.bundle.get("@maxLevel") + ":  " + maxLevel.get(type)).left().pad(5);
        });
        on.row();
    }

    public static void createMessageLine(Table on, String dia, String name) {
        on.row();
        on.label(() -> Core.bundle.get("dialog." + dia + "." + name)).width(35).pad(5);
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
