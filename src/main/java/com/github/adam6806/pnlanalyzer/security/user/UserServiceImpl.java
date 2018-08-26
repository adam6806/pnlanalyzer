package com.github.adam6806.pnlanalyzer.security.user;

import com.github.adam6806.pnlanalyzer.security.PasswordEncoderConfig;
import com.github.adam6806.pnlanalyzer.security.role.Role;
import com.github.adam6806.pnlanalyzer.security.role.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoderConfig passwordEncoderConfig) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(user.getPassword()));
        user.setActive(1);
        List<Role> allRoles = roleRepository.findAll();
        List<String> roleStrings = Arrays.asList("ROLE_GUEST", "ROLE_USER", "ROLE_ADMIN");
        if (allRoles.size() != roleStrings.size()) {
            for (String roleString : roleStrings) {
                Role role = new Role();
                role.setRole(roleString);
                if (!allRoles.contains(role)) {
                    Role savedRole = roleRepository.save(role);
                    allRoles.add(savedRole);
                }
            }
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role guestRole = allRoles.stream().filter(role -> role.getRole().equals("ROLE_GUEST")).findFirst().get();
            Set<Role> roles = new HashSet<>(Collections.singletonList(guestRole));
            user.setRoles(roles);
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }
}