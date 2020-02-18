package com.royal.recreation.config.bean;

import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Primary
public class MyUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = Mongo.buildMongo().eq("username", username).findOne(UserInfo.class);
        if (userInfo == null) {
            throw new UsernameNotFoundException(username);
        }
        return MyUserDetails.builder()
                .id(userInfo.getId())
                .username(username)
                .password(userInfo.getPassword())
                .userType(userInfo.getUserType())
                .status(userInfo.getStatus())
                .build();
    }
}
