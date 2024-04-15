package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    List<HogwartsUser> hogwartsUsers;

    @BeforeEach
    void setUp() {
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2);
        u2.setUsername("eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3);
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        this.hogwartsUsers = new ArrayList<>();
        this.hogwartsUsers.add(u1);
        this.hogwartsUsers.add(u2);
        this.hogwartsUsers.add(u3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllSuccess() {
        //given: arrange inputs and targets, define the behavior of Mock object userRepository
        given(this.userRepository.findAll()).willReturn(this.hogwartsUsers);

        //when: act on the target behavior, act steps should cover the method to be tested
        List<HogwartsUser> actualUsers = this.userService.findAll();

        //then: assert expected outcomes
        assertThat(actualUsers.size()).isEqualTo(this.hogwartsUsers.size());

        //verify userRepository.findAll() is called exactly once
        verify(this.userRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        //given: arrange inputs and targets, define the behavior of Mock object userRepository
        HogwartsUser u = new HogwartsUser();
        u.setId(1);
        u.setUsername("john");
        u.setPassword("123456");
        u.setEnabled(true);
        u.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.of(u)); //define the behavior of the mock object

        //when: act on the target behavior, act steps should cover the method to be tested
        HogwartsUser returnedUser = this.userService.findById(1);

        //then: assert expected outcomes
        assertThat(returnedUser.getId()).isEqualTo(u.getId());
        assertThat(returnedUser.getUsername()).isEqualTo(u.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(u.getPassword());
        assertThat(returnedUser.isEnabled()).isEqualTo(u.isEnabled());
        assertThat(returnedUser.getRoles()).isEqualTo(u.getRoles());
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        //given
        given(this.userRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        //when
        Throwable thrown = catchThrowable(() -> {
            HogwartsUser returnedUser = this.userService.findById(1);
        });

        //then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Could not find user with Id 1 :(");
        verify(this.userRepository, times(1)).findById(Mockito.any(Integer.class));
    }

    @Test
    void testSaveSuccess() {
        //given
        HogwartsUser newUser = new HogwartsUser();
        newUser.setUsername("lily");
        newUser.setPassword("123456");
        newUser.setEnabled(true);
        newUser.setRoles("user");

        given(this.passwordEncoder.encode(newUser.getPassword())).willReturn("Encoded Password");
        given(this.userRepository.save(newUser)).willReturn(newUser);

        //when
        HogwartsUser returnedUser = this.userService.save(newUser);

        //when
        assertThat(returnedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(newUser.getPassword());
        assertThat(returnedUser.isEnabled()).isEqualTo(newUser.isEnabled());
        assertThat(returnedUser.getRoles()).isEqualTo(newUser.getRoles());
        verify(this.userRepository, times(1)).save(newUser);
    }

    @Test
    void testUpdateSuccess() {
        //given
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setId(1);
        oldUser.setUsername("john");
        oldUser.setPassword("123456");
        oldUser.setEnabled(true);
        oldUser.setRoles("admin user");

        HogwartsUser update = new HogwartsUser();
        update.setUsername("john - update");
        update.setPassword("123456");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.of(oldUser));
        given(this.userRepository.save(oldUser)).willReturn(oldUser);

        //when
        HogwartsUser updatedUser = this.userService.update(1, update);

        //when
        assertThat(updatedUser.getId()).isEqualTo(1);
        assertThat(updatedUser.getUsername()).isEqualTo(update.getUsername());
        verify(this.userRepository, times(1)).findById(1);
        verify(this.userRepository, times(1)).save(oldUser);
    }

    @Test
    void testUpdateNotFound() {
        //given
        HogwartsUser update = new HogwartsUser();
        update.setUsername("john - update");
        update.setPassword("123456");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            this.userService.update(1, update);
        });

        //then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Could not find user with Id 1 :(");
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteSuccess() {
        //given
        HogwartsUser user = new HogwartsUser();
        user.setId(1);
        user.setUsername("john");
        user.setPassword("123456");
        user.setEnabled(true);
        user.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.of(user));
        doNothing().when(this.userRepository).deleteById(1);

        //when
        this.userService.delete(1);

        //then
        verify(this.userRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFound() {
        //given
        given(this.userRepository.findById(1)).willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            this.userService.delete(1);
        });

        //when
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Could not find user with Id 1 :(");
        verify(this.userRepository, times(1)).findById(1);
    }
}