package fr.albin.bank_account.infrastructure.adapter.out.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.port.out.userPort.LoadUserPort;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.UserEntity;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.UserRepository;

/**
 * Classe qui implémente l'interface LoadUserPort pour charger un utilisateur à partir de la base de données
 */
@Component
public class LoadUserAdapter implements LoadUserPort {

    private final UserRepository userRepository;

    public LoadUserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> loadUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .map(UserEntity::toUser);
    }

    @Override
    public Optional<User> loadUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .map(UserEntity::toUser);
    }
}
