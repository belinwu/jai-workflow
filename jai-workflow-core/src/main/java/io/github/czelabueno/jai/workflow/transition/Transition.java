package io.github.czelabueno.jai.workflow.transition;

import io.github.czelabueno.jai.workflow.node.Node;
import io.github.czelabueno.jai.workflow.WorkflowStateName;
import lombok.NonNull;

/**
 * Represents a transition between two states in a workflow.
 * The states can be instances of {@link Node} or {@link WorkflowStateName}.
 *
 * @param from the starting state of the transition, could be an instance of {@link Node}, {@link io.github.czelabueno.jai.workflow.node.Conditional} or {@link WorkflowStateName}
 * @param to   the ending state of the transition, could be an instance of {@link Node}, {@link io.github.czelabueno.jai.workflow.node.Conditional} or {@link WorkflowStateName}
 */
public record Transition (TransitionState from, TransitionState to) {

    /**
     * Constructs a Transition with the specified from and to states.
     *
     * @param from the starting state of the transition, must be an instance of {@link Node} or {@link WorkflowStateName}
     * @param to   the ending state of the transition, must be an instance of {@link Node} or {@link WorkflowStateName}
     * @throws IllegalArgumentException if the from state is {@link WorkflowStateName#END},
     *                                  if the to state is {@link WorkflowStateName#START},
     *                                  or if the transition is from {@link WorkflowStateName#START} to {@link WorkflowStateName#END}
     * @throws NullPointerException if the from or to state is null
     */
    public Transition(@NonNull TransitionState from, @NonNull TransitionState to) {
        if (from == WorkflowStateName.END) {
            throw new IllegalArgumentException("Cannot transition from an END state");
        }
        if (to == WorkflowStateName.START) {
            throw new IllegalArgumentException("Cannot transition to a START state");
        }
        if (from == WorkflowStateName.START && to == WorkflowStateName.END) {
            throw new IllegalArgumentException("Cannot transition from START to END state");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Creates a new Transition with the specified from and to states.
     *
     * @param from the starting state of the transition, must be an instance of {@link Node} or {@link WorkflowStateName}
     * @param to   the ending state of the transition, must be an instance of {@link Node} or {@link WorkflowStateName}
     * @return a new Transition instance
     */
    public static Transition from(TransitionState from, TransitionState to) {
        return new Transition(from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

        if (!from.equals(that.from)) return false;
        return to.equals(that.to);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    /**
     * Returns a string representation of the transition.
     *
     * @return a string representation of the transition in the format "from -> to"
     */
    @Override
    public String toString() {
        String transition = "";
        if (from != null) transition = from.graphName() + " -> ";
        if (to != null) transition = transition + to.graphName();
        return transition;
    }
}
