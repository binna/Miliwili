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
@Table(name = "normalPromotionState")
public class NormalPromotionState extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstDate", nullable = false)
    private LocalDate firstDate;

    @Column(name = "secondDate")
    private LocalDate secondDate;

    @Column(name = "thirdDate")
    private LocalDate thirdDate;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}