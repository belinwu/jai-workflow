package io.github.czelabueno.jai.workflow.graph.graphviz;

import io.github.czelabueno.jai.workflow.graph.StyleAttribute;

/**
 * Enum representing the style of the graph in Graphviz.
 * It can be either default or sketchy.
 */
public enum StyleGraph implements StyleAttribute {
    /**
     * Default style.
     */
    DEFAULT("default"),
    /**
     * Sketchy style.
     */
    SKETCHY("sketchy");

    private final String code;

    StyleGraph(String code) {
        this.code = code;
    }

    /**
     * Gets the code representing the style.
     *
     * @return the code representing the style
     */
    @Override
    public String getCode() {
        return code;
    }
}
