package com.app.miliwili.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    @CreationTimestamp
    @Column(name = "dateCreated", nullable = false, updatable = false)
    private Date dateCreated;

    @UpdateTimestamp
    @Column(name = "dateUpdated", nullable = false)
    private Date dateUpdated;

    @Column(name = "status", nullable = false, length = 1)
    private String status = "Y";
}