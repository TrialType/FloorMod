package Floor.FType.FDialog;

import mindustry.entities.abilities.Ability;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class AbilityDialog extends BaseDialog {
    public Ability ability;
    public ProjectsLocated.abilityPack pack;
    public float heavy;

    public AbilityDialog(String title, ProjectsLocated.abilityPack pack) {
        super(title);
        this.pack = pack;

        buttons.button("@back", Icon.left, () -> {

        });
        buttons.button("@apply", Icon.right, () -> {
            pack.ability = this.ability;
            hide();
        });
    }
}
