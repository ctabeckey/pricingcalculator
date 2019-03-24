package org.nanocontext.pricingcalculator.pricingstrategy;

import org.nanocontext.pricingcalculator.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * A simple extension of a Set of ClaimedItem with help functions to calculate
 * totals and discount.
 */
public class ClaimedItems extends HashSet<ClaimedItem> {
    private final static Logger log = LoggerFactory.getLogger(ClaimedItems.class);

    /**
     * Simply returns the total of all of the ClaimItem effective prices.
     * This is the price of all of the items without any discount applied.
     */
    public int getTotalItemEffectivePrices() {
        int total = 0;

        for (ClaimedItem element : this) {
            total += element.getItem().getSku().getEffectivePrice();
        }

        return total;
    }

    /**
     * Returns the effective (discounted) price of ClaimedItem instances as a whole.
     */
    public int getEffectivePrice() {
        int total = 0;

        for (ClaimedItem element : this) {
            total += element.getEffectivePrice();
        }

        return total;
    }

    /**
     * Returns the total of effective prices minus the (collective, discounted) effective price
     */
    public int getDiscount() {
        int totalItemEffectivePrice = getTotalItemEffectivePrices();
        int effectivePrice = getEffectivePrice();

        log.info("getEffectivePrice = {}, getTotalItemEffectivePrices = {}", effectivePrice, totalItemEffectivePrice);
        return totalItemEffectivePrice - effectivePrice;
    }

    /**
     * Determines if ANY of the items in the claimedItems are also in this ClaimedItems
     * instance.
     * Equality is determined by equality of the wrapped Item instance.
     *
     * @return true if any items are included in both lists
     */
    public boolean containsAny(final ClaimedItems claimedItems) {
        for (ClaimedItem claimedItem : claimedItems)
            if(containsItem(claimedItem.getItem()))
                return true;

        return false;
    }

    /**
     *
     * @param item
     * @return
     */
    public boolean containsItem(final Item item) {
        if (item != null)
            for (ClaimedItem claimedItem : this) {
                if (claimedItem.getItem().equals(item))
                    return true;
            }

        return false;
    }
}
