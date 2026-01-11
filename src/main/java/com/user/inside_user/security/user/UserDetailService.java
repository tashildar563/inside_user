package com.user.inside_user.security.user;

import com.user.inside_user.entities.User;
import com.user.inside_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public UserDetailService() {
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = (User)this.userRepository.findByEmail(username).get();
        if(user == null || user.isDeleted()) {
            throw new UsernameNotFoundException("User not found");
        }
        return UserDetail.buildUserDetails(user);
    }
}

