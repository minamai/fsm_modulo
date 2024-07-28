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

    private State<E> createInternalState(String name, boolean finality, E value)
    throws NullPointerException{
        State<E> state = new State<>(name, finality, value);

        // idea: if incorporating a state thinks we're mutating null state
        // then the string is null, so throw that exception instead
        try {
            incorporateNewState(state);
        }
        catch(NullStateMutationException e){
            throw new NullPointerException("Name of non-null state must not be null.");
        }

        return state;
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
        return states.get(name);
    }

    public State<E> getTransition(State<E> state, char c)
    throws InvalidStateException, InvalidCharacterException{
        // check that both state and character are in FSM
        if(!containsState(state)) {
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
    // boolean state checkers

    public boolean isInAlphabet(char c){
        // iff search returns valid (non-negative) index, char is in alphabet
        return Arrays.binarySearch(alphabet, c) >= 0;
    }

    public boolean containsState(State<E> state){
        return states.containsValue(state);
    }

    public boolean isReady(){
        return initState != null;
    }

    public boolean isStringOfAlphabet(String string){
        for(int i = 0; i < string.length(); i++){
            if(!isInAlphabet(string.charAt(i))){
                return false;
            }
        }
        return true;
    }


    //////////////
    // setters

    public void setInitState(State<E> state) throws InvalidStateException{
        if(!containsState(state)) {
            throw new InvalidStateException("Attempting to set state not in FSM to initial state.");
        }
        initState = state;
    }

    public void setTransition(State<E> current, char c, State<E> next)
            throws InvalidStateException, InvalidCharacterException, NullStateMutationException{
        // check that both states and character are in FSM
        if(!containsState(current)) {
            throw new InvalidStateException("Attempting to set transition from state not in FSM.");
        }
        if(!containsState(next)) {
            throw new InvalidStateException("Attempting to set transition to state not in FSM.");
        }
        if(Arrays.binarySearch(alphabet, c) < 0){
            throw new InvalidCharacterException("Attempting to set transition using character not in alphabet.");
        }
        // if from null state, do not change
        if(current.equals(nullState)){
            throw new NullStateMutationException("Attempting to set transition from null state");
        }
        transitionTable.get(current).put(c, next);
    }


    //////////////
    // public state incorporation

    public void incorporateNewState(State<E> state)
    throws NullPointerException, NullStateMutationException{
        // no need to return the state as it's already in the user's hands

        if(state == null){
            throw new NullPointerException("State cannot be null pointer");
        }
        if(state.getName() == null){
            throw new NullStateMutationException("Cannot incorporate new null state");
        }

        // remove all traces of old state with same name
        State<E> sameNameState = getStateByName(state.getName());
        if(sameNameState != null){
            // remove old state from transition table
            transitionTable.remove(sameNameState);
        }

        // add to states list. automatically replaces old state
        states.put(state.getName(), state);

        // add to transition table
        transitionTable.put(state, new HashMap<>());
    }

    public State<E> setNewState(String name) throws NullPointerException{
        // just give every parameter to get it over with
        return createInternalState(name, false, null);
    }

    public State<E> setNewState(String name, E value)throws NullPointerException{
        return createInternalState(name, false, value);
    }

    public State<E> setNewFinalState(String name, E value)throws NullPointerException{
        return createInternalState(name, true, value);
    }


    ////////////////////////
    // functionality

    public State<E> runMachine(String data)
    throws IncompleteMachineException, InvalidCharacterException{
        if(!isReady()){
            throw new IncompleteMachineException("Make sure all required FSM fields are filled in.");
        }
        if(!isStringOfAlphabet(data)){
            throw new InvalidCharacterException("Character in data is not in alphabet.");
        }

        // since transitions aren't too bad (O(1) amortized time and space)
        // it's fine if wew check characters in the alphabet as we move along
        State<E> current = initState;

        // this could be checked by name == null, but oh well
        for(int i = 0; i < data.length(); i++){
            // if current is null state then return it quickly
            if(current.equals(nullState)){
                return current;
            }

            // transition
            char c = data.charAt(i);
            try {
                current = getTransition(current, c);
            }
            catch(InvalidStateException e){
                // if invalid state, return null, because why not
                // should never happen, since that would mean that current state was not in FSM
                // that means that we have a bug on our hands
                return null;
            }
        }

        return current;
    }

}
