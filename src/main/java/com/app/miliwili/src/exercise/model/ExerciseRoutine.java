package com.app.miliwili.src.exercise.model;

import com.app.miliwili.config.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "exerciseRoutine")
public class ExerciseRoutine extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",length = 100, nullable = false)
    private String name;

    @Column(name = "bodyPart",length = 45, nullable = false)
    private String bodyPart;

    @Column(name = "repeatDay", length = 30, nullable = false)
    private String repeaDay;

    @Builder.Default
    @Column(name = "done", nullable = false, length = 1)
    private String done = "N";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false )
    private ExerciseInfo exerciseInfo;

    @OneToMany(mappedBy = "exerciseRoutine", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExerciseRoutineDetail> routineDetails = new ArrayList<>();

    @OneToMany(mappedBy = "exerciseRoutine", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExerciseReport> reports = new ArrayList<>();

    public void addRoutineDetail(ExerciseRoutineDetail detail){
        this.routineDetails.add(detail);
    }

    public void addNewReport(ExerciseReport report){
        this.reports.add(report);
    }

}
