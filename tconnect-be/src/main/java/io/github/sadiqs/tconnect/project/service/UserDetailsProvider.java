package io.github.sadiqs.tconnect.project.service;

import io.github.sadiqs.tconnect.project.model.AppRoles;
import io.github.sadiqs.tconnect.project.model.AppUser;
import io.github.sadiqs.tconnect.project.model.Customer;
import io.github.sadiqs.tconnect.project.model.Tradie;
import io.github.sadiqs.tconnect.project.repository.CustomerRepository;
import io.github.sadiqs.tconnect.project.repository.TradieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsProvider implements UserDetailsService {

    private final CustomerRepository customerRepository;

    private final TradieRepository tradieRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDetails> customerUser = customerRepository.findByUsername(username).map(this::customerUserDetails);
        if (customerUser.isEmpty()) {
            return tradieRepository.findByUsername(username).map(this::tradieUserDetails).orElseThrow(
                    () -> new UsernameNotFoundException("User " + username + " not found")
            );
        } else {
            return customerUser.get();
        }
    }

    public Optional<? extends AppUser> getUserInfo(String username) {
        Optional<Customer> customerUser = customerRepository.findByUsername(username);
        if (customerUser.isEmpty()) {
            return tradieRepository.findByUsername(username);
        } else {
            return customerUser;
        }
    }

    private UserDetails customerUserDetails(Customer customer) {
        return new User(customer.getUsername(), customer.getUsername(), List.of(new SimpleGrantedAuthority(AppRoles.CUSTOMER.name())));
    }

    private UserDetails tradieUserDetails(Tradie tradie) {
        return new User(tradie.getUsername(), tradie.getUsername(), List.of(new SimpleGrantedAuthority(AppRoles.TRADIE.name())));
    }
}
