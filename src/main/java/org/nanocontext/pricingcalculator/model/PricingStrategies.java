package org.nanocontext.pricingcalculator.model;

import org.nanocontext.pricingcalculator.pricingstrategy.DefaultPricingStrategy;
import org.nanocontext.pricingcalculator.pricingstrategy.PricingStrategy;

import java.util.*;

/**
 * PricingStrategies is (for now) simply an extension of HashSet.
 */
public class PricingStrategies extends HashSet<PricingStrategy> {

    /**
     * Returns the default pricing strategy
     */
    public PricingStrategy getDefault() {
        for (PricingStrategy pricingStrategy : this)
            if (pricingStrategy instanceof DefaultPricingStrategy) {
                return pricingStrategy;
            }

        return null;
    }
}
