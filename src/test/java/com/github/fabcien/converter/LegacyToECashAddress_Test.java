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

@DisplayName("Convert address from legacy to eCash format")
public class LegacyToECashAddress_Test {

    @Test
    @DisplayName("Version - P2PKH")
    void testLegacyToECachP2PKH() {
        String legacy_address = "18uzj5qpkmg88uF3R4jKTQRVV3NiQ5SBPf";
        String ecash_address = "ecash:qptvav58e40tcrcwuvufr94u7enkjk6s2qxtsl8nr9";

        assertEquals(ecash_address, AddressConverter.toECashAddress(legacy_address));
    }

    @Test
    @DisplayName("Version - P2SH")
    void testLegacyToECashP2SH() {
        String legacy_address = "3CWFddi6m4ndiGyKqzYvsFYagqDLPVMTzC";
        String ecash_address = "ecash:ppm2qsznhks23z7629mms6s4cwef74vcwv2zrv3l8h";

        assertEquals(ecash_address, AddressConverter.toECashAddress(legacy_address));
    }

}
