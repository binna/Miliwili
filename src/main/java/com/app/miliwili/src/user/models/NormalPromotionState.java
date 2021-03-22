package com.app.miliwili.src.user.models;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"userInfo"})
@Data
@Entity
@Table(name = "normalPromotionState")
public class NormalPromotionState {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstDate", nullable = false)
    private LocalDate firstDate;

    @Column(name = "secondDate")
    private LocalDate secondDate;

    @Column(name = "thirdDate")
    private LocalDate thirdDate;

    @Builder.Default
    @Column(name = "hobong", nullable = false, columnDefinition = "integer default 1")
    private Integer hobong = 1;

    @Builder.Default
    @Column(name = "stateIdx", nullable = false, columnDefinition = "integer default 0")
    private Integer stateIdx = 0;

    @OneToOne
    @JoinColumn(name = "userInfo_id", nullable = false)
    private UserInfo userInfo;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}