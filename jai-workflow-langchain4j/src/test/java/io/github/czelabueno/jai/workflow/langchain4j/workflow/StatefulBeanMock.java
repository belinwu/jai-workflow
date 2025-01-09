package io.github.czelabueno.jai.workflow.langchain4j.workflow;

import io.github.czelabueno.jai.workflow.langchain4j.AbstractStatefulBean;
import lombok.Data;

import java.util.List;

@Data
public class StatefulBeanMock extends AbstractStatefulBean {

    private List<String> documents;
    private String webSearch;

    public StatefulBeanMock() {
    }
}
