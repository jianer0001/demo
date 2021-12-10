FROM openjdk
MAINTAINER 821330378@qq.com
COPY target/demo-0.0.1-SNAPSHOT.jar /demo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/demo-0.0.1-SNAPSHOT.jar"]