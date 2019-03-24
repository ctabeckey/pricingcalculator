package org.nanocontext.pricingcalculator.model;

import org.nanocontext.pricingcalculator.pricingstrategy.ClaimedItem;
import org.nanocontext.pricingcalculator.pricingstrategy.DefaultPricingStrategy;
import org.nanocontext.pricingcalculator.pricingstrategy.PricingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluate the DefaultPricingStrategy for proper behavior
 */
public class DefaultPricingStrategyTest {
    private final static Logger logger = LoggerFactory.getLogger(DefaultPricingStrategyTest.class);
    private final Context context;

    public DefaultPricingStrategyTest() {
        this.context = new Context();
    }

    @DataProvider
    public Object[][] testDataProvider() {
        List<Object[]> testStimulus = new ArrayList<>();

        ScannedItems cart = new ScannedItems();
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        cart.add(this.context.getInventoriedStockKeepingUnits().find("1983"));
        testStimulus.add(new Object[]{cart});

        return testStimulus.toArray(new Object[testStimulus.size()][]);
    }


    @Test(dataProvider = "testDataProvider")
    public void test(final ScannedItems cart) {
        PricingStrategy subject = context.getSubject();
        Set<ClaimedItem> claimedItems = subject.apply(cart);
        // this loop is here just for "visual testing"
        for (ClaimedItem claimedItem : claimedItems) {
            logger.info(claimedItem.toString());
        }
        // assert that ALL items were claimed
        Assert.assertEquals(cart.count(), claimedItems.size());
        // TODO: assert number and identifier of item SKU
    }

    /**
     * the test context is specific to each test iteration,
     * this avoids contention when running threads in parallel
     */
    public static class Context extends AbstractPricingStrategyTest.Context {
        private final DefaultPricingStrategy defaultPricingStrategy;

        Context() {
            defaultPricingStrategy = new DefaultPricingStrategy();
        }

        public PricingStrategy getSubject() {
            return defaultPricingStrategy;
        }
    }
}
