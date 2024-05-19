package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
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

    public WeaponDialog(String title) {
        super(title);

        weapon = new Weapon();
        bulletDialog = new BulletDialog(this, "");
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

    }

    public void rebuildType() {
    }

    public void updateHeavy() {
        heavy = 0.5f;

    }
}
