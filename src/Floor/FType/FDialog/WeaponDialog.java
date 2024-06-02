package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.DialogUtils.*;

public class WeaponDialog extends BaseDialog implements TableGetter {
    public Weapon weapon;
    protected ProjectsLocated.weaponPack pack;
    protected BulletDialog bulletDialog;
    protected LimitBulletType bullet;
    protected float bulletHeavy = 0;
    protected float heavy = 0;
    protected String type = "default";
    protected Table baseOn;
    protected Table typeOn;
    protected Table effectOn;


    public Runnable re = () -> {
        rebuildType();
        rebuildBase();
    };
    public static String dia = "weapon";
    public StrBool levUser = str -> ProjectsLocated.couldUse(str, getVal(str));
    public BoolGetter hevUser = () -> weapon.shoot.shots * bulletHeavy + heavy <= ProjectsLocated.freeSize;

    public WeaponDialog(String title, ProjectsLocated.weaponPack pack) {
        super(title);

        this.pack = pack;
        weapon = pack.weapon == null ? new Weapon() : pack.weapon;
        updateHeavy();
        bulletDialog = new BulletDialog(this, "");
        bulletDialog.hidden(() -> ProjectsLocated.freeSize += this.heavy);
        bullet = new LimitBulletType();
        buttons.button("@back", Icon.left, this::hide);
        buttons.button("@apply", Icon.right, () -> {
            pack.weapon = this.weapon;
            pack.heavy = this.heavy + this.bulletHeavy;
            hide();
        });
        buttons.button("@setZero", () -> {
            weapon.reload = Float.MAX_VALUE;
            weapon.shoot.shots = 0;
            weapon.targetSwitchInterval = weapon.targetInterval = Float.MAX_VALUE;
            rebuild();
        });
        shown(this::rebuild);
        hidden(() -> pack.weapon = this.weapon);
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> baseOn = t);
        cont.pane(t -> typeOn = t);
        rebuildBase();
        rebuildType();
    }

    public void rebuildBase() {
        baseOn.clear();
        baseOn.label(() -> Core.bundle.get("dialog.weapon.bullet")).width(160);
        baseOn.button(Icon.pencilSmall, () -> {
            ProjectsLocated.freeSize -= this.heavy;
            bulletDialog.show();
        }).size(15).pad(15);
        baseOn.row();
        createNumberDialog(baseOn, dia, "x", weapon.x, f -> weapon.x = f, re);
        createNumberDialog(baseOn, dia, "y", weapon.y, f -> weapon.y = f, re);
        createNumberDialog(baseOn, dia, "shootY", weapon.shootY, f -> weapon.shootY = f, re);
        baseOn.row();
        createNumberDialog(baseOn, dia, "shootX", weapon.shootX, f -> weapon.shootX = f, re);
        createNumberDialog(baseOn, dia, "shootCone", weapon.shootCone, f -> weapon.shootCone = f, re);
        createBooleanDialog(baseOn, dia, "rotate", weapon.rotate, b -> weapon.rotate = b, re);
        baseOn.row();
        createNumberDialog(baseOn, dia, "rotateSpeed", weapon.rotateSpeed, f -> weapon.rotateSpeed = f, re);
        createNumberDialog(baseOn, dia, "rotationLimit", weapon.rotationLimit, f -> weapon.rotationLimit = f, re);
        createNumberDialog(baseOn, dia, "baseRotation", weapon.baseRotation, f -> weapon.baseRotation = f, re);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "mirror", weapon.mirror, b -> weapon.mirror = b, re);
        createBooleanDialog(baseOn, dia, "alternate", weapon.alternate, b -> weapon.alternate = b, re);
        createBooleanDialog(baseOn, dia, "continuous", weapon.continuous, b -> weapon.continuous = b, re);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "alwaysContinuous", weapon.alwaysContinuous, b -> weapon.alwaysContinuous = b, re);
        createBooleanDialog(baseOn, dia, "controllable", weapon.controllable, b -> weapon.controllable = b, re);
        createBooleanDialog(baseOn, dia, "aiControllable", weapon.aiControllable, b -> weapon.aiControllable = b, re);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "alwaysShooting", weapon.alwaysShooting, b -> weapon.alwaysShooting = b, re);
        createBooleanDialog(baseOn, dia, "autoTarget", weapon.autoTarget, b -> weapon.autoTarget = b, re);
        createBooleanDialog(baseOn, dia, "predictTarget", weapon.predictTarget, b -> weapon.predictTarget = b, re);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "useAttackRange", weapon.useAttackRange, b -> weapon.useAttackRange = b, re);
        createLevDialog(baseOn, dia, "target", "targetInterval", weapon.targetInterval,
                f -> weapon.targetInterval = f, re, this::updateHeavy, levUser, hevUser);
        createLevDialog(baseOn, dia, "target", "targetSwitchInterval", weapon.targetSwitchInterval,
                f -> weapon.targetSwitchInterval = f, re, this::updateHeavy, levUser, hevUser);
        baseOn.row();
        createLevDialog(baseOn, dia, "reload", "reload", weapon.reload,
                f -> weapon.reload = f, re, this::updateHeavy, levUser, hevUser);
        createNumberDialog(baseOn, dia, "inaccuracy", weapon.inaccuracy, f -> weapon.inaccuracy = f, re);
        createNumberDialog(baseOn, dia, "inaccuracy", weapon.shake, f -> weapon.shake = f, re);
        baseOn.row();
        createNumberDialog(baseOn, dia, "recoil", weapon.recoil, f -> weapon.recoil = f, re);
        createNumberDialog(baseOn, dia, "recoils", weapon.recoils, f -> weapon.recoils = (int) (f + 0), re);
        createNumberDialog(baseOn, dia, "recoilTime", weapon.recoilTime, f -> weapon.recoilTime = f, re);
        baseOn.row();
        createNumberDialog(baseOn, dia, "recoilPow", weapon.recoilPow, f -> weapon.recoilPow = f, re);
        createNumberDialogWithLimit(baseOn, dia, "xRand", weapon.xRand, 25, 0, f -> weapon.xRand = f, re);
        createNumberDialogWithLimit(baseOn, dia, "rotationLimit", weapon.rotationLimit, 361, 0, f -> weapon.rotationLimit = f, re);
        baseOn.row();
        createNumberDialogWithLimit(baseOn, dia, "minWarmup", weapon.minWarmup, 0.99f, 0, f -> weapon.minWarmup = f, re);
        createNumberDialogWithLimit(baseOn, dia, "shootWarmupSpeed", weapon.shootWarmupSpeed, 0.99f, 0, f -> weapon.shootWarmupSpeed = f, re);
        createNumberDialogWithLimit(baseOn, dia, "smoothReloadSpeed", weapon.smoothReloadSpeed, 0.99f, 0, f -> weapon.smoothReloadSpeed = f, re);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "ignoreRotation", weapon.ignoreRotation, b -> weapon.ignoreRotation = b, re);
        createBooleanDialog(baseOn, dia, "noAttack", weapon.noAttack, b -> weapon.noAttack = b, re);
        createBooleanDialog(baseOn, dia, "linearWarmup", weapon.linearWarmup, b -> weapon.linearWarmup = b, re);
    }

    public void rebuildType() {
    }

    public float getVal(String type) {
        return switch (type) {
            case "number" -> weapon.shoot.shots;
            case "reload" -> weapon.reload;
            case "target" -> weapon.targetInterval * weapon.targetSwitchInterval;
            default -> -1;
        };
    }

    public void updateHeavy() {
        heavy = 0.5f;
        heavy += ProjectsLocated.getHeavy("number", weapon.shoot.shots);
        heavy += ProjectsLocated.getHeavy("reload", weapon.reload);
        heavy += ProjectsLocated.getHeavy("target", weapon.targetInterval * weapon.targetSwitchInterval);
    }

    @Override
    public Table get() {
        return effectOn;
    }

    @Override
    public void set(Table table) {
        effectOn = table;
    }
}
