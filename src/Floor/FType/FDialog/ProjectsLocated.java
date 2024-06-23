package Floor.FType.FDialog;

import Floor.FEntities.FAbility.SprintingAbility;
import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.core.GameState;
import mindustry.entities.Effect;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.io.TypeIO;
import mindustry.type.StatusEffect;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectUtils.*;
import static mindustry.Vars.*;

public class ProjectsLocated extends BaseDialog implements EffectTableGetter {
    public static ProjectsLocated projects;
    public static StatusEffect eff;
    public Table effect;
    public float healthBoost = 1, speedBoost = 1;
    public float healD, speedD;
    public SprintingAbility boost;
    public final Seq<weaponPack> weapons = new Seq<>();
    public final Seq<abilityPack> abilities = new Seq<>();
    public Cons<Unit> upper = u -> {
        eff.healthMultiplier = Math.max(0.01f, healthBoost);
        eff.speedMultiplier = Math.max(0.01f, speedBoost);
        u.apply(eff);
        Seq<Weapon> we = new Seq<>();
        Seq<Ability> ab = new Seq<>();
        for (weaponPack wp : weapons) {
            wp.weapon.load();
            wp.weapon.init();
            if (wp.weapon.bullet != null) {
                wp.weapon.bullet.load();
                wp.weapon.bullet.init();
            }
            we.add(wp.weapon);
        }
        for (abilityPack ap : abilities) {
            ab.add(ap.ability);
        }
        if (u.mounts == null) {
            u.mounts = new WeaponMount[we.size];
            for (int i = 0; i < we.size; i++) {
                u.mounts[i] = new WeaponMount(we.get(i));
            }
        } else {
            int len = u.type.weapons.size;
            WeaponMount[] wm = new WeaponMount[len + we.size];
            System.arraycopy(u.mounts, 0, wm, 0, len);
            for (int i = len; i < wm.length; i++) {
                wm[i] = new WeaponMount(we.get(i - len));
            }
            u.mounts = wm;
        }

        if (u.abilities == null) {
            u.abilities = ab.items;
        } else {
            int len = u.type.abilities.size;
            Ability[] a = new Ability[len + ab.size];
            System.arraycopy(u.abilities, 0, a, 0, len);
            for (int i = len; i < a.length; i++) {
                a[i] = ab.get(i - len);
            }
            u.abilities = a;
        }
    };

    protected Unit seed;
    protected Table located, LWeapon, LAbility, base;

    private weaponPack pushW = null;
    private abilityPack pushA = null;

    public static void create() {
        ProjectUtils.init();
        projects = new ProjectsLocated("");
    }

