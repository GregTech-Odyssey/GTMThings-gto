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

    private static Optional<Team> getTeam(UUID playerUUID) {
        if (!isFTBTeamsLoaded) {
            return Optional.empty();
        }
        if (FTBTeamsAPI.api().isManagerLoaded()) {
            return FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUUID);
        }
        if (FTBTeamsAPI.api().isClientManagerLoaded()) {
            return FTBTeamsAPI.api().getClientManager().getTeams().stream()
                    .filter(team -> team.getMembers().contains(playerUUID))
                    .filter(Team::isPartyTeam)
                    .findFirst();
        }
        return Optional.empty();
    }

    /**
     * Returns the FTB Teams party id for the player when teams are available, or the
     * player's own UUID when teams are not loaded or no party team exists.
     */
    public static UUID getTeamUUID(UUID playerUUID) {
        return getTeam(playerUUID).map(Team::getTeamId).orElse(playerUUID);
    }

    /**
     * Returns the display name of the player's FTB Teams party when available,
     * otherwise falls back to the player's own display name.
     */
    public static Component getName(Player player) {
        return getTeam(player.getUUID()).map(Team::getName).orElseGet(player::getName);
    }

    /**
     * Returns the display name for the UUID's FTB Teams party, online player, or the
     * UUID text when neither can be resolved.
     */
    public static Component getName(Level level, UUID playerUUID) {
        var team = getTeam(playerUUID);
        if (team.isPresent()) {
            return team.get().getName();
        }

        Player player = level.getPlayerByUUID(playerUUID);
        if (player != null) return player.getName();
        return Component.literal(playerUUID.toString());
    }

    /**
     * @deprecated use {@link #getName(Player)}.
     */
    @Deprecated(forRemoval = false)
    public static Component GetName(Player player) {
        return getName(player);
    }

    /**
     * @deprecated use {@link #getName(Level, UUID)}.
     */
    @Deprecated(forRemoval = false)
    public static Component GetName(Level level, UUID playerUUID) {
        return getName(level, playerUUID);
    }

    /**
     * Returns true when the UUID can be resolved to an FTB Teams party or an online player.
     */
    public static boolean hasOwner(Level level, UUID playerUUID) {
        return getTeam(playerUUID).isPresent() || level.getPlayerByUUID(playerUUID) != null;
    }
}
