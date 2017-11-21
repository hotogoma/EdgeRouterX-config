#!/bin/bash -eu

kill -SIGTERM $(cat /var/run/lighttpd.pid)
/usr/sbin/lighttpd -f /etc/lighttpd/lighttpd.conf
