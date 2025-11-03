package org.example.userservice.controllers;


import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.LoginUserDto;
import org.example.userservice.dto.RegisterUserDto;
import org.example.userservice.dto.UpdateUserNameDto;
import org.example.userservice.dto.UpdateUserPasswordDto;
import org.example.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDto user){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user.login(),user.password(),user.name()));
    }
    @PostMapping("/login")
    public ResponseEntity<String> logInto(@RequestBody LoginUserDto user){
        return ResponseEntity.ok(userService.loginUser(user.login(),user.password()));
    }
    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestHeader(name = "User-Id") Long id, @RequestBody UpdateUserPasswordDto passwordDto){
        userService.updatePassword(id,passwordDto.password());
        return ResponseEntity.ok("Password updated successfully");
    }
    @PatchMapping("/name")
    public ResponseEntity<String> updateName(@RequestHeader(name = "User-Id") Long id,@RequestBody UpdateUserNameDto nameDto){
        userService.updateName(id, nameDto.name());
        return ResponseEntity.ok("Name updated successfully");
    }
    @DeleteMapping()
    public ResponseEntity<String> deleteUser(@RequestHeader(name="User-Id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

}
