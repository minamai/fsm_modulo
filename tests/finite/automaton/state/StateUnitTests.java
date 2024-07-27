package finite.automaton.state;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class StateUnitTests extends TestCase {

    private final String NAME = "start";
    private final int VALUE = 404;
    private final int VALUE2 = 200;


    ////////////////////////
    // private helpers

    private void assertIntegerValueEquals(int expected, Integer value){
        if(value == null){
            fail("failed to initialize state value");
        }
        assertEquals(expected, value.intValue());
    }

    private void checkWholeStateFinal(State<Integer> state, String name, int value){
        assertEquals(name, state.getName());
        assertTrue(state.isFinal());
        assertIntegerValueEquals(value, state.getValue());
        assertIntegerValueEquals(value, state.getStateResult());
        assertSame(state.getValue(), state.getStateResult());
    }

    private void checkWholeStateNotFinal(State<Integer> state, String name, int value){
        assertEquals(name, state.getName());
        assertFalse(state.isFinal());
        assertIntegerValueEquals(value, state.getValue());
        assertNull(state.getStateResult());
    }

    private void checkValuelessStateFinal(State<Integer> state, String name){
        assertEquals(name, state.getName());
        assertTrue(state.isFinal());
        assertNull(state.getValue());
        assertNull(state.getStateResult());
    }

    private void checkValuelessStateNotFinal(State<Integer> state, String name){
        assertEquals(name, state.getName());
        assertFalse(state.isFinal());
        assertNull(state.getValue());
        assertNull(state.getStateResult());
    }


    ////////////////////////
    // constructor tests
    // also test getters

    @Test
    public void testCreateWholeStateFinal() {
        State<Integer> state = new State<>(NAME, true, VALUE);
        checkWholeStateFinal(state, NAME, VALUE);
    }

    @Test
    public void testCreateWholeStateNotFinal(){
        State<Integer> state = new State<>(NAME, false, VALUE);
        checkWholeStateNotFinal(state, NAME, VALUE);
    }

    @Test
    public void testCreateValuelessFinalState(){
        State<Integer> state = new State<>(NAME, true);
        checkValuelessStateFinal(state, NAME);
    }

    @Test
    public void testCreateValuelessStateNotFinal(){
        State<Integer> state = new State<>(NAME, false);
        checkValuelessStateNotFinal(state, NAME);
    }

    @Test
    public void testCreateBlankState(){
        State<Integer> state = new State<>(NAME);
        checkValuelessStateNotFinal(state, NAME);
    }


    //////////////
    // setter tests

    @Test
    public void testMakeWholeStateFinal(){
        State<Integer> state = new State<>(NAME, false, VALUE);
        Integer oldValue = state.getValue();
        Integer oldResult = state.getStateResult();

        state.setFinality(true);
        Integer newValue = state.getValue();
        Integer newResult = state.getStateResult();

        // check state now
        checkWholeStateFinal(state, NAME, VALUE);

        // check compared to old
        assertSame(oldValue, newValue);
        assertNotSame(oldResult, newResult); // oldResult should be null
    }

    @Test
    public void testKeepWholeStateFinal(){
        State<Integer> state = new State<>(NAME, true, VALUE);
        Integer oldValue = state.getValue();
        Integer oldResult = state.getStateResult();

        state.setFinality(true);
        Integer newValue = state.getValue();
        Integer newResult = state.getStateResult();

        // check values outright
        checkWholeStateFinal(state, NAME, VALUE);

        // check compared to old
        assertSame(oldValue, newValue);
        assertSame(oldResult, newResult);
    }

    @Test
    public void testMakeWholeStateNotFinal(){
        State<Integer> state = new State<>(NAME, true, VALUE);
        Integer oldValue = state.getValue();
        Integer oldResult = state.getStateResult();

        state.setFinality(false);
        Integer newValue = state.getValue();
        Integer newResult = state.getStateResult();

        // check values outright
        checkWholeStateNotFinal(state, NAME, VALUE);

        // check compared to old
        assertSame(oldValue, newValue);
        assertNotSame(oldResult, newResult);
    }

    @Test
    public void testKeepWholeStateNotFinal(){
        State<Integer> state = new State<>(NAME, false, VALUE);
        Integer oldValue = state.getValue();
        Integer oldResult = state.getStateResult();

        state.setFinality(false);
        Integer newValue = state.getValue();
        Integer newResult = state.getStateResult();

        // check values outright
        checkWholeStateNotFinal(state, NAME, VALUE);

        // check compared to old
        assertSame(oldValue, newValue);
        assertSame(oldResult, newResult); // both should be null
    }

    @Test
    public void testSetValueAfterInitFinal(){
        State<Integer> state = new State<>(NAME, true);
        state.setValue(VALUE);

        // check values outright
        checkWholeStateFinal(state, NAME, VALUE);
    }

    @Test
    public void testSetValueAfterInitNotFinal(){
        State<Integer> state = new State<>(NAME, false);
        state.setValue(VALUE);

        // check values outright
        checkWholeStateNotFinal(state, NAME, VALUE);
    }

    @Test
    public void testResetValueAfterInit(){
        State<Integer> state = new State<>(NAME, true, VALUE);
        Integer oldValue = state.getValue();
        state.setValue(VALUE2);

        // check values outright
        checkWholeStateFinal(state, NAME, VALUE2);

        // compare to old value
        assertNotSame(oldValue, state.getValue());
    }
}