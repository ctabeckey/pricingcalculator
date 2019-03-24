package org.nanocontext.pricingcalculator.pricingstrategy;

import org.nanocontext.pricingcalculator.model.ScannedItems;

public interface PricingStrategy {
    /**
     * Apply this PricingStrategy to the given ScannedItems and
     * return the items and effective prices to which this PricingStrategy applies.
     *
     * @param scannedItems
     * @return
     */
    PricingStrategyResult apply(final ScannedItems scannedItems);

    /**
     * Indicates if a PricingStrategy must be accepted as a whole or
     * whether individual items may be used.
     * @return true if the results of the PricingStrategy.apply MUST only be
     * accepted as a whole.
     * In general this is TRUE. The only exception known at this time is the
     * DefaultPricingStrategy, which simply passes through the effective price.
     */
    boolean isAtomic();
}
