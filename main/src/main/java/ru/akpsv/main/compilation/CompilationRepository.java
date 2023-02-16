package ru.akpsv.main.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
