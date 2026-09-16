package com.apexgrid.engine.controller;

import com.apexgrid.engine.dto.request.RegisterTeamRequestDTO;
import com.apexgrid.engine.dto.response.TeamResponseDTO;
import com.apexgrid.engine.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponseDTO registerTeam(@Valid @RequestBody RegisterTeamRequestDTO request) {
        return teamService.registerTeam(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TeamResponseDTO getTeam(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }
}
