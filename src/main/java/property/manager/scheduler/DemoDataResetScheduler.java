package property.manager.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import property.manager.service.DemoDataResetService;

@Component
@RequiredArgsConstructor
public class DemoDataResetScheduler {

    private final DemoDataResetService demoDataResetService;

    @Scheduled(cron = "0 0 2 * * *", zone = "Europe/Warsaw")
    public void resetDemoData() {
        demoDataResetService.resetDemoData();
    }
}
