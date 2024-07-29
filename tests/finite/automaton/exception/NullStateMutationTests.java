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


    //////////////
    // null state transitions tests

    @Test
    public void testSetTransitionFromNull(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> nullState = machine.getNullState();
        State<Integer> state = machine.setNewState(NAME);

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


    //////////////
    // null string tests

    @Test
    public void testNullStringStateIncorporation(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> fakeNullState = new State<>(null);
        // currently, a fake null state can be created freely
        // and will not be equal to any FSM's null state
        assertNotSame(machine.getNullState(), fakeNullState);

        // attempting to incorporate it, though, will fail
        assertThrows("Name of non-null state must not be null.",
                NullStateMutationException.class,
                () -> machine.incorporateNewState(fakeNullState));
    }

    @Test
    public void testNullStringNewState(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertThrows(IllegalArgumentException.class,
                () -> machine.setNewState(null));
        assertThrows(IllegalArgumentException.class,
                () -> machine.setNewState(null, 1));
        assertThrows(IllegalArgumentException.class,
                () -> machine.setNewFinalState(null, 2));
    }


    //////////////
    // null pointer state tests

    @Test
    public void testNullPointerStateIncorporation(){
        FSM<Integer> machine = new FSM<>(ALPHABET);

        // states in FSM's must not be null
        assertThrows(IllegalArgumentException.class,
                () -> machine.incorporateNewState(null));
    }
}
