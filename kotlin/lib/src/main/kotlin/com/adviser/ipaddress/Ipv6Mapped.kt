package com.adviser.ipaddress.kotlin

import java.math.BigInteger

//  Ac
///  It is usually identified as a IPv4 mapped IPv6 address, a particular
///  IPv6 address which aids the transition from IPv4 to IPv6. The
///  structure of the address is
///
///    ::ffff:w.y.x.z
///
///  where w.x.y.z is a normal IPv4 address. For example, the following is
///  a mapped IPv6 address:
///
///    ::ffff:192.168.100.1
///
///  IPAddress is very powerful in handling mapped IPv6 addresses, as the
///  IPv4 portion is stored internally as a normal IPv4 object. Let's have
///  a look at some examples. To create a new mapped address, just use the
///  class builder itself
///
///    ip6 = IPAddress::IPv6::Mapped.new "::ffff:172.16.10.1/128"
///
///  or just use the wrapper method
///
///    ip6 = IPAddress "::ffff:172.16.10.1/128"
///
///  Let's check it's really a mapped address:
///
///    ip6.mapped?
///      ///  true
///
///    ip6.to_string
///      ///  "::FFFF:172.16.10.1/128"
///
///  Now with the +ipv4+ attribute, we can easily access the IPv4 portion
///  of the mapped IPv6 address:
///
///    ip6.ipv4.address
///      ///  "172.16.10.1"
///
///  Internally, the IPv4 address is stored as two 16 bits
///  groups. Therefore all the usual methods for an IPv6 address are
///  working perfectly fine:
///
///    ip6.to_hex
///      ///  "00000000000000000000ffffac100a01"
///
///    ip6.address
///      ///  "0000:0000:0000:0000:0000:ffff:ac10:0a01"
///
///  A mapped IPv6 can also be created just by specify the address in the
///  following format:
///
///    ip6 = IPAddress "::172.16.10.1"
///
///  That is, two colons and the IPv4 address. However, as by RFC, the ffff
///  group will be automatically added at the beginning
///
///    ip6.to_string
///      => "::ffff:172.16.10.1/128"
///
///  making it a mapped IPv6 compatible address.
///
///
///  Creates a new IPv6 IPv4-mapped address
///
///    ip6 = IPAddress::IPv6::Mapped.new "::ffff:172.16.10.1/128"
///
///    ipv6.ipv4.class
///      ///  IPAddress::IPv4
///
///  An IPv6 IPv4-mapped address can also be created using the
///  IPv6 only format of the address:
///
///    ip6 = IPAddress::IPv6::Mapped.new "::0d01:4403"
///
///    ip6.to_string
///      ///  "::ffff:13.1.68.3"
///
class Ipv6Mapped {
    companion object {

        fun create(str: String): Result<IPAddress> {
            val ret = IPAddress.split_at_slash(str)
            val split_colon = ret.addr.split(":")
            if (split_colon.size <= 1) {
                // println!("---1")
                return Result.Err("not mapped format-1: ${str}")
            }
            var netmask = ""
            if (ret.netmask != null) {
                netmask = String.format("/%s", ret.netmask)
            }
            val ipv4_str = split_colon.get(split_colon.size - 1)
            if (IPAddress.is_valid_ipv4(ipv4_str)) {
                val ipv4 = IPAddress.parse(String.format("%s%s", ipv4_str, netmask))
                if (ipv4.isErr()) {
                    // println!("---2")
                    return ipv4
                }
                //mapped = Some(ipv4.unwrap())
                val addr = ipv4.unwrap()
                val ipv6_bits = IpBits.V6
                val part_mod = ipv6_bits.part_mod
                val up_addr = addr.host_address
                val down_addr = addr.host_address

                val rebuild_ipv6 = StringBuilder()
                var colon = ""
                for (i in 0 until split_colon.size - 1) {
                    rebuild_ipv6.append(colon)
                    rebuild_ipv6.append(split_colon.get(i))
                    colon = ":"
                }
                rebuild_ipv6.append(colon)
                val rebuild_ipv4 = String.format("%x:%x/%d",
                        up_addr.shiftRight(IpBits.V6.part_bits).mod(part_mod).intValueExact(),
                        down_addr.mod(part_mod).intValueExact(),
                        ipv6_bits.bits - addr.prefix.host_prefix())
                rebuild_ipv6.append(rebuild_ipv4)
                val r_ipv6 = IPAddress.parse(rebuild_ipv6.toString())
                if (r_ipv6.isErr()) {
                    // println!("---3|{}", &rebuild_ipv6)
                    return r_ipv6
                }
                if (r_ipv6.unwrap().is_mapped()) {
                    return r_ipv6
                }
                val ipv6 = r_ipv6.unwrap()
                val p96bit = ipv6.host_address.shiftRight(32)
                if (!p96bit.equals(BigInteger.ZERO)) {
                    // println!("---4|{}", &rebuild_ipv6)
                    return Result.Err("is not a mapped address:${rebuild_ipv6}")
                }
                val rr_ipv6 = IPAddress.parse(String.format("::ffff:%s", rebuild_ipv4))
                if (rr_ipv6.isErr()) {
                    //println!("---3|{}", &rebuild_ipv6)
                    return rr_ipv6
                }
                return rr_ipv6
            }
            return Result.Err("unknown mapped format:${str}")
        }
    }
}
