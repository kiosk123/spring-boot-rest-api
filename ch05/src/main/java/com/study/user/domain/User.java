package com.study.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.study.post.domain.Post;

import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User {
    
    @Id @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @Setter
    private String password;

    @Setter
    private String ssn;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinDate;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    private User(String name, String password, String ssn, Post post) {
        this.name = name;
        this.password = password;
        this.ssn = ssn;
        posts.add(post);
        post.setUser(this);
    }

    /** Post 삭제시 사용될 수 있는 연관관계 편의 메서드 */
    public void removePost(Post post) {
        Iterator<Post> iter = posts.iterator();
        while (iter.hasNext()) {
            Post target = iter.next();
            if (target.getId().equals(post.getId())) {
                iter.remove();
                break;
            }
        }
    }

}
