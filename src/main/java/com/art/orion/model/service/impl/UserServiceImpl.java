package com.art.orion.model.service.impl;

import com.art.orion.exception.OrionDatabaseException;
import com.art.orion.model.dao.UserDao;
import com.art.orion.model.dao.impl.UserDaoJdbc;
import com.art.orion.model.entity.User;
import com.art.orion.exception.ServiceException;
import com.art.orion.model.service.UserService;
import com.art.orion.model.validator.UserValidator;
import com.art.orion.util.ErrorMessageManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

/**
 * The {@code UserServiceImpl} class represents user service implementation
 *
 * @author Aliaksandr Artsikhovich
 * @version 1.0
 * @see UserService
 */
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger();
    private UserDao userDao = UserDaoJdbc.getInstance();

    @Override
    public boolean checkIsUsernameBusy(String username, StringBuilder validationStatus) throws ServiceException {
        boolean isUsernameBusy;
        try {
            isUsernameBusy = userDao.checkIsUsernameBusy(username);
        } catch (OrionDatabaseException e) {
            validationStatus.append(e.getMessage());
            throw new ServiceException("Database access error occurred while validating username", e);
        }
        if (isUsernameBusy) {
            validationStatus.append(ErrorMessageManager.getMessage("msg.nameExists")).append("\n");
        }
        return isUsernameBusy;
    }

    @Override
    public boolean isFirstUser() throws ServiceException {
        try {
            return userDao.countUsers() == 0;
        } catch (OrionDatabaseException e) {
            throw new ServiceException("An error occurred while counting users in the database", e);
        }
    }

    @Override
    public boolean registerUser(User user) throws ServiceException {
        try {
            return userDao.createUser(user);
        } catch (OrionDatabaseException e) {
            throw new ServiceException("user registration error", e);
        }
    }

    @Override
    public boolean[] validateCredentialsAndActivity(String username, String password) throws ServiceException {
        try {
            return userDao.validateCredentialsAndActivity (username, password);
        } catch (OrionDatabaseException e) {
            throw new ServiceException("Database access error occurred while validating credentials", e);
        }
    }

    @Override
    public Optional<User> findUserByUsername(String username) throws ServiceException {
        try {
            return userDao.findUserByUsername(username);
        } catch (OrionDatabaseException e) {
            throw new ServiceException("An error occurred while retrieving the user from the database", e);
        }
    }

    @Override
    public List<User> findUsers(int limit, int offset) throws ServiceException {
        try {
            return userDao.findUsers(limit, offset);
        } catch (OrionDatabaseException e) {
            throw new ServiceException("An error occurred while retrieving users from the database", e);
        }
    }

    @Override
    public int countUsers() throws ServiceException {
        try {
            return userDao.countUsers();
        } catch (OrionDatabaseException e) {
            throw new ServiceException("An error occurred while retrieving number of users", e);
        }
    }

    @Override
    public boolean updateUser(User user) throws ServiceException {
        try {
           return userDao.updateUser(user);
        } catch (OrionDatabaseException e) {
            throw new ServiceException("An error occurred while updating of user", e);
        }
    }

    @Override
    public boolean deleteUser(String username) throws ServiceException {
        try {
            return userDao.deleteUser(username);
        } catch (OrionDatabaseException e) {
            throw new ServiceException("An error occurred while deleting of user", e);
        }
    }

    @Override
    public boolean isValidUser (String username, String password, String confirmPassword, String firstname,
                                String lastname, String email, StringBuilder validationStatus) {
        boolean isValidUser = true;
        if (UserValidator.isNotValidUsername(username)) {
            validationStatus.append(ErrorMessageManager.getMessage("msg.notValidUsername")).append("\n");
            isValidUser = false;
        }
        if (UserValidator.isNotValidPassword(password)) {
            validationStatus.append(ErrorMessageManager.getMessage("msg.notValidPassword")).append("\n");
            isValidUser = false;
        }
        if (UserValidator.isNotEqualsPasswords(password, confirmPassword)) {
            validationStatus.append(ErrorMessageManager.getMessage("msg.notConfirmPassword")).append("\n");
            isValidUser = false;
        }
        if (UserValidator.isNotValidName(firstname)) {
            validationStatus.append(ErrorMessageManager.getMessage("msg.notValidFirstname")).append("\n");
            isValidUser = false;
        }
        if (UserValidator.isNotValidName(lastname)) {
            validationStatus.append(ErrorMessageManager.getMessage("msg.notValidLastname")).append("\n");
            isValidUser = false;
        }
        if (UserValidator.isNotValidEmail(email)) {
            validationStatus.append(ErrorMessageManager.getMessage("msg.notValidEmail"));
            isValidUser = false;
        }
        logger.log(Level.INFO, "User {} is valid = {}", username, isValidUser);
        return isValidUser;
    }
}
