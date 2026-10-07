# Property Manager

Property Manager is a web application for managing rental properties and automating everyday property management tasks.

The application is being developed for real use in a property rental business.

## Main goals

The application will help with:

- managing properties and rental units,
- managing tenants and leases,
- utility settlements,
- repair and maintenance history,
- reminders and important dates,
- financial and operational reports,
- integration with external services such as wFirma,
- Telegram notifications,
- generating documents and messages.

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

The application currently supports property, unit, tenant and lease management, as well as the core utility settlement domain.

Utility-related functionality includes:

- supplier management,
- utility invoices,
- meter and meter reading management,
- configurable utility allocation rules,
- monthly property costs,
- unit-level cost calculation,
- multiple allocation methods, including:
    - equal split per unit,
    - area-based allocation,
    - submeter-based usage,
    - source-meter usage allocated by area.

The application includes validation, duplicate protection, entity status handling and filtering for selected modules.

## Technologies

- Java 21
- Spring Boot
- PostgreSQL
- Liquibase
- Spring Security
- REST API
- Maven
- MapStruct
- Hibernate / JPA
- Swagger / OpenAPI
- Docker
- Docker Compose
- Hetzner VPS
- Nginx Proxy Manager

## Planned development

Further development will include:

- settlement orchestration and recalculation,
- maintenance and expenses,
- reminders and documents,
- lease amendment history,
- reports and dashboard,
- notifications,
- Telegram integration,
- wFirma API integration.

The project is under active development.