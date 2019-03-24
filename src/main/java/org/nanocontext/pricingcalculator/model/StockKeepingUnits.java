package org.nanocontext.pricingcalculator.model;

import org.nanocontext.pricingcalculator.model.StockKeepingUnit;

import java.util.TreeSet;

/**
 * A Set of ALL of the StockKeepingUnit available. Instances of
 * StockKeepingUnit MUST NOT be created/used outside of this set.
 *
 * Keeping this as a TreeSet will both assure that no duplicates are
 * added and that the natural ordering (by identifier) is maintained,
 * which will help find SKU instances somewhat faster.
 * TODO: here and elsewhere that collections are extensions of base Java
 * collections SHOULD be reviewed for safety, there are more methods exposed
 * than should be.
 */
public class StockKeepingUnits extends TreeSet<StockKeepingUnit> {

    /**
     * Create and add a StockKeepingUnit to this.
     * NOTE: StockKeepingUnit should only be created through this method (and the following similar)
     */
    public final StockKeepingUnit add(final String identifier, final String description, final int basePrice) {
        StockKeepingUnit sku = new StockKeepingUnit(identifier, description, basePrice);
        this.add(sku);
        return sku;
    }

    /**
     * Create and add a StockKeepingUnit to this.
     * NOTE: StockKeepingUnit should only be created through this method (and the previous similar)
     */
    public final StockKeepingUnit add(final String identifier, final String description, final int basePrice, float taxRate) {
        StockKeepingUnit sku = new StockKeepingUnit(identifier, description, basePrice, taxRate);
        this.add(sku);
        return sku;
    }

    /**
     * @param identifier
     * @return
     */
    public StockKeepingUnit find(final String identifier) {
        if (identifier != null) {
            // TODO: optimize for a faster search
            for (StockKeepingUnit stockKeepingUnit : this)
                if (identifier.equals(stockKeepingUnit.getIdentifier()))
                    return stockKeepingUnit;
        }

        return null;
    }

}
