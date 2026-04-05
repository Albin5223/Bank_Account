package fr.albin.bank_account.domain.model;


import java.time.LocalDateTime;
import java.util.Objects;

import fr.albin.bank_account.domain.model.enums.TypeOperation;

/**
 * Représente une opération bancaire unitaire dans l'historique du compte.
 * <p>
 * Une opération est caractérisée par une date d'exécution, un type
 * (création, dépôt, retrait) et un montant associé.
 * </p>
 */
public class Operation {
    
    private final LocalDateTime date;
    private final TypeOperation operation;
    private final double amount;

    public Operation(LocalDateTime date, TypeOperation type, double amount){
        this.date = Objects.requireNonNull(date, "La date de l'opération est obligatoire");
        this.operation = Objects.requireNonNull(type, "Le type d'opération est obligatoire");
        if (amount < 0) {
            throw new IllegalArgumentException("Le montant de l'opération ne peut pas être négatif");
        }
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public TypeOperation getOperation() {
        return operation;
    }

    public double getAmount() {
        return amount;
    }

    

}
