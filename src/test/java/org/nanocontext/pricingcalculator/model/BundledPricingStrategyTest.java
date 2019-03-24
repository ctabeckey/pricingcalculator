package org.nanocontext.pricingcalculator.model;

import org.nanocontext.pricingcalculator.pricingstrategy.BundledPricingStrategy;
import org.nanocontext.pricingcalculator.pricingstrategy.ClaimedItem;
import org.nanocontext.pricingcalculator.pricingstrategy.PricingStrategy;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Unit test for the BundlePricingStrategy class
 */
public class BundledPricingStrategyTest extends AbstractPricingStrategyTest {
    final Context ctx;

    public BundledPricingStrategyTest() {
        this.ctx = new Context();
    }

    @DataProvider
    public Object[][] testDataProvider() {
        List<Object[]> testStimulus = new ArrayList<>();
        ScannedItems scannedItems = null;

        // single item is NOT part of bundle
        scannedItems = new ScannedItems();
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("1983"));
        testStimulus.add(new Object[] {
                scannedItems,
                new Integer[]{}
        });

        // single item is part of bundle
        scannedItems = new ScannedItems();
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("6732"));
        testStimulus.add(new Object[] {
                scannedItems,
                new Integer[]{}
        });

        // correct number of items but none are part of bundle
        scannedItems = new ScannedItems();
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("1983"));
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("1983"));
        testStimulus.add(new Object[] {
                scannedItems,
                new Integer[]{}
        });

        // correct number of items, one is part of bundle
        scannedItems = new ScannedItems();
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("1983"));
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("6732"));
        testStimulus.add(new Object[] {
                scannedItems,
                new Integer[]{}
        });

        // should form a Bundle
        scannedItems = new ScannedItems();
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("6732"));
        scannedItems.add(this.ctx.getInventoriedStockKeepingUnits().find("4900"));
        testStimulus.add(new Object[] {
                scannedItems,
                new Integer[]{
                        Integer.valueOf(499),
                        Integer.valueOf(0)
                }
        });

        return testStimulus.toArray(new Object[testStimulus.size()][]);
    }

    @Test(dataProvider = "testDataProvider")
    public void test(final ScannedItems cart, final Integer[] expectedClaimedPrices) {
        // copy the expected prices and we'll destroy the copy as we assert
        List<Integer> ecp = new ArrayList<>(expectedClaimedPrices.length);
        for (Integer ecpElement : expectedClaimedPrices)
            ecp.add(ecpElement);

        Set<ClaimedItem> claimedItems = ctx.getSubject().apply(cart);
        if (expectedClaimedPrices == null || expectedClaimedPrices.length == 0)
            Assert.assertEquals(claimedItems.size(), 0);
        else {
            for(ClaimedItem item : claimedItems) {
                int effectivePrice = item.getEffectivePrice();
                if (ecp.isEmpty())
                    Assert.fail("Expected claimed items with value " + effectivePrice);

                for (Integer price : ecp)
                    if(price.intValue() == effectivePrice) {
                        ecp.remove(price);
                        break;
                    }
            }
            Assert.assertTrue(ecp.isEmpty());
        }

    }

    /**
     * the test context is specific to each test iteration,
     * this avoids contention when running threads in parallel
     */
    public static class Context extends AbstractPricingStrategyTest.Context {
        private final BundledPricingStrategy subject;

        Context() {
            // create the chips and salsa bundle
            List<StockKeepingUnit> bundleDefinition = new ArrayList<>();
            bundleDefinition.add(getInventoriedStockKeepingUnits().find("6732"));
            bundleDefinition.add(getInventoriedStockKeepingUnits().find("4900"));

            subject = new BundledPricingStrategy(bundleDefinition, 499);
        }

        public PricingStrategy getSubject() {
            return subject;
        }
    }

}
