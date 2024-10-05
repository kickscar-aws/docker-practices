FROM redis:7.4-alpine

VOLUME /data
COPY config/redis.conf /config/redis.conf
RUN chown -R redis:redis /run

EXPOSE 6379

ENTRYPOINT ["redis-server", "/config/redis.conf"]