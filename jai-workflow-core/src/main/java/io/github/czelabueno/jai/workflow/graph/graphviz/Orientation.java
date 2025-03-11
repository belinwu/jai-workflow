package io.github.czelabueno.jai.workflow.graph.graphviz;

import io.github.czelabueno.jai.workflow.graph.StyleAttribute;

/**
 * Enum representing the orientation of the graph in Graphviz.
 * It can be either vertical (top to bottom) or horizontal (left to right).
 */
public enum Orientation implements StyleAttribute {
    /**
     * Vertical orientation (top to bottom).
     */
    VERTICAL("TB"),
    /**
     * Horizontal orientation (left to right).
     */
    HORIZONTAL("LR");

    private final String code;

    Orientation(String code) {
        this.code = code;
    }

    /**
     * Gets the code representing the orientation.
     *
     * @return the code representing the orientation
     */
    public String getCode() {
        return code;
    }
}
