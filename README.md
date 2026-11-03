# Property Manager

Property Manager is a web application for managing rental properties and automating everyday property management tasks.

The application is being developed for real use in a property rental business.

## Main goals

The application helps with:

- managing properties and rental units,
- managing tenants and leases,
- utility settlements,
- meter and meter reading management,
- utility cost allocation,
- repair and maintenance history,
- reminders and important dates,
- financial and operational reports,
- integration with external services,
- notifications and document generation.

## Current modules

- Property
- Unit
- Tenant
- Lease
- Meter
- Meter Reading
- Supplier
- Utility Invoice
- Utility Rule
- Property Cost
- Unit Cost
- Settlement
- Authentication

## Current functionality

The application currently supports property, unit, tenant and lease management, as well as utility cost settlement and recalculation.

Utility-related functionality includes:

- supplier management,
- utility invoice management,
- meter and meter reading management,
- configurable utility allocation rules,
- monthly property costs,
- unit-level cost calculation,
- settlement creation and recalculation,
- multiple allocation methods, including:
    - equal split per unit,
    - area-based allocation,
    - submeter-based usage,
    - source-meter usage allocated by area.

The application also includes:

- JWT authentication,
- role-based authorization,
- ADMIN and DEMO_ADMIN roles,
- endpoint protection using Spring Security,
- request validation,
- duplicate protection,
- entity status handling,
- filtering for selected modules,
- Swagger / OpenAPI documentation.

## Demo access

A public DEMO_ADMIN account is available for testing the application through Swagger UI.

Some administrative operations are restricted to the ADMIN role.

## Technologies

- Java 21
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Liquibase
- Hibernate / JPA
- MapStruct
- REST API
- Swagger / OpenAPI
- Maven
- Docker
- Docker Compose
- Hetzner VPS
- Nginx Proxy Manager

## Planned development

Further development will include:

- automatic daily reset of demo data,
- maintenance and expenses,
- reminders and documents,
- lease amendment history,
- reports and dashboard,
- notifications,
- Telegram integration,
- wFirma API integration,
- OAuth2 authentication.

The project is under active development.
