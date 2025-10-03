package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Course course;

    @Mock
    private CourseRepository courseRepository; // <- precisa

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Course mockCourse = mock(Course.class);
        when(mockCourse.getId()).thenReturn(1L);
        when(mockCourse.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(mockCourse));
    }

    @Test
    void testCreateOpenTextSuccess() {
        TaskDTO dto = new TaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Com quantos dias se faz um teste?");
        dto.setOrder(1);

        OpenTextTask savedTask = new OpenTextTask();
        savedTask.setStatement(dto.getStatement());
        savedTask.setCourse(course);
        savedTask.setOrder(dto.getOrder());

        when(taskRepository.save(any(OpenTextTask.class))).thenReturn(savedTask);

        Task result = taskService.createOpenText(dto);

        assertNotNull(result);
        assertEquals("Com quantos dias se faz um teste?", result.getStatement());
        assertEquals(course, result.getCourse());
        assertEquals(1, result.getOrder());
    }

    @Test
    void testCreateSingleChoiceSuccess() {
        TaskDTO dto = new TaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Hoje é qual dia da semana?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceTask.Option("Sexta", true),
                new SingleChoiceTask.Option("Sabado", false),
                new SingleChoiceTask.Option("Segunda", false)
        ));

        SingleChoiceTask savedTask = new SingleChoiceTask();
        savedTask.setStatement(dto.getStatement());
        savedTask.setCourse(course);
        savedTask.setOrder(dto.getOrder());
        savedTask.setOptions(dto.getOptions());

        when(taskRepository.save(any(SingleChoiceTask.class))).thenReturn(savedTask);

        Task result = taskService.createSingleChoice(dto);

        assertNotNull(result);
        assertEquals("Hoje é qual dia da semana?", result.getStatement());
        assertEquals(course, result.getCourse());
        assertEquals(3, ((SingleChoiceTask) result).getOptions().size());
    }

    @Test
    void testCreateMultipleChoiceSuccess() {
        TaskDTO dto = new TaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Quais desses pertencem ao principio SOLID");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceTask.Option("Single Responsibility", true),
                new SingleChoiceTask.Option("Open/Closed", true),
                new SingleChoiceTask.Option("Interface aggregation", false)
        ));

        MultipleChoiceTask savedTask = new MultipleChoiceTask();
        savedTask.setStatement(dto.getStatement());
        savedTask.setCourse(course);
        savedTask.setOrder(dto.getOrder());
        savedTask.setOptions(dto.getOptions());

        when(taskRepository.save(any(MultipleChoiceTask.class))).thenReturn(savedTask);

        Task result = taskService.createMultipleChoice(dto);

        assertNotNull(result);
        assertEquals(3, ((MultipleChoiceTask) result).getOptions().size());
    }

}
