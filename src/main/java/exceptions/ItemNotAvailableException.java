package exceptions;

public class ItemNotAvailableException extends Exception{
    public ItemNotAvailableException(){
        super("This item is no longer available!");
    }
}
