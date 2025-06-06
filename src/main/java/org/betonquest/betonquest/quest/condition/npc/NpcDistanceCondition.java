package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Checks if the player is close to a npc.
 */
public class NpcDistanceCondition implements OnlineCondition {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Id of the npc.
     */
    private final Variable<NpcID> npcID;

    /**
     * The maximal distance between player and Npc.
     */
    private final Variable<Number> distance;

    /**
     * Create a new Npc Distance Condition.
     *
     * @param featureAPI the Feature API
     * @param npcID      the id of the npc
     * @param distance   the maximal distance between player and npc
     */
    public NpcDistanceCondition(final FeatureAPI featureAPI, final Variable<NpcID> npcID, final Variable<Number> distance) {
        this.featureAPI = featureAPI;
        this.npcID = npcID;
        this.distance = distance;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Location npcLocation = featureAPI.getNpc(npcID.getValue(profile), profile).getLocation();
        final Player player = profile.getPlayer();
        if (!player.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double distance = this.distance.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(player.getLocation()) <= distance * distance;
    }
}
