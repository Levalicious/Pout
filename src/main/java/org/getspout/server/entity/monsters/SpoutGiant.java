package org.getspout.server.entity.monsters;

import org.bukkit.entity.Giant;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;

import java.util.List;

public class SpoutGiant extends SpoutZombie implements Giant {

    public SpoutGiant(SpoutServer server, SpoutWorld world) {
        super(server, world, 53);
    }

    public boolean isStupid() {
        return true;
    }
}
