package io.github.czelabueno.jai.workflow.graph;

import io.github.czelabueno.jai.workflow.graph.graphviz.Orientation;
import io.github.czelabueno.jai.workflow.graph.graphviz.StyleGraph;
import io.github.czelabueno.jai.workflow.transition.Transition;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Interface for generating graph images from workflow transitions computed.
 */
public interface GraphImageGenerator {

    /**
     * Generates a graph image from the given list of transitions and saves it to the default output path: /workflow-image.svg
     *
     * @param transitions the list of transitions to generate the graph image from
     * @throws IOException if an I/O error occurs during image generation
     */
    default void generateImage(List<Transition> transitions) throws IOException {
        generateImage(transitions, "workflow-image.svg");
    }

    /**
     * Generates a graph image with the given format from the given list of transitions and saves it to the default output path.
     *
     * @param transitions the list of transitions to generate the graph image from
     * @param format the format of the generated image
     * @throws IOException if an I/O error occurs during image generation
     */
    default void generateImage(List<Transition> transitions, Format format) throws IOException {
        generateImage(transitions, "workflow-image." + format.name().toLowerCase(), format);
    }

    /**
     * Generates a graph image from the given list of transitions and saves it to the given output path.
     *
     * @param transitions the list of transitions to generate the graph image from
     * @param format the format of the generated image
     * @param styles the styles to apply to the generated image
     * @throws IOException if an I/O error occurs during image generation
     */
    void generateImage(List<Transition> transitions, String outputPath, Format format, StyleAttribute... styles) throws IOException;

    /**
     * Generates a graph image with the given format from the given list of transitions and saves it to the specified output path.
     *
     * @param transitions the list of transitions to generate the graph image from
     * @param outputPath  the path to save the generated graph image
     * @param format the format of the generated image
     * @throws IOException if an I/O error occurs during image generation
     * @throws IllegalArgumentException if the output path is null or empty
     */
    default void generateImage(List<Transition> transitions, String outputPath, Format format) throws IOException {
        generateImage(transitions, outputPath, format, StyleGraph.DEFAULT, Orientation.VERTICAL);
    }

    /**
     * Generates a graph image with {@link Format}.SVG from the given list of transitions and saves it to the specified output path.
     *
     * @param transitions the list of transitions to generate the graph image from
     * @param outputPath  the path to save the generated graph image
     * @throws IOException if an I/O error occurs during image generation
     * @throws IllegalArgumentException if the output path is null or empty
     */
    default void generateImage(List<Transition> transitions, String outputPath) throws IOException {
        generateImage(transitions, outputPath, Format.SVG);
    }

    /**
     * Generates a BufferedImage representation of the workflow graph from the given list of transitions.
     *
     * @param transitions the list of transitions to generate the graph image from
     * @param format the format of the generated image (e.g., SVG, PNG)
     * @param styles optional styles to apply to the graph (e.g., sketchy, orientation)
     * @return the generated BufferedImage representation of the workflow graph
     * @throws RuntimeException if an error occurs during image generation
     */
    BufferedImage generateBufferedImage(List<Transition> transitions, Format format, StyleAttribute... styles) throws RuntimeException;

    /**
     * Generates a BufferedImage representation of the workflow graph from the given list of transitions.
     * @param transitions the list of transitions to generate the graph image from
     * @param styles optional styles to apply to the graph (e.g., sketchy, orientation)
     * @return the generated BufferedImage representation of the workflow graph
     * @throws RuntimeException if an error occurs during image generation
     */
    default BufferedImage generateBufferedImage(List<Transition> transitions, StyleAttribute... styles) throws RuntimeException {
        return generateBufferedImage(transitions, Format.SVG, styles);
    }

    /**
     * Generates a BufferedImage representation of the workflow graph from the given list of transitions.
     * @param transitions the list of transitions to generate the graph image from
     * @param format the format of the generated image (e.g., SVG, PNG)
     * @return the generated BufferedImage representation of the workflow graph
     * @throws RuntimeException if an error occurs during image generation
     */
    default BufferedImage generateBufferedImage(List<Transition> transitions, Format format) throws RuntimeException {
        return generateBufferedImage(transitions, format, StyleGraph.DEFAULT, Orientation.VERTICAL);
    }

    /**
     * Generates a BufferedImage representation of the workflow graph from the given list of transitions.
     * @param transitions the list of transitions to generate the graph image from
     * @return the generated BufferedImage representation of the workflow graph
     * @throws RuntimeException if an error occurs during image generation
     */
    default BufferedImage generateBufferedImage(List<Transition> transitions) throws RuntimeException {
        return generateBufferedImage(transitions, Format.SVG);
    }
}
