package org.nanocontext.pricingcalculator;

/**
 *
 */
public final class Utility {
    // discourage instance construction
    private Utility() { }

    /**
     * A simple NULL check for parameters.
     *
     * @param parameterName the name of the parameter (used to produce a readable message)
     * @param parameter the parameter to check
     */
    public final static void notNull(final String parameterName, final Object parameter) {
        if (parameter == null)
            throw new IllegalArgumentException(String.format("Parameter '%s' is null and must not be", parameterName));
    }
}
