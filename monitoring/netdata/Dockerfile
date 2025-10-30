FROM netdata/netdata:stable
USER root

# Paquetes para email
RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
    msmtp msmtp-mta ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# Config msmtp
COPY msmtprc /etc/msmtprc
#RUN chown root:root /etc/msmtprc && chmod 600 /etc/msmtprc
RUN chgrp netdata /etc/msmtprc && chmod 640 /etc/msmtprc \
 && ln -sf /usr/bin/msmtp /usr/sbin/sendmail \
 && ln -sf /usr/bin/msmtp /usr/bin/sendmail

RUN ln -sf /usr/bin/msmtp /usr/sbin/sendmail
