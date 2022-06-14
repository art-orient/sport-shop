package com.art.orion.model.service;

import com.art.orion.exception.ServiceException;
import com.art.orion.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * The {@code UserService} interface represents user service
 *
 * @author Aliaksandr Artsikhovich
 * @version 1.0
 */
public interface UserService {

    /**
     * Сhecks if the username is busy or free
     *
     * @param username {@link String} the username
     * @param validationStatus {@link StringBuilder} the validation status
     * @return the boolean
     * @throws ServiceException the service exception
     */
    boolean checkIsUsernameBusy(String username, StringBuilder validationStatus) throws ServiceException;

    /**
     * Сhecks if the user logs into the application first
     *
     * @return the boolean
     * @throws ServiceException the service exception
     */
    boolean isFirstUser() throws ServiceException;

    /**
     * Registers a new user
     *
     * @param user {@link User} the user
     * @return the boolean
     * @throws ServiceException the service exception
     */
    boolean registerUser(User user) throws ServiceException;

    /**
     * Validates user credentials and activity
     *
     * @param username {@link String} the username
     * @param password {@link String} the password
     * @return the boolean
     * @throws ServiceException the service exception
     */
    boolean[] validateCredentialsAndActivity (String username, String password) throws ServiceException;

    /**
     * Finds the user by his username
     *
     * @param username {@link String} the username
     * @return {@link Optional} of {@link User} the optional of found user
     * @throws ServiceException the service exception
     */
    Optional<User> findUserByUsername(String username) throws ServiceException;

    /**
     * Finds all users
     *
     * @param limit number of users per page
     * @param offset index of the first user on the page
     * @return {@link List} of {@link User} the list of found users
     * @throws ServiceException the service exception
     */
    List<User> findUsers(int limit, int offset) throws ServiceException;

    /**
     * Сounts the number of all users
     *
     * @return the number of all users
     * @throws ServiceException the service exception
     */
    int countUsers() throws ServiceException;

    /**
     * Changes user data (role & activity), blocks and unblocks the user
     *
     * @param user {@link User} the user
     * @return the boolean
     * @throws ServiceException the service exception
     */
    boolean updateUser(User user) throws ServiceException;

    /**
     * Removes the user by his username
     *
     * @param username {@link String} the username
     * @return the boolean
     * @throws ServiceException the service exception
     */
    boolean deleteUser(String username) throws ServiceException;

    /**
     * Validates user data
     *
     * @param username {@link String} the username
     * @param password {@link String} the password
     * @param confirmPassword {@link String} the confirmed password
     * @param firstname {@link String} the first name
     * @param lastname {@link String} the last name
     * @param email {@link String} the e-mail
     * @param validationStatus {@link StringBuilder} the validation status
     * @return the boolean
     */
    boolean isValidUser(String username, String password, String confirmPassword, String firstname,
                                String lastname, String email, StringBuilder validationStatus);
}