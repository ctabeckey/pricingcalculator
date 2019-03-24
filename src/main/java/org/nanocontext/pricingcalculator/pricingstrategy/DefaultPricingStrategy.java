package org.nanocontext.pricingcalculator.pricingstrategy;

import org.nanocontext.pricingcalculator.model.*;

/**
 * The DefaultPricingStrategy MUST apply to all StockKeepingUnit individually.
 */
public class DefaultPricingStrategy implements PricingStrategy {
    /**
     * The DefaultPricingStrategy applies to all items in the cart.
     * The price for each item is the base price of the SKU plus
     * the tax.
     *
     * @param scannedItems
     * @return
     */
    @Override
    public PricingStrategyResult apply(final ScannedItems scannedItems) {
        PricingStrategyResult pricingStrategyResult = new PricingStrategyResult(this);
        if (scannedItems != null)
            for (Item item : scannedItems) {
                StockKeepingUnit sku = item.getSku();
                ClaimedItem claimedItem = new ClaimedItem(this, item, sku.getEffectivePrice());
                pricingStrategyResult.add(claimedItem);
            }

        return pricingStrategyResult;
    }

    /**
     * Individual items in the generated PricingStrategyResult may be extracted and used
     * during checkout.
     */
    @Override
    public boolean isAtomic() {
        return false;
    }

    @Override
    public String toString() {
        return "DefaultPricingStrategy{}";
    }
}
