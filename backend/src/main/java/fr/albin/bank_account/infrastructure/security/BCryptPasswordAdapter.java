package fr.albin.bank_account.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import fr.albin.bank_account.domain.port.out.userPort.EncodePasswordPort;

/**
 * Classe qui permet d'adapter le mot de passe en utilisant l'algorithme de hachage BCrypt
 */
@Service
public class BCryptPasswordAdapter implements EncodePasswordPort{

    @Override
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
