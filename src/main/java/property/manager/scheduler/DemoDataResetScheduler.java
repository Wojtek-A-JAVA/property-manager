package property.manager.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import property.manager.service.DemoDataGeneratorService;
import property.manager.service.DemoDataResetService;

@Component
@Profile("!prod-priv")
@RequiredArgsConstructor
public class DemoDataResetScheduler {

    private final DemoDataResetService demoDataResetService;
    private final DemoDataGeneratorService demoDataGeneratorService;

    @Scheduled(cron = "0 0 2 * * *", zone = "Europe/Warsaw")
    public void resetDemoData() {
        demoDataResetService.resetDemoData();
        demoDataGeneratorService.generateMissingDemoData();
    }
}
