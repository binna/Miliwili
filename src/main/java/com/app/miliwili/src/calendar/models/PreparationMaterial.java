package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "preparationMaterial")
public class PreparationMaterial extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "readyState", nullable = false, columnDefinition = "varchar(1) default 'F'")
    private String readyState = "F";

    @ManyToOne
    @JoinColumn(name = "dDay_id")
    private DDay dDay;
}
