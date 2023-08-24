#!/bin/bash

set -e

base_dir=$(cd $(dirname "$0");pwd)
project_dir=`dirname ${base_dir}`

JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
CATALINA_HOME=/home/wenlin/opt/apache-tomcat-8.5.84
PATH=/home/wenlin/opt/apache-maven-3.8.6/bin:$PATH
export JAVA_HOME=${JAVA_HOME} CATALINA_HOME=${CATALINA_HOME} PATH=${PATH}

WAR_NAME=my_store
DATABASE_NAME=store
MYSQL_ROOT_PASSWORD=888888

# 部署mysql
  if [ -n "`docker ps | grep mysql`" ]; then
    echo "Mysql已经部署过了。"
  else
    docker run --rm -d --name mysql \
      -e TZ=Asia/Shanghai \
      -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
      -v ${project_dir}/deployment/mysql.cnf:/etc/mysql/conf.d/mysql.cnf \
      -p 3306:3306 \
      mysql
  fi
  set +e
  while true; do
    r_code=`echo "show databases" |docker exec -i mysql mysql -u root --password=${MYSQL_ROOT_PASSWORD}`
    if [ 0 == "$?" ]; then
      echo "Mysql部署ok。"
      break
    else
      echo "等待Mysql启动..."
      sleep 1
    fi
  done
  set -e

# 初始化mysql
  set +e
  while true; do
    r_code=`docker exec -i mysql mysql -u root --password=${MYSQL_ROOT_PASSWORD} < ${base_dir}/clear_db.sql`
    if [ 0 == "$?" ]; then
      echo "Mysql database reset ok。"
      break
    else
      echo "Mysql database reset 失败，重试中..."
      sleep 1
    fi
  done

  while true; do
    r_code=`docker exec -i mysql mysql -u root --password=${MYSQL_ROOT_PASSWORD} ${DATABASE_NAME} < ${base_dir}/init_db.sql`
    if [ 0 == "$?" ]; then
      echo "Mysql初始化ok。"
      break
    else
      echo "Mysql初始化失败，重试中..."
      sleep 1
    fi
  done
  set -e

# 部署redis
  if [ -n "`docker ps | grep redis`" ]; then
    echo "Redis已经部署过了。"
  else
    docker run --rm -d --name redis -p 6379:6379 redis
    echo "Redis部署完成。"
  fi

# 构建部署Application
  cd ${project_dir}
  mvn clean package;
  # rm -rf ${project_dir}/target/${WAR_NAME}

  # 部署tomcat
    # if [ -n "`ps aux |grep tomcat | grep -v 'grep'`" ]; then
    #   echo "Tomcat已经部署过了。"
    # else
    #   ${CATALINA_HOME}/bin/startup.sh
    # fi
    # rm -rf ${CATALINA_HOME}/webapps/$WAR_NAME
    # mv ${project_dir}/target/${WAR_NAME} ${CATALINA_HOME}/webapps/${WAR_NAME}

  if [ -n "`docker ps | grep tomcat`" ]; then
    echo "tomcat已经部署过了，重启中"
    docker restart tomcat
  else
    set +e; docker  network  disconnect  --force  host tomcat; set -e
    docker run --privileged --net=host --rm -d --name tomcat \
        -e LOG_LEVEL=debug \
        -e TZ=Asia/Shanghai \
        -e LOG_FILE=/var/log/store.log \
        -v ${project_dir}/target/${WAR_NAME}:/usr/local/tomcat/webapps/${WAR_NAME} \
        -v ${project_dir}/store.log:/var/log/store.log \
        -p 8080:8080 \
        tomcat:8.5.84
    echo "tomcat部署完成。"
  fi
  # curl -H"Cookie: JSESSIONID=<>" http://localhost:8080/manager/text/reload?path=/${WAR_NAME}

  set +e
  while true; do
    r_code=`curl -s -o /dev/null -w %{http_code} http://localhost:8080/${WAR_NAME}/`
    if [ 0 == "$?" ] && [ 200 == "${r_code}" ]; then
      echo "App部署ok。"
      break
    else
      echo "等待App启动..."
      sleep 0.1
    fi
  done
  set -e

