package Floor.FEntities.FUnit.Geodetic;

import Floor.FAI.GeodeticAI.XuAI;
import Floor.FContent.FEvents;
import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Events;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.PowerModule;

public class XuGou extends FLegsUnit {
    public ItemSeq items = new ItemSeq();
    public float power = 0;

    @Override
    public int classId() {
        return 131;
    }

    public static XuGou create() {
        return new XuGou();
    }

    @Override
    public void update() {
        if (hitTime == 1 && !this.dead && health > 0) {
            for (int i = 1; i <= 2; i++) {
                Unit u = type.create(team);
                u.maxHealth = this.health;
                u.heal();
                u.set((float) (x + hitSize * Math.cos(Math.toRadians(180 * i))),
                        (float) (y + hitSize * Math.sin(Math.toRadians(180 * i))));
                u.rotation(180 * i);
                u.add();
            }
        }

        super.update();

        if (controller instanceof XuAI xa) {
            if (xa.takeTarget != null) {
                Teamc t = xa.takeTarget;
                if (within(t, 8)) {
                    if (t instanceof Unit u) {
                        ItemStack is;
                        if ((is = u.stack) != null && !(is.amount == 0)) {
                            int number = Math.min(100, is.amount);
                            is.amount -= number;
                            items.add(is.item, number);
                        }
                    } else if (t instanceof Building b) {
                        ItemModule im;
                        if ((im = b.items) != null && !im.empty()) {
                            int number = Math.min(100, im.total());
                            Seq<ItemStack> cons = new Seq<>();
                            final int[] num = {number};
                            im.each((item, n) -> {
                                if (num[0] > 0) {
                                    if (num[0] >= n) {
                                        cons.add(new ItemStack(item, n));
                                        num[0] = num[0] - n;
                                    } else {
                                        cons.add(new ItemStack(item, num[0]));
                                        num[0] = 0;
                                    }
                                }
                            });
                            for (ItemStack its : cons) {
                                items.add(its.item, its.amount);
                                im.remove(its.item, its.amount);
                            }
                        }

                        PowerModule pm;
                        if ((pm = b.power) != null && pm.status > 0) {
                            float num = Math.min(0.5f, pm.status);
                            pm.status -= num;
                            power += num * pm.graph.getBatteryCapacity();
                        }
                    }
                }
            }
        }

        float value = power + 3 * items.total;
        if (value > 1000) {
            this.maxHealth *= 2;
            heal();
            Units.nearbyEnemies(team, x, y, 200, u -> {
                Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
                u.kill();
            });
            Units.nearbyBuildings(x, y, 200, b -> {
                if (b.team != team) {
                    b.kill();
                }
            });

            if (power >= 1000) {
                power -= 1000;
            } else {
                int need = (int) (1000 - power);
                Seq<ItemStack> its = new Seq<>();
                for (ItemStack is : items.toSeq()) {
                    int num = is.amount;
                    if (need > 0) {
                        if (num >= need) {
                            num -= need;
                            need = 0;
                        } else {
                            need -= num;
                            num = 0;
                        }
                    }
                    its.add(new ItemStack(is.item, num));
                }
                items = new ItemSeq(its);
                power = 0;
            }
        }
    }

    @Override
    public void write(Writes write) {
        super.write(write);

        write.f(power);
        for (ItemStack is : items.toSeq()) {
            write.i(is.item.id);
            write.i(is.amount);
        }
    }

    @Override
    public void read(Reads read) {
        super.read(read);

        power = read.f();
        for (int i = 0; i < Vars.content.items().size; i++) {
            items.add(Vars.content.item(read.i()), read.i());
        }
    }
}
