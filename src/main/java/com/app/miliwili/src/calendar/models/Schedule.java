package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
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

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "repetition", nullable = false, columnDefinition = "varchar(1) default 'F'")
    private String repetition = "F";

    @Builder.Default
    @Column(name = "push", nullable = false, columnDefinition = "varchar(1) default 'F'")
    private String push = "F";

    @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ToDoList> toDoLists;

    @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrdinaryLeave> ordinaryLeaves;
}