package fr.albin.bank_account.domain.service;

import org.springframework.stereotype.Service;

import fr.albin.bank_account.domain.exception.InvalidCredential;
import fr.albin.bank_account.domain.exception.UserAlreadyExistst;
import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.port.in.userUseCase.LoginUserUseCase;
import fr.albin.bank_account.domain.port.in.userUseCase.RegisterUserUseCase;
import fr.albin.bank_account.domain.port.out.userPort.EncodePasswordPort;
import fr.albin.bank_account.domain.port.out.userPort.GenerateTokenPort;
import fr.albin.bank_account.domain.port.out.userPort.LoadUserPort;
import fr.albin.bank_account.domain.port.out.userPort.SaveUserPort;

@Service
public class UserService implements RegisterUserUseCase, LoginUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final GenerateTokenPort generateTokenPort;
    private final EncodePasswordPort encodePasswordPort;

    public UserService(
        LoadUserPort loadUserPort, 
        SaveUserPort saveUserPort, 
        GenerateTokenPort generateTokenPort,
        EncodePasswordPort encodePasswordPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.generateTokenPort = generateTokenPort;
        this.encodePasswordPort = encodePasswordPort;
    }

    @Override
    public String loginUser(String username, String password) {
        var userOpt = loadUserPort.loadUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new InvalidCredential("Invalid username or password");
        }
        var user = userOpt.get();
        if (!encodePasswordPort.matches(password, user.getPassword())) {
            throw new InvalidCredential("Invalid username or password");
        }
        return generateTokenPort.generateToken(user);  
    }


    @Override
    public String registerUser(String username, String password, String email) {
        if (loadUserPort.loadUserByUsername(username).isPresent()) {
            throw new UserAlreadyExistst("Username already exists");
        }
        String encodedPassword = encodePasswordPort.encode(password);
        var user = new User(username, encodedPassword, email);
        saveUserPort.save(user);
        return generateTokenPort.generateToken(user);
    }
    
}
