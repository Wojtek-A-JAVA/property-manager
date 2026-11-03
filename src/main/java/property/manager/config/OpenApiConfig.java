package property.manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI myOpenApiConfig() {
        Info info = new Info()
                .title("Property Manager API")
                .description("""
                        <b>Features:</b>
                        <ul>
                         <li>Property, unit, tenant and lease management</li>
                         <li>Utility invoices, meters and meter readings</li>
                         <li>Utility cost allocation and settlement recalculation</li>
                         <li>JWT authentication and role-based authorization</li>
                        </ul>
                        
                        <b>Demo access:</b><br>
                        A public DEMO_ADMIN account is available for testing the application.<br>
                        Username: <b>demo_admin</b><br>
                        Password: <b>demo_admin_password</b><br><br>
                        
                        Some administrative operations are available only for the ADMIN role
                        and are intentionally disabled for DEMO_ADMIN.<br><br>
                      
                        The demo database is automatically reset every 24 hours
                        to the default dataset defined by Liquibase migrations.<br><br>
                        
                        Built with Java 21, Spring Boot, Spring Security, JPA/Hibernate,
                        PostgreSQL, Liquibase, MapStruct and OpenAPI/Swagger.<br><br>
                        """);
        return new OpenAPI().info(info)
                .tags(List.of(
                        new Tag().name("01. Authentication")
                                .description("User authentication and JWT token generation"),
                        new Tag().name("02. Properties")
                                .description("Property management endpoints"),
                        new Tag().name("03. Units")
                                .description("Property management endpoints"),
                        new Tag().name("04. Tenants")
                                .description("Tenant management endpoints"),
                        new Tag().name("05. Leases")
                                .description("Lease management endpoints"),
                        new Tag().name("06. Suppliers")
                                .description("Supplier management endpoints"),
                        new Tag().name("07. Utility Invoices")
                                .description("Utility invoice management endpoints"),
                        new Tag().name("08. Utility Rules")
                                .description("Utility cost allocation rule management endpoints"),
                        new Tag().name("09. Meters & Readings")
                                .description("Meter and meter reading management endpoints"),
                        new Tag().name("10. Property Costs")
                                .description("Property-level utility cost management endpoints"),
                        new Tag().name("11. Unit Costs")
                                .description("Unit-level utility cost management endpoints"),
                        new Tag().name("12. Settlements")
                                .description("Utility settlement creation and recalculation"
                                        + " endpoints"),
                        new Tag().name("13. Health Check")
                                .description("Application health check endpoint")
                ))
                .components(new Components().addSecuritySchemes("BearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }
}
