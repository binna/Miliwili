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
@Table(name = "exerciseRoutineDetail")
public class ExerciseRoutineDetail extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "routineTypeId", nullable = false)
    private int routineTypeId;

    @Column(name = "setCount", nullable = false)
    private int setCount;

    @Column(name = "isSame", nullable = false)
    private String isSame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_routine_id", nullable = false )
    private ExerciseRoutine exerciseRoutine;

    @OneToMany(mappedBy = "exerciseRoutineDetail", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExerciseDetailSet> detailSets = new ArrayList<>();

    public void addDetailSet(ExerciseDetailSet detailSet){
        this.detailSets.add(detailSet);
    }
}
