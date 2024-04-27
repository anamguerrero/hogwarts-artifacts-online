package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("Could not find user with Id " + id + " :(");
    }
}