## v4.1.0 IN PROGRESS
### Tech dept
* Reduce log verbosity and remove sensitive RMAPI data exposure ([FHIQC-45](https://issues.folio.org/browse/FHIQC-45))

---

## v4.0.0 2025-02-28
### Breaking changes
* Update module to Java 21 & the latest dependencies ([FHIQC-40](https://issues.folio.org/browse/FHIQC-40))
* Use x-okapi-user-id header instead JWT token parsing ([FHIQC-41](https://issues.folio.org/browse/FHIQC-41))

### Dependencies
* Bump `java` from `17` to `21`
* Bump `vertx` from `4.5.10` to `4.5.13`
* Bump `lombok` from `1.18.34` to `1.18.36`
* Bump `jackson` from `2.17.1` to `2.18.2`
* Bump `log4j` from `2.24.1` to `2.24.3`
* Add `commons-collections4` version `4.4`
* Add `commons-lang3` version `3.17.0`
* Add `okapi-common` version `6.2.0`
* Removed `folio-service-tools`
---

## v3.2.0 2024-10-29
### Dependencies
* Bump `folio-service-tools` from `4.0.0` to `4.1.0`
* Bump `vertx` from `4.5.5` to `4.5.10`
* Bump `lombok` from `1.18.30` to `1.18.34`
* Bump `jackson` from `2.17.0` to `2.17.1`
* Bump `commons-validator` from `1.8.0` to `1.9.0`
* Bump `log4j` from `2.23.1` to `2.24.1`

## v3.1.0 2024-03-18
### Dependencies
* Bump `folio-service-tools` from `3.1.0` to `4.0.0`
* Bump `vertx` from `4.4.6` to `4.5.5`
* Bump `jackson` from `2.15.2` to `2.17.0`
* Bump `commons-validator` from `1.7` to `1.8.0`
* Bump `log4j` from `2.20.0` to `2.23.1`

## 3.0.0 2023-10-11
### Breaking changes
* Update module to Java 17 & the latest dependencies ([FHIQC-30](https://issues.folio.org/browse/FHIQC-30))

### Features
* Add packages facet to response and new packageidfilter filter for GET /eholdings/titles ([FHIQC-29](https://issues.folio.org/browse/FHIQC-29))
* Update GET /eholdings/resources/resourceId to include proxiedUrl ([FHIQC-31](https://issues.folio.org/browse/FHIQC-31))

### Bug fixes
* Use the force parameter for creating a snapshot ([FHIQC-33](https://issues.folio.org/browse/FHIQC-33))

### Dependencies
* Bump `java` from `11` to `17`
* Bump `folio-service-tools` from `1.10.1` to `3.1.0`
* Bump `vertx` from `4.3.8` to `4.4.6`
* Bump `lombok` from `1.18.26` to `1.18.30`
* Bump `jackson` from `2.14.2` to `2.15.2`
* Bump `log4j` from `2.19.0` to `2.20.0`

## 2.3.0 2023-02-14
### Dependencies
* Bump `folio-service-tools` from `1.10.0` to `1.10.1`
* Bump `vertx` from `4.3.4` to `4.3.8`
* Bump `lombok` from `1.18.24` to `1.18.26`
* Bump `jackson` from `2.13.4` to `2.14.2`
* Bump `mockito` from `4.8.0` to `5.1.1`

## 2.2.0 2022-10-19
* FHIQC-23 Upgrade to VertX 4.3.4
* FHIQC-25 Remove usage of PowerMock

## 2.1.0 2022-06-17
* FHIQC-19 Upgrade to VertX 4.3.1

## 2.0.0 2022-02-23
* FHIQC-13 Update 'searchtype' for packages and titles endpoints
* FHIQC-19 Upgrade to VertX 4.2.5

## 1.12.0 2021-06-09
* FHIQC-10 Load Holdings: Refactor using of Vert.X WebClient
* FHIQC-11 Upgrade to Vert.X v4.1.0.CR1

## 1.11.0 2021-03-04
* FHIQC-7 Upgrade to Vert.X v4

## 1.10.3 2020-12-22
* MODKBEKBJ-550 Return ConfigurationError when url is invalid

## 1.10.2 2020-11-04
* MODKBEKBJ-515 GET Titles search | change searchtype from advanced to contains

## 1.10.1 2020-11-02
* MODKBEKBJ-505 Log responses from HoldingsIQ/MODKBEKBJ

## 1.10.0 2020-10-06
* MODKBEKBJ-463 Change status endpoint to send correct messages
* FHIQC-3 Migrate to JDK 11

## 1.9.0 2020-06-07
* FHIQC-1 - Update Vert.X to v3.9.0

## 1.8.0 2020-05-18
* MODKBEKBJ-381 - Using a different RM API Endpoint for status checks
* MODKBEKBJ-432 - Get configuration settings from eHoldings
* Save okapi headers inside OkapiData

## 1.7.0 2020-02-11
* MODKBEKBJ-334 - Add userDefinedFields to TitlePost
* MODKBEKBJ-335 - Add userDefinedFields to PUT resource request
* MODKBEKBJ-344 - Implement new Holdings IQ endpoints

## 1.6.0 2019-11-25
* MODKBEKBJ-339 - Remove dependency on RMB

## 1.5.0 2019-09-24
* MODKBEKBJ-311 - Add "isFullPackage" to PackagePut

## 1.4.0 2019-09-10
* MODKBEKBJ-294 - updated post-processing for titles. 

## 1.3.0 2019-06-05
* Add service for creating and loading holdings snapshot

## 1.2.0 2019-05-07
* Add getValueOrLoad() method to VertxCache

## 1.1.0 2019-03-06
* Add parameter validation 

## 1.0.0 2019-03-01
* Initial module setup.
* Released client library to HoldingsIQ API.
