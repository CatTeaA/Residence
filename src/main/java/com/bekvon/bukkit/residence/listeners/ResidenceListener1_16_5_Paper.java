package com.bekvon.bukkit.residence.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.containers.lm;
import com.bekvon.bukkit.residence.containers.ResAdmin;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.utils.Utils;

import io.papermc.paper.event.block.TargetHitEvent;

public class ResidenceListener1_16_5_Paper implements Listener {

    private Residence plugin;

    public ResidenceListener1_16_5_Paper(Residence plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHitTargetBlock(TargetHitEvent event) {
        // Disabling listener if flag disabled globally
        if (!Flags.use.isGlobalyEnabled())
            return;

        Block block = event.getHitBlock();
        if (block == null)
            return;

        if (plugin.isDisabledWorldListener(block.getWorld()))
            return;

        if (ProjectileHitBlock(event.getEntity(), block)) {
            event.setCancelled(true);
        }
    }

    public static boolean ProjectileHitBlock(Entity entity, Block block) {
        Player player = Utils.potentialProjectileToPlayer(entity);
        if (player != null) {

            if (ResAdmin.isResAdmin(player))
                return false;

            if (FlagPermissions.has(block.getLocation(), player, Flags.use, true))
                return false;

            lm.Flag_Deny.sendMessage(entity, Flags.use);

        } else {
            // Entity not player source
            // Check potential block as a shooter which should be allowed if its inside same
            // residence
            if (Utils.isSourceBlockInsideSameResidence(entity, ClaimedResidence.getByLoc(block.getLocation())))
                return false;

            if (FlagPermissions.has(block.getLocation(), Flags.use, true))
                return false;

        }
        return true;
    }
}
