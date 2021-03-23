package com.app.miliwili.src.user.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"normalPromotionState", "abnormalPromotionState"})
@Data
@Entity
@Table(name = "userInfo")
public class UserInfo extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "stateIdx", nullable = false, updatable = false)
    private Integer stateIdx;

    @Column(name = "serveType", nullable = false, length = 30)
    private String serveType;

    @Column(name = "socialType", nullable = false, length = 1, updatable = false)
    private String socialType;

    @Column(name = "socialId", nullable = false, columnDefinition = "TEXT", updatable = false)
    private String socialId;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "goal", length = 75)
    private String goal;

    @Column(name = "profileImg", columnDefinition = "TEXT")
    private String profileImg;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @OneToOne(mappedBy = "userInfo", orphanRemoval = true, cascade = CascadeType.ALL)
    private NormalPromotionState normalPromotionState;

    @OneToOne(mappedBy = "userInfo", orphanRemoval = true, cascade = CascadeType.ALL)
    private AbnormalPromotionState abnormalPromotionState;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}