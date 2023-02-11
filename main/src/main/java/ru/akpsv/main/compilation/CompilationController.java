package ru.akpsv.main.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.compilation.dto.CompilationDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody CompilationDto compilationDto) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@RequestParam int compId, @RequestBody CompilationDto compilationDto) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@RequestParam int compId){

    }


}
