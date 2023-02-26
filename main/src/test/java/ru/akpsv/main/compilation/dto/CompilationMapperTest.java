//package ru.akpsv.main.compilation.dto;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.akpsv.TestHelper;
//import ru.akpsv.main.compilation.Compilation;
//import ru.akpsv.main.event.dto.EventMapper;
//import ru.akpsv.main.event.dto.EventShortDto;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.not;
//
//@SpringBootTest
//class CompilationMapperTest {
//
//    @Test
//    void toCompilation() {
//        //Подготвока
//        NewCompilationDto newCompilationDto = TestHelper.createNewCompilationDto();
//        //Действия
//        Compilation actualCompilation = CompilationMapper.toCompilation(newCompilationDto);
//        //Проверка
//        assertThat(actualCompilation.getEvents().size(), equalTo(0));
//    }
//
//    @Test
//    void toCompilationDto_Compilation_ReturnsCompilationDto() {
//        //Подготовка
//        Compilation compilation = TestHelper.createCompilation();
//        EventMapper stubEventMapper = Mockito.mock(EventMapper.class);
//        Mockito.when(stubEventMapper.toEventShortDto(Mockito.any())).thenReturn(new EventShortDto());
//        //Действия
//        CompilationDto actualCompilationDto = CompilationMapper.toCompilationDto(compilation);
//        //Проверка
//        assertThat(actualCompilationDto.getId(), not(0L));
//
//
//    }
//}