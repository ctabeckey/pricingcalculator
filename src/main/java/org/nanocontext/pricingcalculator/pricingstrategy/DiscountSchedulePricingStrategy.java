package org.nanocontext.pricingcalculator.pricingstrategy;

import org.nanocontext.pricingcalculator.model.*;

import java.util.*;

/**
 * A PricingStrategy which reduces the price of the nth instances of an applicable
 * item by the discount schedule. The item SKU must be equal.
 * The "discount schedule" is a List of percentages to be applied to each
 * item instance in order. For example if the pricing strategy was 2 for 1
 * then the DiscountSchedule would be [100.0%, 0.0%], that is the first
 * instance is full price and the second instance is free.
 * NOTE: the DiscountSchedule is applied ONLY if a set of Items the same
 * size as the DiscountSchedule can be found. So a DiscountSchedule of
 * [50.0%, 50.0%] will not be applied unless two items can be found to
 * apply to. If three items are found the first two will have the DiscountSchedule
 * applied and the third will not be eligible
 */
public class DiscountSchedulePricingStrategy implements PricingStrategy {
    // the name is used for generating output (so a human can account for applied discounts)
    private final String name;
    private final List<Float> discountSchedule;
    private final Set<StockKeepingUnit> applicableSKU = new HashSet<>();

    /**
     *
     * @param discountSchedule
     * @param applicableSKU
     */
    public DiscountSchedulePricingStrategy(final String name, final List<Float> discountSchedule, final Set<StockKeepingUnit> applicableSKU) {
        this.name = name;
        this.discountSchedule = new ArrayList<>();
        this.discountSchedule.addAll(discountSchedule);
        this.applicableSKU.addAll(applicableSKU);
    }

    public String getName() {
        return name;
    }

    @Override
    public PricingStrategyResult apply(final ScannedItems scannedItems) {
        PricingStrategyResult pricingStrategyResult = new PricingStrategyResult(this);

        int requiredItemCount = this.discountSchedule.size();
        Map<StockKeepingUnit, Set<Item>> candidateItems = new HashMap<>();

        for (Item item : scannedItems) {
            StockKeepingUnit sku = item.getSku();
            if (applicableSKU.contains(sku)) {
                Set<Item> skuItems = candidateItems.get(sku);
                if (skuItems == null) {
                    skuItems = new HashSet<>();
                    candidateItems.put(sku, skuItems);
                }
                skuItems.add(item);

                // claim the items and remove them from the candidates
                if (skuItems.size() == requiredItemCount) {
                    Set<ClaimedItem> skuClaimedItems = applyDiscountSchedule(skuItems);
                    pricingStrategyResult.addAll(skuClaimedItems);
                    candidateItems.remove(sku);
                }
            }
        }

        return pricingStrategyResult;
    }

    /**
     * Apply the discount schedule to the Set of items. The number of Items in the Set
     * MUST be exactly the same as the number of items in the DiscountSchedule.
     *
     * @param skuItems
     * @return
     */
    private Set<ClaimedItem> applyDiscountSchedule(Set<Item> skuItems) {
        int requiredItemCount = this.discountSchedule.size();
        Set<ClaimedItem> result = new HashSet<>();

        Iterator<Item> itemIterator = skuItems.iterator();
        Iterator<Float> discountScheduleIterator = this.discountSchedule.iterator();

        for(int index = 0; index < this.discountSchedule.size(); ++index) {
            Item item = itemIterator.next();
            float discount = discountScheduleIterator.next().floatValue();
            int effectivePrice = (int)(item.getSku().getBasePrice() * discount);

            result.add(new ClaimedItem(this, item, effectivePrice));
        }

        return result;
    }

    /**
     * Individual items in the generated PricingStrategyResult MUST NOT be extracted and used
     * during checkout.
     */
    @Override
    public boolean isAtomic() {
        return true;
    }

    @Override
    public String toString() {
        return "DiscountSchedulePricingStrategy{" +
                "name='" + name + '\'' +
                ", discountSchedule=" + discountSchedule +
                ", applicableSKU=" + applicableSKU +
                '}';
    }
}
