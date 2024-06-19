# How to setup this project

## Prerequisite
> First, you need to install Redis and run it on the default port of your local machine.

## Trigger

1. Start eureka-server
2. Start query-server
3. Start api-gateway

## Post Check

### Check Eureka
1. Access Eureka via `http://localhost:8761`
2. You should see two components there, one query-server, the other is api-gateway

### Check query-server
1. Access QS via `http://localhost:8080/query/dataset?dataSet=anyDataSet`
2. Input username and password as `admin` both
3. Click Login, then you should see the dummy response.

### Check api-gateway
1. Access AGW via `http://127.0.0.1:8888/query/dataset?username=admin&version=1`
2. You should see the dummy response

> #### Q1: why not localhost
> because we set RemoteAddr predicates as 127.0.0.1 in AGW
> #### Q2: why no need input dataSet as param, but username and version
> because we want to test gateway predicates, and filters as following
```yaml
  predicates:
    - Path=/query/**
    - Query=username,admin
    - name: Query
      args:
        param: version
        regexp: \d{1,2}
  filters:
    - AddRequestParameter=dataSet,test.dataSet
```