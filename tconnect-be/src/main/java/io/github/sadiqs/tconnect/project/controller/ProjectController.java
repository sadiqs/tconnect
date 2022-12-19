package io.github.sadiqs.tconnect.project.controller;

import io.github.sadiqs.tconnect.project.model.AppRoles;
import io.github.sadiqs.tconnect.project.model.Bid;
import io.github.sadiqs.tconnect.project.model.Project;
import io.github.sadiqs.tconnect.project.model.request.BidCreateRequest;
import io.github.sadiqs.tconnect.project.model.request.ProjectCreateRequest;
import io.github.sadiqs.tconnect.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/bids")
    @PreAuthorize("hasAuthority('TRADIE')")
    public Bid placeBid(Principal principal, @RequestBody BidCreateRequest bid) {
        return projectService.placeBid(principal.getName(), bid);
    }

    @GetMapping("/bids")
    @PreAuthorize("hasAuthority('TRADIE')")
    public List<Bid> getTradieBids(Principal principal) {
        return projectService.getAllBids(principal.getName());
    }

    @PostMapping("/projects")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public Project createProject(Principal principal, @RequestBody ProjectCreateRequest request) {
        return projectService.createProject(principal.getName(), request);
    }

    @GetMapping("/projects")
    public Iterable<Project> getCustomerProjects(Authentication authentication) {
        var authorities = authentication.getAuthorities();
        if (authorities.stream().anyMatch(authority -> authority.getAuthority().equals(AppRoles.CUSTOMER.name())))
            return projectService.getCustomerProjects(authentication.getName());
        else if (authorities.stream().anyMatch(authority -> authority.getAuthority().equals(AppRoles.TRADIE.name()))) {
            return projectService.getAllProjects();
        }

        throw new AccessDeniedException("You do not have access to projects");
    }
}

