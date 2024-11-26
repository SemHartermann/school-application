package co.inventorsoft.academy.schoolapplication.entity;

import co.inventorsoft.academy.schoolapplication.entity.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;


@Entity
@Table(name = "posts")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id")
    @NotNull
    ClassGroup classGroup;

    @Column(name = "title")
    String postTitle;

    @Column(name = "content")
    String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    Set<Reaction> reactions;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<Comment> comments;

}
