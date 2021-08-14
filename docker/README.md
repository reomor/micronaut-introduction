### DB

```shell
docker run --name mn-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=mn-stock -p 15432:5432 -d postgres
```

* user: postgres
* password: secret
* db_name: mn-stock
