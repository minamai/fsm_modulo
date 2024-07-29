package finite.automaton;

import finite.automaton.state.State;
import finite.automaton.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A finite state machine representation, with a custom state class representing its states.
 * The internal data of the states does not impact the automaton, aside from the constant names.
 * States can be accessed by their names, which are unique for internal states in each FSM.
 * A state is internal to this FSM if it is incorporated into this, and no other state with
 * the same name was incorporated into it, including if this is the FSM that created it.
 * Note that <code>null</code> cannot be used as a state in any FSM.
 *
 * The alphabet is a constant array of characters. While it is possible to alter the alphabet
 * by retaining a reference to it, it is not recommended for that to be done, as the FSM requires
 * it to be externally unaltered for smooth working.
 *
 * Each FSM has a null state, also known as a dead state. By default, it is a non-final state with no
 * pre-set value.It starts as the FSM's only state. One is allowed to modify its finality and value
 * as desired, depending on use case. It is also the unique state that has <code>null</code> as its name,
 * something that is not allowed to change. Also, the null state can only transition to itself,
 * and changing that is erroneous. See the relevane function and <code>NullStateMutationException</code>
 * for details. On the other hand, states are allowed to transition to the null state,
 * although manually setting that is not recommended. In fact, that is the default behavious
 * for all newly-incorporated states and their transitions.
 *
 * @param <E> The return type for states of this FSM.
 */
public class FSM<E> {

    // these exact types may not survive

    /**
     * Set of states of this FSM, mapped to by their names. It defaults to having only the null state
     */
    private final Map<String, State<E>> states;

    /**
     * The alphabet of this FSM.
     */
    private final char[] alphabet;

    /**
     * The initial state for this FSM when it runs. The user currently must set it manually.
     */
    private State<E> initState = null;

    /**
     * The null state for this FSM. It transitions only to itself, something that cannot change.
     * It is currently the only state allowed to have <code>null</code> as a name.
     */
    private final State<E> nullState;


    // html: "&rarr" = "->" but better
    /**
     * The transition map of this FSM. Its two layers denote the (State, char) &rarr State
     * transition structure. The null state is currently not an input in this map, but rather
     * it defaults to mapping to itself regardless of character.
     */
    private final Map<State<E>, Map<Character, State<E>>> transitionTable;


    ////////////////////////
    // private helpers

    /**
     * Sets the alphabet for the map. Currently, changing the alphabet is unsafe,
     * so it is a final field with its setter only used in constructors.
     */
    private char[] setAlphabet(char[] alphabet){
        Arrays.sort(alphabet);
        //// alt version for non-final alphabet:
        // this.alphabet = alphabet;
        return alphabet;
    }

