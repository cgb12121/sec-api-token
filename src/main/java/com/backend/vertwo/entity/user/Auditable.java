package com.backend.vertwo.entity.user;

import com.backend.vertwo.domain.request.RequestContext;
import com.backend.vertwo.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updateAt"}, allowGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Auditable {

    @Id
    @SequenceGenerator(name = "primary_key_seq", sequenceName = "primary_key_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_key_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String referenceId = new AlternativeJdkIdGenerator().generateId().toString();

    @NonNull
    private Long createdBy;

    @NonNull
    private Long updatedBy;

    @NonNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "owner_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_user_owner")
    )
    private User owner;

    @PrePersist
    public void beforePersist() {
        var userId = RequestContext.getUserId();

        if (userId == null) {
            throw new ApiException("Cannot persist entity with null id");
        }

        setCreatedBy(userId);
        setUpdatedBy(userId);
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void afterPersist() {
        var userId = RequestContext.getUserId();

        if (userId == null) {
            throw new ApiException("Cannot update entity with null id");
        }

        setUpdatedBy(userId);
        setUpdatedAt(LocalDateTime.now());
    }
}
