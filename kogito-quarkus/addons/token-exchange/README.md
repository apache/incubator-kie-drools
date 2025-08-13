<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->
# KIE Add-On Token Exchange

This add-on provides OAuth2 token exchange functionality for Kogito Quarkus applications with caching and database persistence.

## Overview

The token-exchange add-on implements OAuth2 Token Exchange functionality, allowing applications to exchange one token for another. This is useful for scenarios where you need to:

- Exchange an access token for a token with different scope or audience
- Impersonate users or services
- Implement token chaining in microservice architectures

The addon includes:
- **Caffeine-based caching** for token storage with per-token expiration
- **Database persistence** for token durability
- **Proactive token refresh** to minimize authentication delays
- **OpenAPI integration** for automated credential management

## Usage

Add the dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>org.kie</groupId>
  <artifactId>kie-addons-quarkus-token-exchange</artifactId>
</dependency>
```

## Components

### Runtime Module
- **OpenApiCustomCredentialProvider**: Main credential provider with token exchange and caching
- **Cache**:
  - `CachedTokens`: Token wrapper with expiration tracking
  - `TokenPolicyManager`: Expiry policy for per-token cache management
  - `TokenEvictionHandler`: Handles cache eviction and proactive refresh
- **Persistence**:
  - `DatabaseTokenDataStore`: Database-backed token storage
  - `TokenCacheRepository`: Repository interface for token CRUD operations
  - `TokenCacheRecord`: JPA entity for token storage
- **Utilities**:
  - `OidcClientUtils`: OIDC client utilities for token exchange
  - `CacheUtils`: Cache key management utilities
  - `ConfigReaderUtils`: Configuration reading utilities


### Deployment Module
- **TokenExchangeProcessor**: Quarkus deployment processor for extension configuration

## Configuration

The token exchange add-on supports various configuration properties under the `sonataflow.security` namespace:

### Token Exchange Configuration

Configure OAuth2 token exchange for specific authentication names:

```properties
# Enable token exchange for a specific auth name
sonataflow.security.auth.{authName}.token-exchange.enabled=true

# Proactive token refresh - seconds before expiration to refresh the token cache
# Default: 300 seconds (5 minutes)
sonataflow.security.auth.{authName}.token-exchange.proactive-refresh-seconds=300

# Global monitor rate for token cache monitoring across all auth names
# Default: 60 seconds (1 minute)
sonataflow.security.auth.token-exchange.monitor-rate-seconds=60
```

### Token Propagation Configuration

Configure token propagation for OpenAPI generator services:

```properties
# Enable token propagation for specific services and auth names
quarkus.openapi-generator.{serviceId}.auth.{authName}.token-propagation=true

# Custom header name for token propagation (optional)
# Default: Authorization
quarkus.openapi-generator.{serviceId}.auth.{authName}.header-name=CUSTOM_HEADER_NAME
```

### Configuration Examples

#### Basic Token Exchange
```properties
# Enable token exchange for 'myService' auth
sonataflow.security.auth.myService.token-exchange.enabled=true
sonataflow.security.auth.myService.token-exchange.proactive-refresh-seconds=180
```

#### Token Exchange with Propagation
```properties
# Enable both token exchange and propagation
sonataflow.security.auth.with_exchange_and_propagation_oauth2.token-exchange.enabled=true
quarkus.openapi-generator.external_service_yaml.auth.with_exchange_and_propagation_oauth2.token-propagation=true
```

**Note**: When both token exchange and token propagation are enabled for the same auth name, token propagation takes precedence and handles the token management. The exchanged token (if available) will be used for propagation to downstream services.

#### Global Monitoring Configuration
```properties
# Monitor token cache every 30 seconds
sonataflow.security.auth.token-exchange.monitor-rate-seconds=30
```

### Related OIDC Client Configuration

When using token exchange, you'll also need to configure the corresponding OIDC clients:

```properties
# OIDC client for token exchange
quarkus.oidc-client.{authName}.auth-server-url=${keycloak.server.url}
quarkus.oidc-client.{authName}.token-path=/realms/{realm}/protocol/openid-connect/token
quarkus.oidc-client.{authName}.discovery-enabled=false
quarkus.oidc-client.{authName}.client-id=kogito-app
quarkus.oidc-client.{authName}.grant.type=exchange
quarkus.oidc-client.{authName}.credentials.client-secret.method=basic
quarkus.oidc-client.{authName}.credentials.client-secret.value=secret
```

### Configuration Parameters

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `sonataflow.security.auth.{authName}.token-exchange.enabled` | Boolean | `false` | Enable OAuth2 token exchange for the specified auth name |
| `sonataflow.security.auth.{authName}.token-exchange.proactive-refresh-seconds` | Long | `300` | Seconds before token expiration to proactively refresh the cache |
| `sonataflow.security.auth.token-exchange.monitor-rate-seconds` | Long | `60` | Global monitoring rate for token cache across all auth names |
| `quarkus.openapi-generator.{serviceId}.auth.{authName}.token-propagation` | Boolean | `false` | Enable token propagation for OpenAPI generated services |
| `quarkus.openapi-generator.{serviceId}.auth.{authName}.header-name` | String | `Authorization` | Custom header name for token propagation |

### Notes

- Replace `{authName}` with your specific authentication configuration name
- Replace `{serviceId}` with your OpenAPI service identifier  
- Token exchange requires proper OIDC client configuration
- Proactive refresh helps minimize authentication delays by refreshing tokens before they expire
- Monitor rate controls how frequently the cache eviction handler checks for expired tokens
- **Important**: When both token exchange and token propagation are configured for the same auth name, token propagation takes precedence over token exchange. The system will use the exchanged token for propagation to downstream services.

## License

Licensed under the Apache License, Version 2.0. 