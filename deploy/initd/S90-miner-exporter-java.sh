#!/bin/sh

DAEMON="miner-exporter"
PIDFILE="/var/run/$DAEMON.pid"

if [ -f /etc/default/miner-exporter ]; then
  . /etc/default/miner-exporter
fi

start() {
        printf 'Starting %s: ' "$DAEMON"
        start-stop-daemon -S -q -b -m -p "$PIDFILE" -x $DAEMON
        status=$?
        if [ "$status" -eq 0 ]; then
                echo "OK"
        else
                echo "FAIL"
        fi
        return "$status"
}

stop() {
        printf 'Stopping %s: ' "$DAEMON"
        start-stop-daemon -K -q -p "$PIDFILE"
        status=$?
        if [ "$status" -eq 0 ]; then
                echo "OK"
        else
                echo "FAIL"
        fi
        return "$status"
}

restart() {
        stop
        sleep 1
        start
}

case "$1" in
        start|stop|restart)
                "$1";;
        reload)
                restart;;
        *)
                echo "Usage: $0 {start|stop|restart|reload}"
                exit 1
esac