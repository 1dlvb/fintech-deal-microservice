package com.fintech.deal.util;

/**
 * Utility class for enhancing strings with wildcards for matching.
 * @author Matushkin Anton
 */
public final class WildcatEnhancer {

    private WildcatEnhancer() {}

    /**
     * Enhances the given string with leading and trailing '%' characters,
     * allowing it to be used in SQL queries for wildcard matching.
     * @param string the string to enhance with wildcards.
     * @return the enhanced string with '%' at both ends.
     */
    public static String enhanceWithWildcatMatching(String string) {
        return String.format("%%%s%%", string);
    }

}
