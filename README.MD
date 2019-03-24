org.nanocontext.pricingcalculator.model.CheckoutTest implements the given test case.

Assumptions and Known Problems
1.) No attempt has been made to address accumulated float rounding errors. Interface and implementation
  should use BigDecimal. Note that this has the potential to cause failures when testing more complex pricing strategies.
2.) Currency is always assumed to be in indivisible units (i.e. "cents" but that is just semantics as far as the
  implementation is concerned.
3.) An item may be eligible for multiple discounts:
  a.) it is assumed that only one discount can apply to one item
  b.) which set of discounts to use is problematic when multiple are applicable (see #8 for more)
4.) The determination of which set of discounts to apply is made somewhat simplistically and may result in unoptimized
  (i.e. higher total price) than optimal. If the applicable discounts DO NOT overlap then the existing implementation
  will result in lowest total price.
5.) The application of pricing strategy (discounts) with respect to individual item tax rates is not specified:
  a.) it is assumed that the base price with tax (effective price in code) is used
6.) Composite classes (PricingStrategies, StockKeepingUnits and ClaimedItems) are simple derivations of base Java collections,
 this is expedient for a POC but leaves the access too permissive for production
7.) Exception handling and parameter validation need work (time boxed).
8.) There is an known bug when PricingStrategyResult overlap. PricingStrategy implementations are restricted
  to a single result set of applicable items. Where there is overlap in PricingStrategy application it is
  logically possible to accept part of a PricingStrategyResult that still meets the rules for the
  PricingStrategy. To fix this requires a refactor so that the PricingStrategy can return multiple
  PricingStrategyResult (time boxed).
9.) This library is NOT thread-safe. (time boxed)

Model
1.) A ScannedItems is composed of 0..n Item instances.
2.) An Item represents a single item in the cart (e.g. one Colgate Toothbrush)
3.) An Item has a unique identifier and relation to one StockKeepingUnit
4.) A StockKeepingUnit is an item type (e.g. Colgate Toothbrush)
5.) A PricingStrategy is an encapsulation of an algorithm that determines the price
of 0..n Item within the ScannedItems
6.) The DefaultPricingStrategy MUST apply to any Item in a ScannedItems
7.) Checkout implements orchestration of the checkout process.

Sequence
1.) Populate the ShoppingCart
2.) Submit ShoppingCart to Checkout
  a.) Run all available PricingStrategy on the ShoppingCart
  b.) Select collection of PricingStrategy to optimize for lowest total price

Caveat
I believe that the total for the example given is incorrect. The "buy one, get one" for toothbrush
should be applied twice and it is only applied once?
 Checkout c = new Checkout(todaysScheme);
 c.scan(“1983”); // toothbrush (199)
 c.scan(“4900”); // salsa (349)
 c.scan(“8873”); // milk (249)
 c.scan(“6732”); // chips (249)
 c.scan(“0923”); // wine (1692 = 1549 * 1.0925)
 c.scan(“1983”); // toothbrush (199)
 c.scan(“1983”); // toothbrush (199)
 c.scan(“1983”); // toothbrush (199)
 int cents = c.getTotal();
 assert 3037 == cents

should evaluate as :
 398 (items 1983 -> 4 instances into 2 BOGO)
 499 (items 4900 and 6732 Bundle)
 1692 (item 0923 with tax)
 249 (item 8873)

 2838 (total)
