package property.manager.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "utility_rules_history")
@Getter
@Setter
public class UtilityRuleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utility_rule_id", nullable = false)
    private Long utilityRuleId;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "utility_type", nullable = false)
    private UtilityType utilityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_utility_type", nullable = false)
    private UtilityType invoiceUtilityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "allocation_method", nullable = false)
    private AllocationMethod allocationMethod;

    @ElementCollection
    @CollectionTable(name = "utility_rules_history_units",
            joinColumns = @JoinColumn(name = "utility_rules_history_id"))
    @Column(name = "unit_id")
    private List<Long> unitIds = new ArrayList<>();

    @Column(name = "unit_meter_required", nullable = false)
    private boolean unitMeterRequired;

    @Column(name = "property_meter_required", nullable = false)
    private boolean propertyMeterRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type")
    private MeasurementType measurementType;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_purpose")
    private MeterPurpose meterPurpose;

    @Column(name = "start_month", nullable = false, length = 7)
    private YearMonth startMonth;

    @Column(name = "end_month", length = 7)
    private YearMonth endMonth;

    private String notes;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "rule_created_at", nullable = false)
    private Instant ruleCreatedAt;

    @CreationTimestamp
    @Column(name = "archived_at", nullable = false, updatable = false)
    private Instant archivedAt;
}
