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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "property_costs")
@Getter
@Setter
@NoArgsConstructor
public class PropertyCost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utility_invoice_id", nullable = false)
    private UtilityInvoice utilityInvoice;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "billing_month", nullable = false, length = 7)
    private YearMonth billingMonth;

    private String notes;

    public PropertyCost(
            Property property, UtilityInvoice utilityInvoice,
            BigDecimal amount, YearMonth billingMonth, String notes) {
        this.property = property;
        this.utilityInvoice = utilityInvoice;
        this.amount = amount;
        this.billingMonth = billingMonth;
        this.notes = notes;
    }
}
