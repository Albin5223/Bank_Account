package fr.albin.bank_account.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.BankAccountEntity;


/**
 * Repository Spring Data JPA pour l'entité BankAccountEntity. Cette interface étend JpaRepository
 * et fournit des méthodes pour accéder aux données des comptes bancaires dans la base de données.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, String> {

    BankAccountEntity findByAccountNumber(String accountNumber);
}
