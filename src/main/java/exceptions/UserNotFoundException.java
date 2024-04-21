package exceptions;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(){
        super("Wrong username or password!");
    }
}
