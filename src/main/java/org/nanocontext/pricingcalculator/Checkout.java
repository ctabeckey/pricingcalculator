package org.nanocontext.pricingcalculator;

import org.nanocontext.pricingcalculator.model.PricingStrategies;
import org.nanocontext.pricingcalculator.model.ScannedItems;
import org.nanocontext.pricingcalculator.model.StockKeepingUnit;
import org.nanocontext.pricingcalculator.model.StockKeepingUnits;
import org.nanocontext.pricingcalculator.pricingstrategy.*;

import java.util.*;

/**
 *
 */
public class Checkout {
    // all of the item types that are sold
    private final StockKeepingUnits stockKeepingUnits;
    // essentially the discounts that can be applied, plus a default "pass-through"
    private final PricingStrategies pricingStrategies;
    // a collection of the items as each one is scanned at checkout
    private final ScannedItems scannedItems;

    /**
     *
     * @param stockKeepingUnits must be non-null
     * @param pricingStrategies must be non-null
     */
    public Checkout(final StockKeepingUnits stockKeepingUnits, final PricingStrategies pricingStrategies) {
        Utility.notNull("stockKeepingUnits", stockKeepingUnits);
        Utility.notNull("pricingStrategies", pricingStrategies);

        this.stockKeepingUnits = stockKeepingUnits;
        this.pricingStrategies = pricingStrategies;
        this.scannedItems = new ScannedItems();
    }

    /**
     * Scan an individual item
     * @param identifier
     */
    public void scan(final String identifier) {
        StockKeepingUnit sku = this.stockKeepingUnits.find(identifier);
        if (sku == null)
            // TODO: replace IllegalArgumentException with a business (checked) exception
            throw new IllegalArgumentException("Unknown item identifier.");
        scannedItems.add(sku);
    }

    /**
     * Getting the total includes applying the available PricingStrategy, determining
     * the "optimal" PricingStrategy application, and then totaling the result.
     *
     * @return the total amount with discounts applied
     */
    public int getTotal() {
        // the result will contain all of the items and their resulting price after
        // discounts are applied
        ClaimedItems result = new ClaimedItems();

        SortedSet<PricingStrategyResult> appliedPricingStrategies = new TreeSet<PricingStrategyResult>(
                // create a Comparator that sorts by getDiscount() descending
                new Comparator<PricingStrategyResult>() {
                    @Override
                    public int compare(PricingStrategyResult claimedItems1, PricingStrategyResult claimedItems2) {
                        return claimedItems2.getDiscount() - claimedItems1.getDiscount();
                    }
                }
        );

        // apply all of the available PricingStrategy and retain the results
        // in the Set which is sorted descending by discount
        for (PricingStrategy pricingStrategy : pricingStrategies) {
            appliedPricingStrategies.add(pricingStrategy.apply(scannedItems));
        }

        for (PricingStrategyResult pricingStrategyResult : appliedPricingStrategies) {
            // if the PricingStrategy is atomic (all or nothing) then
            // determine if any items have already been added to the result
            if (pricingStrategyResult.getPricingStrategy().isAtomic()) {
                // if any items are already in the result then the entire PricingStrategy
                // is inapplicable, ignore its results
                if (! result.containsAny(pricingStrategyResult))
                    result.addAll(pricingStrategyResult);
            } else {
                // add all of the ClaimItem that are NOT already in the result
                for (ClaimedItem claimedItem : pricingStrategyResult)
                    if (! result.containsItem(claimedItem.getItem()))
                        result.add(claimedItem);
            }
        }

        return result.getEffectivePrice();
    }
}
