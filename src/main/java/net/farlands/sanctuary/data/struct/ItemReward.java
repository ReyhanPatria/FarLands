package net.farlands.sanctuary.data.struct;

import com.kicas.rp.util.Pair;

import net.farlands.sanctuary.util.FLUtils;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A reward!
 */
public class ItemReward extends JsonItemStack implements Comparable<ItemReward> {
    private final int rarity;

    public ItemReward() {
        super();
        this.rarity = 0;
    }

    @Override
    public int compareTo(ItemReward other) {
        return Integer.compare(rarity, other.rarity);
    }

    // The larger the bias, the less likely a rare item is to be selected
    public static Pair<ItemStack, Integer> randomReward(List<ItemReward> rewards, double bias) {
        int index = FLUtils.biasedRandom(rewards.size(), bias);
        return new Pair<>(
                rewards.stream().sorted(ItemReward::compareTo).collect(Collectors.toList()).get(index).getStack(),
                index
        );
    }
}
