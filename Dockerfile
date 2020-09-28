FROM openjdk:11

COPY . /src

RUN set -x \
  && cd /src \
  && ./mvnw clean package \
  && ls -alR

FROM openjdk:11

RUN set -x \
  && mkdir /app

COPY --from=0 /src/mocks/gateway-mock/target/gateway-mock-1.0.0-SNAPSHOT.jar /app/

WORKDIR /app

CMD ["java", "-jar", "gateway-mock-1.0.0-SNAPSHOT.jar"]