package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Icon;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

public class WeaponDialog extends BaseDialog {
    public Weapon weapon;
    public ProjectsLocated.weaponPack pack;
    public BulletDialog bulletDialog;
    public LimitBulletType bullet;
    public float bulletHeavy = 0;
    public float heavy = 0;
    public String type = "default";
    public Table baseOn;
    public Table typeOn;

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
    }

    public void rebuild() {
        rebuildBase();
        rebuildType();
    }

    public void rebuildBase() {
        cont.pane(t -> {
            baseOn = t;
            t.label(() -> Core.bundle.get("dialog.weapon.bullet")).width(160);
            t.button(Icon.pencilSmall, () -> {
                ProjectsLocated.freeSize -= this.heavy;
                bulletDialog.show();
            }).size(15).pad(15);
            t.row();
        }).grow();
    }

    public void rebuildType() {
    }

    public void updateHeavy() {
        heavy = 0.5f;
        heavy += ProjectsLocated.getHeavy("number", weapon.shoot.shots);
        heavy += ProjectsLocated.getHeavy("reload", weapon.reload);
        heavy += ProjectsLocated.getHeavy("target", weapon.targetInterval * weapon.targetSwitchInterval);
    }
}
