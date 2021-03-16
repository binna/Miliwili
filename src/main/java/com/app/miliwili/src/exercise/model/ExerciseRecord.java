package com.app.miliwili.src.exercise.model;

import com.app.miliwili.src.user.models.User;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "exerciseRecord")
public class ExerciseRecord  {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exerciseStatus")
    private String exerciseStatus;

    @OneToOne
    @JoinColumn(name = "routine_id", nullable = false )
    private ExerciseRoutine exerciseRoutine;


}
