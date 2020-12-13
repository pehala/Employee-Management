# core project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `core-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/core-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/core-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

# RESTEasy JAX-RS

<p>A Hello World RESTEasy resource</p>

Guide: https://quarkus.io/guides/rest-json

## Run database
```
docker run --rm \
          --name dev-postgres \
          -e POSTGRES_PASSWORD=pass \
          -v ${HOME}/postgres-data/:/var/lib/postgresql/data:Z \
          -p 5432:5432 \
          postgres
```
`psql -h localhost -U postgres`

## DayWork
 http POST :8080/work/create date=2020-02-02 employee:='{"address": "Brno","dateOfBirth": "1990-01-01","hourlyWage": 5.0,"id": 1,"insuranceCompany": "Union","mobile": "0900000000","name": "Martin","startContract": "2020-11-01","surname": "Hrasko"}' order:='{"id": 3,"info": "Zasad mrkvu","mobile": "0910000000","name": "Jozef","state": "PENDING","surname": "Orac"}'
