package ted_2001.testweight;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Weight {

    double weight;
    Player p;

    Inventory inv;

    public Weight(Inventory Inv, Player p,double weight) {
        this.inv = inv;
        this.p = p;
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Player getP() {
        return p;
    }




}
