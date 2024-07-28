package finite.automaton;

import finite.automaton.state.State;
import finite.automaton.exceptions.*;

import java.util.*;


// E is the output type
public class FSM<E> {

    // these exact types may not survive

    // canonical set of states of this FSM
    // all states used must be here
    private final Map<String, State<E>> states;

    private final char[] alphabet;
    private int alphaLength; // for easy access

    private State<E> initState;

    private final State<E> nullState;

    // for now a 2-tier map
    // decisions decisions (how to handle unset values: null state or null value?)
    private final Map<State<E>, Map<Character, State<E>>> transitionTable;


    ////////////////////////
    // private helpers

    // private because alphabet is fundamental
    // changes are not a good idea
    private char[] setAlphabet(char[] alphabet){
        this.alphaLength = alphabet.length;
        Arrays.sort(alphabet);
        //// alt version for non-final alphabet:
        // this.alphabet = alphabet;
        return alphabet;
    }

    private void incorporateNewState(State<E> state){
        // add to states list
        // TODO: where to handle overwriting of states
        states.put(state.getName(), state);

        // add to transition table
        transitionTable.put(state, new HashMap<>());
    }


    ////////////////////////
    // constructors

    public FSM(char[] alphabet){
        // this is a way to set final values while doing a helper function
        // final values cannot be set outside a
        // credit: https://stackoverflow.com/questions/11758440/initialize-final-variable-within-constructor-in-another-method
        this.alphabet = setAlphabet(alphabet);
        nullState = new State<>(null);

        states = new HashMap<>();
        states.put(null, nullState);

        transitionTable = new HashMap<>();
    }


    //////////////
    // getters

    public List<State<E>> getStates() {
        return new ArrayList<>(states.values());
    }

    public char[] getAlphabet() {
        return alphabet;
    }

    public State<E> getInitState(){
        return initState;
    }

    public State<E> getNullState(){
        return nullState;
    }

    public State<E> getStateByName(String name){
        // can this be improved?
        return states.get(name);
    }

    public State<E> getTransition(State<E> state, char c)
    throws InvalidStateException, InvalidCharacterException{
        // check that both state and character are in FSM
        if(!states.containsValue(state)) {
            throw new InvalidStateException("Attempting to transition from state not in FSM.");
        }
        if(Arrays.binarySearch(alphabet, c) < 0){
            throw new InvalidCharacterException("Attempting to transition using character not in alphabet.");
        }
        // if null state, return null state
        if(state.equals(nullState)){
            return nullState;
        }
        State<E> output = transitionTable.get(state).get(c);
        // if no explicit mapping, output nullState
        return output != null ? output : nullState;
    }


    //////////////
    // setters & field constructors

    public void setInitState(State<E> state) throws InvalidStateException{
        if(!states.containsValue(state)) {
            throw new InvalidStateException("Attempting to set state not in FSM to initial state.");
        }
        initState = state;
    }

    public State<E> addNewState(String name){
        State<E> state = new State<>(name);
        incorporateNewState(state);
        return state;
    }

    public State<E> addNewState(String name, E value){
        State<E> state = new State<>(name, false, value);
        incorporateNewState(state);
        return state;
    }

    public State<E> addNewFinalState(String name, E value){
        State<E> state = new State<>(name, true, value);
        incorporateNewState(state);
        return state;
    }

    public void setTransition(State<E> current, char c, State<E> next)
            throws InvalidStateException, InvalidCharacterException, NullStateMutationException{
        // check that both states and character are in FSM
        if(!states.containsValue(current)) {
            throw new InvalidStateException("Attempting to set transition from state not in FSM.");
        }
        if(!states.containsValue(next)) {
            throw new InvalidStateException("Attempting to set transition to state not in FSM.");
        }
        if(Arrays.binarySearch(alphabet, c) < 0){
            throw new InvalidCharacterException("Attempting to transition using character not in alphabet.");
        }
        // if from null state, do not change
        if(current.equals(nullState)){
            throw new NullStateMutationException("Attempting to set transition from null state");
        }
        transitionTable.get(current).put(c, next);
    }
}
