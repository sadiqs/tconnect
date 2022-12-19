package io.github.sadiqs.tconnect.project.config;

import io.github.sadiqs.tconnect.project.model.Bid;
import io.github.sadiqs.tconnect.project.model.Customer;
import io.github.sadiqs.tconnect.project.model.Project;
import io.github.sadiqs.tconnect.project.model.Tradie;
import io.github.sadiqs.tconnect.project.repository.BidRepository;
import io.github.sadiqs.tconnect.project.repository.CustomerRepository;
import io.github.sadiqs.tconnect.project.repository.ProjectRepository;
import io.github.sadiqs.tconnect.project.repository.TradieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Period;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    CustomerRepository repository;

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .anyRequest().authenticated();
        http.formLogin(form -> {
            form.successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()));
            form.failureHandler(((request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value())));
        });
        http.httpBasic(basic -> basic.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
        http.csrf(csrf -> csrf.disable());
        http.logout(logout -> logout.logoutUrl("/logout"));
        return http.build();
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerRepository customerRepository,
                                        TradieRepository tradieRepository,
                                        BidRepository bidRepository,
                                        ProjectRepository projectRepository) {
        return new ApplicationRunner() {
            @Override
            @Transactional
            public void run(ApplicationArguments args) throws Exception {
                log.info("Initializing with seed users");

                var bid = new Bid();
                Project project = new Project();
                project.setBids(List.of(bid));
                project.setTitle("The grand project");
                project.setDescription("""
                        The project is to construct a patio in the
                        backyard, and a fire place.
                        """);
                project.setExpectedHours(55);
                project.setBiddingEndTime(Instant.now().plus(Period.ofDays(4)));
                bid.setProject(project);
                Tradie tradie1 = createTradie("tradie1", "Tradie One");
                Tradie tradie2 = createTradie("tradie2", "Tradie Two");
                bid.setTradie(tradie1);
                tradieRepository.save(tradie1);
                tradieRepository.save(tradie2);

                Customer customer = new Customer();
                customer.setUsername("customer1");
                customer.setName("Customer One");
                customerRepository.save(customer);

                project.setCustomer(customer);
                projectRepository.save(project);
                bidRepository.save(bid);
            }

            private Tradie createTradie(String username, String displayName) {
                Tradie tradie = new Tradie();
                tradie.setName(displayName);
                tradie.setTrade("carpenter");
                tradie.setExperience(4);
                tradie.setUsername(username);
                return tradie;
            }
        };
    }
}
