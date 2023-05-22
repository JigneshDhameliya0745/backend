package com.backend.service;

import com.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUserDetailsService.class);
    private String TAG_NAME = "JwtUserDetailsService";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info(TAG_NAME + " :: inside loadUserByUsername : username :: " + username);

        com.backend.models.User user = userRepository.findUserByUserEmail(username);

        if(user != null){
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName());
            List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
            authorityList.add(authority);
            return new org.springframework.security.core.userdetails.User(user.getUserEmail(), user.getUserPassword(), authorityList);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
