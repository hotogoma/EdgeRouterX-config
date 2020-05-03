firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-name WANv6_IN {
        default-action drop
        description "WAN inbound traffic forwarded to LAN"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    ipv6-name WANv6_LOCAL {
        default-action drop
        description "WAN inbound traffic to the router"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
        rule 30 {
            action accept
            description "Allow IPv6 icmp"
            protocol ipv6-icmp
        }
        rule 40 {
            action accept
            description "allow dhcpv6"
            destination {
                port 546
            }
            protocol udp
        }
    }
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    name DSLite_IN {
        default-action drop
        description "DSLite to LAN"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    name DSLite_LOCAL {
        default-action drop
        description "DSLite to Router"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 40 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    name WAN_IN {
        default-action drop
        description "WAN to internal"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "WAN to router"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    options {
        mss-clamp {
            mss 1412
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        description "Internet (PPPoE)"
        dhcpv6-pd {
            pd 0 {
                interface switch0 {
                    host-address ::1
                    prefix-id :0
                    service slaac
                }
                prefix-length /60
            }
            prefix-only
            rapid-commit enable
        }
        duplex auto
        pppoe 0 {
            default-route auto
            firewall {
                in {
                    ipv6-name WANv6_IN
                    name WAN_IN
                }
                local {
                    ipv6-name WANv6_LOCAL
                    name WAN_LOCAL
                }
            }
            mtu 1492
            name-server auto
            password XXXXXXXX
            user-id XXXXXXXX
        }
        speed auto
    }
    ethernet eth1 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth2 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth3 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth4 {
        description "Local (AP)"
        duplex auto
        poe {
            output pthru
        }
        speed auto
    }
    ipv6-tunnel v6tun0 {
        description DSLite
        encapsulation ipip6
        firewall {
            in {
                name DSLite_IN
            }
            local {
                name DSLite_LOCAL
            }
        }
        local-ip 2409:252:9440:1410::1
        multicast disable
        remote-ip 2404:8e01::feed:100
        ttl 64
    }
    loopback lo {
    }
    switch switch0 {
        address 192.168.1.1/24
        description Local
        mtu 1500
        switch-port {
            interface eth1 {
            }
            interface eth2 {
            }
            interface eth3 {
            }
            interface eth4 {
            }
            vlan-aware disable
        }
    }
}
protocols {
    static {
        interface-route 0.0.0.0/0 {
            next-hop-interface pppoe0 {
                distance 100
            }
            next-hop-interface v6tun0 {
            }
        }
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN {
            authoritative enable
            subnet 192.168.1.0/24 {
                default-router 192.168.1.1
                dns-server 192.168.1.1
                lease 86400
                start 192.168.1.38 {
                    stop 192.168.1.243
                }
                static-mapping GPU {
                    ip-address 192.168.1.3
                    mac-address 00:01:2e:71:c4:cf
                }
                static-mapping NAS {
                    ip-address 192.168.1.2
                    mac-address 00:11:32:71:e5:07
                }
            }
        }
        static-arp disable
        use-dnsmasq disable
    }
    dns {
        forwarding {
            cache-size 150
            listen-on switch0
        }
    }
    gui {
        http-port 80
        https-port 443
        older-ciphers enable
    }
    nat {
        rule 5010 {
            description "masquerade for WAN"
            outbound-interface pppoe0
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
}
system {
    host-name ubnt
    login {
        user hoto {
            authentication {
                encrypted-password $5$tEX6nG9LuywuNgJp$jTIUnL0NeeTTFPRGbqbQwPzpLwcDAicSmPzUcfZ3/lD
                public-keys hoto {
                    key AAAAC3NzaC1lZDI1NTE5AAAAINnVuS7SbDnhncvy6W4U06DeRqYyWWuUtOpSNCSyoSl/
                    type ssh-ed25519
                }
            }
            level admin
        }
    }
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    static-host-mapping {
        host-name gpu.mrkch.jp {
            inet 192.168.1.3
        }
        host-name nas.mrkch.jp {
            inet 192.168.1.2
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone UTC
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:suspend@1:system@4:ubnt-pptp@1:ubnt-udapi-server@1:ubnt-unms@1:ubnt-util@1:vrrp@1:vyatta-netflow@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v2.0.8.5247496.191120.1124 */
