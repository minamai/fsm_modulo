package finite.automaton.exceptions;


/**
 * Exception class used for whenever a character outside of a machine's alphabet was about to be used.
 */
public class InvalidCharacterException extends Exception{
    public InvalidCharacterException(String msg){
        super(msg);
    }
}
