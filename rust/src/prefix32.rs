// extern crate ipaddress;

//use std::vec::Vec;
// use num::bigint::BigUint;
// use num_traits::cast::ToPrimitive;
//use num_traits::cast::ToPrimitive;

// use ipaddress::Prefix;
// ![feature(associated_consts)]
// #[derive(Ord,PartialOrd,Eq,PartialEq,Debug,Copy,Clone)]
// pub struct Prefix32 {
//    pub num: u8
// }
pub struct Prefix32 {
}



impl Prefix32 {
    #[allow(unused_variables)]
    fn from(my: &::Prefix, num: u8) -> Result<::Prefix, String> {
        return Prefix32::new(num);
    }
    //  Gives the prefix in IPv4 dotted decimal format,
    //  i.e. the canonical netmask we're all used to
    //
    //    prefix = IPAddress::Prefix32.new 24
    //
    //    prefix.to_ip
    //      // => "255.255.255.0"
    //
    pub fn to_ip_str(my: &Vec<u16>) -> String {
          return format!("{}.{}.{}.{}",
            my.get(0).unwrap(), my.get(1).unwrap(),
            my.get(2).unwrap(), my.get(3).unwrap())
    }

    #[allow(unused_comparisons)]
    pub fn new(num: u8) -> Result<::Prefix, String> {
        if 0 <= num && num <= 32 {
            static _FROM: &'static (Fn(&::Prefix, u8) -> Result<::Prefix, String>) = &Prefix32::from;
            static _TO_IP_STR: &'static (Fn(&Vec<u16>) -> String) = &Prefix32::to_ip_str;
            return Ok(::Prefix {
                num: num,
                ip_bits: 32,
                ip_part_bits: 8,
                in_mask: ::Prefix::in_mask(32),
                vt_from: _FROM,
                vt_to_ip_str: _TO_IP_STR
            });
        }
        return Err(format!("Prefix must be in range 0..32, got: {}", num));
    }

    pub fn parse_netmask(netmask: String) -> Result<::Prefix, String> {
        let mut shift = 24;
        let mut ip: u32 = 0;
        for i in netmask.split(".") {
            ip = ip | ((i.parse::<u8>().unwrap() as u32) << shift);
            shift -= 8;
        }

        let mut nulls = 0;
        while nulls < 32 {
            if 0 != (ip & 0x1) {
                break;
            }
            ip = ip >> 1;
            nulls += 1;
        }
        let mut one_prefix = 0;
        while nulls < 32 {
            if 1 == (ip & 0x1) {
                one_prefix += 1;
            } else {
                return Err(format!("Prefix must be 111 and 000 {}", &netmask));
            }
            ip = ip >> 1;
            nulls += 1;
        }
        return Prefix32::new(one_prefix);
    }



    //  An array of octets of the IPv4 dotted decimal
    //  format
    //
    //    prefix = IPAddress::Prefix32.new 24
    //
    //    prefix.octets
    //      // => [255, 255, 255, 0]
    //
    // pub fn octets(&self) -> Vec<u16> {
    //     let value = self.to_u32();
    //     return vec![(value >> 24) as u8, (value >> 16) as u8, (value >> 8) as u8, value as u8];
    // }

    //  Unsigned 32 bits decimal number representing
    //  the prefix
    //
    //    prefix = IPAddress::Prefix32.new 24
    //
    //    prefix.to_u32
    //      // => 4294967040
    //
    // pub fn to_u32(&self) -> u32 {
    //     4711
    //     // return self.net_mask().to_u32()
    //     // return (::IN4MASK >> self.host_prefix()) << self.host_prefix()
    // }

    //  Shortcut for the octecs in the dotted decimal
    //  representation
    //
    //    prefix = IPAddress::Prefix32.new 24
    //
    //    prefix[2]
    //      // => 255
    //
    // pub fn get(&self, index: usize) -> u8 {
    //     return *self.octets().get(index).unwrap();
    // }
    // pub fn hostmask(&self) -> String {
    //     return ::to_ipv4_str(self.to_u32().wrapping_neg());
    // }
    //
    //  Creates a new prefix by parsing a netmask in
    //  dotted decimal form
    //
    //    prefix = IPAddress::Prefix32::parse_netmask "255.255.255.0"
    //      // => 24
    //

} //  class Prefix32 < Prefix