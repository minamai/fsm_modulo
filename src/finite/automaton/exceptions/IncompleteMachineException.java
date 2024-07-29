package finite.automaton.exceptions;


/**
 * Exception class used for whenever the machine was about to run while not ready.
 */
public class IncompleteMachineException extends Exception{
    public IncompleteMachineException(String msg){
        super(msg);
    }
}
