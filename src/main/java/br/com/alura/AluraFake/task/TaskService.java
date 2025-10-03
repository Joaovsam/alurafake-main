package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(CourseRepository courseRepository,
            TaskRepository taskRepository
    ) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createOpenText(TaskDTO req) {
        validateStatement(req.getStatement());
        validateOrderPositive(req.getOrder());
        Course course = loadCourseAndCheckBuilding(req.getCourseId());
        ensureStatementUniqueInCourse(course, req.getStatement());
        ensureOrderSequence(course, req.getOrder());

        shiftOrdersIfNeeded(course, req.getOrder());

        OpenTextTask task = new OpenTextTask();
        task.setCourse(course);
        task.setStatement(req.getStatement().trim());
        task.setOrder(req.getOrder());

        return taskRepository.save(task);
    }

    @Transactional
    public Task createSingleChoice(TaskDTO req) {
        validateStatement(req.getStatement());
        validateOrderPositive(req.getOrder());
        Course course = loadCourseAndCheckBuilding(req.getCourseId());
        ensureStatementUniqueInCourse(course, req.getStatement());
        ensureOrderSequence(course, req.getOrder());

        var options = req.getOptions();
        if (options == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "options é obrigatório");
        }
        if (options.size() < 2 || options.size() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alternativa deve ter entre 2 e 5 alternativas");
        }

        Set<String> seen = new HashSet<>();
        int correctCount = 0;
        for (SingleChoiceTask.Option o : options) {
            if (o.getOption() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativa sem texto");
            }
            String text = o.getOption().trim();
            if (text.length() < 4 || text.length() > 80) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativas devem ter entre 4 e 80 caracteres");
            }
            if (text.equalsIgnoreCase(req.getStatement().trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativa não pode ser igual ao enunciado");
            }
            if (!seen.add(text.toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativas não podem ser iguais entre si");
            }
            if (Boolean.TRUE.equals(o.isCorrect())) {
                correctCount++;
            }
        }
        if (correctCount != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Atividade de escolha unica deve ter exatamente uma alternativa correta");
        }

        shiftOrdersIfNeeded(course, req.getOrder());

        SingleChoiceTask task = new SingleChoiceTask();
        task.setCourse(course);
        task.setStatement(req.getStatement().trim());
        task.setOrder(req.getOrder());
        task.setOptions(options);

        Task saved = taskRepository.save(task);

        return saved;
    }

    @Transactional
    public Task createMultipleChoice(TaskDTO req) {
        validateStatement(req.getStatement());
        validateOrderPositive(req.getOrder());
        Course course = loadCourseAndCheckBuilding(req.getCourseId());
        ensureStatementUniqueInCourse(course, req.getStatement());
        ensureOrderSequence(course, req.getOrder());

        var options = req.getOptions();
        if (options == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "options é obrigatório");
        }
        if (options.size() < 3 || options.size() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alternativa deve ter entre 3 e 5 alternativas");
        }

        Set<String> seen = new HashSet<>();
        int correctCount = 0;
        for (SingleChoiceTask.Option o : options) {
            if (o.getOption() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativa sem texto");
            }
            String text = o.getOption().trim();
            if (text.length() < 4 || text.length() > 80) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativas devem ter entre 4 e 80 caracteres");
            }
            if (text.equalsIgnoreCase(req.getStatement().trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativa não pode ser igual ao enunciado");
            }
            if (!seen.add(text.toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alternativas não podem ser iguais entre si");
            }
            if (Boolean.TRUE.equals(o.isCorrect())) {
                correctCount++;
            }
        }
        if (correctCount < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Atividades de escolha multipla deve ter duas ou mais alternativas corretas");
        }
        if (correctCount == options.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Atividades de escolha multipla deve ter ao menos uma alternativa incorreta");
        }

        shiftOrdersIfNeeded(course, req.getOrder());

        MultipleChoiceTask task = new MultipleChoiceTask();
        task.setCourse(course);
        task.setStatement(req.getStatement().trim());
        task.setOrder(req.getOrder());
        task.setOptions(options);

        Task saved = taskRepository.save(task);

        return saved;
    }

    private void validateStatement(String statement) {
        if (statement == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "statement é obrigatório");
        }
        int len = statement.trim().length();
        if (len < 4 || len > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "statement deve ter entre 4 e 255 caracteres");
        }
    }

    private void validateOrderPositive(Integer order) {
        if (order == null || order <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "order deve ser inteiro positivo");
        }
    }

    private Course loadCourseAndCheckBuilding(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        if (course.getStatus() != Status.BUILDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso não está em BUILDING");
        }
        return course;
    }

    private void ensureStatementUniqueInCourse(Course course, String statement) {
        if (taskRepository.existsByCourseAndStatement(course, statement)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma atividade com esse enunciado no curso");
        }
    }

    private void ensureOrderSequence(Course course, Integer newOrder) {
        List<Task> tasks = taskRepository.findByCourseOrderByOrderAsc(course);
        int size = tasks.size();
        if (newOrder > size + 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Ordem inválida. Próxima ordem permitida é %d", size + 1));
        }
    }

    private void shiftOrdersIfNeeded(Course course, Integer newOrder) {
        List<Task> affected = taskRepository.findByCourseAndOrderGreaterThanEqualOrderByOrderAsc(course, newOrder);
        if (!affected.isEmpty()) {
            for (Task t : affected) {
                t.setOrder(t.getOrder() + 1);
            }
            taskRepository.saveAll(affected);
        }
    }
}
