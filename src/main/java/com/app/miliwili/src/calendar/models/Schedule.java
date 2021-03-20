package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import com.app.miliwili.src.user.models.User;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"user", "scheduleDates", "toDoLists"})
@Data
@Entity
@Table(name = "schedule")
public class Schedule extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color", nullable = false, length = 30)
    private String color;

    @Column(name = "distinction", nullable = false, length = 10)
    private String distinction;

    @Column(name = "title", nullable = false, length = 60)
    private String title;

    @Builder.Default
    @Column(name = "repetition", nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String repetition = "N";

    @Builder.Default
    @Column(name = "push", nullable = false, columnDefinition = "varchar(1) default 'F'")
    private String push = "F";

    @Column(name = "pushDeviceToken", columnDefinition = "TEXT")
    private String pushDeviceToken;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OrderBy("date asc")
    @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ScheduleDate> scheduleDates;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ToDoList> toDoLists;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    private Set<ScheduleVacation> scheduleVacations;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}