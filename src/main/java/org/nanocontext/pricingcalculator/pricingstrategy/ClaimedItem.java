package org.nanocontext.pricingcalculator.pricingstrategy;

import org.nanocontext.pricingcalculator.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates the items and effective price of specific item in a ScannedItems
 * to which a PricingStrategy has been applied.
 */
public class ClaimedItem {
    private final static Logger log = LoggerFactory.getLogger(ClaimedItem.class);
    private final PricingStrategy pricingStrategy;
    private final Item item;
    private final int effectivePrice;

    /**
     * The constructor, requires non-null pricingStrategy and item.
     * @param pricingStrategy
     * @param item
     * @param effectivePrice
     */
    public ClaimedItem(final PricingStrategy pricingStrategy, final Item item, final int effectivePrice) {
        this.pricingStrategy = pricingStrategy;
        this.item = item;
        this.effectivePrice = effectivePrice;
        log.info("ClaimedItem using {}, {} -> {}", pricingStrategy.toString(), item.getSku().getEffectivePrice(), effectivePrice);
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    public Item getItem() {
        return item;
    }

    public int getEffectivePrice() {
        return effectivePrice;
    }

    @Override
    public String toString() {
        return "ClaimedItem{" +
                "item=" + item.toString() +
                ", effectivePrice=" + effectivePrice +
                '}';
    }
}
