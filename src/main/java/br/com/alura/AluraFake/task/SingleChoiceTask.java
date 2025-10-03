package br.com.alura.AluraFake.task;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class SingleChoiceTask extends Task {

    @ElementCollection
    private List<Option> options;

    @Embeddable
    public static class Option {

        private String option;
        private boolean isCorrect;

        public Option(String option, boolean isCorrect) {
            this.option = option;
            this.isCorrect = isCorrect;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

}
