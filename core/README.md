core service
================
REST API Employee
--------
* [POST] `/api/employee/create`\
  Creates new employee. Requires admin rights.
* [PUT] `/api/employee/<id>/update`\
  Updates employee with given `id`. Requires admin rights.
* [DELETE] `/api/employee/<id>/delete`\
  Removes employee with given `id`. Requires admin rights.
* [GET] `/api/employee/<id>`\
  Returns employee with given `id`. Requires admin rights.
* [GET] `/api/employee/`\
  Returns all employees. Requires admin rights.
* [GET] `/api/employee/search/<id>`\
  Query params: Substring `search`.\
  Returns employees who has substring `search` in name or surname or username. Requires admin rights.
  If there is no employee with substring in name or surname or username, then empty list is returned.
* [GET] `/api/employee/me`\
  Returns employee who is requested.
  
REST API Order
--------
* [POST] `/api/order/create`\
  Creates new order. Requires admin rights.
* [PUT] `/api/order/<id>/update`\
  Updates order with given `id`. Requires admin rights.
* [DELETE] `/api/order/<id>/delete`\
  Removes order with given `id`. Requires admin rights.
* [GET] `/api/order/<id>`\
  Returns order with given `id`.
* [GET] `/api/order/`\
  Returns all orders.
* [GET] `/api/order/search`\
  Query params: Substring `search`.\
  Returns orders which has substring `search` in name or surname or company.
  If there is no order with substring in name or surname or company, then empty list is returned.
  
REST API Workday
--------
* [POST] `/api/workday/create`\
  Creates new workday. Requires admin rights.
* [PUT] `/api/workday/<id>/update`\
  Updates workday with given `id`. Requires admin rights.
* [DELETE] `/api/workday/<id>/delete`\
  Removes workday with given `id`. Requires admin rights.
* [GET] `/api/workday/<id>`\
  Returns workday with given `id`.
* [GET] `/api/workday/`\
  Returns all workdays. Requires admin rights.
* [GET] `/api/workday/employee/{id}/date`\
  Query params: Dates `from` and `to`.\
  Returns workdays of employee with given `id` within dates `from` to `to`.
  If the employee has no workday within given range, then empty list is returned.
* [GET] `/api/workday/order/{id}`\
  Returns workdays of order with given `id`.
  If the order has no workday, then empty list is returned.
  
## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Run database
```
docker run --rm \
          --name dev-postgres \
          -e POSTGRES_PASSWORD=pass \
          -v ${HOME}/postgres-data/:/var/lib/postgresql/data:Z \
          -p 5432:5432 \
          postgres
```

login to postgre client
`psql -h localhost -U postgres`

commands
```
create database core;
create database salary;
create database vacation;
\l
\c core
\dt
```

## Run prometheus
```
 docker run --rm --name prometheus -p 9090:9090  -v <path to project>/Employee-Management/core/src/main/resources/prometheus.yml:/etc/prometheus/prometheus.yml:Z prom/prometheus:v2.14.0 --config.file=/etc/prometheus/prometheus.yml
```

## Create token
admin
```
export access_token_admin=$(\
    curl --insecure -X POST http://localhost:8180/auth/realms/myrealm/protocol/openid-connect/token \
    --user employee-management:710078a7-7db6-4268-9cfa-3b39f2630412 \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=admin&password=admin&grant_type=password' | jq --raw-output '.access_token' \
 )
```

user
```
export access_token=$(\
    curl --insecure -X POST http://localhost:8180/auth/realms/myrealm/protocol/openid-connect/token \
    --user employee-management:710078a7-7db6-4268-9cfa-3b39f2630412 \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=adam&password=adam&grant_type=password' | jq --raw-output '.access_token' \
 )
```