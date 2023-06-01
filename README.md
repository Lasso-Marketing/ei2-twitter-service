# ei2-twitter-service

A Cloud Run service... [description]

## Getting Started
### Prerequisites
* Gradle 8.1+
* JDK 17+
* git
* Docker Engine
* Docker Compose
* `gcloud` CLI
  * Install helpful components (with `gcloud components install ...`):
    * docker-credential-gcr

## Running

To run with `docker-compose`, run from the project root:

    $ gradle jibDockerBuild && docker-compose up

In IntelliJ IDEA, a Compose Deployment can be used, with `jibDockerBuild`
configured as a "Before launch" step.

Alternatively you can run the class Ei2TwitterServiceApplication.
Apply environment entry `SPRING_PROFILES_ACTIVE=local`.

To run locally from the terminal use the following command
`./gradlew bootRun --args='--spring.profiles.active=local'`

## Testing

TODO

## Maintenance
This project most likely was generated initially from
[ei2-service-template](https://github.com/Lasso-Marketing/ei2-service-template).
That project should be monitored for changes that may need to be copied here
to support features required for all services.

## Project details
### Organization
* `app/` is for application source and Gradle configuration
  * `app/src/intTest` contains integration tests
* `bin/` has some useful binaries for builds/images
* `cloudbuild/` contains Cloud Build configuration

### Spring

* Profile "local" generally should be applied when running locally
* JSON-formatted values in GCP Secrets Manager are additionally supported
  (see Java class `GcpSecretsPropertySourceLocator`). The `sm://` convention is
  always another option where preferable.


---

###### Generated from ei2-service-template