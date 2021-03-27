package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import com.app.miliwili.src.user.models.UserInfo;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"userInfo", "ddayWorks", "ddayDiaries"})
@Data
@Entity
@Table(name = "dday")
public class DDay extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ddayType", nullable = false, length = 10, updatable = false)
    private String ddayType;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "subtitle", length = 60)
    private String subtitle;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

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

    @OrderBy("id asc")
    @OneToMany(mappedBy = "dday", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DDayWork> ddayWorks;

    @OrderBy("date asc")
    @OneToMany(mappedBy = "dday", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<DDayDiary> ddayDiaries;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}