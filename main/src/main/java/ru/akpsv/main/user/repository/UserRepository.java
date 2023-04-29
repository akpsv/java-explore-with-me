package ru.akpsv.main.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryAdvanced {
}
