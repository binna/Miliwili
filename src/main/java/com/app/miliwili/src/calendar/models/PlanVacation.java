package com.app.miliwili.src.calendar.models;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"plan"})
@Data
@Entity
@Table(name = "planVacation")
public class PlanVacation {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "vacationId", nullable = false)
    private Long vacationId;

    @Builder.Default
    @Column(name = "status", nullable = false, columnDefinition = "varchar(1) default 'Y'")
    private String status = "Y";

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}