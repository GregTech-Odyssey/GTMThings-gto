package com.hepdd.gtmthings.common.cover;

import static org.junit.jupiter.api.Assertions.assertSame;

import net.minecraft.core.Direction;

import org.junit.jupiter.api.Test;

class AdvancedWirelessTransferCoverTest {

    @Test
    void readFacingOrFallbackUsesParsedDirection() {
        assertSame(Direction.UP, AdvancedWirelessTransferCover.readFacingOrFallback("up", Direction.SOUTH));
    }

    @Test
    void readFacingOrFallbackUsesFallbackForInvalidDirection() {
        assertSame(Direction.SOUTH, AdvancedWirelessTransferCover.readFacingOrFallback("sideways", Direction.SOUTH));
    }

    @Test
    void readFacingOrFallbackUsesFallbackForMissingDirection() {
        assertSame(Direction.SOUTH, AdvancedWirelessTransferCover.readFacingOrFallback(null, Direction.SOUTH));
    }

    @Test
    void readFacingOrFallbackUsesNorthWhenDirectionAndFallbackAreInvalid() {
        assertSame(Direction.NORTH, AdvancedWirelessTransferCover.readFacingOrFallback("sideways", null));
    }

    @Test
    void sanitizeFacingUsesFallbackForNullFacing() {
        assertSame(Direction.EAST, AdvancedWirelessTransferCover.sanitizeFacing(null, Direction.EAST));
    }
}
