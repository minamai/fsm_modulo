package finite.automaton.exceptions;


/**
 * Exception class thrown whenever an invalid alteration relating to the null state is attempted.
 * This includes attempting to force the null state to transition to another state,
 * and attempting to incorporate a new state internal to the FSM using a null value for the string.
 */
public class NullStateMutationException extends Exception{
    public NullStateMutationException(String msg){
        super(msg);
    }
}
