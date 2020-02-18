package com.royal.recreation.config.bean;

import com.royal.recreation.core.type.Status;
import com.royal.recreation.core.type.UserType;
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
public class MyUserDetails implements UserDetails {
    private String id;
    private String username;
    private String password;
    private UserType userType;
    private Status status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        UserType[] values = UserType.values();
        for (int i = userType.ordinal(); i < values.length; i++) {
            list.add(new SimpleGrantedAuthority(values[i].toString()));
        }
        return list;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == Status.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
