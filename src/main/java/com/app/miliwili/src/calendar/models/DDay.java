package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import com.app.miliwili.src.user.models.UserInfo;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "dDay")
public class DDay extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distinction", nullable = false, length = 10)
    private String distinction;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "subtitle", nullable = false, length = 60)
    private String subtitle;

    @Column(name = "startDay", nullable = false)
    private LocalDate startDay;

    @Column(name = "endDay", nullable = false)
    private LocalDate endDay;

    @Builder.Default
    @Column(name = "link", nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String link = "N";

    @Builder.Default
    @Column(name = "choiceCalendar", nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String choiceCalendar = "N";

    @Column(name = "placeLat", precision = 16, scale = 14)
    private BigDecimal placeLat;

    @Column(name = "placeLon", precision = 17, scale = 14)
    private BigDecimal placeLon;

    @OneToOne
    @JoinColumn(name = "userInfo_id", nullable = false)
    private UserInfo userInfo;

    @OneToMany(mappedBy = "dDay", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Supplies> supplies;
}