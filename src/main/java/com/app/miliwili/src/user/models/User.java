package com.app.miliwili.src.user.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "stateIdx", nullable = false, updatable = false)
    private Integer stateIdx;

    @Column(name = "serveType", nullable = false, length = 45)
    private String serveType;

    @Column(name = "socialType", nullable = false, length = 1, updatable = false)
    private String socialType;

    @Column(name = "socialId", nullable = false, length = 1000, updatable = false)
    private String socialId;

    @Column(name = "goal", length = 100)
    private String goal;

    @Column(name = "profileImg")
    private String profileImg;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private NormalPromotionState normalPromotionState;

    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private AbnormalPromotionState abnormalPromotionState;
}