package com.nazire.booktrack;

import com.nazire.booktrack.model.Role;
import com.nazire.booktrack.model.User;
import com.nazire.booktrack.repository.RoleRepository;
import com.nazire.booktrack.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class BooktrackApplication {

	private static Logger LOG = LoggerFactory.getLogger(BooktrackApplication.class);

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(BooktrackApplication.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Bean
	public CommandLineRunner initializeData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
		return args -> {
			// Rolleri oluştur
			Role userRole = createRoleIfNotExists(roleRepository, "ROLE_USER");
			Role adminRole = createRoleIfNotExists(roleRepository, "ROLE_ADMIN");
			
			// Test admin kullanıcısı oluştur
			createUserIfNotExists(userRepository, passwordEncoder, "admin@test.com", "password", "Test Admin", Set.of(userRole, adminRole));
		};
	}
	
	private Role createRoleIfNotExists(RoleRepository roleRepository, String roleName) {
		return roleRepository.findByName(roleName)
				.orElseGet(() -> {
					Role role = new Role(roleName);
					Role savedRole = roleRepository.save(role);
					LOG.info("{} rolü oluşturuldu.", roleName);
					return savedRole;
				});
	}
	
	private void createUserIfNotExists(UserRepository userRepository, PasswordEncoder passwordEncoder,
			String email, String password, String username, Set<Role> roles) {
		if (userRepository.findByEmail(email).isEmpty()) {
			User user = new User(email, passwordEncoder.encode(password), username);
			user.setRoles(roles);
			userRepository.save(user);
			LOG.info("{} kullanıcısı oluşturuldu.", email);
		}
	}
}
