package fr.albin.bank_account.domain.port.out.userPort;


/**
 * Interface qui permet d'encoder un mot de passe
 */
public interface EncodePasswordPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
