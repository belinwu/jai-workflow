package io.github.czelabueno.jai.workflow.langchain4j.internal;

import io.github.czelabueno.jai.workflow.DefaultStateWorkflow;
import io.github.czelabueno.jai.workflow.StateWorkflow;
import io.github.czelabueno.jai.workflow.langchain4j.AbstractStatefulBean;
import io.github.czelabueno.jai.workflow.langchain4j.JAiWorkflow;
import io.github.czelabueno.jai.workflow.langchain4j.node.StreamingNode;
import io.github.czelabueno.jai.workflow.node.Node;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import io.github.czelabueno.jai.workflow.transition.Transition;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

/**
 * DefaultJAiWorkflow is a default implementation of the JAiWorkflow interface.
 * It defines the workflow for processing user messages and generating AI responses.
 *
 * @param <T> the type of the stateful bean, which extends AbstractStatefulBean
 */
public class DefaultJAiWorkflow<T extends AbstractStatefulBean> extends DefaultStateWorkflow implements JAiWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefaultJAiWorkflow.class);

    private final T statefulBean;
    private final Boolean runStreaming;

    /**
     * Constructs a new DefaultJAiWorkflow with the specified parameters.
     *
     * @param statefulBean the stateful bean holding the state of the workflow
     * @param transitions the list of transition to be performed in the workflow
     * @param runStreaming flag indicating whether to run the workflow in stream mode
     */
    public DefaultJAiWorkflow(T statefulBean,
                              List<Transition> transitions,
                              Node<T, ?> startNode,
                              Boolean runStreaming) {
        super(DefaultStateWorkflow.<T>builder()
                .statefulBean(statefulBean)
                .addEdges(transitions.toArray(new Transition[0])));
        this.statefulBean = statefulBean;
        this.startNode(startNode);
        this.runStreaming = getOrDefault(runStreaming, false);
        // check if workflowOutputPath is valid
        // this.generateWorkflowImage = workflowImageOutputPath != null || getOrDefault(generateWorkflowImage, false);
        // this.workflowImageOutputPath = workflowImageOutputPath;
    }

    @Override
    public AiMessage answer(UserMessage question) {
        // Set User question to stateful bean
        this.statefulBean.setQuestion(question.singleText());
        // Run workflow in synchronous mode
        this.run();
        return AiMessage.from(this.statefulBean.getGeneration());
    }

    @Override
    public Flux<String> answerStream(UserMessage question) {
        if (!this.runStreaming || !isLastNodeAStreamingNode()) {
            throw new IllegalStateException("The last node of the workflow must be a StreamingNode to run in stream mode");
        }
        // Set User question to stateful bean
        this.statefulBean.setQuestion(question.singleText());
        // Run workflow in stream mode
        if (this.runStreaming) {
            this.runStream(node -> {
                if (node instanceof StreamingNode streamingNode) {
                    log.debug("StreamingNode processed: " + streamingNode.getName());
                }
                log.debug("Node processed: " + ((Node) node).getName());
            });
        }
        return this.statefulBean.getGenerationStream();
    }

    @Override
    public JAiWorkflow getWorkflowImage(Path workflowImageOutputPath) throws IOException {
        if (this.wasRun()) {
            this.generateComputedWorkflowImage(workflowImageOutputPath.toAbsolutePath().toString());
        } else {
            this.generateWorkflowImage(workflowImageOutputPath.toAbsolutePath().toString());
        }
        return this;
    }

    @Override
    public BufferedImage getWorkflowImage() {
        if (this.wasRun()) {
            return this.generateComputedWorkflowBufferedImage();
        } else {
            return this.generateWorkflowBufferedImage();
        }
    }

    private Boolean isLastNodeAStreamingNode() {
        return this.getLastNode() instanceof StreamingNode;
    }
}
