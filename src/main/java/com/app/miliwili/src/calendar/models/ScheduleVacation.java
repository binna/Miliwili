package com.app.miliwili.src.calendar.models;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"schedule"})
@Data
@Entity
@Table(name = "scheduleVacation")
public class ScheduleVacation {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "count", nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

//    @OneToMany(mappedBy = "scheduleLeaveData", orphanRemoval = true, cascade = CascadeType.ALL)
//    private List<Leave> leaves;
    @Column(name = "vacationId", nullable = false)
    private Long vacationId;
}