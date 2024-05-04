package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import mindustry.ui.dialogs.BaseDialog;

public class WeaponDialog extends BaseDialog {
    public LimitBulletType bullet;

    public WeaponDialog(String title, DialogStyle style) {
        super(title, style);
    }

    public WeaponDialog(String title) {
        super(title);
    }
}
