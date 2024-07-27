package finite.automaton.state;

import static org.junit.Assert.*;


/**
 * A collection of functions designed to check the exact status of a state
 */
public class StateValueChecker{

    private static void assertIntegerValueEquals(int expected, Integer value){
        if(value == null){
            fail("failed to initialize state value");
        }
        assertEquals(expected, value.intValue());
    }

    public static void checkWholeStateFinal(State<Integer> state, String name, int value){
        assertEquals(name, state.getName());
        assertTrue(state.isFinal());
        assertIntegerValueEquals(value, state.getValue());
        assertIntegerValueEquals(value, state.getStateResult());
        assertSame(state.getValue(), state.getStateResult());
    }

    public static void checkWholeStateNotFinal(State<Integer> state, String name, int value){
        assertEquals(name, state.getName());
        assertFalse(state.isFinal());
        assertIntegerValueEquals(value, state.getValue());
        assertNull(state.getStateResult());
    }

    public static void checkValuelessStateFinal(State<Integer> state, String name){
        assertEquals(name, state.getName());
        assertTrue(state.isFinal());
        assertNull(state.getValue());
        assertNull(state.getStateResult());
    }

    public static void checkValuelessStateNotFinal(State<Integer> state, String name){
        assertEquals(name, state.getName());
        assertFalse(state.isFinal());
        assertNull(state.getValue());
        assertNull(state.getStateResult());
    }
}
