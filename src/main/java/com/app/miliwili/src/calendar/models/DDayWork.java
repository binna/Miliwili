package com.app.miliwili.src.calendar.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"dday"})
@Data
@Entity
@Table(name = "ddayWork")
public class DDayWork extends BaseEntity {
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
    @JoinColumn(name = "dday_id", nullable = false)
    private DDay dday;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}