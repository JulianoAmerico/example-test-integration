package com.example.entity;

import com.example.api.model.UserRequest;
import com.example.api.model.UserResponse;
import com.example.rabbitmq.message.UserMessage;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
@ToString(of = {"name", "document"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "document", nullable = false)
    private String document;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public <X extends Throwable> void isValidToSave(Supplier<? extends X> exceptionSupplier) throws X {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(document)) {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return document.equals(user.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document);
    }

    public static User toDomain(UserRequest request) {
        return User.builder()
                .name(request.name())
                .document(request.document())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getDocument(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static UserMessage toMessage(User user) {
        return new UserMessage(
                user.getId(),
                user.getName(),
                user.getDocument()
        );
    }

    public static User toDomain(UserMessage message) {
        return User.builder()
                .id(message.id())
                .name(message.name())
                .document(message.document())
                .build();
    }

    public User copy(String name, String document) {
        return new User(
                this.id,
                name,
                document,
                this.createdAt,
                this.updatedAt
        );
    }
}