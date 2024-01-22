package ru.heumn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.heumn.repository.dto.UserDto;
import ru.heumn.repository.entities.UserEntity;
import ru.heumn.repository.enums.Role;
import ru.heumn.repository.exceptions.BadRequestException;
import ru.heumn.repository.exceptions.ConflictRequestException;
import ru.heumn.repository.exceptions.NotFoundException;
import ru.heumn.repository.factories.UserDtoFactory;
import ru.heumn.repository.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDtoFactory userDtoFactory;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        Optional<UserEntity> user = userRepository.findByLogin(name);

        if(user.isPresent())
        {
            user.get().setLastLogin(Instant.now());
            userRepository.save(user.get());
            return user.get();
        }
        else {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        // тут было userRepository.findByLogin(name)

    }

    public void updateUser(UserDto userDto) throws NotFoundException, ConflictRequestException {
        UserEntity user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id не найден"));

        if(!user.getPassword().equals(userDto.getPassword()))
        {
            String encodedPassword = new BCryptPasswordEncoder().encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
        }
        if(userRepository.findByLogin(userDto.getLogin()).isPresent()){
            throw new ConflictRequestException("данный логин уже есть у другого пользователя");
        }
        else{
            userRepository.save(userDtoFactory.makeUserEntity(userDto));
        }
    }

    public UserEntity addUser(UserDto userDto) throws ConflictRequestException {

        String encodedPassword = new BCryptPasswordEncoder().encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        UserEntity user = userDtoFactory.makeUserEntity(userDto);

        if(userRepository.findByLogin(user.getLogin()).isPresent()){
            throw new ConflictRequestException("данный логин уже есть у другого пользователя");
        }
        else {
            user.setActive(true);
            user.setDateCreate(Instant.now());

            userRepository.save(user);
            return user;
        }
    }

    public Boolean deleteUser(Long id) throws NotFoundException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));

        try {
            userRepository.delete(user);
            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    public List<UserDto> getAllUsers(){

        List<UserEntity> userEntityList = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        userEntityList
                .forEach(user -> userDtoList.add(userDtoFactory.makeUserDto(user)));

        return userDtoList;
    }

    public UserDto setRoleForUser(Long id, Role role) throws NotFoundException, ConflictRequestException {

        Optional<UserEntity> user = userRepository.findById(id);

        if(user.isPresent() )
        {
            if(!user.get().getRoles().contains(role))
            {
                user.get().getRoles().add(role);
                userRepository.save(user.get());
                return userDtoFactory.makeUserDto(user.get());
            }
            else
            {
                throw new ConflictRequestException("the user already has this role");
            }
        }
        else
        {
            throw new NotFoundException("the user with this id was not found");
        }
    }

    public void deleteRoleForUser(Long id, Role role) throws NotFoundException, BadRequestException {

        Optional<UserEntity> user = userRepository.findById(id);

        if(user.isPresent() )
        {
            if(user.get().getRoles().contains(role))
            {
                user.get().getRoles().remove(role);
                userRepository.save(user.get());
            }
            else
            {
                throw new BadRequestException("The user does not have such a role");
            }
        }
        else
        {
            throw new NotFoundException("the user with this id was not found");
        }
    }
}
