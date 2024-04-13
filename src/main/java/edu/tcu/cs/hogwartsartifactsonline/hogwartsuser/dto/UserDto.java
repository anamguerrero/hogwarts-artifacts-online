package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.dto;

import jakarta.validation.constraints.NotEmpty;

//this is what the client can see per se
public record UserDto(Integer id,
                      @NotEmpty(message = "username is required.")
                      String username,
                      boolean enabled,
                      @NotEmpty(message = "roles are required.")
                      String roles) {
}