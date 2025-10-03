package br.com.alura.AluraFake.task;

import jakarta.persistence.ElementCollection;
import java.util.List;

public class MultipleChoiceTask extends Task {

    @ElementCollection
    private List<SingleChoiceTask.Option> options;

    public List<SingleChoiceTask.Option> getOptions() {
        return options;
    }

    public void setOptions(List<SingleChoiceTask.Option> options) {
        this.options = options;
    }

}
