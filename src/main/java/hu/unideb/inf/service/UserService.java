package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.EmailAlreadyTakenException;
import hu.unideb.inf.exceptions.IncorrectPasswordException;
import hu.unideb.inf.exceptions.UserNotFoundException;
import hu.unideb.inf.exceptions.UsernameAlreadyTakenException;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.entiry.User;
import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.repository.UserRepository;
import hu.unideb.inf.security.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper mapper;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EntityMapper mapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public UserDto register(UserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyTakenException(
                    "A felhasználónév már foglalt: " + dto.getUsername()
            );
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyTakenException(
                    "Ez az email cím már regisztrálva van: " + dto.getEmail()
            );
        }

        User user = mapper.dtoToUser(dto);
        user.setPass(passwordEncoder.encode(dto.getPassword()));

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
                        "A felhasználónév már foglalt: " + dto.getUsername()
                );
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null
                && !dto.getEmail().isBlank()
                && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyTakenException(
                        "Ez az email cím már foglalt: " + dto.getEmail()
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

    public void changePassword(Integer id, UserDto dto) {
        User user = findUserOrThrow(id);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPass())) {
            throw new IncorrectPasswordException("A jelenlegi jelszó helytelen!");
        }

        user.setPass(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
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
                || !passwordEncoder.matches(dto.getPassword(), userOpt.get().getPass())) {
            return Optional.empty();
        }

        User user = userOpt.get();
        user.setLastLogin(LocalDateTime.now());

        return Optional.of(mapper.userToDto(userRepository.save(user)));
    }

    private User findUserOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("Felhasználó nem található (ID: " + id + ")"));
    }
}
