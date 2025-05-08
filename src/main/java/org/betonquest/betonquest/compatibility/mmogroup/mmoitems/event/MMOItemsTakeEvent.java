package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.event;

import net.Indyuce.mmoitems.api.Type;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.quest.event.take.AbstractTakeEvent;
import org.betonquest.betonquest.quest.event.take.CheckType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Removes MMOItems from player's inventory and/or backpack
 */
public class MMOItemsTakeEvent extends AbstractTakeEvent {

    /**
     * The type of the MMO item to be removed.
     */
    private final Variable<Type> itemType;

    /**
     * The ID of the MMO item to be removed.
     */
    private final Variable<String> itemID;

    /**
     * The variable number representing the amount of items to delete.
     */
    private final Variable<Number> deleteAmountVar;

    /**
     * A map to keep track of the needed deletions for each player.
     */
    private final Map<UUID, Integer> neededDeletions = new ConcurrentHashMap<>();

    /**
     * Constructs a new MMOItemsTakeEvent.
     *
     * @param itemType           the type of the MMO item
     * @param itemID             the ID of the MMO item
     * @param deleteAmountVar    the variable number representing the amount of items to delete
     * @param checkOrder         the order in which the checks should be performed
     * @param notificationSender the notification sender to use
     */
    public MMOItemsTakeEvent(final Variable<Type> itemType, final Variable<String> itemID, final Variable<Number> deleteAmountVar, final List<CheckType> checkOrder, final NotificationSender notificationSender) {
        super(checkOrder, notificationSender);
        this.itemType = itemType;
        this.itemID = itemID;
        this.deleteAmountVar = deleteAmountVar;
    }

    /**
     * Executes the event, removing the specified amount of items from the player's inventory and/or backpack.
     *
     * @param profile the profile of the player
     * @throws QuestException if an error occurs during execution
     */
    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final int deleteAmount = deleteAmountVar.getValue(profile).intValue();
        neededDeletions.put(profile.getProfileUUID(), deleteAmount);

        checkSelectedTypes(profile);

        final ItemStack item = MMOItemsUtils.getMMOItemStack(itemType.getValue(profile), itemID.getValue(profile));
        final String itemName = item.getItemMeta().getDisplayName();
        notificationSender.sendNotification(profile,
                new PluginMessage.Replacement("item", Component.text(itemName)),
                new PluginMessage.Replacement("amount", Component.text(deleteAmount - neededDeletions.get(profile.getProfileUUID()))));
    }

    /**
     * Takes the desired amount of items from the player's inventory and/or backpack.
     *
     * @param profile the profile of the player
     * @param items   the items to take from
     * @return the remaining items after taking the desired amount
     */
    @Override
    protected ItemStack[] takeDesiredAmount(final Profile profile, final ItemStack... items) throws QuestException {
        int desiredDeletions = Objects.requireNonNull(neededDeletions.get(profile.getProfileUUID()));

        for (int i = 0; i < items.length && desiredDeletions > 0; i++) {
            final ItemStack item = items[i];
            if (MMOItemsUtils.equalsMMOItem(item, itemType.getValue(profile), itemID.getValue(profile))) {
                if (item.getAmount() <= desiredDeletions) {
                    items[i] = null;
                    desiredDeletions = desiredDeletions - item.getAmount();
                } else {
                    item.setAmount(item.getAmount() - desiredDeletions);
                    desiredDeletions = 0;
                }
            }
        }

        neededDeletions.put(profile.getProfileUUID(), desiredDeletions);
        return items;
    }
}
