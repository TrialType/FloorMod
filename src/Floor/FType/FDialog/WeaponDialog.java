package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

public class WeaponDialog extends BaseDialog {
    public Weapon weapon;
    public BulletDialog bulletDialog;
    public LimitBulletType bullet;
    public float bulletHeavy = 0;
    public float heavy = 0;
    public String type = "default";
    public Table baseOn;
    public Table typeOn;

    public WeaponDialog(String title) {
        super(title);

        weapon = new Weapon();
        bulletDialog = new BulletDialog(this, "");
        bulletDialog.hidden(() -> ProjectsLocated.freeSize += this.heavy);
        bullet = new LimitBulletType();
        buttons.button("@back", Icon.left, () -> {

        });
        buttons.button("@apply", Icon.right, () -> {

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
    }
}
