package hu.unideb.inf.controller;

import hu.unideb.inf.model.dto.*;
import hu.unideb.inf.model.entity.UserTrip;
import hu.unideb.inf.service.UserService;
import hu.unideb.inf.service.UserTripService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {

    private final UserService userService;
    private final UserTripService userTripService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody UserDto request) {
        return userService.register(request);
    }

    @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
    public String login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/{userId}/trip/{tripId}")
    public UserTripDto addTripToUser(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer userId, @PathVariable Integer tripId) {
        return userTripService.addTripToUser(userId, tripId);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        return userService.getProfile(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())));
    }

    @GetMapping("/{userId}/trip/{tripId}")
    public UserTripDto getUserTrip(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer userId, @PathVariable Integer tripId) {
        return userTripService.getUserTrip(userId, tripId);
    }

    @GetMapping("/{userId}/trips")
    public List<UserTripDto> getAllUserTrip(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer userId) {
        return userTripService.getAllUserTrip(userId);
    }

    @PatchMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserDto updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())), request);
    }

    @PatchMapping("/{userId}/trip/{tripId}/change-permission/{permissionLevel}")
    public UserTripDto modifyPermissionLevelOnTrip(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer userId, @PathVariable Integer tripId, @PathVariable String permissionLevel) {
        return userTripService.modifyPermissionLevelOnTrip(userId, tripId, permissionLevel);
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

    @DeleteMapping("/{userId}/trip/{tripId}")
    public void deleteTripFromUser(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer userId, @PathVariable Integer tripId) {
        userTripService.deleteTripFromUser(userId, tripId);
    }

    @DeleteMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@AuthenticationPrincipal Jwt jwt) {
        userService.deleteById(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())));
    }

}