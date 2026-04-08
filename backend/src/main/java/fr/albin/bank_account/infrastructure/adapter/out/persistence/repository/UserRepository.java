package fr.albin.bank_account.infrastructure.adapter.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.UserEntity;
/**
 * Repository Spring Data JPA pour l'entité UserEntity. Cette interface étend JpaRepository
 * et fournit des méthodes pour accéder aux données des utilisateurs dans la base de données.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
}
 