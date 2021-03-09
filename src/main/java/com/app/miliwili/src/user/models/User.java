package com.app.miliwili.src.user.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "serveType", nullable = false, length = 45)
    private String serveType;

    @Column(name = "stateIdx", nullable = false)
    private Integer stateIdx;

    @Column(name = "socialType", nullable = false, length = 1)
    private String socialType;

    @Column(name = "socialId", nullable = false, length = 1000)
    private String socialId;

    @Column(name = "goal", length = 100)
    private String goal;

    @Column(name = "goalDate")
    private LocalDate goalDate;

    @Column(name = "profileImg")
    private String profileImg;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;
}