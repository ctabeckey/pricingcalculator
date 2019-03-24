package org.nanocontext.pricingcalculator.model;

public abstract class AbstractTest {
    /**
     * the test context is specific to each test iteration,
     * this avoids contention when running threads in parallel
     */
    public abstract static class Context {
        private final StockKeepingUnits inventoriedStockKeepingUnits;

        Context() {
            inventoriedStockKeepingUnits = loadStockKeepingUnits();
        }

        /**
         * Degenerate implementation to load StockKeepingUnits for testing
         *
         * @return a StockKeepingUnits instance populated with all known items
         */
        private StockKeepingUnits loadStockKeepingUnits() {
            StockKeepingUnits stockKeepingUnits = new StockKeepingUnits();

            stockKeepingUnits.add("1983", "toothbrush", 199);
            stockKeepingUnits.add("4900", "salsa", 349);
            stockKeepingUnits.add("8873", "milk", 249);
            stockKeepingUnits.add("6732", "chips", 249);
            stockKeepingUnits.add("0923", "wine", 1549, 0.0925f);

            return stockKeepingUnits;
        }



        public StockKeepingUnits getInventoriedStockKeepingUnits() {
            return inventoriedStockKeepingUnits;
        }
    }
}
