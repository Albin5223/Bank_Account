package fr.albin.bank_account.infrastructure.adapter.out.persistence.adapter;

import org.springframework.stereotype.Component;

import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.port.out.userPort.SaveUserPort;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.UserEntity;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.UserRepository;


/**
 * Classe qui implémente l'interface SaveUserPort pour sauvegarder un utilisateur dans la base de données
 */
@Component
public class SaveUserAdapter implements SaveUserPort{

    private final UserRepository userRepository;

    public SaveUserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) {
        userRepository.save(new UserEntity(user));
    }
    
}
