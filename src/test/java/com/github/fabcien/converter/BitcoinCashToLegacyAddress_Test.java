package com.github.fabcien.converter;

/*-
 * -----------------------LICENSE_START-----------------------
 * eCash address converter
 * %%
 * Copyright (C) 2021 Fabcien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -----------------------LICENSE_END-----------------------
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Convert address from BCH to legacy format")
public class BitcoinCashToLegacyAddress_Test {

    @Test
    @DisplayName("Version - P2PKH")
    void testBitcoinCashToLegacyP2PKH() {
        String legacy_address = "18uzj5qpkmg88uF3R4jKTQRVV3NiQ5SBPf";
        String bch_address = "bitcoincash:qptvav58e40tcrcwuvufr94u7enkjk6s2qlxy5uf9j";

        assertEquals(legacy_address, AddressConverter.BitcoinCashToLegacyAddress(bch_address));
    }

    @Test
    @DisplayName("Version - P2SH")
    void testBitcoinCashToLegacyP2SH() {
        String legacy_address = "3CWFddi6m4ndiGyKqzYvsFYagqDLPVMTzC";
        String bch_address = "bitcoincash:ppm2qsznhks23z7629mms6s4cwef74vcwvn0h829pq";

        assertEquals(legacy_address, AddressConverter.BitcoinCashToLegacyAddress(bch_address));
    }

    @Test
    @DisplayName("No prefix")
    void testBitcoinCashToLegacyNoPrefix() {
        String legacy_address = "3CWFddi6m4ndiGyKqzYvsFYagqDLPVMTzC";
        String bch_address = "ppm2qsznhks23z7629mms6s4cwef74vcwvn0h829pq";

        assertEquals(legacy_address, AddressConverter.BitcoinCashToLegacyAddress(bch_address));
    }

    @Test
    @DisplayName("Wrong prefix")
    void testBitcoinCashToLegacyWrongPrefix() {
        String cash_address = "foo:ppm2qsznhks23z7629mms6s4cwef74vcwvn0h829pq";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
           () -> AddressConverter.BitcoinCashToLegacyAddress(cash_address),
           "Expected BitcoinCashToLegacyAddress to throw an IllegalArgumentException");
        assertTrue(e.getMessage().contains("foo:"));
        assertTrue(e.getMessage().contains("bitcoincash:"));
    }
}
