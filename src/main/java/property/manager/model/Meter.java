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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "meters")
@Getter
@Setter
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false)
    private MeasurementType measurementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterPurpose purpose;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    private String notes;
}
