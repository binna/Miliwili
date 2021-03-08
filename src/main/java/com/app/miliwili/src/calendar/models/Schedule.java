package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Accessors(chain = true)
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

    @Column(name = "distinction", nullable = false, length = 10)
    private String distinction;

    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Column(name = "startDate", nullable = false)
    private Date startDate;

    @Column(name = "endDate", nullable = false)
    private Date endDate;

    @Column(name = "repetition", nullable = false, length = 1, columnDefinition = "CHAR(1) default 'F'")
    private String repetition;

    @Column(name = "push", nullable = false, length = 1, columnDefinition = "CHAR(1) default 'F'")
    private String push;

    @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ToDoList> toDoLists;

    @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrdinaryLeave> ordinaryLeaves;
}