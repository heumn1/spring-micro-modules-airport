package ru.heumn.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.heumn.repository.dto.UserDto;
import ru.heumn.repository.entities.UserEntity;
import ru.heumn.repository.enums.Role;
import ru.heumn.repository.exceptions.BadRequestException;
import ru.heumn.repository.exceptions.ConflictRequestException;
import ru.heumn.repository.exceptions.NotFoundException;
import ru.heumn.repository.factories.UserDtoFactory;
import ru.heumn.repository.repository.UserRepository;
import ru.heumn.services.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDtoFactory userDtoFactory;
    @Autowired
    UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) throws NotFoundException {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));

            return new ResponseEntity<>(userDtoFactory.makeUserDto(user), HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto, BindingResult bindingResult)
            throws BadRequestException, ConflictRequestException {

        hasErrorDto(bindingResult);
        UserDto user = userDtoFactory.makeUserDto(userService.addUser(userDto));

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id)
            throws NotFoundException{

        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserDto userDto, BindingResult bindingResult)
            throws NotFoundException, BadRequestException, ConflictRequestException {

        hasErrorDto(bindingResult);
        userService.updateUser(userDto);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/asd")
    public ResponseEntity<UserDto> asfas() throws NotFoundException, ConflictRequestException {
        return new ResponseEntity<>(userService.setRoleForUser(102L, Role.ADMIN_ROLE), HttpStatus.OK);
    }

    private void hasErrorDto(BindingResult bindingResult) throws BadRequestException {
        if(bindingResult.hasErrors()) {
            List<String> list = new ArrayList<>();
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                String defaultMessage = objectError.getDefaultMessage();
                list.add(defaultMessage);
            }
            throw new BadRequestException(list.toString());
        }
    }
}
