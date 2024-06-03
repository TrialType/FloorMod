package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.scene.ui.layout.Table;
import mindustry.entities.Effect;
import mindustry.entities.effect.*;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectDialogUtils.*;

public class EffectDialog extends BaseDialog implements EffectTableGetter {
    protected static String dia = "effect";
    protected Effect effect;
    protected Cons<Effect> apply;
    protected String type = "wave";
    protected Table base, tty, eff;
    protected Runnable reb = this::rebuildBase, ret = this::rebuildType;

    public EffectDialog(String title, Cons<Effect> apply) {
        super(title);

        this.apply = apply;
        effect = new WaveEffect();
        buttons.button("@back", Icon.left, this::hide);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            apply.get(effect);
            hide();
        });
        shown(this::rebuild);
    }

    public EffectDialog(String title, Cons<Effect> apply, Effect def) {
        this(title, apply);
        setEffect(def);
    }

    public void setEffect(Effect effect) {
        this.effect = effect == null ? new WaveEffect() : effect;
        setType();
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> {
            t.label(() -> Core.bundle.get("dialog.effect." + type)).pad(5);
            t.button(b -> {
                b.image(Icon.pencilSmall);

                b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                    tb.clear();
                    tb.button(Core.bundle.get("dialog.effect.wave"), () -> {
                        if (type.equals("wave")) {
                            hide.run();
                            return;
                        }
                        WaveEffect waveEffect = new WaveEffect();
                        waveEffect.lifetime = effect.lifetime;
                        waveEffect.clip = effect.clip;
                        waveEffect.startDelay = effect.startDelay;
                        waveEffect.baseRotation = effect.baseRotation;
                        waveEffect.followParent = effect.followParent;
                        waveEffect.rotWithParent = effect.rotWithParent;
                        waveEffect.layer = effect.layer;
                        waveEffect.layerDuration = effect.layerDuration;
                        effect = waveEffect;
                        rebuildType();
                        hide.run();
                    }).growX();
                    tb.row();
                    tb.button(Core.bundle.get("dialog.effect.wrap"), () -> {
                        if (type.equals("wrap")) {
                            hide.run();
                            return;
                        }
                        WrapEffect wrapEffect = new WrapEffect();
                        wrapEffect.lifetime = effect.lifetime;
                        wrapEffect.clip = effect.clip;
                        wrapEffect.startDelay = effect.startDelay;
                        wrapEffect.baseRotation = effect.baseRotation;
                        wrapEffect.followParent = effect.followParent;
                        wrapEffect.rotWithParent = effect.rotWithParent;
                        wrapEffect.layer = effect.layer;
                        wrapEffect.layerDuration = effect.layerDuration;
                        effect = wrapEffect;
                        rebuildType();
                        hide.run();
                    }).growX();
                    tb.row();
                    tb.button(Core.bundle.get("dialog.effect.radial"), () -> {
                        if (type.equals("radial")) {
                            hide.run();
                            return;
                        }
                        RadialEffect radialEffect = new RadialEffect();
                        radialEffect.lifetime = effect.lifetime;
                        radialEffect.clip = effect.clip;
                        radialEffect.startDelay = effect.startDelay;
                        radialEffect.baseRotation = effect.baseRotation;
                        radialEffect.followParent = effect.followParent;
                        radialEffect.rotWithParent = effect.rotWithParent;
                        radialEffect.layer = effect.layer;
                        radialEffect.layerDuration = effect.layerDuration;
                        effect = radialEffect;
                        rebuildType();
                        hide.run();
                    }).growX();
                    tb.row();
                    tb.button(Core.bundle.get("dialog.effect.particle"), () -> {
                        if (type.equals("particle")) {
                            hide.run();
                            return;
                        }
                        ParticleEffect particleEffect = new ParticleEffect();
                        particleEffect.lifetime = effect.lifetime;
                        particleEffect.clip = effect.clip;
                        particleEffect.startDelay = effect.startDelay;
                        particleEffect.baseRotation = effect.baseRotation;
                        particleEffect.followParent = effect.followParent;
                        particleEffect.rotWithParent = effect.rotWithParent;
                        particleEffect.layer = effect.layer;
                        particleEffect.layerDuration = effect.layerDuration;
                        effect = particleEffect;
                        rebuildType();
                        hide.run();
                    }).growX();
                    tb.row();
                    tb.button(Core.bundle.get("dialog.effect.explosion"), () -> {
                        if (type.equals("explosion")) {
                            hide.run();
                            return;
                        }
                        ExplosionEffect explosionEffect = new ExplosionEffect();
                        explosionEffect.lifetime = effect.lifetime;
                        explosionEffect.clip = effect.clip;
                        explosionEffect.startDelay = effect.startDelay;
                        explosionEffect.baseRotation = effect.baseRotation;
                        explosionEffect.followParent = effect.followParent;
                        explosionEffect.rotWithParent = effect.rotWithParent;
                        explosionEffect.layer = effect.layer;
                        explosionEffect.layerDuration = effect.layerDuration;
                        effect = explosionEffect;
                        rebuildType();
                        hide.run();
                    }).growX();
                }));
            }, () -> {
            });
        }).growX();
        cont.pane(t -> base = t);
        cont.pane(t -> tty = t);
        rebuildBase();
        rebuildType();
    }

    public void rebuildBase() {
        base.clear();
        createNumberDialog(base, dia, "lifetime", effect.lifetime,
                f -> effect.lifetime = f, reb);
        createNumberDialog(base, dia, "clip", effect.clip,
                f -> effect.clip = f, reb);
        createNumberDialog(base, dia, "startDelay", effect.startDelay,
                f -> effect.startDelay = f, reb);
        base.row();
        createNumberDialog(base, dia, "baseRotation", effect.baseRotation,
                f -> effect.baseRotation = f, reb);
        createNumberDialog(base, dia, "layer", effect.layer,
                f -> effect.layer = f, reb);
        createNumberDialog(base, dia, "layerDuration", effect.layerDuration,
                f -> effect.layerDuration = f, reb);
        base.row();
        createBooleanDialog(base, dia, "followParent", effect.followParent,
                b -> effect.followParent = b, reb);
        createBooleanDialog(base, dia, "rotWithParent", effect.rotWithParent,
                b -> effect.rotWithParent = b, reb);
    }

    public void rebuildType() {
        tty.clear();
        switch (type) {
            case "wave": {
                WaveEffect waveEffect = (WaveEffect) effect;
                createNumberDialog(tty, dia, "sizeFrom", waveEffect.sizeFrom,
                        f -> waveEffect.strokeFrom = f, ret);
                createNumberDialog(tty, dia, "sizeTo", waveEffect.sizeTo,
                        f -> waveEffect.sizeTo = f, ret);
                createNumberDialog(tty, dia, "sides", waveEffect.sides,
                        f -> waveEffect.sides = (int) (f + 0), ret);
                tty.row();
                createNumberDialog(tty, dia, "lightScl", waveEffect.lightScl,
                        f -> waveEffect.lightScl = f, ret);
                createNumberDialog(tty, dia, "lightOpacity", waveEffect.lightOpacity,
                        f -> waveEffect.lightOpacity = f, ret);
                createNumberDialog(tty, dia, "rotation", waveEffect.rotation,
                        f -> waveEffect.rotation = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "strokeFrom", waveEffect.strokeFrom,
                        f -> waveEffect.strokeFrom = f, ret);
                createNumberDialog(tty, dia, "strokeTo", waveEffect.strokeTo,
                        f -> waveEffect.strokeTo = f, ret);
                createNumberDialog(tty, dia, "offsetX", waveEffect.offsetX,
                        f -> waveEffect.offsetX = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "offsetY", waveEffect.offsetY,
                        f -> waveEffect.offsetY = f, ret);
                createInterpolSelect(tty, dia, "interp", i -> waveEffect.interp = i);
                createInterpolSelect(tty, dia, "lightInterp", i -> waveEffect.lightInterp = i);
                tty.row();
                createColorDialog(tty, dia, "colorFrom", waveEffect.colorFrom,
                        c -> waveEffect.colorFrom = c, ret);
                createColorDialog(tty, dia, "colorTo", waveEffect.colorTo,
                        c -> waveEffect.colorTo = c, ret);
                createColorDialog(tty, dia, "lightColor", waveEffect.lightColor,
                        c -> waveEffect.lightColor = c, ret);
            }
            case "wrap": {
                WrapEffect wrapEffect = (WrapEffect) effect;
                createNumberDialog(tty, dia, "rotation", wrapEffect.rotation,
                        f -> wrapEffect.rotation = f, ret);
                if (!(wrapEffect.effect instanceof MultiEffect)) {
                    wrapEffect.effect = new MultiEffect();
                }
                createEffectList(tty, this, dia, "effect", wrapEffect.effect);
                createColorDialog(tty, dia, "color", wrapEffect.color,
                        c -> wrapEffect.color = c, ret);
            }
            case "radial": {
                RadialEffect radialEffect = (RadialEffect) effect;
                createNumberDialog(tty, dia, "rotationSpacing", radialEffect.rotationSpacing,
                        f -> radialEffect.rotationSpacing = f, ret);
                createNumberDialog(tty, dia, "rotationOffset", radialEffect.rotationOffset,
                        f -> radialEffect.rotationOffset = f, ret);
                createNumberDialog(tty, dia, "lengthOffset", radialEffect.lengthOffset,
                        f -> radialEffect.lengthOffset = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "amount", radialEffect.amount,
                        f -> radialEffect.amount = (int) (f + 0), ret);
                if (!(radialEffect.effect instanceof MultiEffect)) {
                    radialEffect.effect = new MultiEffect();
                }
                createEffectList(tty, this, dia, "effect", radialEffect.effect);
            }
            case "particle": {
                ParticleEffect particleEffect = (ParticleEffect) effect;
                createNumberDialog(tty, dia, "particles", particleEffect.particles,
                        f -> particleEffect.particles = (int) (f + 0), ret);
                createBooleanDialog(tty, dia, "randLength", particleEffect.randLength,
                        b -> particleEffect.randLength = b, ret);
                createBooleanDialog(tty, dia, "casingFlip", particleEffect.casingFlip,
                        b -> particleEffect.casingFlip = b, ret);
                tty.row();
                createNumberDialog(tty, dia, "cone", particleEffect.cone,
                        f -> particleEffect.cone = f, ret);
                createNumberDialog(tty, dia, "length", particleEffect.length,
                        f -> particleEffect.length = f, ret);
                createNumberDialog(tty, dia, "baseLength", particleEffect.baseLength,
                        f -> particleEffect.baseLength = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "offsetX", particleEffect.offsetX,
                        f -> particleEffect.offsetX = f, ret);
                createNumberDialog(tty, dia, "offsetY", particleEffect.offsetY,
                        f -> particleEffect.offsetY = f, ret);
                createNumberDialog(tty, dia, "lightScl", particleEffect.lightScl,
                        f -> particleEffect.lightScl = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "lightOpacity", particleEffect.lightOpacity,
                        f -> particleEffect.lightOpacity = f, ret);
                createInterpolSelect(tty, dia, "interp", i -> particleEffect.interp = i);
                createInterpolSelect(tty, dia, "sizeInterp", i -> particleEffect.sizeInterp = i);
                tty.row();
                createColorDialog(tty, dia, "colorFrom", particleEffect.colorFrom,
                        c -> particleEffect.colorFrom = c, ret);
                createColorDialog(tty, dia, "colorTo", particleEffect.colorTo,
                        c -> particleEffect.colorTo = c, ret);
                createColorDialog(tty, dia, "lightColor", particleEffect.lightColor,
                        c -> particleEffect.lightColor = c, ret);
            }
            case "explosion": {
                ExplosionEffect explosionEffect = (ExplosionEffect) effect;
                createNumberDialog(tty, dia, "waveLife", explosionEffect.waveLife,
                        f -> explosionEffect.waveLife = f, ret);
                createNumberDialog(tty, dia, "waveStroke", explosionEffect.waveStroke,
                        f -> explosionEffect.waveStroke = f, ret);
                createNumberDialog(tty, dia, "waveRad", explosionEffect.waveRad,
                        f -> explosionEffect.waveRad = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "waveRadBase", explosionEffect.waveRadBase,
                        f -> explosionEffect.waveRadBase = f, ret);
                createNumberDialog(tty, dia, "sparkStroke", explosionEffect.sparkStroke,
                        f -> explosionEffect.sparkStroke = f, ret);
                createNumberDialog(tty, dia, "sparkRad", explosionEffect.sparkRad,
                        f -> explosionEffect.sparkRad = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "sparkLen", explosionEffect.sparkLen,
                        f -> explosionEffect.sparkLen = f, ret);
                createNumberDialog(tty, dia, "smokeSize", explosionEffect.smokeSize,
                        f -> explosionEffect.smokeSize = f, ret);
                createNumberDialog(tty, dia, "smokeSizeBase", explosionEffect.smokeSizeBase,
                        f -> explosionEffect.smokeSizeBase = f, ret);
                tty.row();
                createNumberDialog(tty, dia, "smokeRad", explosionEffect.smokeRad,
                        f -> explosionEffect.smokeRad = f, ret);
                createNumberDialog(tty, dia, "smokes", explosionEffect.smokes,
                        f -> explosionEffect.smokes = (int) (f + 0), ret);
                createNumberDialog(tty, dia, "sparks", explosionEffect.sparks,
                        f -> explosionEffect.sparks = (int) (f + 0), ret);
                tty.row();
                createColorDialog(tty, dia, "waveColor", explosionEffect.waveColor,
                        c -> explosionEffect.waveColor = c, ret);
                createColorDialog(tty, dia, "smokeColor", explosionEffect.smokeColor,
                        c -> explosionEffect.smokeColor = c, ret);
                createColorDialog(tty, dia, "sparkColor", explosionEffect.sparkColor,
                        c -> explosionEffect.sparkColor = c, ret);
            }
        }
    }

    public void setType() {
        if (effect instanceof WrapEffect) {
            type = "wrap";
        } else if (effect instanceof RadialEffect) {
            type = "radial";
        } else if (effect instanceof ParticleEffect) {
            type = "particle";
        } else if (effect instanceof ExplosionEffect) {
            type = "explosion";
        } else {
            if (!(effect instanceof WaveEffect)) effect = new WaveEffect();
            type = "wave";
        }
    }

    @Override
    public Table get() {
        return eff;
    }

    @Override
    public void set(Table table) {
        eff = table;
    }
}
