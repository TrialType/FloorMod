package Floor.FType.FDialog;

import arc.func.Cons;
import mindustry.entities.Effect;
import mindustry.ui.dialogs.BaseDialog;

public class EffectDialog extends BaseDialog {
    public Effect effect;
    protected Cons<Effect> apply;

    public EffectDialog(String title, Cons<Effect> apply) {
        super(title);

        this.apply = apply;

    }
}
