package fr.albin.bank_account.domain.model.enums;

public enum Role {
    USER,
    ADMIN;

    public static String getName(Role role){
        return switch (role) {
            case USER -> "USER";
            case ADMIN -> "ADMIN";
        };
    }
}
