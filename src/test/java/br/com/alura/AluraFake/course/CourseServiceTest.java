package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.TaskRepository;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import br.com.alura.AluraFake.task.*;
import org.springframework.web.server.ResponseStatusException;

public class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    private Course course;
    private List<Task> tasks;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        course = new Course();
        course.setStatus(Status.BUILDING);

        OpenTextTask openTask = new OpenTextTask();
        openTask.setOrder(1);
        SingleChoiceTask singleTask = new SingleChoiceTask();
        singleTask.setOrder(2);
        MultipleChoiceTask multiTask = new MultipleChoiceTask();
        multiTask.setOrder(3);

        tasks = List.of(openTask, singleTask, multiTask);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(tasks);
        when(courseRepository.save(course)).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testPublishCourseSuccess() {
        Course published = courseService.publishCourse(1L);
        assertEquals(Status.PUBLISHED, published.getStatus());
        assertNotNull(published);
    }

    @Test
    void testPublishCourseWithoutTasks() {
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> courseService.publishCourse(1L));

        assertEquals("Curso deve possuir atividades para publicar", exception.getReason());
    }

    @Test
    void testPublishCourseWithWrongStatus() {
        when(course.getStatus()).thenReturn(Status.PUBLISHED);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> courseService.publishCourse(1L));

        assertEquals("Curso deve estar em BUILDING para ser publicar", exception.getReason());
    }
}
