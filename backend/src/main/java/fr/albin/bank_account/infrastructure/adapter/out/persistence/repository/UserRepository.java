package fr.albin.bank_account.infrastructure.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.UserEntity;
/**
 * Repository Spring Data JPA pour l'entité UserEntity. Cette interface étend JpaRepository
 * et fournit des méthodes pour accéder aux données des utilisateurs dans la base de données.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
}
