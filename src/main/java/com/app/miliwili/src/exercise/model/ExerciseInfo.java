package com.app.miliwili.src.exercise.model;

import com.app.miliwili.config.BaseEntity;
import com.app.miliwili.src.user.models.UserInfo;
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
@Table(name = "exerciseInfo")
public class ExerciseInfo extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "goalWeight")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double goalWeight;

    @Column(name = "firstWeight")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double firstWeight;

    /**
     * User 회원 가입할 시에 필요할 지?
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false )
    private UserInfo user;

    /**
     * 양방향
     */
    @OneToMany(mappedBy = "exerciseInfo", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExerciseWeightRecord> weightRecords = new ArrayList<>();

    @OneToMany(mappedBy = "exerciseInfo", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExerciseRoutine> exerciseRoutines = new ArrayList<>();

    public void addWeightRecord(ExerciseWeightRecord record){
        this.weightRecords.add(record);
    }

    public void addExerciseRoutine(ExerciseRoutine routine){
        this.exerciseRoutines.add(routine);
    }



}
