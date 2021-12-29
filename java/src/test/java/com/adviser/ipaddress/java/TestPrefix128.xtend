package com.adviser.ipaddress.java

import java.math.BigInteger
import java.util.HashMap

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TestPrefix128 {

    static class Prefix128Test {
        public HashMap<Integer, BigInteger> u128_hash = new HashMap<Integer, BigInteger>()
    }

    def Prefix128Test setup() {
        val p128t = new Prefix128Test()
        p128t.u128_hash.put(32, new BigInteger("340282366841710300949110269838224261120", 10))
        p128t.u128_hash.put(64, new BigInteger("340282366920938463444927863358058659840", 10))
        p128t.u128_hash.put(96, new BigInteger("340282366920938463463374607427473244160", 10))
        p128t.u128_hash.put(126, new BigInteger("340282366920938463463374607431768211452", 10))
        return p128t;
    }

    @Test
    def test_initialize() {
        assertTrue(Prefix128.create(129).isErr());
        assertTrue(Prefix128.create(64).isOk());
    }

    @Test
    def test_method_bits() {
        val prefix = Prefix128.create(64).unwrap();
        var str = new StringBuilder();
        for (var i = 0; i < 64; i++) {
            str.append("1");
        }
        for (var i = 0; i < 64; i++) {
            str.append("0");
        }
        assertEquals(str.toString(), prefix.bits())
    }
    @Test
    def test_method_to_u32() {
        setup().u128_hash.forEach[num, u128|
            assertEquals(u128, Prefix128.create(num).unwrap().netmask())
        ]
    }
}
