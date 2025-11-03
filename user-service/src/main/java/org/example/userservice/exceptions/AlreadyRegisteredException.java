package org.example.userservice.exceptions;

public class AlreadyRegisteredException extends RuntimeException{
    public AlreadyRegisteredException(){
        super("User with this login is already registered");
    }
}
