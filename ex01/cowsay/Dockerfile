FROM debian

RUN apt update && apt install -y cowsay fortune

COPY entrypoint.sh /
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
