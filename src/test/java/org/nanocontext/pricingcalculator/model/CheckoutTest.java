package org.nanocontext.pricingcalculator.model;

import org.nanocontext.pricingcalculator.Checkout;
import org.nanocontext.pricingcalculator.pricingstrategy.BundledPricingStrategy;
import org.nanocontext.pricingcalculator.pricingstrategy.DefaultPricingStrategy;
import org.nanocontext.pricingcalculator.pricingstrategy.DiscountSchedulePricingStrategy;
import org.nanocontext.pricingcalculator.pricingstrategy.PricingStrategy;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Unit tests for the Checkout class
 * Specifically test:
 * Pricing Category
 * Example Pricing Scheme
 * Simple - A carton of milk (item #8873) costs $2.49
 * Buy X, Get Y Free - Buy two toothbrushes (item #1983) for $1.99 each, get one free
 * Additional Taxes - A bottle of wine (item #0923) costs $15.49 and is taxed an additional 9.25%
 * Bundled - Chips and salsa (items #6732 and #4900) cost $4.99 together, but they cost $2.49 and $3.49 alone, respectively.
 *
 * Checkout c = new Checkout(todaysScheme);
 * c.scan(“1983”); // toothbrush (199)
 * c.scan(“4900”); // salsa (349)
 * c.scan(“8873”); // milk (249)
 * c.scan(“6732”); // chips (249)
 * c.scan(“0923”); // wine (1692 = 1549 * 1.0925)
 * c.scan(“1983”); // toothbrush (199)
 * c.scan(“1983”); // toothbrush (199)
 * c.scan(“1983”); // toothbrush (199)
 * int cents = c.getTotal();
 * assert 3037 == cents
 *
 * 398 (items 1983 -> 4 instances into 2 BOGO)
 * 499 (item 4900 and 6732 Bundle)
 * 1692 (item 0923 with tax)
 * 249 (item 8873)
 *
 * 2838 (total)
 */
public class CheckoutTest extends AbstractTest {

    @DataProvider
    public Object[][] testDataProvider() {
        return new Object[][] {
                {Arrays.asList(), 0},
                {Arrays.asList("1983", "4900", "8873", "6732", "0923", "1983", "1983", "1983"), 2838},
        };
    }


    @Test(dataProvider = "testDataProvider")
    public void test(final List<String> skuIdentifiers, final int expectedTotal) {
        Context ctx = new Context();

        if(skuIdentifiers != null)
            for(String skuIdentifier : skuIdentifiers)
                ctx.getSubject().scan(skuIdentifier);

        Assert.assertEquals(ctx.getSubject().getTotal(), expectedTotal);
    }

    class Context extends AbstractTest.Context {
        private final PricingStrategies pricingStrategies;
        private final Checkout subject;

        public Context() {
            this.pricingStrategies = loadPricingStrategies(getInventoriedStockKeepingUnits());
            this.subject = new Checkout(getInventoriedStockKeepingUnits(), pricingStrategies);
        }

        public Checkout getSubject() {
            return subject;
        }

        /**
         * Degenerate implementation to load PricingStrategies for POC demonstration
         *
         * @param stockKeepingUnits - required non-null
         * @return a PricingStrategies instance populated with all known discounts
         */
        private PricingStrategies loadPricingStrategies(final StockKeepingUnits stockKeepingUnits) {
            PricingStrategies pricingStrategies = new PricingStrategies();

            // Create the BOGO for toothbrushes
            List<Float> discountSchedule = Arrays.asList(Float.valueOf(1), Float.valueOf(0));
            Set<StockKeepingUnit> applicableSkus = new HashSet<>();
            StockKeepingUnit stockKeepingUnit = stockKeepingUnits.find("1983");
            if (stockKeepingUnit == null)
                throw new IllegalArgumentException("Pricing strategy references unknown SKU");
            applicableSkus.add(stockKeepingUnit);
            PricingStrategy toothbrushBogo = new DiscountSchedulePricingStrategy("Toothbrush BOGO", discountSchedule, applicableSkus);
            pricingStrategies.add(toothbrushBogo);

            // create the chips and salsa bundle
            List<StockKeepingUnit> bundleDefinition = new ArrayList<>();
            bundleDefinition.add(getInventoriedStockKeepingUnits().find("6732"));
            bundleDefinition.add(getInventoriedStockKeepingUnits().find("4900"));
            PricingStrategy chipsAndSalsaBundle = new BundledPricingStrategy(bundleDefinition, 499);
            pricingStrategies.add(chipsAndSalsaBundle);

            // the DefaultPricingStrategy MUST always be included
            pricingStrategies.add(new DefaultPricingStrategy());

            return pricingStrategies;
        }
    }
}
