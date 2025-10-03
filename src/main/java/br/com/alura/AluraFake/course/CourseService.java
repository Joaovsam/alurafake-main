package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.MultipleChoiceTask;
import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.task.SingleChoiceTask;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Course publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));

        if (course.getStatus() != Status.BUILDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso deve estar em BUILDING para ser publicar");
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrderAsc(course);
        if (tasks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso deve possuir atividades para publicar");
        }

        boolean hasOpen = tasks.stream().anyMatch(t -> t instanceof OpenTextTask);
        boolean hasSingle = tasks.stream().anyMatch(t -> t instanceof SingleChoiceTask);
        boolean hasMulti = tasks.stream().anyMatch(t -> t instanceof MultipleChoiceTask);

        if (!(hasOpen && hasSingle && hasMulti)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso deve conter ao menos uma atividade de cada tipo");
        }

        var orders = tasks.stream().map(Task::getOrder).sorted().collect(Collectors.toList());
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i) != i + 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ordem das atividades não está em sequência contínua");
            }
        }

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }
}
