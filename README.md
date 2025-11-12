# folio-holdingsiq-client

[![FOLIO](https://img.shields.io/badge/FOLIO-Library-green)](https://www.folio.org/)
[![Release Version](https://img.shields.io/github/v/release/folio-org/folio-holdingsiq-client?sort=semver&label=Latest%20Release)](https://github.com/folio-org/folio-holdingsiq-client/releases)
[![Java Version](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/projects/jdk/21/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.folio%3Afolio-holdingsiq-client&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=org.folio%3Amod-quick-marc)

Copyright © 2019–2025 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

<!-- TOC -->
* [folio-holdingsiq-client](#folio-holdingsiq-client)
  * [Introduction](#introduction)
  * [Overview](#overview)
    * [Main Components](#main-components)
    * [Getting Started](#getting-started)
      * [Configuration](#configuration)
      * [Service Initialization](#service-initialization)
      * [Example Usage](#example-usage)
  * [Additional Information](#additional-information)
    * [Issue tracker](#issue-tracker)
    * [Contributing](#contributing)
<!-- TOC -->

## Introduction

A Java client library for interacting with the HoldingsIQ (RMAPI) service. This library provides a high-level API for managing electronic resources, including providers, packages, titles, and resources in FOLIO.

**Requirements:**
- Vert.x 5.0.x+
- Java 21+

## Overview

The `folio-holdingsiq-client` provides a comprehensive Java client for the HoldingsIQ API, enabling FOLIO modules to manage electronic holdings and knowledge base data.

### Main Components

- **HoldingsIQService** — Main service interface for interacting with HoldingsIQ API
- **ProviderHoldingsIQService** — Manages provider (vendor) operations
- **PackagesHoldingsIQService** — Handles package management
- **TitlesHoldingsIQService** — Manages title operations
- **ResourcesHoldingsIQService** — Handles resource management
- **ConfigurationService** — Manages API configuration and credentials

### Getting Started

#### Configuration

Create a `Configuration` object with your HoldingsIQ API credentials:

```java
Configuration config = Configuration.builder()
  .customerId("your-customer-id")
  .apiKey("your-api-key")
  .url("https://api.ebsco.io")
  .build();
```

#### Service Initialization

Initialize the HoldingsIQ service with Vert.x and configuration:

```java
Vertx vertx = Vertx.vertx();
HoldingsIQService service = new HoldingsIQServiceImpl(config, vertx);
```

#### Example Usage

**Retrieve providers:**
```java
CompletableFuture<Vendors> future = service.retrieveProviders("searchString", 1, 25, Sort.NAME)
.thenAccept(vendors -> {
  // Process vendors
});
```

**Retrieve packages:**
```java
PackagesFilter filter = PackagesFilter.builder()
  .contentType(PackageContentType.E_BOOK)
  .selected(PackageSelectedFilter.SELECTED)
  .build();

CompletableFuture<Packages> future = service.retrievePackages(providerId, filter, Sort.NAME, 1, 25);
```

**Update title:**
```java
Title updatedTitle = title.toBuilder()
  .isSelected(true)
  .build();

CompletableFuture<Void> future = service.updateTitle(providerId, packageId, titleId, updatedTitle);
```

## Additional Information

**Example modules using this library:**
- [mod-kb-ebsco-java](https://github.com/folio-org/mod-kb-ebsco-java) - EBSCO Knowledge Base integration module

For more FOLIO developer documentation, visit [dev.folio.org](https://dev.folio.org/)

### Issue tracker

See project [FHIQC](https://folio-org.atlassian.net/browse/FHIQC)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker/).

### Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.
