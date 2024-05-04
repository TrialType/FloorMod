package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import mindustry.ui.dialogs.BaseDialog;

public class WeaponDialog extends BaseDialog {
    public LimitBulletType bullet;
    public float bulletHeavy = 0;
    public float heavy = 0;
    public WeaponDialog(String title) {
        super(title);
    }
}
