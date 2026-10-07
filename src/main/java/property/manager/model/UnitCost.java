package property.manager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "unit_costs")
@Getter
@Setter
public class UnitCost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_cost_id", nullable = false)
    private PropertyCost propertyCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utility_rule_id", nullable = false)
    private UtilityRule utilityRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id")
    private Meter meter;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal consumption;

    @Column(name = "billing_month", nullable = false, length = 7)
    private YearMonth billingMonth;

    private String notes;
}
