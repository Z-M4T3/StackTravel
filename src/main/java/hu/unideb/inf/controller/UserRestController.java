package hu.unideb.inf.controller;

import hu.unideb.inf.model.dto.LoginRequest;
import hu.unideb.inf.model.dto.ChangePasswordRequest;
import hu.unideb.inf.model.dto.ProfileUpdateRequest;
import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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

    @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
    public String login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        return userService.getProfile(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())));
    }

    @PatchMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserDto updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())), request);
    }

    @PostMapping(value = "/password-reset", consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String requestPasswordReset(@RequestBody String email) {
        return userService.requestPasswordReset(email);
    }

    @PostMapping(value = "/password-reset/confirm", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        userService.resetPassword(token, newPassword);
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())),
                request.getPassword(), request.getNewPassword());
    }

    @DeleteMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@AuthenticationPrincipal Jwt jwt) {
        userService.deleteById(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())));
    }

}
