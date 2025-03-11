package io.github.czelabueno.jai.workflow.langchain4j;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import io.github.czelabueno.jai.workflow.StateWorkflow;
import reactor.core.publisher.Flux;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

/**
 * The {@link JAiWorkflow} interface defines the entry-point contract for a workflow that processes user messages
 * and generates AI responses. It provides basic methods for synchronous and asynchronous (streaming) responses.
 */
public interface JAiWorkflow extends StateWorkflow {

    /**
     * Generates an AI response to the given question.
     * This method ensures that the question is not null before processing.
     *
     * @param question the question to be answered
     * @return the AI response as a string
     * @throws IllegalArgumentException if the question is null
     */
    default String answer(String question){
        ensureNotNull(question, "question");
        return answer(new UserMessage(question)).text();
    }

    /**
     * Generates an AI response to the given user message.
     *
     * @param question the UserMessage containing the question
     * @return the AI response as an AiMessage
     */
    AiMessage answer(UserMessage question);

    /**
     * Generates a streaming AI response to the given question.
     * This method ensures that the question is not null before processing.
     *
     * @param question the question to be answered
     * @return a Flux stream of the AI response tokens
     * @throws IllegalArgumentException if the question is null
     */
    default Flux<String> answerStream(String question){
        ensureNotNull(question, "question");
        return answerStream(new UserMessage(question));
    }

    /**
     * Generates a streaming AI response to the given user message.
     *
     * @param question the UserMessage containing the question
     * @return a Flux stream of the AI response tokens
     */
    Flux<String> answerStream(UserMessage question);

    /**
     * Generates a workflow image of the current workflow state.
     * This method ensures that the workflow image output path is not null before processing.
     *
     * @param workflowImageOutputPath the path to save the workflow image
     * @return the workflow image as a {@link JAiWorkflow}
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if the workflow image output path is null
     */
    JAiWorkflow getWorkflowImage(Path workflowImageOutputPath) throws IOException;

    /**
     * Gets the workflow image as a {@link BufferedImage}.
     *
     * @return the workflow image as a {@link BufferedImage}
     */
    BufferedImage getWorkflowImage();
}
