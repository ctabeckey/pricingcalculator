package org.nanocontext.pricingcalculator.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 */
public class StockKeepingUnit implements Comparable<StockKeepingUnit> {
    /** the identifier is the SKU number */
    private final String identifier;
    /** description is a human readable description */
    private final String description;
    /** the base price is the price without tax or discounts */
    private final int basePrice;
    /** the tax rate is the the rate as a percentage */
    private final float taxRate;

    /**
     * Create an instance with a 0% tax rate
     * NOTE: Instances may only be created through the StockKeepingUnits collection
     */
    StockKeepingUnit(final String identifier, final String description, final int basePrice) {
        this(identifier, description, basePrice, 0.0f);
    }

    /**
     * Create an instance with a defined tax rate
     * NOTE: Instances may only be created through the StockKeepingUnits collection
     */
    StockKeepingUnit(final String identifier, final String description, final int basePrice, final float taxRate) {
        this.identifier = identifier;
        this.description = description;
        this.basePrice = basePrice;
        this.taxRate = taxRate;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public float getTaxRate() {
        return taxRate;
    }

    public int getTaxAmount() {
        return (int)(taxRate * basePrice);
    }

    /**
     * The price with tax
     */
    public int getEffectivePrice() {
        return getBasePrice() + getTaxAmount();
    }

    /**
     * equals and hashCode are sensitive to the identifier ONLY
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockKeepingUnit that = (StockKeepingUnit) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    /**
     * Natural ordering is by identifier, this is consistent with equals and hashCode.
     * @param that another StockKeepingUnit instance to compare against
     * @return the compareTo of the SKU identifier
     */
    @Override
    public int compareTo(final StockKeepingUnit that) {
        return that == null ? -1 : this.identifier.compareTo(that.identifier);
    }

    @Override
    public String toString() {
        return "StockKeepingUnit{" +
                "identifier='" + identifier + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", taxRate=" + taxRate +
                '}';
    }
}
