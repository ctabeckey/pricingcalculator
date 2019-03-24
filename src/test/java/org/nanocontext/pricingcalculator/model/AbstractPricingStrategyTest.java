package org.nanocontext.pricingcalculator.model;

import org.nanocontext.pricingcalculator.pricingstrategy.PricingStrategy;

/**
 *
 */
public abstract class AbstractPricingStrategyTest extends AbstractTest {

    /**
     * the test context is specific to each test iteration,
     * this avoids contention when running threads in parallel
     */
    public abstract static class Context extends AbstractTest.Context {
        public abstract PricingStrategy getSubject();
    }
}
