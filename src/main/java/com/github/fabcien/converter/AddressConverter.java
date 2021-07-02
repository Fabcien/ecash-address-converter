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

import com.github.fabcien.converter.b58.B58;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class AddressConverter {

    private static final String SEPARATOR = ":";

    private static final String ECASH_PREFIX = "ecash";
    private static final int[] ECASH_PREFIX_BYTES = new int[]{5, 3, 1, 19, 8, 0};

    private static final String BCH_PREFIX = "bitcoincash";
    private static final int[] BCH_PREFIX_BYTES = new int[]{2, 9, 20, 3, 15, 9, 14, 3, 1, 19, 8, 0};

    private static final BigInteger[] GENERATORS = new BigInteger[]{
            new BigInteger("98f2bc8e61", 16),
            new BigInteger("79b76d99e2", 16),
            new BigInteger("f33e5fb3c4", 16),
            new BigInteger("ae2eabe2a8", 16),
            new BigInteger("1e4f43e470", 16)};

    private static final BigInteger POLYMOD_CONSTANT = new BigInteger("07ffffffff", 16);

    private static String toCashAddress(String legacyAddress, String prefix, int[] prefixBytes) {
        int oldVersion = B58.decode(legacyAddress)[0];
        int newVersion = getVersion(true, oldVersion);
        byte[] payloadBytes = B58.decodeChecked(legacyAddress, oldVersion);

        int[] payload = new int[payloadBytes.length];
        for (int i = 0; i < payloadBytes.length; i++) {
            payload[i] = payloadBytes[i];
            if (payload[i] < 0)
                payload[i] += 256;
        }
        payload = concatArrays(new int[]{newVersion}, payload);

        payload = convertBits(payload, 8, 5);
        int[] checksum = checksum(prefixBytes, payload);
        String cashAddress = Base32.encode(concatArrays(payload, checksum));
        return prefix + SEPARATOR + cashAddress;
    }

    public static String legacyToECashAddress(String legacyAddress) {
        return toCashAddress(legacyAddress, ECASH_PREFIX, ECASH_PREFIX_BYTES);
    }

    public static String legacyToBitcoinCashAddress(String legacyAddress) {
        return toCashAddress(legacyAddress, BCH_PREFIX, BCH_PREFIX_BYTES);
    }

    private static String cashToLegacyAddress(String cashAddress) {
        int[] decoded = Base32.decode(cashAddress);
        int[] converted = convertBits(decoded, 5, 8);
        int[] payload = Arrays.copyOfRange(converted, 1, converted.length - 6);
        byte[] payloadBytes = new byte[payload.length];
        for (int i = 0; i < payloadBytes.length; payloadBytes[i] = (byte) payload[i++]);

        return B58.encodeToStringChecked(payloadBytes, getVersion(false, converted[0]));
    }

    private static String checkAndRemovePrefix(String cashAddress, String prefix) {
        if (cashAddress.contains(SEPARATOR)) {
            String[] parts = cashAddress.split(SEPARATOR);
            if (! parts[0].equals(prefix)) {
                throw new IllegalArgumentException(
                    "Invalid prefix for an eCash address: '" + parts[0] + ":', expected '" + prefix + ":'");
            }
            return parts[1];
        }
        return cashAddress;
    }

    public static String eCashToLegacyAddress(String ecashAddress) {
        String addressNoPrefix = checkAndRemovePrefix(ecashAddress, ECASH_PREFIX);
        return cashToLegacyAddress(addressNoPrefix);
    }

    public static String BitcoinCashToLegacyAddress(String bchAddress) {
        String addressNoPrefix = checkAndRemovePrefix(bchAddress, BCH_PREFIX);
        return cashToLegacyAddress(addressNoPrefix);
    }

    public static String eCashToBitcoinCashAddress(String ecashAddress) {
        String legacy = eCashToLegacyAddress(ecashAddress);
        return legacyToBitcoinCashAddress(legacy);
    }

    public static String BitcoinCashToECashAddress(String bchAddress) {
        String legacy = BitcoinCashToLegacyAddress(bchAddress);
        return legacyToECashAddress(legacy);
    }

    private static int getVersion(boolean legacy, int version) {
        if (legacy) {
            if (version == 5) // P2SH
                return 8;
        } else
        if (version == 8)
            return 5; // P2SH
        return 0; //P2PKH
    }

    private static int[] checksum(int[] prefixBytes, int[] payload) {
        BigInteger poly = polymod(concatArrays(concatArrays(prefixBytes, payload), new int[]{0, 0, 0, 0, 0, 0, 0, 0}));
        int[] checksum = new int[8];

        for (int i = 0; i < 8; i++) {
            checksum[i] = poly.shiftRight(5 * (7 - i)).byteValue() & 0x1f;
        }
        return checksum;
    }

    private static BigInteger polymod(int[] values) {
        BigInteger chk = BigInteger.ONE;

        for (int value : values) {
            byte c0 = chk.shiftRight(35).byteValue();
            chk = chk.and(POLYMOD_CONSTANT).shiftLeft(5)
                    .xor(new BigInteger(String.format("%02x", value), 16));

            if ((c0 & 0x01) != 0)
                chk = chk.xor(GENERATORS[0]);
            if ((c0 & 0x02) != 0)
                chk = chk.xor(GENERATORS[1]);
            if ((c0 & 0x04) != 0)
                chk = chk.xor(GENERATORS[2]);
            if ((c0 & 0x08) != 0)
                chk = chk.xor(GENERATORS[3]);
            if ((c0 & 0x10) != 0)
                chk = chk.xor(GENERATORS[4]);
        }
        return chk.xor(BigInteger.ONE);
    }

    private static int[] convertBits(int[] bytes8Bits, int from, int to) {
        int mask = ((1 << to) - 1);
        int accumulator = 0;
        int bits = 0;
        int max_acc = (1 << (from + to - 1)) - 1;
        ArrayList<Integer> list = new ArrayList<>();
        for (int value : bytes8Bits) {
            accumulator = ((accumulator << from) | value) & max_acc;
            bits += from;
            while (bits >= to) {
                bits -= to;
                list.add((accumulator >> bits) & mask);
            }
        }

        if (bits > 0) {
            list.add(((accumulator << (to - bits)) & mask));
        }

        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }

        return result;
    }

    private static int[] concatArrays(int[] first, int[] second) {
        int[] concatenatedBytes = new int[first.length + second.length];

        System.arraycopy(first, 0, concatenatedBytes, 0, first.length);
        System.arraycopy(second, 0, concatenatedBytes, first.length, second.length);

        return concatenatedBytes;
    }
}
