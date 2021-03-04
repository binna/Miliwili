package com.app.miliwili.src.user.models;

import com.app.miliwili.config.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

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
    @Column(name = "no", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "profileImageURL", columnDefinition = "TEXT")
    private String profileImageURL;

    @Column(name = "phoneNumber", length = 13)
    private String phoneNumber;

    @Column(name = "birthday", length = 10)
    private String birthday;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "snsDiv", nullable = false, length = 1)
    private String snsDiv;

    @Column(name = "role", nullable = false, columnDefinition = "integer default 1")
    private Integer role;

    @Column(name = "image_status", nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String imageStatus;

    @Column(name = "id", length = 100)
    private String id;


    public User(String name, String nickname,String profileImageURL ,String phoneNumber,
                String birthday,String email,String gender,String snsDiv,Integer role,String imageStatus,
                String id) {
        this.name = name;
        this.nickname = nickname;
        this.profileImageURL = profileImageURL;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.email = email;
        this.gender = gender;
        this.snsDiv = snsDiv;
        this.role = role;
        this.imageStatus = imageStatus;
        this.id = id;
    }
}