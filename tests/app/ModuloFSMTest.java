package app;

import finite.automaton.FSM;
import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Test;

import static finite.automaton.state.StateValueChecker.checkWholeStateFinal;

public class ModuloFSMTest extends TestCase {
    private final static int MAX_MODULO = 50;
    private final static int MAX_BASE = Character.MAX_RADIX;
    private final static int UPPER_BOUND = 2 * MAX_BASE * MAX_BASE;


    //////////////
    // private helpers

    private static void assertIntegerValueEquals(int expected, Integer value){
        if(value == null){
            fail("failed to initialize state value");
        }
        assertEquals(expected, value.intValue());
    }

    private static void testOneMachine(int base, int modulo){

        FSM<Integer> machine = ModuloFSMFactory.makeModuloFSM(base, modulo);
        // assert basic features of machine:
        // - machine is not null
        // - one state per modulo value + null state (unremovable)
        // - alphabet is as long as base
        assertNotNull(machine);
        assertEquals(modulo + 1, machine.getStates().size());
        assertEquals(base, machine.getAlphabet().length);

        // check states match up with bases
        for(int i = 0; i < modulo; i++){
            String name = "S" + i;
            checkWholeStateFinal(machine.getStateByName(name), name, i);
        }

        // check state zero
        assertSame(machine.getStateByName("S0"), machine.getInitState());


        // run machine, make sure all outputs are valid
        try {
            // try every integer as a string up to upper bound
            for (int num = 0; num <= UPPER_BOUND; num++) {
                String numString = Integer.toUnsignedString(num, base);
                int expected = num % modulo;

                State<Integer> output = machine.runMachine(numString);
                assertNotNull(output);
                assertIntegerValueEquals(expected, output.getStateResult());
            }
        }
        catch(Exception e){
            fail("Failed to process all test cases.");
        }
    }


    //////////////
    // valid tests

    @Test
    public void testAllMachines(){
        for(int modulo = 2; modulo < MAX_MODULO; modulo++){
            for(int base = 2; base < MAX_BASE; base++){
                testOneMachine(base, modulo);
            }
        }
    }


    //////////////
    // invalid tests

    @Test
    public void testTooLowModuloMachines(){
        for(int base = 2; base <= 10; base++){
            for(int modulo = -4; modulo < 2; modulo++){
                assertNull(ModuloFSMFactory.makeModuloFSM(base, modulo));
            }
        }
    }

    @Test
    public void testTooLowBaseMachines(){
        for(int base = -4; base < 2; base++){
            for(int modulo = 2; modulo <= 10; modulo++){
                assertNull(ModuloFSMFactory.makeModuloFSM(base, modulo));
            }
        }
    }

    @Test
    public void testTooHighBaseMachines(){
        for(int base = MAX_BASE + 1; base < MAX_BASE * MAX_BASE; base++){
            for(int modulo = 2; modulo < 10; modulo++){
                assertNull(ModuloFSMFactory.makeModuloFSM(base, modulo));
            }
        }
    }
}
