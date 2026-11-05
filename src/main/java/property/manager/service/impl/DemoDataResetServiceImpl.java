package property.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property.manager.service.DemoDataResetService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoDataResetServiceImpl implements DemoDataResetService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void resetDemoData() {
        log.info("Starting demo data reset");
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    unit_costs, property_costs, utility_rules_history_units, utility_rules_history,
                    utility_rule_units, utility_rules, meter_readings, meters, utility_invoices,
                    suppliers, leases, tenants, units, properties
                RESTART IDENTITY
                """);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/changelog/demo-data.sql"));
        populator.execute(jdbcTemplate.getDataSource());
        log.info("Demo data reset completed successfully");
    }
}
