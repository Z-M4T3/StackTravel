package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.EmailAlreadyTakenException;
import hu.unideb.inf.exceptions.UserNotFoundException;
import hu.unideb.inf.exceptions.UsernameAlreadyTakenException;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.entity.User;
import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final EntityMapper mapper;
    private final PasswordManagementService passwordManagementService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MessageSource messageSource,
            EntityMapper mapper,
            PasswordManagementService passwordManagementService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
        this.mapper = mapper;
        this.passwordManagementService = passwordManagementService;
    }

    public UserDto register(UserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyTakenException(
                    message("user.username.taken", dto.getUsername())
            );
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyTakenException(
                    message("user.email.already-registered", dto.getEmail())
            );
        }

        User user = mapper.dtoToUser(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);
        return mapper.userToDto(saved);
    }

    public UserDto updateProfile(Integer id, UserDto dto) {
        User user = findUserOrThrow(id);

        if (dto.getUsername() != null
                && !dto.getUsername().isBlank()
                && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new UsernameAlreadyTakenException(
                        message("user.username.taken", dto.getUsername())
                );
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null
                && !dto.getEmail().isBlank()
                && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyTakenException(
                        message("user.email.taken", dto.getEmail())
                );
            }
            user.setLastEmail(user.getEmail());
            user.setEmail(dto.getEmail());
            user.setLastEmailChange(LocalDateTime.now());
        }

        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }
        if (dto.getPicture() != null) {
            user.setPicture(dto.getPicture());
        }

        return mapper.userToDto(userRepository.save(user));
    }

    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        passwordManagementService.changePassword(userId, currentPassword, newPassword);
    }

    public void resetPassword(String rawToken, String newPassword) {
        passwordManagementService.resetPassword(rawToken, newPassword);
    }

    public String requestPasswordReset(String email) {
        return passwordManagementService.requestPasswordReset(email);
    }

    public void deleteById(Integer id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    public Optional<UserDto> authenticate(UserDto dto) {
        Optional<User> userOpt = userRepository.findByUsername(dto.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(dto.getUsernameOrEmail());
        }

        if (userOpt.isEmpty()
                || !passwordEncoder.matches(dto.getPassword(), userOpt.get().getPasswordHash())) {
            return Optional.empty();
        }

        User user = userOpt.get();
        user.setLastLogin(LocalDateTime.now());

        return Optional.of(mapper.userToDto(userRepository.save(user)));
    }


    //helpers
    private String message(String code, Object... arguments) {
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }

    private User findUserOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(message("user.not-found", id)));
    }
}
