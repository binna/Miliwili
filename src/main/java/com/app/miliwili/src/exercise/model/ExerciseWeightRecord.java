package com.app.miliwili.src.exercise.model;

import com.app.miliwili.config.BaseEntity;
import com.app.miliwili.src.user.models.User;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "exerciseWeightRecord")
public class ExerciseWeightRecord extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false )
    private ExerciseInfo exerciseInfo;

}
