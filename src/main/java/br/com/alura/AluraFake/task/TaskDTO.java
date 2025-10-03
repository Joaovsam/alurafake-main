package br.com.alura.AluraFake.task;

import java.util.List;

public class TaskDTO {

    private String statement;
    private int order;
    private List<SingleChoiceTask.Option> options;
    private Long courseId;

    public TaskDTO(String statement, int order, List<SingleChoiceTask.Option> options, Long courseId) {
        this.statement = statement;
        this.order = order;
        this.options = options;
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<SingleChoiceTask.Option> getOptions() {
        return options;
    }

    public void setOptions(List<SingleChoiceTask.Option> options) {
        this.options = options;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

}
