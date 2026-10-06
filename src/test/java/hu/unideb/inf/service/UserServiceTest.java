package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.EmailAlreadyTakenException;
import hu.unideb.inf.exceptions.IncorrectPasswordException;
import hu.unideb.inf.exceptions.UserNotFoundException;
import hu.unideb.inf.exceptions.UsernameAlreadyTakenException;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.model.entiry.User;
import hu.unideb.inf.repository.UserRepository;
import hu.unideb.inf.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Integer USER_ID = 42;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPass("stored-hash");
        user.setBirthDate(LocalDate.of(1995, 4, 12));
        user.setPicture("old-picture.png");

        userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setUsername("alice");
        userDto.setEmail("alice@example.com");
        userDto.setUsernameOrEmail("alice");
        userDto.setPassword("old-password");
        userDto.setNewPassword("new-password");
        userDto.setBirthDate(LocalDate.of(1995, 4, 12));
        userDto.setPicture("old-picture.png");
    }

    @Test
    void registerShouldEncodePasswordSaveUserAndReturnDto() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(mapper.dtoToUser(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("new-hash");
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.userToDto(user)).thenReturn(userDto);

        UserDto result = userService.register(userDto);

        assertSame(userDto, result);
        assertEquals("new-hash", user.getPass());
        verify(userRepository).save(user);
        verify(mapper).dtoToUser(userDto);
        verify(mapper).userToDto(user);
    }

    @Test
    void registerShouldRejectTakenUsername() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
                () -> userService.register(userDto));

        verify(userRepository, never()).save(user);
    }

    @Test
    void registerShouldRejectTakenEmail() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyTakenException.class,
                () -> userService.register(userDto));

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateProfileShouldUpdateProvidedFieldsAndTrackEmailChange() {
        userDto.setUsername("alice-new");
        userDto.setEmail("alice-new@example.com");
        userDto.setBirthDate(LocalDate.of(1996, 5, 20));
        userDto.setPicture("new-picture.png");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("alice-new")).thenReturn(false);
        when(userRepository.existsByEmail("alice-new@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.userToDto(user)).thenReturn(userDto);

        UserDto result = userService.updateProfile(USER_ID, userDto);

        assertSame(userDto, result);
        assertEquals("alice-new", user.getUsername());
        assertEquals("alice@example.com", user.getLastEmail());
        assertEquals("alice-new@example.com", user.getEmail());
        assertNotNull(user.getLastEmailChange());
        assertEquals(LocalDate.of(1996, 5, 20), user.getBirthDate());
        assertEquals("new-picture.png", user.getPicture());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfileShouldRejectTakenUsername() {
        userDto.setUsername("another-user");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("another-user")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
                () -> userService.updateProfile(USER_ID, userDto));

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateProfileShouldRejectTakenEmail() {
        userDto.setEmail("another@example.com");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("another@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyTakenException.class,
                () -> userService.updateProfile(USER_ID, userDto));

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateProfileShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateProfile(USER_ID, userDto));
    }

    @Test
    void changePasswordShouldVerifyCurrentPasswordAndSaveNewHash() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "stored-hash")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        userService.changePassword(USER_ID, userDto);

        assertEquals("new-hash", user.getPass());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordShouldRejectIncorrectCurrentPassword() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "stored-hash")).thenReturn(false);

        assertThrows(IncorrectPasswordException.class,
                () -> userService.changePassword(USER_ID, userDto));

        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteByIdShouldDeleteExistingUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.deleteById(USER_ID);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteByIdShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteById(USER_ID));

        verify(userRepository, never()).delete(user);
    }

    @Test
    void authenticateShouldFindByUsernameAndUpdateLastLogin() {
        userDto.setUsernameOrEmail("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "stored-hash")).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.userToDto(user)).thenReturn(userDto);

        Optional<UserDto> result = userService.authenticate(userDto);

        assertTrue(result.isPresent());
        assertSame(userDto, result.get());
        assertNotNull(user.getLastLogin());
        verify(userRepository, never()).findByEmail("alice");
    }

    @Test
    void authenticateShouldFallBackToEmail() {
        userDto.setUsernameOrEmail("alice@example.com");
        when(userRepository.findByUsername("alice@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "stored-hash")).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.userToDto(user)).thenReturn(userDto);

        Optional<UserDto> result = userService.authenticate(userDto);

        assertTrue(result.isPresent());
        assertNotNull(user.getLastLogin());
        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void authenticateShouldReturnEmptyForWrongPassword() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "stored-hash")).thenReturn(false);

        Optional<UserDto> result = userService.authenticate(userDto);

        assertFalse(result.isPresent());
        verify(userRepository, never()).save(user);
    }

    @Test
    void authenticateShouldReturnEmptyWhenUserDoesNotExist() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("alice")).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.authenticate(userDto);

        assertFalse(result.isPresent());
        verify(passwordEncoder, never()).matches(userDto.getPassword(), user.getPass());
    }
}
