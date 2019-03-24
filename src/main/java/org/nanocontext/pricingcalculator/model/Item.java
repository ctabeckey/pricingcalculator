package org.nanocontext.pricingcalculator.model;

import java.util.Objects;
import java.util.UUID;

/**
 * An item in the shopping cart.
 * An Item has a unique identifier to allow PricingStrategy instances
 * to "claim" individual items.
 */
public class Item {
    // Primary Key - a unique identifier of a single Item in the shopping cart
    private final String identifier = UUID.randomUUID().toString();

    // Foreign Key - the SKU of the item
    private final StockKeepingUnit sku;

    public Item(StockKeepingUnit sku) {
        this.sku = sku;
    }

    public String getIdentifier() {
        return identifier;
    }

    public StockKeepingUnit getSku() {
        return sku;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(identifier, item.identifier) &&
                Objects.equals(sku, item.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, sku);
    }

    @Override
    public String toString() {
        return "Item{" +
                "identifier='" + identifier + '\'' +
                ", sku=" + sku.toString() +
                '}';
    }
}
