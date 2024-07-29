package finite.automaton.exceptions;


/**
 * Exception class used for whenever a state external to an FSM was about to be used in it.
 */
public class InvalidStateException extends Exception{
    public InvalidStateException(String msg){
        super(msg);
    }
}