    /**
     * Creates a new state then attempts to incorporates it. The name cannot be <code>null</code>.
     * @throws NullPointerException If name is <code>null</code>.
     */
    private State<E> createInternalState(@NotNull String name, boolean finality, E value)
    throws NullPointerException{
        State<E> state = new State<>(name, finality, value);

        // idea: if incorporating a state thinks we're mutating null state
        //          then the string is null, so throw that exception instead
        // alternative: throw null exception on name immediately
        //      This is not done in order to not pollute signature of the function
        //          with never-thrown NullStateMutationException
        //      Also, it's possible that, in the future, something else
        //          may cause NullStateMutationException.
        //          the current design allows for easier incorporation of that.
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

    /**
     * Constructs an FSM with the given alphabet, no initial state, and only the null state.
     * @param alphabet The alphabet of this FSM.
     */
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

    /**
     * Returns a list of all internal states of this FSM, including the null state.
     * @return A list of all internal states of this FSM.
     */
    public List<State<E>> getStates() {
        return new ArrayList<>(states.values());
    }

    /**
     * Returns the alphabet of this FSM.
     * @return The alphabet of this FSM.
     */
    public char[] getAlphabet() {
        return alphabet;
    }

    /**
     * Returns the initial state of this FSM, or null if uninitialized.
     * @return The initial state of this FSM.
     */
    public State<E> getInitState(){
        return initState;
    }

    /**
     * Returns the null, or dead, state of this FSM. It is currently the only state allowed
     * to have <code>null</code> as a name.It transitions only to itself, something that cannot change.
     * Its internal data, however, can be changed as desired by the use case.
     *
     * @return The null state of this FSM.
     */
    public State<E> getNullState(){
        return nullState;
    }

    /**
     * Returns the state with the provided name in this FSM, or <code>null</code> if none exist.
     *
     * @param name The name of the state being searched for.
     * @return The state with the given name, or <code>null</code> if there is none.
     */
    public State<E> getStateByName(String name){
        return states.get(name);
    }

    /**
     * Returns the state that results from the provided transition, or this machine's
     * null state by default.
     *
     * @param state The state from which the transition takes place.
     * @param c The character instigating the transition.
     *
     * @return The state with the given name, or <code>null</code> if there is none.
     *
     * @throws InvalidStateException If the state is not an internal state of this FSM
     * @throws InvalidStateException If the character is not in the alphabet of this FSM
     */
    public State<E> getTransition(@NotNull State<E> state, char c)
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

    /**
     * Returns true if the provided character is in the alphabet of this FSM.
     * @param c The character being queried
     * @return <code>true</code> if the provided character is in the alphabet.
     */
    public boolean isInAlphabet(char c){
        // iff search returns valid (non-negative) index, char is in alphabet
        return Arrays.binarySearch(alphabet, c) >= 0;
    }

    /**
     * Returns true if the provided state is a part of this FSM.
     * @param state The state being queried
     * @return <code>true</code> if the provided state is a part of this FSM.
     */
    public boolean containsState(State<E> state){
        return states.containsValue(state);
    }

    /**
     * Returns true if this FSM is ready to be executed. This includes whether or not
     * an initial state is set.
     *
     * @return <code>true</code> if this FSM is ready to be executed.
     */
    public boolean isReady(){
        return initState != null;
    }

    /**
     * Returns true if the provided string is made up of only characters in the alphabet of this FSM.
     * @param string The string being queried
     * @return <code>true</code> if the provided string is made up of characters only in the alphabet.
     */
    public boolean isStringOfAlphabet(String string){
        if(string == null){
            return false;
        }
        for(int i = 0; i < string.length(); i++){
            if(!isInAlphabet(string.charAt(i))){
                return false;
            }
        }
        return true;
    }


    //////////////
    // setters

    /**
     * Sets the initial state of this FSM to the provided one. The provided state must be in this FSM.
     * @param state The state being made the initial state.
     * @throws InvalidStateException If the provided state is not in this FSM.
     */
    public void setInitState(@NotNull State<E> state) throws InvalidStateException{
        if(!containsState(state)) {
            throw new InvalidStateException("Attempting to set state not in FSM to initial state.");
        }
        initState = state;
    }

    /**
     * Sets the transition from the provided old state and character to the provided new state.
     * The provided states and character must be in this FSM.
     * @param current The state being made the beginning of the transition.
     * @throws InvalidStateException If the provided state is not in this FSM.
     */
    public void setTransition(@NotNull State<E> current, char c, @NotNull State<E> next)
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

    /**
     * Incorporates the provided state into the FSM. Note that this overrides the old state
     * with the same name, and resets all maps for this state to default to the null state.
     * The provided state cannot be <code>null</code>, nor can its name.
     *
     * @param state The state being incorporated.
     * @throws NullPointerException If the provided state is <code>null</code>.
     * @throws NullStateMutationException If the provided state is attempting to replace the null state.
     *      This is possible if its name is <code>null</code>.
     */
    public void incorporateNewState(@NotNull State<E> state)
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

        // add to states list and transitions list. automatically replaces old state
        states.put(state.getName(), state);
        transitionTable.put(state, new HashMap<>());
    }

    /**
     * Creates a new non-final state with a null value and the provided name.
     * @param name The identifying name of the state.
     * @throws NullPointerException If the provided name is <code>null</code>.
     */
    public State<E> setNewState(@NotNull String name) throws NullPointerException{
        // just give every parameter to get it over with
        return createInternalState(name, false, null);
    }

    /**
     * Creates a new non-final state with the provided name and value.
     * @param name The identifying name of the state.
     * @throws NullPointerException If the provided name is <code>null</code>.
     */
    public State<E> setNewState(@NotNull String name, E value) throws NullPointerException{
        return createInternalState(name, false, value);
    }

    /**
     * Creates a new final state with the provided name and value.
     * @param name The identifying name of the state.
     * @throws NullPointerException If the provided name is <code>null</code>.
     */
    public State<E> setNewFinalState(@NotNull String name, E value) throws NullPointerException{
        return createInternalState(name, true, value);
    }


    ////////////////////////
    // functionality

    /**
     * Runs the finite state machine on the provided data using the current initial state
     * and transition table. Returns the final state of the FSM after its execution.
     * The data must be a string made up of this machine's alphabet.
     *
     * @param data The string for the machine to process.
     * @return The final state from execution on the provided data.
     * @throws IncompleteMachineException If the machine is not ready. For example,
     *      if there is no initial state set.
     * @throws InvalidCharacterException If the string is not made up of this machine's alphabet.
     */
    public State<E> runMachine(@NotNull String data)
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
