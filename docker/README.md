## DB

### Postgres
```shell
docker run --name mn-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=mn-stock -p 15432:5432 -d postgres
```

* user: postgres
* password: secret
* db_name: mn-stock

### MySQL

```shell
docker run --name mn-mysql -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_PASSWORD=secret -e MYSQL_DATABASE=mn-fund -p 13306:3306 -d mysql:8.0
```

* user: root
* password: secret
* db_name: mn-fund

### Kafka

[docker-compose.yml](https://github.com/lensesio/fast-data-dev)
lensesio/fast-data-dev:2.2
```shell
docker-compose up -d
```

Lenses.io
```shell
docker run -e ADV_HOST=127.0.0.1 -e EULA="https://licenses.lenses.io/download/lensesdl?id=ID" --rm -p 3030:3030 -p 9092:9092 lensesio/box
```

### Native

build

```shell
cd mn-fund
docker build -t mn-fund .
```

run service

```shell
cd docker
docker network create -d overlay testservice
docker stack deploy -c mn-fund.stack.yml mn-fund-stack
docker stats
docker stack rm mn-fund-stack
```

### Redis

```shell
docker run --name mn-redis -p 6379:6379 -d redis:6-alpine
```

### MongoDB

```shell
docker run --name mn-mogodb -p 27017:27017 mongo:4.4
```
