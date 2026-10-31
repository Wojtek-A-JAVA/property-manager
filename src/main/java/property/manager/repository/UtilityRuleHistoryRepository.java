package property.manager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import property.manager.model.UtilityRuleHistory;

public interface UtilityRuleHistoryRepository extends JpaRepository<UtilityRuleHistory, Long> {

    Optional<UtilityRuleHistory> findFirstByUtilityRuleIdOrderByArchivedAtDesc(Long utilityRuleId);
}
