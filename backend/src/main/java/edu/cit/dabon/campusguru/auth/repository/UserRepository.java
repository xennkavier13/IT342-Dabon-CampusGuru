package edu.cit.dabon.campusguru.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.dabon.campusguru.auth.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByInstitutionalEmail(String institutionalEmail);
    boolean existsByUsername(String username);
    boolean existsByInstitutionalEmail(String institutionalEmail);
}
