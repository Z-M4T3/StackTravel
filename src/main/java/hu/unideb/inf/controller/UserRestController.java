package hu.unideb.inf.controller;

import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody UserDto request) {
        return userService.register(request);
    }


    @PostMapping("/password-reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String requestPasswordReset(
            @Valid @RequestBody String email) {
        return userService.requestPasswordReset(email);
    }

}
