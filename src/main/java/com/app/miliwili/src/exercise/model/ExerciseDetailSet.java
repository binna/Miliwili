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
@Table(name = "exerciseDetailSet")
public class ExerciseDetailSet extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setIdx", nullable = false)
    private Integer setIdx;

    @Column(name = "setWeight")
    private Double setWeight;

    @Column(name = "setCount")
    private Integer setCount;

    @Column(name = "setTime")
    private Integer setTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_routine_detail_id", nullable = false )
    private ExerciseRoutineDetail exerciseRoutineDetail;
}
