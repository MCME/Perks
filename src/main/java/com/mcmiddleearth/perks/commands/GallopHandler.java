/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.Perk;
import java.util.logging.Logger;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class GallopHandler extends PerksCommandHandler {

    private static double speedFactor = PerksPlugin.getPerkDouble("gallop", "speedFactor", 2);
    
    public GallopHandler(Perk perk, String... permissionNodes) {
        super(0, true, perk, permissionNodes);
    }

    @Override
    public String getShortDescription(String subcommand) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Giddyap! Make your horse gallop.";
    }

    @Override
    public String getUsageDescription(String subcommand) {
        return ": You need to ride a horse before using this command. It will make your horse gallop.";
    }

    @Override
    protected void execute(CommandSender cs, final String cmd, String... args) {
        final Player player = (Player)cs;
        if (!player.isInsideVehicle()
                || !(player.getVehicle() instanceof Horse)) {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You are not mounted on a horse!");
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                AttributeInstance attrib = ((Horse)player.getVehicle()).getAttribute(Attribute.MOVEMENT_SPEED);
                AttributeModifier mod = new AttributeModifier(player.getUniqueId(),
                                                            "Perks Gallop",speedFactor,
                                                            AttributeModifier.Operation.MULTIPLY_SCALAR_1);
                if(cmd.equals("gallop")) {
                    if(!attrib.getModifiers().contains(mod)) {
                        attrib.addModifier(mod);
                    }
                } else if(cmd.equals("whoa")) {
                    attrib.removeModifier(mod);
                }
            }
        }.runTaskLater(PerksPlugin.getInstance(),20);

    }
    
}
