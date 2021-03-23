package com.app.miliwili.src.exercise.model;

import com.app.miliwili.config.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "exerciseReport")
public class ExerciseReport extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "totalTime", nullable = false)
    private String totalTime;

    @Column(name = "exerciseStatus", length = 100, nullable = false)
    private String exerciseStatus;

    @Column(name = "reportText", length = 900)
    private String reportText = "";

    @ManyToOne
    @JoinColumn(name = "exercise_routine_id", nullable = false )
    private ExerciseRoutine exerciseRoutine;


}
