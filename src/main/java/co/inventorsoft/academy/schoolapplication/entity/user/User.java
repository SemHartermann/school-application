package co.inventorsoft.academy.schoolapplication.entity.user;

import co.inventorsoft.academy.schoolapplication.entity.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AuditableEntity implements UserDetails {

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AccountStatus accountStatus;

    @Column(name = "verification_token", unique = true)
    String verificationToken;

    @Column(name = "token_expiry_date")
    LocalDateTime tokenExpiryDate;

    public boolean isVerificationTokenExpired() {
        return tokenExpiryDate.isBefore(LocalDateTime.now());
    }

    public void updateVerificationToken(String newToken, LocalDateTime newExpiryDate) {
        this.verificationToken = newToken;
        this.tokenExpiryDate = newExpiryDate;
    }
  
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountStatus != AccountStatus.EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accountStatus != AccountStatus.CREDENTIALS_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}