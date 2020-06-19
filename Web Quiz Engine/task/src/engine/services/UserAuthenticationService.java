package engine.services;

import engine.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthenticationService implements UserDetailsService {

    @Autowired
    protected UserRepository userRepo;

    public UserAuthenticationService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<engine.models.User> user = userRepo.findByEmail(email);
        if (user.equals(Optional.empty())) {
            throw new UsernameNotFoundException("User not found.");
        }
        return User.withUsername(user.get().getEmail()).password(user.get().getPassword())
                .authorities("USER").build();
    }
}
