package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import com.app.miliwili.src.user.models.UserInfo;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"userInfo", "toDoLists", "planVacations", "diaries"})
@Data
@Entity
@Table(name = "plan")
public class Plan extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color", nullable = false, length = 30)
    private String color;

    @Column(name = "planType", nullable = false, length = 10, updatable = false)
    private String planType;

    @Column(name = "title", nullable = false, length = 60)
    private String title;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "push", nullable = false, columnDefinition = "varchar(1) default 'F'")
    private String push = "F";

    @Column(name = "pushDeviceToken", columnDefinition = "TEXT")
    private String pushDeviceToken;

    @OneToOne
    @JoinColumn(name = "userInfo_id", nullable = false)
    private UserInfo userInfo;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "plan", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ToDoList> toDoLists;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "plan", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PlanVacation> planVacations;

    @OrderBy("date asc")
    @OneToMany(mappedBy = "plan", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Diary> diaries;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}