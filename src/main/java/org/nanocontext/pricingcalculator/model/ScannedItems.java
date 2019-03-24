package org.nanocontext.pricingcalculator.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A ScannedItems is a Composition of SKU identifiers.
 * Duplicate SKU identifiers are allowed, and simply indicate multiple
 * instances of an item (i.e. three toothbrushes are represented as
 * three elements in the ScannedItems contents)
 */
public class ScannedItems implements Iterable<Item> {
    private final List<Item> contents;

    public ScannedItems() {
        this.contents = new LinkedList<>();
    }

    /**
     * @param sku
     */
    public void add(final StockKeepingUnit sku) {
        this.contents.add(new Item(sku));
    }

    /**
     * Provided for completeness but unused.
     */
    public void remove(final StockKeepingUnit sku) {
        Item dead = findFirst(sku);
        if (dead != null)
            this.contents.remove(dead);
    }

    /**
     * Find the first instance of a SKU in this list by the SKU identifier
     * @return the item if found, else null
     */
    private Item findFirst(final String skuIdentifier) {
        if (skuIdentifier != null)
            for (Item item : this.contents)
                if (item.getSku().getIdentifier().equals(skuIdentifier))
                    return item;

        return null;
    }

    /**
     * Find the first instance of a SKU in this list
     * @return the item if found, else null
     */
    private Item findFirst(final StockKeepingUnit sku) {
        if (sku != null)
            for (Item item : this.contents)
                if (sku.equals(item.getSku()))
                    return item;

         return null;
    }


    public int count() {
        return this.contents.size();
    }

    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableList(this.contents).iterator();
    }

    @Override
    public void forEach(Consumer<? super Item> action) {
        this.contents.forEach(action);
    }
}
