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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leases")
@Getter
@Setter
public class Lease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "rent_net_amount", nullable = false)
    private BigDecimal rentNetAmount;

    @Column(name = "additional_costs_net_amount")
    private BigDecimal additionalCostsNetAmount;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "vat_rate", nullable = false)
    private VatRate vatRate;

    @Column(name = "payment_due_day", nullable = false)
    private Integer paymentDueDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "lease_status", nullable = false)
    private LeaseStatus leaseStatus = LeaseStatus.ACTIVE;

    private String notes;
}
