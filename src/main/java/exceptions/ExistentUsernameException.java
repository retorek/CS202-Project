package exceptions;

public class ExistentUsernameException extends Exception{
    public ExistentUsernameException(){
        super("This username already taken!");
    }
}
