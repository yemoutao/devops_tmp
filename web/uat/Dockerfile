FROM nginx:1.21.6

#ADD uat-accounts.updf.com.conf /etc/nginx/conf.d/
#ADD uat-checkout.updf.com.conf /etc/nginx/conf.d/
ADD *.updf.com.conf /etc/nginx/conf.d/

RUN mkdir -pv /data/www/vhosts/uat-accounts.updf.com/dist && \
    mkdir -pv /data/www/vhosts/uat-checkout.updf.com/dist && \
    rm -f /etc/nginx/conf.d/default.conf

ADD accounts.tar.gz /data/www/vhosts/uat-accounts.updf.com/
ADD checkout.tar.gz /data/www/vhosts/uat-checkout.updf.com/
ADD admin.tar.gz /data/www/vhosts/admin.updf.com/

RUN chown -R nginx.nginx /data/www/vhosts/

