package com.app.miliwili.src.calendar.models;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "dDay")
public class DDay {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distinction", nullable = false, length = 10)
    private String distinction;

    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Column(name = "subtitle", nullable = false, length = 90)
    private String subtitle;

    @Column(name = "startDate", nullable = false)
    private Date startDate;

    @Column(name = "endDate", nullable = false)
    private Date endDate;

    @Column(name = "link", nullable = false, columnDefinition = "CHAR(1) default 'N'")
    private String link;

    @Column(name = "placeLat", precision = 16, scale = 14)
    private BigDecimal placeLat;

    @Column(name = "placeLon", precision = 17, scale = 14)
    private BigDecimal placeLon;

    @OneToMany(mappedBy = "dDay", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PreparationMaterial> preparationMaterials;
}