package org.nanocontext.pricingcalculator.pricingstrategy;

import org.nanocontext.pricingcalculator.Utility;
import org.nanocontext.pricingcalculator.model.Item;
import org.nanocontext.pricingcalculator.model.ScannedItems;
import org.nanocontext.pricingcalculator.model.StockKeepingUnit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A PricingStrategy which looks for groups of items, each with a specified SKU
 * identifier and prices them all as a group. In practice it sets the effective
 * price of the first item to the bundleDefinition price and the effective price of all others
 * in the group to 0.
 *
 * For example, initializing an instance of this PricingStrategy as
 *    new BundledPricingStrategy(499, "6732", "4900")
 * the result will be that the effective price of item "6732" will be 499 and the effective
 * price of all other items (in this case: "4900") will be set to 0
 *
 * NOTE: there is a bit of trickiness needed for cases where there are multiple bundles:
 * scanned items = "6732", "6732", "4900", "4900"
 */
public class BundledPricingStrategy implements PricingStrategy {
    // the StockKeepingUnit instances that make up a bundleDefinition
    private final List<StockKeepingUnit> bundleDefinition;
    // the price of the entire bundleDefinition, applied to the first Item
    private final int effectivePrice;
    // A List of Lists of Item instances, used to build bundles
    // each element List will be populated with Item instances from the ScannedItems
    // until all instances that make up a bundleDefinition are included, then the Item instances
    // are claimed
    private final List<Bundle> candidateBundles = new LinkedList<>();

    /**
     *
     */
    public BundledPricingStrategy(final List<StockKeepingUnit> bundleDefinition, final int effectivePrice) {
        Utility.notNull("bundleDefinition", bundleDefinition);
        this.bundleDefinition = bundleDefinition;
        this.effectivePrice = effectivePrice;
    }

    /**
     *
     */
    @Override
    public PricingStrategyResult apply(final ScannedItems scannedItems) {
        PricingStrategyResult result = new PricingStrategyResult(this);

        for (Item item : scannedItems) {
            Bundle candidateBundle = saveInCandidateBundle(item);
            if (candidateBundle != null && candidateBundle.isComplete()) {
                int effectivePrice = this.effectivePrice;
                for(Item bundleItem : candidateBundle) {
                    // the first item in the bundle has the effective price of the whole bundle
                    result.add(new ClaimedItem(this, bundleItem, effectivePrice));
                    // after the first item in the bundle, the prices are effectively 0
                    effectivePrice = 0;

                    // logically, this remove isn't absolutely necessary
                    // because the Bundle will never require more Item
                    this.candidateBundles.remove(candidateBundle);
                }
            }
        }

        return result;
    }

    /**
     * Determine if the Item is "interesting" and if it is, add it to a Bundle,
     * creating a new Bundle if needed.
     * @return the Bundle that the Item was added to, or null if not added
     */
    private Bundle saveInCandidateBundle(final Item item) {
        if (bundleDefinition.contains(item.getSku())) {
            for (Bundle bundle : candidateBundles) {
                if (bundle.addIfRequired(item))
                    return bundle;
            }

            // not added but is interesting, create a new Bundle
            Bundle bundle = new Bundle();
            bundle.addIfRequired(item);
            candidateBundles.add(bundle);
             return bundle;
        }

        // not interesting, ignore it
        return null;
    }

    /**
     * All items must be included
     */
    @Override
    public boolean isAtomic() {
        return true;
    }

    /**
     * A class that is used to build bundles of required Item.
     * Once a Bundle is "fully populated" the Item instances
     * in the Bundle should be claimed for this PricingStrategy
     */
    private class Bundle extends ArrayList<Item> {
        // Start with all the SKUs that make upa Bundle and
        // remove from this List as we build the Bundle.
        //
        private List<StockKeepingUnit> required = new LinkedList<>();

        Bundle() {
            required = new LinkedList<>();
            required.addAll(bundleDefinition);
        }

        /**
         * Add the Item if the Bundle needs an Item of the type.
         * Return TRUE if added, else return FALSE
         */
        boolean addIfRequired(final Item item) {
            if (required.contains(item.getSku())) {
                this.add(item);
                required.remove(item.getSku());
                return true;
            } else
                return false;
        }

        public boolean isComplete() {
            return required.isEmpty();
        }
    }

    @Override
    public String toString() {
        return "BundledPricingStrategy{" +
                "bundleDefinition=" + bundleDefinition +
                ", effectivePrice=" + effectivePrice +
                ", candidateBundles=" + candidateBundles +
                '}';
    }
}
