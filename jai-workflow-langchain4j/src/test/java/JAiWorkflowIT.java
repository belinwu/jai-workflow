import io.github.czelabueno.jai.workflow.langchain4j.JAiWorkflow;
import io.github.czelabueno.jai.workflow.langchain4j.internal.DefaultJAiWorkflow;
import io.github.czelabueno.jai.workflow.langchain4j.node.StreamingNode;
import io.github.czelabueno.jai.workflow.node.Node;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.mistralai.MistralAiStreamingChatModel;
import io.github.czelabueno.jai.workflow.transition.Transition;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import io.github.czelabueno.jai.workflow.langchain4j.workflow.NodeFunctionsMock;
import io.github.czelabueno.jai.workflow.langchain4j.workflow.StatefulBeanMock;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static io.github.czelabueno.jai.workflow.WorkflowStateName.END;
import static io.github.czelabueno.jai.workflow.langchain4j.workflow.NodeFunctionsMock.generate;
import static io.github.czelabueno.jai.workflow.langchain4j.workflow.NodeFunctionsMock.retrieve;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

// JAiWorkflowIT is an integration test class that demonstrates how to use JAiWorkflow with LangChain4j to build agentic systems and orchestrated AI workflows.
// The workflow tested in this class is a simple example that retrieves documents, grades them, and generates a summary of the documents using the Mistral AI API.
//
// Workflow definition:
// START -> Retrieve Node -> Grade Documents Node -> Generate Node -> END
//
// The setUp method initializes the JAiWorkflow and JAiWorkflowStreaming objects with the MistralAiChatModel and MistralAiStreamingChatModel classes, respectively.
// These models are used to generate AI responses in both synchronous and streaming modes.
//
// The should_answer_question method tests the synchronous answer method of the JAiWorkflow class by providing a question and checking if the answer contains the expected text.
// The should_answer_stream_question method tests the streaming answerStream method of the JAiWorkflow class by providing a question and checking if the answer contains the expected tokens.
//
// This integration test class showcases how JAiWorkflow and LangChain4j can be combined to create complex AI-driven workflows that can process and generate information in a structured manner.
class JAiWorkflowIT {

    String[] documents = new String[]{
            "https://lilianweng.github.io/posts/2023-06-23-agent/",
            "https://lilianweng.github.io/posts/2023-03-15-prompt-engineering/",
            "https://lilianweng.github.io/posts/2023-10-25-adv-attack-llm/"
    };

    ChatLanguageModel model = MistralAiChatModel.builder()
            .apiKey(System.getenv("MISTRAL_AI_API_KEY"))
            .modelName(MistralAiChatModelName.MISTRAL_LARGE_LATEST)
            .temperature(0.0)
            .build();

    StreamingChatLanguageModel streamingModel = MistralAiStreamingChatModel.builder()
            .apiKey(System.getenv("MISTRAL_AI_API_KEY"))
            .modelName(MistralAiChatModelName.MISTRAL_LARGE_LATEST)
            .temperature(0.0)
            .build();

    JAiWorkflow jAiWorkflow;
    JAiWorkflow jAiWorkflowStreaming;

    @BeforeEach()
    void setUp() {
        // Define a stateful bean to store the state of the workflow
        StatefulBeanMock statefulBean = new StatefulBeanMock();

        // Define nodes with your custom functions
        Node<StatefulBeanMock, StatefulBeanMock> retrieveNode = Node.from("Retrieve Node", obj -> retrieve(obj, documents));
        Node<StatefulBeanMock, StatefulBeanMock> gradeDocumentsNode = Node.from("Grade Documents Node", NodeFunctionsMock::gradeDocuments);
        Node<StatefulBeanMock, StatefulBeanMock> generateNode = Node.from("Generate Node", obj -> generate(obj, model));
        StreamingNode<StatefulBeanMock> generateStreamingNode = StreamingNode.from(
                "Generate Node",
                NodeFunctionsMock::generateUserMessageFromStatefulBean,
                streamingModel);

        // Build workflows of the synchronous and streaming ways
        jAiWorkflow = new DefaultJAiWorkflow<StatefulBeanMock>(
                statefulBean,
                Arrays.asList(
                        Transition.from(retrieveNode, gradeDocumentsNode),
                        Transition.from(gradeDocumentsNode, generateNode),
                        Transition.from(generateNode, END)
                ),
                retrieveNode,
                false
        );
        // Define your workflow transitions using edges and the entry point of the workflow
        jAiWorkflowStreaming = new DefaultJAiWorkflow<StatefulBeanMock>(
                statefulBean,
                Arrays.asList(
                        Transition.from(retrieveNode, gradeDocumentsNode),
                        Transition.from(gradeDocumentsNode, generateStreamingNode),
                        Transition.from(generateStreamingNode, END)
                ),
                retrieveNode,
                true
        );
    }

    @Test
    void should_answer_question() {
        // given
        String question = "Summarizes the importance of building agents with LLMs";

        // when
        String answer = jAiWorkflow.answer(question);
        BufferedImage image = jAiWorkflow.getWorkflowImage();

        // then
        assertThat(answer).containsIgnoringWhitespaces("brain of an autonomous agent system");
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isGreaterThan(0);
        assertThat(image.getHeight()).isGreaterThan(0);
    }

    @Test
    void should_answer_stream_with_non_streamingNode_throw_IllegalStateException() {
        // given
        String question = "Summarizes the importance of building agents with LLMs";

        // when
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> jAiWorkflow.answerStream(question))
                .withMessage("The last node of the workflow must be a StreamingNode to run in stream mode");
    }

    @SneakyThrows // I/O exceptions are caught
    @Test
    void should_answer_stream_question() {
        // given
        String question = "Summarizes the importance of building agents with LLMs";
        List<String> expectedTokens = Arrays.asList("building", "agent", "system","general","problem", "solver");

        // when
        Flux<String> tokens = jAiWorkflowStreaming.answerStream(question);
        String strPath = "images/corrective-rag-workflow.svg";
        Path path = Paths.get(strPath);
        jAiWorkflowStreaming.getWorkflowImage(path);

        // then
        StepVerifier.create(tokens)
                .expectNextMatches(token -> expectedTokens.stream().anyMatch(token.toLowerCase()::contains))
                .expectNextCount(1)
                .thenCancel()
                .verify();
        String answer = tokens.collectList().block().stream().collect(joining());
        assertThat(expectedTokens)
                .anySatisfy(token -> assertThat(answer).containsIgnoringWhitespaces(token));
        assertThat(Files.exists(path)).isTrue();
    }
}
