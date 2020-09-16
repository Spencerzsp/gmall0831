#!/bin/bash
gmall0831=/home/spencer/jar
case $1 in
start)
    for host in wbbigdata00 wbbigdata01 wbbigdata02 ; do
        ssh $host "source /etc/profile; nohup java -jar $gmall0831/gmall-logger-0.0.1-SNAPSHOT.jar 1>>$gmall0831/gmall.out 2>>$gmall0831/gmall.error &"
    done
;;
stop)
    for host in wbbigdata00 wbbigdata01 wbbigdata02 ; do
        ssh $host "source /etc/profile; jps | grep gmall-logger-0.0.1-SNAPSHOT.jar | awk {'print \$1'} | xargs kill -9"
    done
;;
help)
    echo "启动日志服务："
    echo "  all_log.sh start"
    echo "停止日志服务："
    echo "  all_log.sh stop"

;;
*)
    echo "你的姿势不对，换个姿势再来一次！"
;;
esac