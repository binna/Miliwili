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
    private Long setIdx;

    @Column(name = "setWeight")
    private Long setWeight;

    @Column(name = "setCount")
    private Long setCount;

    @Column(name = "setTime")
    private Long setTime;

    @ManyToOne
    @JoinColumn(name = "exerciseRoutinedetail_id", nullable = false )
    private ExerciseRoutineDetail exerciseRoutineDetail;
}
