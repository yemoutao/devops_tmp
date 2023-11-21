#bin/bash
#create user 'updf_save'@'%' identified by 'ae2LdteYdrjsave';
#grant execute,SELECT,RELOAD,LOCK TABLES,REPLICATION CLIENT,SHOW VIEW,EVENT,TRIGGER,PROCESS on *.* to updf_save;
#flush privileges;

if [ -f "/tmp/bak_updf.sql" ];then
   rm -f /tmp/bak_updf.sql
else 
   echo "文件不存在"
fi

DATE=`date +%Y-%m-%d` 
DB_NAME="updf"
USER_BAK="updf_save"
PASSWD_BAK="ae2LdteYdrjsave"
PROD_ADR="pc-0xij4224ig74bgco3.mysql.polardb.rds.aliyuncs.com"

mysqldump -u $USER_BAK -p$PASSWD_BAK -h $PROD_ADR $DB_NAME > /tmp/$DB_NAME-$DATE.sql


sleep 5s


UAT_ADR="pc-0xi60719p94ux678m.mysql.polardb.rds.aliyuncs.com"

mysql -u $USER_BAK -p$PASSWD_BAK -h $UAT_ADR < /tmp/$DB_NAME-$DATE.sql

mv /tmp/$DB_NAME-$DATE.sql  /tmp/bak_updf.sql