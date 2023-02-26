package ru.akpsv.main.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.compilation.dto.CompilationDto;
import ru.akpsv.main.compilation.dto.NewCompilationDto;
import ru.akpsv.main.compilation.dto.UpdateCompilationRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    /**
     * Admin: Подборки событий. АПИ для работы с подборками событий
     */

    /**
     * Создание подборки
     *
     * @param newCompilationDto
     * @return
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/compilations")
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@RequestBody UpdateCompilationRequest updatingRequest, @PathVariable Long compId) {
        return compilationService.updateCompilation(compId, updatingRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteById(compId);
    }


    /**
     * Public: Подборки событий. Публичный АПИ для работы с подборками событий
     */
    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }


}
