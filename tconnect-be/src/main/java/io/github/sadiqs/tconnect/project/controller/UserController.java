package io.github.sadiqs.tconnect.project.controller;

import io.github.sadiqs.tconnect.project.model.AppUser;
import io.github.sadiqs.tconnect.project.service.UserDetailsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    final UserDetailsProvider userService;

    @GetMapping
    Optional<? extends AppUser> userDetails(Principal principal) {
        return userService.getUserInfo(principal.getName());
    }

}
