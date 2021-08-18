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
