package org.nanocontext.pricingcalculator.pricingstrategy;

import org.nanocontext.pricingcalculator.Utility;

import java.util.HashSet;

/**
 * A Set of ClaimedItem with a few convenience methods for calculating total prices
 */
public class PricingStrategyResult extends ClaimedItems {
    // the PricingStrategy that produces this result
    private final PricingStrategy pricingStrategy;

    /**
     * The PricingStrategy that created this PricingStrategyResult instance must be non-null
     */
    public PricingStrategyResult(final PricingStrategy pricingStrategy) {
        Utility.notNull("pricingStrategy", pricingStrategy);
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Returns the PricingStrategy that produced this result
     */
    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }
}
