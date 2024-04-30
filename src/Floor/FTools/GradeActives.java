package Floor.FTools;

import Floor.FContent.FItems;
import mindustry.gen.Unit;
import mindustry.type.Item;

public class GradeActives {
    public static int[] allProLev = new int[12];
    public final static active speed = u -> {
    }, health = u -> {
    }, copper = u -> {
    }, laser = u -> {
    }, reload = u -> {
    }, shield = u -> {
    }, splash = u -> {
    }, prices = u -> {
    }, slow = u -> {
    }, knock = u -> {
    }, percent = u -> {
    };

    private GradeActives() {
    }

    public static void updateProjects() {
        Item[] items;
        for (int i = 0; i < FItems.allProject.length - 1; i++) {
            items = FItems.allProject[i];
            for (int j = items.length; j > 0; j--) {
                if (items[j - 1].unlocked()) {
                    allProLev[i] = j;
                    break;
                } else if (j == 1) {
                    allProLev[i] = 0;
                    break;
                }
            }
        }
    }

    public interface active {
        void get(Unit unit);
    }
}
