package co.inventorsoft.academy.schoolapplication.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static co.inventorsoft.academy.schoolapplication.entity.user.Permission.*;

@Getter

@RequiredArgsConstructor
public enum Role {
  
    SUPER_ADMIN(getAllPermissions()),

    SCHOOL_ADMIN(Set.of(

            // TODO: define SCHOOL_ADMIN permissions
    )),
    STUDENT(Set.of(
            // TODO: define STUDENT permissions
    )),
    TEACHER(Set.of(

    )),
    PARENT(Set.of(
            // TODO: define PARENT permissions
    )),
    MEDICAL_STAFF(Set.of(
            // TODO: define MEDICAL_STAFF permissions
    ));

    private final Set<Permission> permissions;

    private static Set<Permission> getAllPermissions() {
        return EnumSet.allOf(Permission.class);
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
      
        return authorities;
    }
}