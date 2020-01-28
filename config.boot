firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
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
            action accept
            description "Allow L2TP"
            destination {
                port 500,1701,4500
            }
            log disable
            protocol udp
        }
        rule 40 {
            action accept
            description "Allow ESP"
            log disable
            protocol esp
        }
        rule 50 {
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
        duplex auto
        pppoe 0 {
            default-route auto
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
port-forward {
    auto-firewall enable
    hairpin-nat enable
    lan-interface switch0
    wan-interface pppoe0
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
                start 192.168.1.2 {
                    stop 192.168.1.199
                }
                static-mapping GPU {
                    ip-address 192.168.1.3
                    mac-address 00:01:2e:71:c4:cf
                }
                static-mapping NAS {
                    ip-address 192.168.1.2
                    mac-address 00:11:32:71:e5:07
                }
                static-mapping PI {
                    ip-address 192.168.1.4
                    mac-address b8:27:eb:87:3d:ad
                }
                static-mapping PRINTER {
                    ip-address 192.168.1.5
                    mac-address 38:9d:92:bc:e0:cf
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
            listen-on lo
            options listen-address=192.168.1.1
        }
    }
    gui {
        cert-file /config/auth/server.pem
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
    host-name mrkch.jp
    ip {
        override-hostname-ip 192.168.1.1
    }
    login {
        user hoto {
            authentication {
                encrypted-password $6$YZIKJ5KEg0rAN$XAzaoR9JH/O1enPUSHG3t6frXHMQctlF3aPGMKTA2AKyrs8dr9uPF9r0nWWAA3NQrPh3PCsZr/OtD.XgixMoY0
                plaintext-password ""
                public-keys DM-1702095 {
                    key AAAAC3NzaC1lZDI1NTE5AAAAINnVuS7SbDnhncvy6W4U06DeRqYyWWuUtOpSNCSyoSl/
                    type ssh-ed25519
                }
                public-keys HotoMac {
                    key AAAAC3NzaC1lZDI1NTE5AAAAID/cYHQa6M4tOGiMaQ/kQmH0E1vuCnvRg91KjXkYAh2s
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
        host-name pi.mrkch.jp {
            inet 192.168.1.4
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
    traffic-analysis {
        dpi disable
        export disable
    }
}
vpn {
    ipsec {
        allow-access-to-local-interface disable
        auto-firewall-nat-exclude disable
        ipsec-interfaces {
            interface pppoe0
        }
        nat-networks {
            allowed-network 0.0.0.0/0 {
            }
        }
        nat-traversal enable
    }
    l2tp {
        remote-access {
            authentication {
                local-users {
                    username goma {
                        password XXXXXXXX
                    }
                    username hoto {
                        password XXXXXXXX
                    }
                    username papix {
                        password XXXXXXXX
                    }
                }
                mode local
            }
            client-ip-pool {
                start 192.168.1.200
                stop 192.168.1.254
            }
            dns-servers {
                server-1 192.168.1.1
                server-2 8.8.8.8
            }
            idle 1800
            ipsec-settings {
                authentication {
                    mode pre-shared-secret
                    pre-shared-secret XXXXXXXX
                }
                ike-lifetime 3600
                lifetime 3600
            }
            mtu 1280
            outside-address 0.0.0.0
        }
    }
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:suspend@1:system@4:ubnt-pptp@1:ubnt-udapi-server@1:ubnt-unms@1:ubnt-util@1:vrrp@1:vyatta-netflow@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v2.0.8.5247496.191120.1124 */