    public ProjectsLocated(String title) {
        super(title);

        shown(this::rebuild);
        shown(ProjectUtils::updateHeavy);

        hidden(() -> upper.get(seed));
        //hidden(this::removeTables);

        buttons.button("@back", Icon.left, () -> {
            state.set(GameState.State.playing);
            set(seed);
            hide();
        }).width(100);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            state.set(GameState.State.playing);
            Core.settings.put("floor-project-heal", healthBoost);
            Core.settings.put("floor-project-speed", speedBoost);
            hide();
        }).width(100);
        buttons.button(Core.bundle.get("dialog.weapon.add"), Icon.add, () -> {
            if (freeSize >= 0.5f) {
                weaponPack w = new weaponPack();
                weapons.add(w);
                freeSize -= w.heavy;
                rebuildWeapon();
                //updateTable();
            }
        }).width(200);
        buttons.button(Core.bundle.get("dialog.ability.add"), Icon.add, () -> {
            if (freeSize >= 0.5f) {
                abilityPack a = new abilityPack();
                abilities.add(a);
                freeSize -= a.heavy;
                rebuildAbility();
                //updateTable();
            }
        }).width(200);
        buttons.button(Core.bundle.get("dialog.unit.base"), Icon.pencil, () -> {
            BaseDialog bd = new BaseDialog("");
            healD = healthBoost;
            speedD = speedBoost;
            bd.cont.pane(t -> {
                base = t;
                bd.setBackground(Tex.buttonEdge1);
                rebuildBase();
            });

            bd.buttons.button("@back", Icon.left, bd::hide).width(100);
            bd.buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
                if ((getHeavy("speed", speedD) + getHeavy("health", healD)) <= freeSize) {
                    healthBoost = Math.max(0.01f, healD);
                    speedBoost = Math.max(0.01f, speedD);
                    bd.hide();
                } else {
                    ui.showInfo(Core.bundle.get("@tooHeavy"));
                }
            }).width(100);
            bd.shown(() -> freeSize += getBaseHeavy(false));
            //bd.shown(this::hideTables);
            bd.hidden(() -> freeSize -= getBaseHeavy(true));
            bd.hidden(this::showTables);
            bd.show();
        }).width(200);
    }

    public void rebuildBase() {
        base.clear();
        base.table(t -> {
            createLevDialog(t, "unit", "health", "healBoost", healD,
                    f -> healD = Math.max(0.01f, f), this::rebuildBase,
                    () -> {
                    }, () -> couldUse("health", healD),
                    () -> getBaseHeavy(true) <= freeSize);
            createLevDialog(t, "unit", "speed", "speedBoost", speedD,
                    f -> speedD = Math.max(0.01f, f), this::rebuildBase,
                    () -> {
                    }, () -> couldUse("speed", speedD),
                    () -> getBaseHeavy(true) <= freeSize);
        });

        base.row();
        base.table(t -> {
            t.label(() -> Core.bundle.get("dialog.unit.boost")).left().width(150);
            t.label(() -> Core.bundle.get("@heavyUse") + ": " + (boost == null ? 0 :
                    getHeavy("boost", boost.damage + boost.maxLength) +
                            getHeavy("boostReload", (60 / boost.reload)))).left().width(150);
            if (boost == null) {
                t.button(Icon.add, () -> {
                    boost = new SprintingAbility();
                    rebuildBase();
                }).left().pad(5);
                t.row();
            } else {
                t.button(Icon.trash, () -> {
                    boost = null;
                    rebuildBase();
                }).pad(5);
                t.row();
                createLevDialog(t, "unit", "boost", "damage", boost.damage,
                        f -> boost.damage = f, this::rebuildBase, () -> {
                        }, () -> couldUse("boost", boost.damage + boost.maxLength),
                        () -> getBaseHeavy(true) <= freeSize);
                createLevDialog(t, "unit", "boost", "maxLength", boost.maxLength,
                        f -> boost.maxLength = f, this::rebuildBase, () -> {
                        }, () -> couldUse("boost", boost.damage + boost.maxLength),
                        () -> getBaseHeavy(true) <= freeSize);
                createLevDialog(t, "unit", "boostReload", "reload", boost.reload,
                        f -> boost.reload = f, this::rebuildBase, () -> {
                        }, () -> couldUse("boostReload", (60 / boost.reload)),
                        () -> getBaseHeavy(true) <= freeSize);
                t.row();
                createNumberDialog(t, "unit", "powerReload", boost.powerReload,
                        f -> boost.powerReload = f, this::rebuildBase);
                createEffectList(t, this, "unit", "powerEffect",
                        () -> boost.powerEffect, e -> boost.powerEffect = e);
                createEffectList(t, this, "unit", "maxPowerEffect",
                        () -> boost.maxPowerEffect, e -> boost.maxPowerEffect = e);
            }
        }).width(1350);
    }

    public float getBaseHeavy(boolean write) {
        if (write) {
            return getHeavy("speed", speedD) + getHeavy("health", healD) + (boost == null ? 0 :
                    (getHeavy("boost", boost.damage + boost.maxLength) +
                            getHeavy("boostReload", (60 / boost.reload))));
        } else {
            return getHeavy("speed", speedBoost) + getHeavy("health", healthBoost) + (boost == null ? 0 :
                    (getHeavy("boost", boost.damage + boost.maxLength) +
                            getHeavy("boostReload", (60 / boost.reload))));
        }
    }

    public void rebuild() {
        cont.clear();
//        cont.pane(t -> {
//            t.setBackground(Tex.buttonEdge1);
//            located = t;
//        }).width(900).height(900);
        cont.pane(t -> {
            t.table(w -> {
                w.setBackground(Tex.buttonEdge1);
                LWeapon = w;
            })/*.width(500)*/.growY();
            t.table(a -> {
                a.setBackground(Tex.buttonEdge1);
                LAbility = a;
            })/*.width(500)*/.growY();
        })/*.width(1000)*/;

        //rebuildLocated();
        rebuildWeapon();
        rebuildAbility();
    }

    public void rebuildLocated() {
        located.clear();
        located.image(Core.atlas.find("gamma", "")).width(700).height(700);
    }

    public void rebuildWeapon() {
        LWeapon.clear();
        LWeapon.table(t -> {
            t.setBackground(Tex.buttonEdge1);
            t.label(() -> Core.bundle.get("@heavyUse") + getWeaponHeavy() + "/" + maxSize);
        }).width(500).row();
        for (int i = 0; i < weapons.size; i++) {
            weaponPack wp = weapons.get(i);
            int finalI = i;
            LWeapon.table(t -> {
                t.setBackground(pushW == wp ? Tex.buttonDown : Tex.windowEmpty);

                t.clicked(() -> {
                    pushW = pushW == wp ? null : wp;
                    rebuildWeapon();
                });
                t.label(() -> Core.bundle.get("dialog.weapon.index") + ": " + finalI).left().width(100).pad(5);
                t.label(() -> Core.bundle.get("@heavyUse") + wp.heavy).left().width(100).pad(5);
                t.button(Icon.pencil, () -> {
                    if (freeSize > 0) {
                        wp.dialog.show();
                    } else {
                        ui.showInfo(Core.bundle.get("tooHeavyDrop"));
                    }
                }).pad(5);
                t.button(Icon.trash, () -> {
                    freeSize += wp.heavy;
                    weapons.remove(finalI);
                    if (wp.on != null) {
                        Core.app.post(wp.on::remove);
                        wp.on.actions(Actions.fadeOut(0.001f), Actions.remove());
                    }
                    pushW = pushW == wp ? null : pushW;
                    rebuildWeapon();
                }).pad(5);
            }).width(500);
            LWeapon.row();
        }
    }

    public void rebuildAbility() {
        LAbility.clear();
        LAbility.table(t -> {
            t.setBackground(Tex.buttonEdge1);
            t.label(() -> Core.bundle.get("@heavyUse") + getAbilityHeavy() + "/" + maxSize);
        }).width(500).row();
        for (int i = 0; i < abilities.size; i++) {
            int finalI = i;
            abilityPack ap = abilities.get(finalI);
            LAbility.table(t -> {
                t.setBackground(ap == pushA ? Tex.buttonDown : Tex.windowEmpty);

                t.clicked(() -> {
                    pushA = pushA == ap ? null : ap;
                    rebuildAbility();
                });
                t.label(() -> Core.bundle.get("dialog.ability.index") + ": " + finalI).left().width(100).pad(5);
                t.label(() -> Core.bundle.get("@heavyUse") + ap.heavy);
                t.button(Icon.pencil, () -> {
                    if (freeSize > 0) {
                        ap.dialog.show();
                    } else {
                        ui.showInfo(Core.bundle.get("tooHeavyDrop"));
                    }
                }).pad(5);
                t.button(Icon.trash, () -> {
                    freeSize += ap.heavy;
                    abilities.remove(finalI);
                    if (ap.on != null) {
                        Core.app.post(ap.on::remove);
                    }
                    pushA = pushA == ap ? null : pushA;
                    rebuildAbility();
                }).pad(5);
            }).width(500).row();
        }
    }

    public void updateTable() {
        for (int i = 0; i < weapons.size; i++) {
            int finalI = i;
            weaponPack wp = weapons.get(finalI);

            if (wp.on == null) {
                Table t = new Table();
                t.setSize(210, 40);

                t.update(() -> {
                    t.setBackground(pushW == wp ? Tex.buttonDown : Tex.windowEmpty);
                    t.setPosition(8 * wp.weapon.x + located.x + located.getWidth() / 2,
                            8 * wp.weapon.y + located.y + located.getHeight() / 2);

                    t.keepInStage();
                    t.invalidateHierarchy();
                    t.pack();
                });

                Core.scene.add(t);

                t.top().pane(w -> {
                    w.setSize(200, 30);

                    w.label(() -> Core.bundle.get("dialog.weapon.index") + ": " + finalI);
                }).top();

                t.actions(Actions.alpha(0), Actions.fadeIn(0.001f));

                t.pack();
                wp.on = t;
            }
        }
    }

    public void removeTables() {
        for (weaponPack wp : weapons) {
            if (wp.on != null) {
                wp.on.actions(Actions.fadeOut(0), Actions.remove());
                Core.app.post(wp.on::remove);
            }
        }
        for (abilityPack ap : abilities) {
            if (ap.on != null) {
                ap.on.actions(Actions.fadeOut(0), Actions.remove());
                Core.app.post(ap.on::remove);
            }
        }
    }

    public void hideTables() {
        for (weaponPack wp : weapons) {
            if (wp.on != null) {
                wp.on.actions(Actions.fadeOut(0.001f));
            }
        }
        for (abilityPack ap : abilities) {
            if (ap.on != null) {
                ap.on.actions(Actions.fadeOut(0.001f));
            }
        }
    }

    public void showTables() {
        for (weaponPack wp : weapons) {
            if (wp.on != null) {
                wp.on.actions(Actions.fadeIn(0.001f));
            }
        }
        for (abilityPack ap : abilities) {
            if (ap.on != null) {
                ap.on.actions(Actions.fadeIn(0.001f));
            }
        }
    }

    public void set(Unit unit) {
        this.seed = unit;
        weapons.clear();
        abilities.clear();
        if (seed != null && seed.spawnedByCore) {
            if (seed.hasEffect(eff)) {
                healthBoost = eff.healthMultiplier;
                speedBoost = eff.speedMultiplier;
                WeaponMount[] mounts = seed.mounts;
                for (int i = seed.type.weapons.size; i < mounts.length; i++) {
                    weapons.add(new weaponPack(mounts[i].weapon));
                }
                Ability[] a = seed.abilities;
                for (int i = seed.type.abilities.size; i < a.length; i++) {
                    abilities.add(new abilityPack(a[i]));
                }
            }
        }
        //updateTable();
    }

    public void set(Ability[] a) {
        for (Ability ability : a) {
            abilities.add(new abilityPack(ability));
        }
        updateHeavy();
    }

    public void set(WeaponMount[] w) {
        for (WeaponMount mount : w) {
            weapons.add(new weaponPack(mount.weapon));
        }
        updateHeavy();
    }

    public float getWeaponHeavy() {
        float h = 0;
        for (weaponPack wp : weapons) {
            h += wp.heavy;
        }
        return h;
    }

    public float getAbilityHeavy() {
        float heavy = 0;
        for (abilityPack ap : abilities) {
            heavy += ap.heavy;
        }
        return heavy;
    }

    @Override
    public Table get() {
        return effect;
    }

    @Override
    public void set(Table table) {
        effect = table;
    }

    public void setZero() {
        abilities.clear();
        weapons.clear();
        boost = null;
        healthBoost = eff.healthMultiplier = 1;
        speedBoost = eff.speedMultiplier = 1;
        updateHeavy();
    }

    public void read(Reads read) {
        weapons.clear();
        abilities.clear();
        int num = read.i();
        for (int i = 0; i < num; i++) {
            String type = read.str();
            switch (type) {
                case "suppressionField" -> {
                    SuppressionFieldAbility a = new SuppressionFieldAbility();
                    a.active = read.bool();
                    a.x = read.f();
                    a.y = read.f();
                    a.applyParticleChance = read.f();
                    a.color = TypeIO.readColor(read);
                    a.layer = read.f();
                    a.orbMidScl = read.f();
                    a.orbRadius = read.f();
                    a.orbSinMag = read.f();
                    a.orbSinScl = read.f();
                    a.particleColor = TypeIO.readColor(read);
                    a.particleLen = read.f();
                    a.particleLife = read.f();
                    a.particles = read.i();
                    a.particleSize = read.f();
                    a.range = read.f();
                    a.reload = read.f();
                    a.rotateScl = read.f();
                    abilities.add(new abilityPack(a));
                }
                case "regen" -> {
                    RegenAbility a = new RegenAbility();
                    a.amount = read.f();
                    a.percentAmount = read.f();
                    abilities.add(new abilityPack(a));
                }
                case "forceField" ->
                        abilities.add(new abilityPack(new ForceFieldAbility(read.f(), read.f(), read.f(), read.f(), read.i(), read.f())));
                case "shieldArc" -> {
                    ShieldArcAbility a = new ShieldArcAbility();
                    a.radius = read.f();
                    a.regen = read.f();
                    a.max = read.f();
                    a.cooldown = read.f();
                    a.angle = read.f();
                    a.angleOffset = read.f();
                    a.whenShooting = read.bool();
                    a.width = read.f();
                    a.drawArc = read.bool();
                    abilities.add(new abilityPack(a));
                }
                case "armorPlate" -> {
                    ArmorPlateAbility a = new ArmorPlateAbility();
                    a.healthMultiplier = read.f();
                    a.color = TypeIO.readColor(read);
                    a.z = read.f();
                    abilities.add(new abilityPack(a));
                }
                case "moveLightning" -> {
                    MoveLightningAbility a = new MoveLightningAbility(read.f(), read.i(), read.f(), read.f(),
                            read.f(), read.f(), TypeIO.readColor(read));
                    a.x = read.f();
                    if (read.bool()) {
                        a.bullet = readBullet(read);
                    }
                    a.bulletAngle = read.f();
                    a.bulletSpread = read.f();
                    a.alternate = read.bool();
                    a.shootEffect = TypeIO.readEffect(read);
                    a.parentizeEffects = read.bool();
                    a.shootSound = TypeIO.readSound(read);
                    abilities.add(new abilityPack(a));
                }
                case "repairField" -> {
                    RepairFieldAbility a = new RepairFieldAbility(read.f(), read.f(), read.f());
                    a.activeEffect = TypeIO.readEffect(read);
                    a.healEffect = TypeIO.readEffect(read);
                    a.parentizeEffects = read.bool();
                    abilities.add(new abilityPack(a));
                }
            }
        }
        num = read.i();
        for (int i = 0; i < num; i++) {
            String type = read.str();
            Weapon w;
            switch (type) {
                case "repair" -> w = new RepairBeamWeapon();
                case "defense" -> w = new PointDefenseWeapon();
                default -> w = new Weapon();
            }
            if (read.bool()) {
                w.bullet = readBullet(read);
            }
            w.ejectEffect = TypeIO.readEffect(read);
            w.x = read.f();
            w.y = read.f();
            w.shootY = read.f();
            w.shootX = read.f();
            w.shootCone = read.f();
            w.rotate = read.bool();
            w.rotateSpeed = read.f();
            w.baseRotation = read.f();
            w.mirror = read.bool();
            w.alternate = read.bool();
            w.continuous = read.bool();
            w.alwaysContinuous = read.bool();
            w.controllable = read.bool();
            w.aiControllable = read.bool();
            w.alwaysShooting = read.bool();
            w.autoTarget = read.bool();
            w.predictTarget = read.bool();
            w.useAttackRange = read.bool();
            w.targetInterval = read.f();
            w.targetSwitchInterval = read.f();
            w.reload = read.f();
            w.inaccuracy = read.f();
            w.shake = read.f();
            w.recoil = read.f();
            w.recoils = read.i();
            w.recoilTime = read.f();
            w.recoilPow = read.f();
            w.xRand = read.f();
            w.rotationLimit = read.f();
            w.minWarmup = read.f();
            w.shootWarmupSpeed = read.f();
            w.smoothReloadSpeed = read.f();
            w.ignoreRotation = read.bool();
            w.noAttack = read.bool();
            w.linearWarmup = read.bool();
            w.parts = new Seq<>(readParts(read));
            w.shoot = readShoot(read);
            if (w instanceof RepairBeamWeapon r) {
                r.repairSpeed = read.f();
                r.fractionRepairSpeed = read.f();
                r.beamWidth = read.f();
                r.pulseRadius = read.f();
                r.pulseStroke = read.f();
                r.widthSinMag = read.f();
                r.widthSinScl = read.f();
                r.recentDamageMultiplier = read.f();
                r.targetBuildings = read.bool();
                r.targetUnits = read.bool();
                r.laserColor = TypeIO.readColor(read);
                r.laserTopColor = TypeIO.readColor(read);
                r.healColor = TypeIO.readColor(read);
                r.healEffect = TypeIO.readEffect(read);
            } else if (w instanceof PointDefenseWeapon p) {
                p.color = TypeIO.readColor(read);
                p.beamEffect = TypeIO.readEffect(read);
            }
            weapons.add(new weaponPack(w));
        }
        eff.healthMultiplier = healthBoost = read.f();
        eff.speedMultiplier = speedBoost = read.f();
    }

    public void write(Writes write) {
        for (weaponPack w : weapons) {
            w.weapon.load();
            w.weapon.init();
            if (w.weapon.bullet != null) {
                w.weapon.bullet.load();
                w.weapon.bullet.init();
            }
        }
        write.i(abilities.size);
        for (abilityPack ap : abilities) {
            Ability ability = ap.ability;
            if (ability instanceof SuppressionFieldAbility a) {
                write.str("suppressionField");
                write.bool(a.active);
                write.f(a.x);
                write.f(a.y);
                write.f(a.applyParticleChance);
                TypeIO.writeColor(write, a.color);
                write.f(a.layer);
                write.f(a.orbMidScl);
                write.f(a.orbRadius);
                write.f(a.orbSinMag);
                write.f(a.orbSinScl);
                TypeIO.writeColor(write, a.particleColor);
                write.f(a.particleLen);
                write.f(a.particleLife);
                write.i(a.particles);
                write.f(a.particleSize);
                write.f(a.range);
                write.f(a.reload);
                write.f(a.rotateScl);
            } else if (ability instanceof RegenAbility a) {
                write.str("regen");
                write.f(a.amount);
                write.f(a.percentAmount);
            } else if (ability instanceof ForceFieldAbility a) {
                write.str("forceField");
                write.f(a.radius);
                write.f(a.regen);
                write.f(a.max);
                write.f(a.cooldown);
                write.i(a.sides);
                write.f(a.rotation);
            } else if (ability instanceof ShieldArcAbility a) {
                write.str("shieldArc");
                write.f(a.radius);
                write.f(a.regen);
                write.f(a.max);
                write.f(a.cooldown);
                write.f(a.angle);
                write.f(a.angleOffset);
                write.bool(a.whenShooting);
                write.f(a.width);
                write.bool(a.drawArc);
            } else if (ability instanceof ArmorPlateAbility a) {
                write.str("armorPlate");
                write.f(a.healthMultiplier);
                TypeIO.writeColor(write, a.color);
                write.f(a.z);
            } else if (ability instanceof MoveLightningAbility a) {
                write.str("moveLightning");
                write.f(a.damage);
                write.i(a.length);
                write.f(a.chance);
                write.f(a.y);
                write.f(a.minSpeed);
                write.f(a.maxSpeed);
                TypeIO.writeColor(write, a.color);
                write.f(a.x);
                write.bool(a.bullet != null);
                if (a.bullet != null) {
                    writeBullet(write, a.bullet);
                }
                write.f(a.bulletAngle);
                write.f(a.bulletSpread);
                write.bool(a.alternate);
                TypeIO.writeEffect(write, a.shootEffect);
                write.bool(a.parentizeEffects);
                TypeIO.writeSound(write, a.shootSound);
            } else if (ability instanceof RepairFieldAbility a) {
                write.str("repairField");
                write.f(a.amount);
                write.f(a.reload);
                write.f(a.range);
                TypeIO.writeEffect(write, a.activeEffect);
                TypeIO.writeEffect(write, a.healEffect);
                write.bool(a.parentizeEffects);
            }
        }
        write.i(weapons.size);
        for (weaponPack we : weapons) {
            Weapon weapon = we.weapon;
            if (weapon instanceof RepairBeamWeapon) {
                write.str("repair");
            } else if (weapon instanceof PointDefenseWeapon) {
                write.str("defense");
            } else {
                write.str("default");
            }
            write.bool(weapon.bullet != null);
            if (weapon.bullet != null) {
                writeBullet(write, weapon.bullet);
            }
            TypeIO.writeEffect(write, weapon.ejectEffect);
            write.f(weapon.x);
            write.f(weapon.y);
            write.f(weapon.shootY);
            write.f(weapon.shootX);
            write.f(weapon.shootCone);
            write.bool(weapon.rotate);
            write.f(weapon.rotateSpeed);
            write.f(weapon.baseRotation);
            write.bool(weapon.mirror);
            write.bool(weapon.alternate);
            write.bool(weapon.continuous);
            write.bool(weapon.alwaysContinuous);
            write.bool(weapon.controllable);
            write.bool(weapon.aiControllable);
            write.bool(weapon.alwaysShooting);
            write.bool(weapon.autoTarget);
            write.bool(weapon.predictTarget);
            write.bool(weapon.useAttackRange);
            write.f(weapon.targetInterval);
            write.f(weapon.targetSwitchInterval);
            write.f(weapon.reload);
            write.f(weapon.inaccuracy);
            write.f(weapon.shake);
            write.f(weapon.recoil);
            write.i(weapon.recoils);
            write.f(weapon.recoilTime);
            write.f(weapon.recoilPow);
            write.f(weapon.xRand);
            write.f(weapon.rotationLimit);
            write.f(weapon.minWarmup);
            write.f(weapon.shootWarmupSpeed);
            write.f(weapon.smoothReloadSpeed);
            write.bool(weapon.ignoreRotation);
            write.bool(weapon.noAttack);
            write.bool(weapon.linearWarmup);
            writeParts(write, weapon.parts.size == 0 ? new DrawPart[0] : weapon.parts.items);
            writeShoot(write, weapon.shoot);
            if (weapon instanceof RepairBeamWeapon r) {
                write.f(r.repairSpeed);
                write.f(r.fractionRepairSpeed);
                write.f(r.beamWidth);
                write.f(r.pulseRadius);
                write.f(r.pulseStroke);
                write.f(r.widthSinMag);
                write.f(r.widthSinScl);
                write.f(r.recentDamageMultiplier);
                write.bool(r.targetBuildings);
                write.bool(r.targetUnits);
                TypeIO.writeColor(write, r.laserColor);
                TypeIO.writeColor(write, r.laserTopColor);
                TypeIO.writeColor(write, r.healColor);
                TypeIO.writeEffect(write, r.healEffect);
            } else if (weapon instanceof PointDefenseWeapon p) {
                TypeIO.writeColor(write, p.color);
                TypeIO.writeEffect(write, p.beamEffect);
            }
        }
        write.f(healthBoost);
        write.f(speedBoost);
    }

    public void writeBullet(Writes write, BulletType bullet) {
        LimitBulletType l;
        if (bullet instanceof LimitBulletType) {
            l = (LimitBulletType) bullet;
        } else {
            l = null;
        }
        write.f(bullet.damage);
        write.f(bullet.fragAngle);
        write.i(bullet.fragBullets);
        write.bool(bullet.fragBullet != null);
        if (bullet.fragBullet != null) {
            writeBullet(write, bullet.fragBullet);
        }
        write.f(bullet.lightningAngle);
        write.f(bullet.lightningCone);
        write.i(bullet.lightningLength);
        write.i(bullet.lightningLengthRand);
        write.f(bullet.lightningDamage);
        write.i(bullet.lightning);
        write.f(bullet.lifetime);
        write.f(bullet.speed);
        write.f(bullet.hitSize);
        write.f(bullet.drawSize);
        write.f(bullet.drag);
        write.i(bullet.pierceCap);
        write.f(bullet.pierceDamageFactor);
        write.f(bullet.optimalLifeFract);
        write.f(bullet.layer);
        write.bool(bullet.pierce);
        write.bool(bullet.pierceBuilding);
        write.bool(bullet.removeAfterPierce);
        TypeIO.writeSound(write, bullet.hitSound);
        write.f(bullet.hitSoundPitch);
        write.f(bullet.hitSoundVolume);
        TypeIO.writeSound(write, bullet.despawnSound);
        write.f(bullet.inaccuracy);
        write.f(bullet.ammoMultiplier);
        write.f(bullet.reloadMultiplier);
        write.f(bullet.buildingDamageMultiplier);
        write.f(bullet.recoil);
        write.f(bullet.splashDamage);
        write.bool(bullet.killShooter);
        write.bool(bullet.instantDisappear);
        write.bool(bullet.scaledSplashDamage);
        write.f(bullet.knockback);
        write.f(bullet.createChance);
        write.f(bullet.rangeChange);
        write.bool(bullet.impact);
        write.bool(bullet.collidesTiles);
        write.bool(bullet.collidesTeam);
        write.bool(bullet.collidesAir);
        write.bool(bullet.collidesGround);
        write.bool(bullet.collides);
        write.bool(bullet.collideFloor);
        write.bool(bullet.collideTerrain);
        write.bool(bullet.keepVelocity);
        write.bool(bullet.scaleLife);
        write.bool(bullet.hittable);
        write.bool(bullet.reflectable);
        write.bool(bullet.absorbable);
        write.bool(bullet.backMove);
        write.bool(bullet.ignoreSpawnAngle);
        write.f(bullet.rangeOverride);
        write.f(bullet.healPercent);
        write.f(bullet.healAmount);
        write.bool(bullet.fragOnHit);
        write.bool(bullet.fragOnAbsorb);
        write.bool(bullet.pierceArmor);
        write.f(bullet.hitShake);
        write.f(bullet.despawnShake);
        write.f(bullet.fragRandomSpread);
        write.f(bullet.fragSpread);
        write.f(bullet.fragAngle);
        write.bool(bullet.trailRotation);
        write.bool(bullet.splashDamagePierce);
        writeParts(write, bullet.parts.items);
        //(bullet.trailInterp);
        write.f(bullet.trailChance);
        write.f(bullet.trailInterval);
        write.f(bullet.trailParam);
        write.i(bullet.trailLength);
        write.f(bullet.trailWidth);
        write.f(bullet.trailSinMag);
        write.f(bullet.trailSinScl);
        write.f(bullet.splashDamageRadius);
        write.f(bullet.homingPower);
        write.f(bullet.homingRange);
        write.f(bullet.homingDelay);
        write.f(bullet.suppressionRange);
        write.f(bullet.suppressionDuration);
        write.f(bullet.suppressionEffectChance);
        write.f(bullet.weaveScale);
        write.f(bullet.weaveMag);
        write.bool(bullet.weaveRandom);
        write.i(bullet.puddles);
        write.f(bullet.puddleRange);
        write.f(bullet.puddleAmount);
        write.f(bullet.lightRadius);
        write.f(bullet.lightOpacity);
        write.bool(bullet.puddleLiquid != null);
        if (bullet.puddleLiquid != null) {
            TypeIO.writeLiquid(write, bullet.puddleLiquid);
        }
        TypeIO.writeColor(write, bullet.hitColor);
        TypeIO.writeColor(write, bullet.healColor);
        TypeIO.writeColor(write, bullet.trailColor);
        TypeIO.writeColor(write, bullet.lightningColor);
        TypeIO.writeColor(write, bullet.lightColor);
        writeEffect(write, bullet.shootEffect);
        writeEffect(write, bullet.despawnEffect);
        writeEffect(write, bullet.hitEffect);
        writeEffect(write, bullet.chargeEffect);
        writeEffect(write, bullet.smokeEffect);
        writeEffect(write, bullet.healEffect);
        writeEffect(write, bullet.trailEffect);
        write.bool(l != null);
        if (l != null) {
            write.str(l.type);
            write.bool(l.hitUnits);
            write.bool(l.haveEmp);
            write.bool(l.havePercent);
            write.bool(l.continuous);
            write.bool(l.largeHit);
            write.bool(l.drawFlare);
            write.bool(l.rotateFlare);
            write.f(l.percent);
            write.f(l.empDamage);
            write.f(l.radius);
            write.f(l.timeDuration);
            write.f(l.timeIncrease);
            write.f(l.powerDamageScl);
            write.f(l.powerSclDecrease);
            write.f(l.unitDamageScl);
            write.f(l.width);
            write.f(l.height);
            write.f(l.shrinkX);
            write.f(l.shrinkY);
            write.f(l.spin);
            write.f(l.rotationOffset);
            write.f(l.damageInterval);
            write.f(l.shake);
            write.f(l.flareLength);
            write.f(l.lightStroke);
            write.f(l.oscScl);
            write.f(l.oscMag);
            write.f(l.flareWidth);
            write.f(l.flareInnerScl);
            write.f(l.flareInnerLenScl);
            write.f(l.flareLayer);
            write.f(l.flareRotSpeed);
            write.f(l.laserCLength);
            write.f(l.fadeTime);
            write.f(l.strokeFrom);
            write.f(l.strokeTo);
            write.f(l.pointyScaling);
            write.f(l.backLength);
            write.f(l.frontLength);
            write.f(l.laserLength);
            write.f(l.lengthFalloff);
            write.f(l.sideLength);
            write.f(l.sideWidth);
            write.f(l.sideAngle);
            write.f(l.lightningSpacing);
            write.f(l.lightningDelay);
            write.f(l.lightningAngleRand);
            write.f(l.trailSpacing);
            write.f(l.railLength);
            write.f(l.pointEffectSpace);
            write.i(l.lengthWidthPans.length);
            for (float f : l.lengthWidthPans) {
                write.f(f);
            }
            write.i(l.divisions);
            write.i(l.bulletLightningLength);
            write.i(l.bulletLightningLengthRand);
            TypeIO.writeColor(write, l.backColor);
            TypeIO.writeColor(write, l.frontColor);
            TypeIO.writeColor(write, l.mixColorFrom);
            TypeIO.writeColor(write, l.mixColorTo);
            TypeIO.writeColor(write, l.flareColor);
            TypeIO.writeColor(write, l.bulletLightningColor);
            write.i(l.colors.length);
            for (Color c : l.colors) {
                TypeIO.writeColor(write, c);
            }
            writeEffect(write, l.hitPowerEffect);
            writeEffect(write, l.chainEffect);
            writeEffect(write, l.applyEffect);
            writeEffect(write, l.laserEffect);
            writeEffect(write, l.pierceEffect);
            writeEffect(write, l.pointEffect);
            writeEffect(write, l.lineEffect);
            writeEffect(write, l.endEffect);
            //shrinkInterp;
            //lengthInterp;
        }
    }

    public void writeParts(Writes write, DrawPart[] parts) {
        write.i(parts.length);
        for (DrawPart part : parts) {
            if (part instanceof ShapePart) {
                write.str("shape");
            } else if (part instanceof HoverPart) {
                write.str("hover");
            } else if (part instanceof HaloPart) {
                write.str("halo");
            } else if (part instanceof FlarePart) {
                write.str("flare");
            }
            write.bool(part.turretShading);
            write.bool(part.under);
            write.i(part.weaponIndex);
            write.i(part.recoilIndex);
            if (part instanceof ShapePart s) {
                write.bool(s.circle);
                write.bool(s.hollow);
                write.bool(s.mirror);
                write.f(s.x);
                write.f(s.y);
                write.f(s.rotation);
                write.f(s.moveX);
                write.f(s.moveY);
                write.f(s.moveRot);
                write.i(s.sides);
                write.f(s.radius);
                write.f(s.radiusTo);
                write.f(s.stroke);
                write.f(s.strokeTo);
                write.f(s.rotateSpeed);
                write.f(s.layer);
                write.f(s.layerOffset);
                TypeIO.writeColor(write, s.color);
                TypeIO.writeColor(write, s.colorTo);
            } else if (part instanceof HoverPart h) {
                write.f(h.x);
                write.f(h.y);
                write.f(h.rotation);
                write.f(h.phase);
                write.f(h.stroke);
                write.f(h.minStroke);
                write.f(h.radius);
                write.i(h.circles);
                write.i(h.sides);
                write.bool(h.mirror);
                write.f(h.layer);
                write.f(h.layerOffset);
                TypeIO.writeColor(write, h.color);
            } else if (part instanceof HaloPart h) {
                write.bool(h.tri);
                write.bool(h.hollow);
                write.bool(h.mirror);
                write.f(h.radius);
                write.i(h.sides);
                write.f(h.radiusTo);
                write.i(h.shapes);
                write.f(h.stroke);
                write.f(h.strokeTo);
                write.f(h.x);
                write.f(h.y);
                write.f(h.shapeRotation);
                write.f(h.moveX);
                write.f(h.moveY);
                write.f(h.shapeMoveRot);
                write.f(h.haloRotateSpeed);
                write.f(h.haloRotation);
                write.f(h.rotateSpeed);
                write.f(h.triLength);
                write.f(h.triLengthTo);
                write.f(h.haloRadius);
                write.f(h.haloRadiusTo);
                write.f(h.layer);
                write.f(h.layerOffset);
                TypeIO.writeColor(write, h.color);
                TypeIO.writeColor(write, h.colorTo);
            } else if (part instanceof FlarePart f) {
                write.i(f.sides);
                write.f(f.radius);
                write.f(f.radiusTo);
                write.f(f.stroke);
                write.f(f.innerScl);
                write.f(f.innerRadScl);
                write.f(f.x);
                write.f(f.y);
                write.f(f.rotation);
                write.f(f.rotMove);
                write.f(f.spinSpeed);
                write.f(f.layer);
                write.bool(f.followRotation);
                TypeIO.writeColor(write, f.color1);
                TypeIO.writeColor(write, f.color2);
            }
        }
    }

    public void writeShoot(Writes write, ShootPattern shoot) {
        if (shoot instanceof ShootSummon) {
            write.str("summon");
        } else if (shoot instanceof ShootSpread) {
            write.str("spread");
        } else if (shoot instanceof ShootSine) {
            write.str("sine");
        } else if (shoot instanceof ShootMulti) {
            write.str("multi");
        } else if (shoot instanceof ShootHelix) {
            write.str("helix");
        } else if (shoot instanceof ShootBarrel) {
            write.str("barrel");
        } else if (shoot instanceof ShootAlternate) {
            write.str("alternate");
        } else {
            write.str("pattern");
        }
        write.i(shoot.shots);
        write.f(shoot.firstShotDelay);
        write.f(shoot.shotDelay);
        if (shoot instanceof ShootSummon s) {
            write.f(s.x);
            write.f(s.y);
            write.f(s.radius);
            write.f(s.spread);
        } else if (shoot instanceof ShootSpread s) {
            write.f(s.spread);
        } else if (shoot instanceof ShootSine s) {
            write.f(s.scl);
            write.f(s.mag);
        } else if (shoot instanceof ShootMulti m) {
            writeShoot(write, m.source);
            write.i(m.dest.length);
            for (ShootPattern s : m.dest) {
                writeShoot(write, s);
            }
        } else if (shoot instanceof ShootHelix h) {
            write.f(h.scl);
            write.f(h.mag);
            write.f(h.offset);
        } else if (shoot instanceof ShootBarrel b) {
            write.i(b.barrelOffset);
            write.i(b.barrels.length);
            for (float f : b.barrels) {
                write.f(f);
            }
        } else if (shoot instanceof ShootAlternate a) {
            write.i(a.barrels);
            write.f(a.spread);
            write.i(a.barrelOffset);
        }
    }

    public void writeEffect(Writes write, Effect effect) {
        if (effect instanceof WaveEffect) {
            write.str("wave");
        } else if (effect instanceof WrapEffect) {
            write.str("wrap");
        } else if (effect instanceof RadialEffect) {
            write.str("radial");
        } else if (effect instanceof ParticleEffect) {
            write.str("particle");
        } else if (effect instanceof ExplosionEffect) {
            write.str("explosion");
        } else {
            write.str("effect");
            TypeIO.writeEffect(write, effect);
            return;
        }
        write.f(effect.lifetime);
        write.f(effect.clip);
        write.f(effect.startDelay);
        write.f(effect.baseRotation);
        write.bool(effect.followParent);
        write.bool(effect.rotWithParent);
        write.f(effect.layer);
        write.f(effect.layerDuration);
        if (effect instanceof WaveEffect waveEffect) {
            write.f(waveEffect.sizeFrom);
            write.f(waveEffect.sizeTo);
            write.i(waveEffect.sides);
            write.f(waveEffect.lightScl);
            write.f(waveEffect.lightOpacity);
            write.f(waveEffect.rotation);
            write.f(waveEffect.strokeFrom);
            write.f(waveEffect.strokeTo);
            write.f(waveEffect.offsetX);
            write.f(waveEffect.offsetY);
            TypeIO.writeColor(write, waveEffect.colorFrom);
            TypeIO.writeColor(write, waveEffect.colorTo);
            TypeIO.writeColor(write, waveEffect.lightColor);
            //(waveEffect.interp);
            //(waveEffect.lightInterp);
        } else if (effect instanceof WrapEffect wrapEffect) {
            write.f(wrapEffect.rotation);
            writeEffect(write, wrapEffect.effect);
            TypeIO.writeColor(write, wrapEffect.color);
        } else if (effect instanceof RadialEffect radialEffect) {
            write.f(radialEffect.rotationSpacing);
            write.f(radialEffect.rotationOffset);
            write.f(radialEffect.lengthOffset);
            write.i(radialEffect.amount);
            writeEffect(write, radialEffect.effect);
        } else if (effect instanceof ParticleEffect particleEffect) {
            write.i(particleEffect.particles);
            write.bool(particleEffect.randLength);
            write.bool(particleEffect.casingFlip);
            write.f(particleEffect.cone);
            write.f(particleEffect.length);
            write.f(particleEffect.baseLength);
            write.f(particleEffect.offsetX);
            write.f(particleEffect.offsetY);
            write.f(particleEffect.lightScl);
            write.f(particleEffect.strokeFrom);
            write.f(particleEffect.strokeTo);
            write.f(particleEffect.lenFrom);
            write.f(particleEffect.lenTo);
            write.bool(particleEffect.line);
            write.bool(particleEffect.cap);
            write.f(particleEffect.lightOpacity);
            //(particleEffect.interp);
            //(particleEffect.sizeInterp);
            TypeIO.writeColor(write, particleEffect.colorFrom);
            TypeIO.writeColor(write, particleEffect.colorTo);
            TypeIO.writeColor(write, particleEffect.lightColor);
        } else {
            ExplosionEffect explosionEffect = (ExplosionEffect) effect;
            write.f(explosionEffect.waveLife);
            write.f(explosionEffect.waveStroke);
            write.f(explosionEffect.waveRad);
            write.f(explosionEffect.waveRadBase);
            write.f(explosionEffect.sparkStroke);
            write.f(explosionEffect.sparkRad);
            write.f(explosionEffect.sparkLen);
            write.f(explosionEffect.smokeSize);
            write.f(explosionEffect.smokeSizeBase);
            write.f(explosionEffect.smokeRad);
            write.i(explosionEffect.smokes);
            write.i(explosionEffect.sparks);
            TypeIO.writeColor(write, explosionEffect.waveColor);
            TypeIO.writeColor(write, explosionEffect.smokeColor);
            TypeIO.writeColor(write, explosionEffect.sparkColor);
        }
    }

    public LimitBulletType readBullet(Reads read) {
        LimitBulletType bullet = new LimitBulletType();
        bullet.damage = read.f();
        bullet.fragAngle = read.f();
        bullet.fragBullets = read.i();
        if (read.bool()) {
            bullet.fragBullet = readBullet(read);
        }
        bullet.lightningAngle = read.f();
        bullet.lightningCone = read.f();
        bullet.lightningLength = read.i();
        bullet.lightningLengthRand = read.i();
        bullet.lightningDamage = read.f();
        bullet.lightning = read.i();
        bullet.lifetime = read.f();
        bullet.speed = read.f();
        bullet.hitSize = read.f();
        bullet.drawSize = read.f();
        bullet.drag = read.f();
        bullet.pierceCap = read.i();
        bullet.pierceDamageFactor = read.f();
        bullet.optimalLifeFract = read.f();
        bullet.layer = read.f();
        bullet.pierce = read.bool();
        bullet.pierceBuilding = read.bool();
        bullet.removeAfterPierce = read.bool();
        bullet.hitSound = TypeIO.readSound(read);
        bullet.hitSoundPitch = read.f();
        bullet.hitSoundVolume = read.f();
        bullet.despawnSound = TypeIO.readSound(read);
        bullet.inaccuracy = read.f();
        bullet.ammoMultiplier = read.f();
        bullet.reloadMultiplier = read.f();
        bullet.buildingDamageMultiplier = read.f();
        bullet.recoil = read.f();
        bullet.splashDamage = read.f();
        bullet.killShooter = read.bool();
        bullet.instantDisappear = read.bool();
        bullet.scaledSplashDamage = read.bool();
        bullet.knockback = read.f();
        bullet.createChance = read.f();
        bullet.rangeChange = read.f();
        bullet.impact = read.bool();
        bullet.collidesTiles = read.bool();
        bullet.collidesTeam = read.bool();
        bullet.collidesAir = read.bool();
        bullet.collidesGround = read.bool();
        bullet.collides = read.bool();
        bullet.collideFloor = read.bool();
        bullet.collideTerrain = read.bool();
        bullet.keepVelocity = read.bool();
        bullet.scaleLife = read.bool();
        bullet.hittable = read.bool();
        bullet.reflectable = read.bool();
        bullet.absorbable = read.bool();
        bullet.backMove = read.bool();
        bullet.ignoreSpawnAngle = read.bool();
        bullet.rangeOverride = read.f();
        bullet.healPercent = read.f();
        bullet.healAmount = read.f();
        bullet.fragOnHit = read.bool();
        bullet.fragOnAbsorb = read.bool();
        bullet.pierceArmor = read.bool();
        bullet.hitShake = read.f();
        bullet.despawnShake = read.f();
        bullet.fragRandomSpread = read.f();
        bullet.fragSpread = read.f();
        bullet.fragAngle = read.f();
        bullet.trailRotation = read.bool();
        bullet.splashDamagePierce = read.bool();
        bullet.parts = new Seq<>(readParts(read));
        bullet.trailChance = read.f();
        bullet.trailInterval = read.f();
        bullet.trailParam = read.f();
        bullet.trailLength = read.i();
        bullet.trailWidth = read.f();
        bullet.trailSinMag = read.f();
        bullet.trailSinScl = read.f();
        bullet.splashDamageRadius = read.f();
        bullet.homingPower = read.f();
        bullet.homingRange = read.f();
        bullet.homingDelay = read.f();
        bullet.suppressionRange = read.f();
        bullet.suppressionDuration = read.f();
        bullet.suppressionEffectChance = read.f();
        bullet.weaveScale = read.f();
        bullet.weaveMag = read.f();
        bullet.weaveRandom = read.bool();
        bullet.puddles = read.i();
        bullet.puddleRange = read.f();
        bullet.puddleAmount = read.f();
        bullet.lightRadius = read.f();
        bullet.lightOpacity = read.f();
        if (read.bool()) {
            bullet.puddleLiquid = TypeIO.readLiquid(read);
        }
        bullet.hitColor = TypeIO.readColor(read);
        bullet.healColor = TypeIO.readColor(read);
        bullet.trailColor = TypeIO.readColor(read);
        bullet.lightningColor = TypeIO.readColor(read);
        bullet.lightColor = TypeIO.readColor(read);
        bullet.shootEffect = readEffect(read);
        bullet.despawnEffect = readEffect(read);
        bullet.hitEffect = readEffect(read);
        bullet.chargeEffect = readEffect(read);
        bullet.smokeEffect = readEffect(read);
        bullet.healEffect = readEffect(read);
        bullet.trailEffect = readEffect(read);
        if (read.bool()) {
            bullet.type = read.str();
            bullet.hitUnits = read.bool();
            bullet.haveEmp = read.bool();
            bullet.havePercent = read.bool();
            bullet.continuous = read.bool();
            bullet.largeHit = read.bool();
            bullet.drawFlare = read.bool();
            bullet.rotateFlare = read.bool();
            bullet.percent = read.f();
            bullet.empDamage = read.f();
            bullet.radius = read.f();
            bullet.timeDuration = read.f();
            bullet.timeIncrease = read.f();
            bullet.powerDamageScl = read.f();
            bullet.powerSclDecrease = read.f();
            bullet.unitDamageScl = read.f();
            bullet.width = read.f();
            bullet.height = read.f();
            bullet.shrinkX = read.f();
            bullet.shrinkY = read.f();
            bullet.spin = read.f();
            bullet.rotationOffset = read.f();
            bullet.damageInterval = read.f();
            bullet.shake = read.f();
            bullet.flareLength = read.f();
            bullet.lightStroke = read.f();
            bullet.oscScl = read.f();
            bullet.oscMag = read.f();
            bullet.flareWidth = read.f();
            bullet.flareInnerScl = read.f();
            bullet.flareInnerLenScl = read.f();
            bullet.flareLayer = read.f();
            bullet.flareRotSpeed = read.f();
            bullet.laserCLength = read.f();
            bullet.fadeTime = read.f();
            bullet.strokeFrom = read.f();
            bullet.strokeTo = read.f();
            bullet.pointyScaling = read.f();
            bullet.backLength = read.f();
            bullet.frontLength = read.f();
            bullet.laserLength = read.f();
            bullet.lengthFalloff = read.f();
            bullet.sideLength = read.f();
            bullet.sideWidth = read.f();
            bullet.sideAngle = read.f();
            bullet.lightningSpacing = read.f();
            bullet.lightningDelay = read.f();
            bullet.lightningAngleRand = read.f();
            bullet.trailSpacing = read.f();
            bullet.railLength = read.f();
            bullet.pointEffectSpace = read.f();
            bullet.lengthWidthPans = new float[read.i()];
            for (int i = 0; i < bullet.lengthWidthPans.length; i++) {
                bullet.lengthWidthPans[i] = read.f();
            }
            bullet.divisions = read.i();
            bullet.bulletLightningLength = read.i();
            bullet.bulletLightningLengthRand = read.i();
            bullet.backColor = TypeIO.readColor(read);
            bullet.frontColor = TypeIO.readColor(read);
            bullet.mixColorFrom = TypeIO.readColor(read);
            bullet.mixColorTo = TypeIO.readColor(read);
            bullet.flareColor = TypeIO.readColor(read);
            bullet.bulletLightningColor = TypeIO.readColor(read);
            bullet.colors = new Color[read.i()];
            for (int i = 0; i < bullet.colors.length; i++) {
                bullet.colors[i] = TypeIO.readColor(read);
            }
            bullet.hitPowerEffect = readEffect(read);
            bullet.chainEffect = readEffect(read);
            bullet.applyEffect = readEffect(read);
            bullet.laserEffect = readEffect(read);
            bullet.pierceEffect = readEffect(read);
            bullet.pointEffect = readEffect(read);
            bullet.lineEffect = readEffect(read);
            bullet.endEffect = readEffect(read);
        }
        bullet.init();
        bullet.load();
        return bullet;
    }

    public DrawPart[] readParts(Reads read) {
        int num = read.i();
        DrawPart[] result = new DrawPart[num];
        for (int i = 0; i < num; i++) {
            String type = read.str();
            boolean turretShading = read.bool();
            boolean under = read.bool();
            int weaponIndex = read.i();
            int recoilIndex = read.i();
            switch (type) {
                case "shape" -> {
                    ShapePart s = new ShapePart();
                    s.turretShading = turretShading;
                    s.under = under;
                    s.weaponIndex = weaponIndex;
                    s.recoilIndex = recoilIndex;
                    s.circle = read.bool();
                    s.hollow = read.bool();
                    s.mirror = read.bool();
                    s.x = read.f();
                    s.y = read.f();
                    s.rotation = read.f();
                    s.moveX = read.f();
                    s.moveY = read.f();
                    s.moveRot = read.f();
                    s.sides = read.i();
                    s.radius = read.f();
                    s.radiusTo = read.f();
                    s.stroke = read.f();
                    s.strokeTo = read.f();
                    s.rotateSpeed = read.f();
                    s.layer = read.f();
                    s.layerOffset = read.f();
                    s.color = TypeIO.readColor(read);
                    s.colorTo = TypeIO.readColor(read);
                    result[i] = s;
                }
                case "hover" -> {
                    HoverPart h = new HoverPart();
                    h.turretShading = turretShading;
                    h.under = under;
                    h.weaponIndex = weaponIndex;
                    h.recoilIndex = recoilIndex;
                    h.x = read.f();
                    h.y = read.f();
                    h.rotation = read.f();
                    h.phase = read.f();
                    h.stroke = read.f();
                    h.minStroke = read.f();
                    h.radius = read.f();
                    h.circles = read.i();
                    h.sides = read.i();
                    h.mirror = read.bool();
                    h.layer = read.f();
                    h.layerOffset = read.f();
                    h.color = TypeIO.readColor(read);
                    result[i] = h;
                }
                case "halo" -> {
                    HaloPart h = new HaloPart();
                    h.turretShading = turretShading;
                    h.under = under;
                    h.weaponIndex = weaponIndex;
                    h.recoilIndex = recoilIndex;
                    h.tri = read.bool();
                    h.hollow = read.bool();
                    h.mirror = read.bool();
                    h.radius = read.f();
                    h.sides = read.i();
                    h.radiusTo = read.f();
                    h.shapes = read.i();
                    h.stroke = read.f();
                    h.strokeTo = read.f();
                    h.x = read.f();
                    h.y = read.f();
                    h.shapeRotation = read.f();
                    h.moveX = read.f();
                    h.moveY = read.f();
                    h.shapeMoveRot = read.f();
                    h.haloRotateSpeed = read.f();
                    h.haloRotation = read.f();
                    h.rotateSpeed = read.f();
                    h.triLength = read.f();
                    h.triLengthTo = read.f();
                    h.haloRadius = read.f();
                    h.haloRadiusTo = read.f();
                    h.layer = read.f();
                    h.layerOffset = read.f();
                    h.color = TypeIO.readColor(read);
                    h.colorTo = TypeIO.readColor(read);
                    result[i] = h;
                }
                case "flare" -> {
                    FlarePart f = new FlarePart();
                    f.turretShading = turretShading;
                    f.under = under;
                    f.weaponIndex = weaponIndex;
                    f.recoilIndex = recoilIndex;
                    f.sides = read.i();
                    f.radius = read.f();
                    f.radiusTo = read.f();
                    f.innerScl = read.f();
                    f.innerRadScl = read.f();
                    f.x = read.f();
                    f.y = read.f();
                    f.rotation = read.f();
                    f.rotMove = read.f();
                    f.spinSpeed = read.f();
                    f.layer = read.f();
                    f.followRotation = read.bool();
                    f.color1 = TypeIO.readColor(read);
                    f.color2 = TypeIO.readColor(read);
                    result[i] = f;
                }
            }
        }
        return result;
    }

    public ShootPattern readShoot(Reads read) {
        String type = read.str();
        int shots = read.i();
        float firstShotDelay = read.f();
        float shotDelay = read.f();
        switch (type) {
            case "summon" -> {
                ShootSummon s = new ShootSummon(read.f(), read.f(), read.f(), read.f());
                s.shots = shots;
                s.firstShotDelay = firstShotDelay;
                s.shotDelay = shotDelay;
                return s;
            }
            case "spread" -> {
                ShootSpread s = new ShootSpread();
                s.shots = shots;
                s.firstShotDelay = firstShotDelay;
                s.shotDelay = shotDelay;
                s.spread = read.f();
                return s;
            }
            case "sine" -> {
                ShootSine s = new ShootSine();
                s.shots = shots;
                s.firstShotDelay = firstShotDelay;
                s.shotDelay = shotDelay;
                s.scl = read.f();
                s.mag = read.f();
                return s;
            }
            case "multi" -> {
                ShootMulti m = new ShootMulti();
                m.shots = shots;
                m.firstShotDelay = firstShotDelay;
                m.shotDelay = shotDelay;
                m.source = readShoot(read);
                int num = read.i();
                m.dest = new ShootPattern[num];
                for (int i = 0; i < num; i++) {
                    m.dest[i] = readShoot(read);
                }
                return m;
            }
            case "helix" -> {
                ShootHelix h = new ShootHelix();
                h.shots = shots;
                h.firstShotDelay = firstShotDelay;
                h.shotDelay = shotDelay;
                h.scl = read.f();
                h.mag = read.f();
                h.offset = read.f();
                return h;
            }
            case "barrel" -> {
                ShootBarrel b = new ShootBarrel();
                b.shots = shots;
                b.firstShotDelay = firstShotDelay;
                b.shotDelay = shotDelay;
                b.barrelOffset = read.i();
                int num = read.i();
                b.barrels = new float[num];
                for (int i = 0; i < num; i++) {
                    b.barrels[i] = read.f();
                }
                return b;
            }
            case "alternate" -> {
                ShootAlternate a = new ShootAlternate();
                a.shots = shots;
                a.firstShotDelay = firstShotDelay;
                a.shotDelay = shotDelay;
                a.barrelOffset = read.i();
                a.spread = read.f();
                a.barrels = read.i();
                return a;
            }
            case "pattern" -> {
                ShootPattern p = new ShootPattern();
                p.shots = shots;
                p.firstShotDelay = firstShotDelay;
                p.shotDelay = shotDelay;
                return p;
            }
            default -> {
                return new ShootPattern();
            }
        }
    }

    public Effect readEffect(Reads read) {
        String type = read.str();
        if (type.equals("effect")) {
            return TypeIO.readEffect(read);
        }
        Effect e;
        float lifetime = read.f();
        float clip = read.f();
        float startDelay = read.f();
        float baseRotation = read.f();
        boolean followParent = read.bool();
        boolean rotWithParent = read.bool();
        float layer = read.f();
        float layerDuration = read.f();
        switch (type) {
            case "wave" -> {
                e = new WaveEffect();
                WaveEffect w = (WaveEffect) e;
                w.sizeFrom = read.f();
                w.sizeTo = read.f();
                w.sides = read.i();
                w.lightScl = read.f();
                w.lightOpacity = read.f();
                w.rotation = read.f();
                w.strokeFrom = read.f();
                w.strokeTo = read.f();
                w.offsetX = read.f();
                w.offsetY = read.f();
                w.colorFrom = TypeIO.readColor(read);
                w.colorTo = TypeIO.readColor(read);
                w.lightColor = TypeIO.readColor(read);
            }
            case "wrap" -> {
                e = new WrapEffect();
                WrapEffect w = (WrapEffect) e;
                w.rotation = read.f();
                w.effect = readEffect(read);
                w.color = TypeIO.readColor(read);
            }
            case "radial" -> {
                e = new RadialEffect();
                RadialEffect r = (RadialEffect) e;
                r.rotationSpacing = read.f();
                r.rotationOffset = read.f();
                r.lengthOffset = read.f();
                r.amount = read.i();
                r.effect = readEffect(read);
            }
            case "particle" -> {
                e = new ParticleEffect();
                ParticleEffect p = (ParticleEffect) e;
                p.particles = read.i();
                p.randLength = read.bool();
                p.casingFlip = read.bool();
                p.cone = read.f();
                p.length = read.f();
                p.baseLength = read.f();
                p.offsetX = read.f();
                p.offsetY = read.f();
                p.lightScl = read.f();
                p.strokeFrom = read.f();
                p.strokeTo = read.f();
                p.lenFrom = read.f();
                p.lenTo = read.f();
                p.line = read.bool();
                p.cap = read.bool();
                p.lightOpacity = read.f();
                p.colorFrom = TypeIO.readColor(read);
                p.colorTo = TypeIO.readColor(read);
                p.lightColor = TypeIO.readColor(read);
            }
            default -> {
                e = new ExplosionEffect();
                ExplosionEffect ex = (ExplosionEffect) e;
                ex.waveLife = read.f();
                ex.waveStroke = read.f();
                ex.waveRad = read.f();
                ex.waveRadBase = read.f();
                ex.sparkStroke = read.f();
                ex.sparkRad = read.f();
                ex.sparkLen = read.f();
                ex.smokeSize = read.f();
                ex.smokeSizeBase = read.f();
                ex.smokeRad = read.f();
                ex.smokes = read.i();
                ex.sparks = read.i();
                ex.waveColor = TypeIO.readColor(read);
                ex.smokeColor = TypeIO.readColor(read);
                ex.sparkColor = TypeIO.readColor(read);
            }
        }
        e.lifetime = lifetime;
        e.clip = clip;
        e.startDelay = startDelay;
        e.baseRotation = baseRotation;
        e.followParent = followParent;
        e.rotWithParent = rotWithParent;
        e.layer = layer;
        e.layerDuration = layerDuration;
        return e;
    }

    public class weaponPack {
        public Table on;
        public Weapon weapon;
        public WeaponDialog dialog;
        public float heavy;

        public weaponPack() {
            weapon = new Weapon();
            weapon.reload = 500;
            weapon.targetSwitchInterval = weapon.targetInterval = 500;
            dialog = new WeaponDialog("", weapon, w -> weapon = w, f -> heavy = f);
            //dialog.shown(ProjectsLocated.this::hideTables);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = 0.5f + dialog.bulletHeavy;
        }

        public weaponPack(Weapon weapon) {
            this.weapon = weapon;
            dialog = new WeaponDialog("", this.weapon, w -> this.weapon = w, f -> heavy = f);
            //dialog.shown(ProjectsLocated.this::hideTables);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = dialog.heavy + getShootVal(weapon.shoot) * dialog.bulletHeavy;
        }
    }

    public class abilityPack {
        public Table on;
        public Ability ability;
        public AbilityDialog dialog;
        public float heavy;

        public abilityPack() {
            ability = new ForceFieldAbility(90, 0, 0, Float.MAX_VALUE, 0, 0);
            dialog = new AbilityDialog("", () -> ability, a -> ability = a, f -> heavy = f);
            //dialog.shown(ProjectsLocated.this::hideTables);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = dialog.heavy;
        }

        public abilityPack(Ability ability) {
            this.ability = ability;
            dialog = new AbilityDialog("", () -> this.ability, a -> this.ability = a, f -> heavy = f);
            //dialog.shown(ProjectsLocated.this::hideTables);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = dialog.heavy + dialog.bulletHeavy;
        }
    }
}