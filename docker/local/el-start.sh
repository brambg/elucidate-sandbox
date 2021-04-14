docker-compose up --detach
ip=$(ifconfig eth0|grep 172|cut -d " " -f 10)
echo "elucidate is running at http://$ip:8080/annotation"
