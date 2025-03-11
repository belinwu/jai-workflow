package io.github.czelabueno.jai.workflow.graph.graphviz;

import io.github.czelabueno.jai.workflow.graph.Format;
import io.github.czelabueno.jai.workflow.node.Node;
import io.github.czelabueno.jai.workflow.transition.ComputedTransition;
import io.github.czelabueno.jai.workflow.transition.Transition;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.czelabueno.jai.workflow.WorkflowStateName.END;
import static io.github.czelabueno.jai.workflow.WorkflowStateName.START;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GraphvizImageGeneratorTest {

    GraphvizImageGenerator.GraphvizImageGeneratorBuilder builder;
    String dotFormat = "digraph { a -> b; }";

    List<ComputedTransition> computedTransitions;

    List<Transition> transitions;

    @BeforeEach
    void setUp() {
        builder = GraphvizImageGenerator.builder();

        // node mocked to simulate that the transition was computed
        Node a = mock(Node.class);
        Node b = mock(Node.class);
        Node c = mock(Node.class);
        when(a.output()).thenReturn("mockedPayloadOfNodeA");
        when(b.output()).thenReturn("mockedPayloadOfNodeB");
        when(c.output()).thenReturn("mockedPayloadOfNodeC");
        when(a.graphName()).thenReturn("a");
        when(b.graphName()).thenReturn("b");
        when(c.graphName()).thenReturn("c");

        transitions = Arrays.asList(
                Transition.from(START, a),
                Transition.from(a, b),
                Transition.from(b, c),
                Transition.from(c, END)
        );

        computedTransitions = IntStream.range(0, transitions.size())
                .mapToObj(i -> ComputedTransition.from(i + 1, transitions.get(i)))
                .toList();
    }

    @Test
    void test_builder_and_doFormat() {
        // given
        assertThat(builder).isNotNull();
        // when
        GraphvizImageGenerator generator = builder.dotFormat(dotFormat).build();
        // then
        assertThat(generator).isNotNull();
    }

    @Test
    void test_builder_and_computed_transitions() {
        // given
        assertThat(builder).isNotNull();
        // when
        GraphvizImageGenerator generator = builder.computedTransitions(computedTransitions).build();
        // then
        assertThat(generator).isNotNull();
    }

    @Test
    void test_throw_illegalArgumentException_when_generate_image_with_invalid_transitions() {
        // given
        List<Transition> transitions = Collections.EMPTY_LIST;
        // when
        GraphvizImageGenerator generator = builder.build(); // built without dotFormat
        // then
        assertThat(generator).isNotNull();
        assertThatThrownBy(() -> generator.generateImage(transitions)) // empty transitions
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transitions list can not be null or empty when dotFormat is null. Cannot generate image.");
    }

    @Test
    void test_throw_illegalArgumentException_when_generate_image_with_invalid_outputPath() {
        // given
        List<Transition> transitions = Arrays.asList(
                Transition.from(
                    Node.from("a", s -> s + "1"),
                    Node.from("b", s -> s + "2")
                )
        );
        // when
        GraphvizImageGenerator generator = builder.build();
        // then
        assertThat(generator).isNotNull();
        assertThatThrownBy(() -> generator.generateImage(transitions, "")) // empty output path
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Output path can not be null or empty. Cannot generate image.");
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_dotFormat_provided() {
        // given
        GraphvizImageGenerator generator = builder.dotFormat("digraph { a -> b -> c -> d; }").build();
        // when
        assertThat(generator).isNotNull();
        generator.generateImage(null); // transitions are not required when dotFormat is provided
        // then
        Path path = Paths.get("workflow-image.svg");
        assertThat(Files.exists(path)).isTrue();
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_dotFormat_and_outputPath() {
        // given
        GraphvizImageGenerator generator = builder.dotFormat(dotFormat).build();
        // when
        assertThat(generator).isNotNull();
        String customOutputPath = "image/my-workflow-from-test.svg";
        generator.generateImage(null, customOutputPath);
        // then
        Path path = Paths.get(customOutputPath);
        assertThat(Files.exists(path)).isTrue();
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_dotFormat_and_format_PNG_and_sketchy_style() {
        // given
        GraphvizImageGenerator generator = builder.dotFormat("digraph { a -> b -> c -> d; }").build();
        // when
        assertThat(generator).isNotNull();
        String strPath = "image/my-workflow-dot-styles-from-test.png";
        generator.generateImage(null, strPath, Format.PNG, StyleGraph.SKETCHY); // transitions are not required when dotFormat is provided
        // then
        Path path = Paths.get(strPath);
        assertThat(Files.exists(path)).isTrue();
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_transitions() {
        // given
        List<Transition> transitions = Arrays.asList(
                Transition.from(
                        Node.from("a", s -> s + "1"),
                        Node.from("b", s -> s + "2")
                )
        );
        GraphvizImageGenerator generator = builder.build();
        // when
        assertThat(generator).isNotNull();
        generator.generateImage(transitions);
        // then
        Path path = Paths.get("workflow-image.svg"); // default output path
        assertThat(Files.exists(path)).isTrue();
        String content = String.join("\n", Files.readAllLines(path));
        assertThat(content.trim()).startsWith("<svg"); // default image format is SVG
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_transitions_and_outputPath() {
        // given
        List<Transition> transitions = Arrays.asList(
                Transition.from(
                        Node.from("a", s -> s + "1"),
                        Node.from("b", s -> s + "2")
                )
        );
        GraphvizImageGenerator generator = builder.build();
        // when
        assertThat(generator).isNotNull();
        String strPath = "image/my-workflow-from-test.svg";
        generator.generateImage(transitions, strPath);
        // then
        Path path = Paths.get(strPath);
        assertThat(Files.exists(path)).isTrue(); // custom output path
        String content = String.join("\n", Files.readAllLines(path));
        assertThat(content.trim()).startsWith("<svg"); // default image format is SVG
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_transitions_and_outputPath_and_format_PNG() {
        // given
        List<Transition> transitions = Arrays.asList(
                Transition.from(
                        Node.from("a", s -> s + "1"),
                        Node.from("b", s -> s + "2")
                )
        );
        GraphvizImageGenerator generator = builder.build();
        // when
        assertThat(generator).isNotNull();
        String strPath = "image/my-workflow-from-test.png";
        generator.generateImage(transitions, strPath, Format.PNG);
        // then
        Path path = Paths.get(strPath);
        assertThat(Files.exists(path)).isTrue(); // custom output path
        assertThat(Files.probeContentType(path)).startsWith("image/png"); // file content-type
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_transitions_output_and_styles() {
        // given
        List<Transition> transitions = Arrays.asList(
                Transition.from(
                        Node.from("a", s -> s + "1"),
                        Node.from("b", s -> s + "2")
                )
        );
        GraphvizImageGenerator generator = builder.build();
        // when
        assertThat(generator).isNotNull();
        String strPath = "image/my-workflow-from-test-sketchy.png";
        generator.generateImage(transitions, strPath, Format.PNG, StyleGraph.SKETCHY, Orientation.HORIZONTAL);
        // then
        Path path = Paths.get(strPath);
        assertThat(Files.exists(path)).isTrue(); // custom output path
    }

    @SneakyThrows
    @Test
    void test_buffered_image_with_all_params() {
        // given
        List<Transition> transitions = Arrays.asList(
                Transition.from(
                        Node.from("a", s -> s + "1"),
                        Node.from("b", s -> s + "2")
                )
        );
        GraphvizImageGenerator generator = builder.build();
        // when
        assertThat(generator).isNotNull();
        BufferedImage image = generator.generateBufferedImage(
                transitions,
                Format.SVG,
                StyleGraph.SKETCHY, // style attribute
                Orientation.HORIZONTAL); // style attribute
        // then
        assertThat(image).isNotNull();
        assertThat(image.getType()).isEqualTo(BufferedImage.TYPE_INT_ARGB);
        assertThat(image.getWidth()).isGreaterThan(0);
        assertThat(image.getHeight()).isGreaterThan(0);
    }

    @SneakyThrows
    @Test
    void test_buffered_image_with_dotFormat_and_styles() {
        // given
        GraphvizImageGenerator generator = builder.dotFormat("digraph { a -> b -> c -> d; }").build();
        // when
        assertThat(generator).isNotNull();
        BufferedImage image = generator.generateBufferedImage(null,
                Format.SVG,
                StyleGraph.SKETCHY, // style attribute
                Orientation.HORIZONTAL); // style attribute
        // then
        assertThat(image).isNotNull();
        assertThat(image.getType()).isEqualTo(BufferedImage.TYPE_INT_ARGB);
        assertThat(image.getWidth()).isGreaterThan(0);
        assertThat(image.getHeight()).isGreaterThan(0);
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_computedTransitions_and_styles() {
        // given
        GraphvizImageGenerator generator = builder.computedTransitions(computedTransitions).build();
        // when
        assertThat(generator).isNotNull();
        String strPath = "image/my-workflow-from-test-computed-transitions.png";
        generator.generateImage(transitions,
                strPath,
                Format.PNG,
                StyleGraph.SKETCHY,
                Orientation.HORIZONTAL);
        // then
        Path path = Paths.get(strPath);
        assertThat(Files.exists(path)).isTrue(); // custom output path
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_computedTransitions_and_styles_and_format_SVG() {
        // given
        GraphvizImageGenerator generator = builder.computedTransitions(computedTransitions).build();
        // when
        assertThat(generator).isNotNull();
        String strPath = "image/my-workflow-from-test-computed-transitions.svg";
        generator.generateImage(transitions,
                strPath,
                Format.SVG,
                Orientation.VERTICAL);
        // then
        Path path = Paths.get(strPath);
        assertThat(Files.exists(path)).isTrue(); // custom output path
    }

    @SneakyThrows
    @Test
    void test_generate_image_with_computedTransitions_and_styles_and_dotFormat() {
        // given
        GraphvizImageGenerator generator = builder
                .computedTransitions(computedTransitions) // will be ignored when dotFormat is provided
                .dotFormat("digraph { _start_ -> a -> b -> c -> d -> _end_; }")
                .build();
        // when
        assertThat(generator).isNotNull();
        String strPath = "image/my-workflow-from-test-dot-computed-transitions.svg";
        generator.generateImage(null,
                strPath,
                Format.SVG,
                StyleGraph.SKETCHY,
                Orientation.HORIZONTAL);
        // then
        Path path = Paths.get(strPath);
        assertThat(Files.exists(path)).isTrue(); // custom output path
    }

    @SneakyThrows
    @Test
    void test_buffered_image_with_computedTransitions_and_styles() {
        // given
        GraphvizImageGenerator generator = builder.computedTransitions(computedTransitions).build();
        // when
        assertThat(generator).isNotNull();
        BufferedImage image = generator.generateBufferedImage(transitions,
                Format.SVG,
                StyleGraph.SKETCHY,
                Orientation.HORIZONTAL);
        // then
        assertThat(image).isNotNull();
        assertThat(image.getType()).isEqualTo(BufferedImage.TYPE_INT_ARGB);
        assertThat(image.getWidth()).isGreaterThan(0);
        assertThat(image.getHeight()).isGreaterThan(0);
    }

    @SneakyThrows
    @Test
    void test_buffered_image_with_computedTransitions_and_styles_and_format_PNG() {
        // given
        GraphvizImageGenerator generator = builder.computedTransitions(computedTransitions).build();
        // when
        assertThat(generator).isNotNull();
        BufferedImage image = generator.generateBufferedImage(transitions,
                Format.PNG,
                Orientation.VERTICAL);
        // then
        assertThat(image).isNotNull();
        assertThat(image.getType()).isEqualTo(BufferedImage.TYPE_INT_ARGB);
        assertThat(image.getWidth()).isGreaterThan(0);
        assertThat(image.getHeight()).isGreaterThan(0);
    }
}
