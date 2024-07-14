package com.backend.vertwo.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
@JsonInclude(NON_DEFAULT)
public class User extends Auditable implements UserDetails {

    @Column(unique = true, updatable = false, nullable = false)
    private Long userId;

    private String firstName;

    private String lastName;

    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    @Column(nullable = true)
    private String phone;

    private Integer loginAttempts;

    private LocalDateTime lastLogin;

    private boolean accountNonExpired;

    private boolean accountNonBlocked;

    private boolean enable;

    private boolean multiFactorAuthentication;

    private String bio;

    private String avatar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Role role;

    @OneToOne(mappedBy = "user")
    private CredentialUser credentialUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role == null ? Collections.emptyList() :
                List.of(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
    }

    @Override
    public String getPassword() {
        return credentialUser == null ? null : credentialUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonBlocked;
    }

    @Override
    public boolean isEnabled() {
        return this.enable;
    }

}
