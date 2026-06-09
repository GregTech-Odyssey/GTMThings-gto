package com.hepdd.gtmthings.utils;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;

import java.util.Optional;
import java.util.UUID;

public class TeamUtil {

    private static final boolean isFTBTeamsLoaded = GTCEu.isModLoaded("ftbteams");

    /**
     * Returns the shared team UUID when FTB Teams is available, otherwise the player UUID.
     */
    public static UUID getTeamUUID(UUID playerUUID) {
        if (isFTBTeamsLoaded && FTBTeamsAPI.api().isManagerLoaded()) {
            var team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUUID);
            return team.map(Team::getTeamId).orElse(playerUUID);
        } else if (isFTBTeamsLoaded && FTBTeamsAPI.api().isClientManagerLoaded()) {
            // Multiplayer client-side
            var team = FTBTeamsAPI.api().getClientManager().getTeams().stream().filter(
                    t -> t.getMembers().contains(playerUUID)).findFirst();
            if (team.isPresent() && team.get().isPartyTeam()) {
                return team.get().getTeamId();
            }
        }

        return playerUUID;
    }

    /**
     * Returns the display name for the player's team when available, otherwise the player name.
     */
    public static Component getName(Player player) {
        if (isFTBTeamsLoaded && FTBTeamsAPI.api().isManagerLoaded()) {
            Optional<Team> team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(player.getUUID());
            if (team.isPresent()) return team.get().getName();
        } else if (isFTBTeamsLoaded && FTBTeamsAPI.api().isClientManagerLoaded()) {
            // Multiplayer client-side
            var team = FTBTeamsAPI.api().getClientManager().getTeams().stream().filter(
                    t -> t.getMembers().contains(player.getUUID())).findFirst();
            if (team.isPresent() && team.get().isPartyTeam()) {
                return team.get().getName();
            }
        }

        return player.getName();
    }

    /**
     * Returns the display name for a team/player UUID in the supplied level.
     */
    public static Component getName(Level level, UUID playerUUID) {
        if (isFTBTeamsLoaded && FTBTeamsAPI.api().isManagerLoaded()) {
            var team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUUID);
            if (team.isPresent()) {
                return team.get().getName();
            }
        } else if (isFTBTeamsLoaded && FTBTeamsAPI.api().isClientManagerLoaded()) {
            // Multiplayer client-side
            var team = FTBTeamsAPI.api().getClientManager().getTeams().stream().filter(
                    t -> t.getMembers().contains(playerUUID)).findFirst();
            if (team.isPresent() && team.get().isPartyTeam()) {
                return team.get().getName();
            }
        }

        Player player = level.getPlayerByUUID(playerUUID);
        if (player != null) return player.getName();
        return Component.literal(playerUUID.toString());
    }

    /**
     * Returns true when the UUID resolves to a loaded team or an online player.
     */
    public static boolean hasOwner(Level level, UUID playerUUID) {
        if (isFTBTeamsLoaded && FTBTeamsAPI.api().isManagerLoaded()) {
            var team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUUID);
            if (team.isPresent()) {
                return true;
            }
        } else if (isFTBTeamsLoaded && FTBTeamsAPI.api().isClientManagerLoaded()) {
            // Multiplayer client-side
            var team = FTBTeamsAPI.api().getClientManager().getTeams().stream().filter(
                    t -> t.getMembers().contains(playerUUID)).findFirst();
            if (team.isPresent() && team.get().isPartyTeam()) {
                return true;
            }
        }

        return (level.getPlayerByUUID(playerUUID) != null);
    }

    @Deprecated(forRemoval = false)
    public static Component GetName(Player player) {
        return getName(player);
    }

    @Deprecated(forRemoval = false)
    public static Component GetName(Level level, UUID playerUUID) {
        return getName(level, playerUUID);
    }
}
