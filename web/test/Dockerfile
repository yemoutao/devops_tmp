FROM nginx:1.21.6

#ADD local-accounts.updf.cn.conf /etc/nginx/conf.d/
#ADD local-checkout.updf.cn.conf /etc/nginx/conf.d/
ADD *.updf.cn.conf /etc/nginx/conf.d/

RUN mkdir -pv /data/www/vhosts/local-accounts.updf.cn/dist && \
    mkdir -pv /data/www/vhosts/local-checkout.updf.cn/dist && \
    mkdir -pv /data/www/vhosts/local-admin.updf.cn/dist && \
    rm -f /etc/nginx/conf.d/default.conf

ADD local-accounts.updf.cn.tar.gz /data/www/vhosts/local-accounts.updf.cn/
ADD local-checkout.updf.cn.tar.gz /data/www/vhosts/local-checkout.updf.cn/
ADD local-admin.updf.cn.tar.gz /data/www/vhosts/local-admin.updf.cn/

RUN chown -R nginx.nginx /data/www/vhosts/

