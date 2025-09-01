모바일 신분증 인증 sp-with-sdk
========

## Profiles
local, dev, prod

## JDK (1.8)

## Build & Push & Cmd
#### build
```
docker build ./ -t growis-registry.kr.ncr.ntruss.com/sp-with-sdk:{version} --platform linux/amd64,linux/arm64
```
#### push
```
docker push growis-registry.kr.ncr.ntruss.com/sp-with-sdk:{version}
```
#### docker compose up
```
docker compose -f docker/sp-with-sdk/docker-compose.yml up -d
```
#### logs
```
tail -f docker/sp-with-sdk/logs/app.log
```
