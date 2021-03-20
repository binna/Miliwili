package com.app.miliwili.src.user.models;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "vacation")
public class Vacation {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distinction", nullable = false, length = 60, updatable = false)
    private String distinction;

    @Column(name = "title", nullable = false, length = 60)
    private String title;

    @Builder.Default
    @Column(name = "useDays", nullable = false, columnDefinition = "integer default 0")
    private Integer useDays = 0;

    @Column(name = "totalDays", nullable = false)
    private Integer totalDays;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}