function edit_network() {
    echo -e " \e[0;$2m$1\e[0m"
    #初始化更改静态IP
    networkfile="/etc/sysconfig/network-scripts/ifcfg-ens192"
    sed -i 's/BOOTPROTO=dhcp/BOOTPROTO=static/g' $networkfile
    sed -i 's/ONBOOT=no/ONBOOT=yes/g' $networkfile
    cat << EOF >>  $networkfile
    IPADDR=192.168.50.244
    GATEWAY=192.168.50.1
    NETMASK=255.255.255.0
    DNS1=114.114.114.114
EOF
    service network restart
}


function edit_network_env() {
    echo -e " \e[0;$2m$1\e[0m"
    #初始化更改静态IP
    ETH=`ip a  | sed -n '1{p}' | tr ':' ' ' | awk '{print $1}'`
    networkfile="/etc/sysconfig/network-scripts/ifcfg-$ETH"
    sed -i 's/BOOTPROTO=*/BOOTPROTO=static/g' $networkfile
    sed -i 's/ONBOOT=no/ONBOOT=yes/g' $networkfile
    cat << EOF >>  $networkfile
    IPADDR=192.168.50.241
    GATEWAY=192.168.50.1
    NETMASK=255.255.255.0
    DNS1=114.114.114.114
EOF
    service network restart
}

function install_nginx() {
    yum install -y gd-devel openssl openssl-devel zlib zlib-devel pcre pcre-devel gcc c++ GeoIP GeoIP-devel GeoIP-data
    tar -zxf nginx-1.21.6.tar.gz 
    cd nginx-1.21.6
    ./configure --with-http_ssl_module --with-http_stub_status_module --with-http_gzip_static_module --http-client-body-temp-path=/var/tmp/nginx/client/ --http-proxy-temp-path=/var/tmp/nginx/proxy/ --http-fastcgi-temp-path=/var/tmp/nginx/fcgi/ --http-uwsgi-temp-path=/var/tmp/nginx/uwsgi --http-scgi-temp-path=/var/tmp/nginx/scgi --with-pcre --prefix=/usr/local/nginx
    make && make install
}


function install_mysql8() {
#    rpm -e mariadb  > /dev/null 2>&1
#    rpm –e mysql > /dev/null 2>&1
    read -p "mysql tar filename:" mysqlFileName
    tar -zxf $mysqlFileName.tar.gz
    mv $mysqlFileName /usr/local/mysql
    useradd mysql
    mkdir -pv /data/mysql/share
    cat >/etc/my.cnf << EOF
[mysql]
port = 3306
socket = /data/mysql/mysql.sock
[mysqld]
port = 3306
mysqlx_port = 33060
mysqlx_socket = /data/mysql/mysqlx.sock
basedir = /usr/local/mysql
datadir = /data/mysql/
socket = /data/mysql/mysql.sock
pid-file = /data/mysql/mysqld.pid
log-error = /data/mysql/error.log
#这个就是用之前的身份认证插件
default-authentication-plugin = mysql_native_password
#保证日志的时间正确
log_timestamps = SYSTEM
EOF
    chown -R mysql.mysql /data/mysql /usr/local/mysql /etc/my.cnf
    /usr/local/mysql/bin/mysqld --initialize --user=mysql --datadir=/data/mysql  --basedir=/usr/local/mysql
    ln -s  /usr/local/mysql/support-files/mysql.server /etc/rc.d/init.d/mysqld

}


#ALTER user 'root'@'localhost' IDENTIFIED BY '你的密码';
#CREATE USER  'user_name'@'host'  IDENTIFIED BY  'password';
#grant all privileges on *.* to 'username'@'host' with grant option;

install_mysql8


function install_php {
    yum install -y libxml2 libxml2-devel openssl openssl-devel bzip2 bzip2-devel libcurl libcurl-devel libjpeg libjpeg-devel libpng libpng-devel freetype freetype-devel gmp gmp-devel libmcrypt libmcrypt-devel readline readline-devel libxslt libxslt-devel zlib zlib-devel glibc glibc-devel glib2 glib2-devel ncurses curl gdbm-devel db4-devel libXpm-devel libX11-devel gd-devel gmp-devel expat-devel xmlrpc-c xmlrpc-c-devel libicu-devel libmcrypt-devel libmemcached-devel
    yum -y install readline-devel
    yum -y install http://mirror.centos.org/centos-7/7/cloud/x86_64/openstack-queens/Packages/o/oniguruma-6.7.0-1.el7.x86_64.rpm
    yum -y install http://mirror.centos.org/centos-7/7/cloud/x86_64/openstack-queens/Packages/o/oniguruma-devel-6.7.0-1.el7.x86_64.rpm
    yum install sqlite-devel -y
}