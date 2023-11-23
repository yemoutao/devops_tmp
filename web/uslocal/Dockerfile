FROM nginx:1.21.6

#ADD uslocal-accounts.updf.cn.conf /etc/nginx/conf.d/
#ADD uslocal-checkout.updf.cn.conf /etc/nginx/conf.d/
ADD *.updf.cn.conf /etc/nginx/conf.d/

RUN mkdir -pv /data/www/vhosts/uslocal-accounts.updf.cn/dist && \
    mkdir -pv /data/www/vhosts/uslocal-checkout.updf.cn/dist && \
    mkdir -pv /data/www/vhosts/uslocal-admin.updf.cn/dist && \
    rm -f /etc/nginx/conf.d/default.conf

ADD uslocal-accounts.updf.cn.tar.gz /data/www/vhosts/uslocal-accounts.updf.cn/
ADD uslocal-checkout.updf.cn.tar.gz /data/www/vhosts/uslocal-checkout.updf.cn/
ADD uslocal-admin.updf.cn.tar.gz /data/www/vhosts/uslocal-admin.updf.cn/

RUN chown -R nginx.nginx /data/www/vhosts/

