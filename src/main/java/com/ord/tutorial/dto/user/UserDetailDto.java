package com.ord.tutorial.dto.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class UserDetailDto implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private Boolean enabled;
    private List<String> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<>();
        if (this.permissions != null) {
            for (String permission : this.permissions) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission);
                result.add(grantedAuthority);
            }
        }
        return result;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
