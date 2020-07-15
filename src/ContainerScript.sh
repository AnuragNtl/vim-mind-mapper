apk add vim && \
     chmod +x /task && \
     apk add gcc && \
     apk add g++ && \
     apk add bash && \
     apk add openjdk11-jre && \
     apk add curl && \
     apk add python3 && \ 
     apk add python3-dev  && \
     apk add py-pip && \
     apk add ttyd && \
     pip3 install -r /requirements.txt && \
     apk add --update nodejs npm && \
     npm install --prefix /graphVisualize && \
     cd /usr/local/ && \
    ln -s /usr/local/groovy-3.0.4 groovy && \
    /usr/local/groovy/bin/groovy -v && \
    cd /usr/local/bin && \
    ln -s /usr/local/groovy/bin/groovy groovy && \
    ln -s /task /usr/bin/task && \
    [ -e /usr/bin/python ] && rm /usr/bin/python && \
    ln -s /usr/bin/python3 /usr/bin/python

