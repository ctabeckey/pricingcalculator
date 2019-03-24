package org.nanocontext.pricingcalculator.model;

import org.nanocontext.pricingcalculator.pricingstrategy.ClaimedItem;
import org.nanocontext.pricingcalculator.pricingstrategy.DiscountSchedulePricingStrategy;
import org.nanocontext.pricingcalculator.pricingstrategy.PricingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

public class DiscountSchedulePricingStrategyTest {
    private final static Logger logger = LoggerFactory.getLogger(DefaultPricingStrategyTest.class);
    private final DiscountSchedulePricingStrategyTest.Context context;

    public DiscountSchedulePricingStrategyTest() {
        this.context = new DiscountSchedulePricingStrategyTest.Context();
    }

    @DataProvider
    public Object[][] testDataProvider() {
        List<Object[]> testStimulus = new ArrayList<>();
        ScannedItems cart = null;

        cart = new ScannedItems();
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        testStimulus.add(new Object[] {
                cart,
                new Integer[]{}
        });

        cart = new ScannedItems();
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        testStimulus.add(new Object[] {
                cart,
                new Integer[]{
                        this.context.getInventoriedStockKeepingUnits().find("1983").getBasePrice(),
                        Integer.valueOf(0)
                }
        });

        cart = new ScannedItems();
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        testStimulus.add(new Object[] {
                cart,
                new Integer[]{
                        this.context.getInventoriedStockKeepingUnits().find("1983").getBasePrice(),
                        Integer.valueOf(0)
                }
        });

        cart = new ScannedItems();
        cart.add(this.context.getInventoriedStockKeepingUnits().find("4900"));
        cart.add(this.context.getInventoriedStockKeepingUnits().find("4900"));
        testStimulus.add(new Object[] {
                cart,
                new Integer[]{}
        });

        cart = new ScannedItems();
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        cart.add(this.context.getInventoriedStockKeepingUnits().find("4900"));
        testStimulus.add(new Object[] {
                cart,
                new Integer[]{
                        this.context.getInventoriedStockKeepingUnits().find("1983").getBasePrice(),
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

        Set<ClaimedItem> claimedItems = context.getSubject().apply(cart);
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
     * Create a Context with a test subject that gives BOGO on item 1983 only
     */
    public class Context extends AbstractPricingStrategyTest.Context {
        private DiscountSchedulePricingStrategy subject;

        @Override
        public PricingStrategy getSubject() {
            List<Float> discountSchedule = Arrays.asList(Float.valueOf(1), Float.valueOf(0));
            Set<StockKeepingUnit> applicableSkus = new HashSet<>();

            // this will make toothbrushes buy one get one free
            applicableSkus.add(getInventoriedStockKeepingUnits().find("1983"));
            subject = new DiscountSchedulePricingStrategy("Toothbrush BOGO", discountSchedule, applicableSkus);

            return subject;
        }
    }
}
