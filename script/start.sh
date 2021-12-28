#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ec2-user/apps
DEPLOY_DIRECTORY=/home/ec2-user/deploy
PROJECT_NAME=rest-api

echo "> old 파일 이동"
mv $REPOSITORY/$PROJECT_NAME/*.jar $REPOSITORY/$PROJECT_NAME/old/

echo "> Build 파일 복사"
cp $DEPLOY_DIRECTORY/$PROJECT_NAME/*.jar $REPOSITORY/$PROJECT_NAME/

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/$PROJECT_NAME/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"
chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"
IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."
nohup java -jar \
    -Dspring.config.location=classpath:/application.yml,classpath:/application-$IDLE_PROFILE.yml,/home/ec2-user/apps/config/application-db.yml \
    -Dspring.profiles.active=$IDLE_PROFILE \
    -Dfile.encoding=UTF-8 \
    $JAR_NAME > $REPOSITORY/$PROJECT_NAME/nohup.out 2>&1 &