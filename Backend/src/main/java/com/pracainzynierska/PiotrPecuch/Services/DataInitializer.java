package com.pracainzynierska.PiotrPecuch.Services;

import com.pracainzynierska.PiotrPecuch.models.ERole;
import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.Role;
import com.pracainzynierska.PiotrPecuch.models.User;
import com.pracainzynierska.PiotrPecuch.repository.RoleRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.beans.Encoder;
import java.util.HashSet;
import java.util.Set;

import static org.odftoolkit.odfdom.dom.style.OdfStyleFamily.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.firstPassword}")
    private String firstPassword;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder1) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder1;
    }


    @Override
    public void run(String... args) {
        if(roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_USER));
            roleRepository.save(new Role(ERole.ROLE_MODERATOR));
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.ROLE_ADMIN));
            User user = new User(adminEmail, encoder.encode(firstPassword), null);
            user.setRoles(roles);
            userRepository.save(user);
        }

    }
}