package finite.automaton.exception;

import finite.automaton.FSM;
import finite.automaton.exceptions.NullStateMutationException;
import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class NullStateMutationTests extends TestCase {
    char[] ALPHABET = {'0', '1', '2'};
    String NAME = "hello";

    @Test
    public void testSetTransitionFromNull(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> nullState = machine.getNullState();
        State<Integer> state = machine.addNewState(NAME);

        assertThrows("Attempting to set transition from null state",
                NullStateMutationException.class,
                () -> machine.setTransition(nullState, ALPHABET[0], state));

        try{
            assertSame(nullState, machine.getTransition(nullState, ALPHABET[0]));
        }
        catch(Exception e){
            fail("Failed to get null transition: " + e.getMessage());
        }
    }

    @Test
    public void testSetTransitionFromNullToNull(){
        // currently, this shares the same behaviour.
        // it's possible, though highly unlikely, that this will change
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> nullState = machine.getNullState();

        assertThrows("Attempting to set transition from null state",
                NullStateMutationException.class,
                () -> machine.setTransition(nullState, ALPHABET[0], nullState));

        try{
            assertSame(nullState, machine.getTransition(nullState, ALPHABET[0]));
        }
            catch(Exception e){
            fail("Failed to get null transition: " + e.getMessage());
        }
    }
}
