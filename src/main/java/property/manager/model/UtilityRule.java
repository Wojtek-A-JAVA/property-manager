package property.manager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "utility_rules")
@Getter
@Setter
public class UtilityRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Enumerated(EnumType.STRING)
    @Column(name = "utility_type", nullable = false)
    private UtilityType utilityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "allocation_method", nullable = false)
    private AllocationMethod allocationMethod;

    @ManyToMany
    @JoinTable(name = "utility_rule_units",
            joinColumns = @JoinColumn(name = "utility_rule_id"),
            inverseJoinColumns = @JoinColumn(name = "unit_id"))
    private List<Unit> units = new ArrayList<>();

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
    private boolean active = true;
}
