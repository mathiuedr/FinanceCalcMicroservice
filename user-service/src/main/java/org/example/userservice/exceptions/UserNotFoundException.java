package org.example.userservice.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("User with this login and password not found");
    }
}
