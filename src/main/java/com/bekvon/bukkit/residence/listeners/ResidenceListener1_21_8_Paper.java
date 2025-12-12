package com.bekvon.bukkit.residence.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.containers.ResAdmin;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.FlagPermissions.FlagCombo;
import com.bekvon.bukkit.residence.utils.Utils;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;

public class ResidenceListener1_21_8_Paper implements Listener {

    private Residence plugin;

    public ResidenceListener1_21_8_Paper(Residence plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onKnockbackTest(EntityKnockbackEvent event) {

        if (event.getCause() != io.papermc.paper.event.entity.EntityKnockbackEvent.Cause.EXPLOSION)
            return;

        Entity entity =  event.getEntity();
        Location loc = entity.getLocation();

        if (Utils.isAnimal(entity)) {
            if (FlagPermissions.has(loc, Flags.animalkilling, FlagCombo.OnlyFalse))
                event.setCancelled(true);

        } else if (ResidenceEntityListener.isMonster(entity)) {
            if (FlagPermissions.has(loc, Flags.mobkilling, FlagCombo.OnlyFalse))
                event.setCancelled(true);

        } else if (entity instanceof Player) {
            if (FlagPermissions.has(loc, Flags.pvp, FlagCombo.OnlyFalse))
                event.setCancelled(true);

        } else if (entity instanceof Boat || entity instanceof Minecart) {
            if (FlagPermissions.has(loc, Flags.vehicledestroy, FlagCombo.OnlyFalse))
                event.setCancelled(true);

        } else if (entity.getType().equals(EntityType.ARMOR_STAND)) {
            if (FlagPermissions.has(loc, Flags.destroy, FlagCombo.OnlyFalse))
                event.setCancelled(true);

        }
    }

    @EventHandler
    public void onKnockback(EntityPushedByEntityAttackEvent event) {

        if (shouldCancelKnockBack(event.getEntity(), event.getPushedBy()))
            event.setCancelled(true);
    }

    public static boolean shouldCancelKnockBack(Entity entity, Entity pushedBy) {
        Location loc = entity.getLocation();

        Player player = Utils.potentialProjectileToPlayer(pushedBy);

        if (Utils.isAnimal(entity))
            return flagCheck(loc, player, Flags.animalkilling);

        if (ResidenceEntityListener.isMonster(entity))
            return flagCheck(loc, player, Flags.mobkilling);

        if (entity instanceof Player) {
            if (FlagPermissions.has(loc, Flags.pvp, FlagCombo.OnlyFalse))
                return true;
            return false;
        }

        if (entity instanceof Boat || entity instanceof Minecart)
            return flagCheck(loc, player, Flags.vehicledestroy);

        if (entity.getType().equals(EntityType.ARMOR_STAND))
            return flagCheck(loc, player, Flags.destroy);

        return false;
    }

    private static boolean flagCheck(Location loc, Player pushedBy, Flags flag) {
        if (pushedBy != null) {
            if (ResAdmin.isResAdmin(pushedBy))
                return false;
            if (FlagPermissions.has(loc, pushedBy, flag, FlagCombo.OnlyFalse))
                return true;
        } else {
            if (FlagPermissions.has(loc, flag, FlagCombo.OnlyFalse))
                return true;
        }
        return false;
    }
}
