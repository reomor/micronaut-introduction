FROM ghcr.io/graalvm/graalvm-ce:latest as builder
#FROM ghcr.io/graalvm/graalvm-ce:java11-21.1.0 as builder
RUN gu install native-image

COPY . /app
WORKDIR /app

RUN chmod +x mvnw && ./mvnw clean package -DskipTests=true -Dpackaging=native-image
#RUN native-image -cp /app/target/mn-fund*.jar com.github.reomor.FundApplication

FROM frolvlad/alpine-glibc:alpine-3.12
RUN apk update && apk add libstdc++ libgcc
#RUN apk update && apk add libc6-compat libstdc++
#RUN apk add --no-cache --update \
#    libc6-compat \
#    libstdc++

COPY --from=builder /app/target/mn-fund /app/mn-fund
EXPOSE 8082

CMD /app/mn-fund