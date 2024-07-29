package app;

import finite.automaton.FSM;
import finite.automaton.state.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuloFSMFactory {

    // In this version of Java, integers get converted to bases higher than 10 up to base 36,
    // using lowercase letters for higher digit values. These are all valid digits outputted, then

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private static final int MAX_BASE = Character.MAX_RADIX;

    static FSM<Integer> makeModuloFSM(int base, int modulo){
        if(base < 2 || base > MAX_BASE || modulo < 2){
            return null;
        }

        FSM<Integer> machine = new FSM<>(Arrays.copyOfRange(DIGITS, 0, base));

        // generate states
        List<State<Integer>> stateList = new ArrayList<>(modulo);
        for(int i = 0; i < modulo; i++){
            stateList.add(machine.setNewFinalState("S" + i, i));
        }

        // make transitions
        try {
            // states are enumerated by digit from 0 to modulo
            // transitions are enumerated by character from 0 to base
            for (int mod = 0; mod < modulo; mod++) {
                for (int digit = 0; digit < base; digit++) {

                    // idea: concatenating is adding digit to end
                    // in numbers that is: (mod | digit) -> mod * BASE + digit
                    machine.setTransition(stateList.get(mod), DIGITS[digit],
                            stateList.get((mod * base + digit) % modulo) );
                }
            }

            machine.setInitState(stateList.get(0));
        }
        catch(Exception e){
            return null;
        }
        return machine;
    }
}
