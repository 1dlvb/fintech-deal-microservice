package com.fintech.deal.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WildcatEnhancerTests {

    @Test
    void testIsEnhanceWithWildcatMatchingReturnsProperString() {
        assertEquals("%test%", WildcatEnhancer.enhanceWithWildcatMatching("test"));
    }

    @Test
    void testIsEnhanceWithWildcatMatchingReturnsProperStringWhenPercentSymbolIsGiven() {
        assertEquals("%%%", WildcatEnhancer.enhanceWithWildcatMatching("%"));
    }

    @Test
    void testIsEnhanceWithWildcatMatchingReturnsProperStringWhenEmptyString() {
        assertEquals("%%", WildcatEnhancer.enhanceWithWildcatMatching(""));
    }

}