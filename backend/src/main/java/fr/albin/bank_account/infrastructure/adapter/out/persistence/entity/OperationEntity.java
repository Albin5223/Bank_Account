package fr.albin.bank_account.infrastructure.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import fr.albin.bank_account.domain.model.Operation;
import fr.albin.bank_account.domain.model.enums.TypeOperation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**
 * Entité JPA représentant une opération bancaire. Cette classe est utilisée pour la persistance
 * des opérations bancaires dans la base de données.
 */
@Entity
@Table(name = "bank_account_operation")
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_date", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private TypeOperation operationType;

    @Column(name = "amount", nullable = false)
    private double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number", nullable = false)
    private BankAccountEntity bankAccount;

    protected OperationEntity() {
    }

    public OperationEntity(LocalDateTime date, TypeOperation operationType, double amount) {
        this.date = date;
        this.operationType = operationType;
        this.amount = amount;
    }

    public OperationEntity(Operation operation) {
        this(operation.getDate(), operation.getOperation(), operation.getAmount());
    }

    public void setBankAccount(BankAccountEntity bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Operation toOperation() {
        return new Operation(date, operationType, amount);
    }
}
