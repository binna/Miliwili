package com.app.miliwili.src.calendar.models;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "toDoList")
public class ToDoList {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "processingStatus", nullable = false, columnDefinition = "varchar(1) default 'F'")
    private String processingStatus = "F";

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
}