package Floor.FType.FStatusEffect;

import Floor.FTools.HighChange;
import mindustry.type.StatusEffect;

public class HighChangeStatus extends StatusEffect implements HighChange {
    public float damageTo = 1;
    public float speedTo = 1;
    public float reloadTo = 1;
    public float healthTo = 1;
    public float buildTo = 1;
    public float dargTo = 1;
    public HighChangeStatus(String name) {
        super(name);
    }

    @Override
    public float damageTo() {
        return damageTo;
    }

    @Override
    public float speedTo() {
        return speedTo;
    }

    @Override
    public float reloadTo() {
        return reloadTo;
    }

    @Override
    public float healthTo() {
        return healthTo;
    }

    @Override
    public float buildTo() {
        return buildTo;
    }

    @Override
    public float dargTo() {
        return dargTo;
    }
}
