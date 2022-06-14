package com.art.orion.model.dao.impl;

import com.art.orion.exception.OrionDatabaseException;
import com.art.orion.model.entity.Clothing;
import com.art.orion.model.entity.ProductDetails;
import com.art.orion.model.pool.ConnectionPool;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.art.orion.model.dao.column.ClothingColumn.ACTIVE;
import static com.art.orion.model.dao.column.ClothingColumn.ACTIVE_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.BRAND;
import static com.art.orion.model.dao.column.ClothingColumn.BRAND_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.CLOTHING_ID;
import static com.art.orion.model.dao.column.ClothingColumn.CLOTHING_ID_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.COLOR;
import static com.art.orion.model.dao.column.ClothingColumn.COLOR_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.COST;
import static com.art.orion.model.dao.column.ClothingColumn.COST_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.DESCRIPTION_EN;
import static com.art.orion.model.dao.column.ClothingColumn.DESCRIPTION_EN_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.DESCRIPTION_RU;
import static com.art.orion.model.dao.column.ClothingColumn.DESCRIPTION_RU_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.IMAGE_PATH;
import static com.art.orion.model.dao.column.ClothingColumn.IMAGE_PATH_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.MODEL_NAME;
import static com.art.orion.model.dao.column.ClothingColumn.MODEL_NAME_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.TYPE_EN;
import static com.art.orion.model.dao.column.ClothingColumn.TYPE_EN_INDEX;
import static com.art.orion.model.dao.column.ClothingColumn.TYPE_RU;
import static com.art.orion.model.dao.column.ClothingColumn.TYPE_RU_INDEX;
import static com.art.orion.util.Constant.DATABASE_EXCEPTION;

/**
 * The {@code ClothingJdbc} class works with database table clothing
 *
 * @author Aliaksandr Artsikhovich
 * @version 1.0
 */
public class ClothingJdbc {
    private static final Logger logger = LogManager.getLogger();
    private static final ClothingJdbc INSTANCE = new ClothingJdbc();
    private static final String SELECT = "SELECT clothing_id, type_Ru, type_En, brand, model_name, " +
            "description_RU, description_EN, image_path, color, cost, active FROM clothing ";
    private static final String SELECT_ACTIVE_CLOTHING = SELECT + "WHERE active = 1 LIMIT ? OFFSET ?";
    private static final String INSERT_CLOTHING = "INSERT INTO clothing " +
            "(type_Ru, type_En, brand, model_name, description_RU, description_EN, image_path, color, cost, active)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_CLOTHING_BY_ID = SELECT + "WHERE clothing_id = ?";
    private static final String SELECT_ALL_CLOTHING = SELECT + "LIMIT ? OFFSET ?";
    private static final String UPDATE_CLOTHING = "UPDATE clothing SET type_Ru = ?, type_En = ?," +
            " brand = ?, model_name = ?, description_RU = ?, description_EN = ?, image_path = ?, color = ?, " +
            "cost = ?, active = ? WHERE clothing_id = ?";
    private static final int UPDATE_ID_INDEX = 11;
    private static final Map<String, Integer> indices;

    static {
        indices = new HashMap<>();
        indices.put(BRAND, BRAND_INDEX);
        indices.put(MODEL_NAME, MODEL_NAME_INDEX);
        indices.put(DESCRIPTION_RU, DESCRIPTION_RU_INDEX);
        indices.put(DESCRIPTION_EN, DESCRIPTION_EN_INDEX);
        indices.put(IMAGE_PATH, IMAGE_PATH_INDEX);
        indices.put(COST, COST_INDEX);
        indices.put(ACTIVE, ACTIVE_INDEX);
    }

    private ClothingJdbc() {
    }

    public static ClothingJdbc getInstance() {
        return INSTANCE;
    }

    /**
     * Saves the clothing
     *
     * @param clothing {@link Clothing} the clothing
     * @throws OrionDatabaseException the OrionDatabaseException exception
     */
    public void addClothingToDatabase(Clothing clothing) throws OrionDatabaseException {
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_CLOTHING)){
            fillStatement(statement, clothing);
            statement.executeUpdate();
            logger.log(Level.INFO, () -> "The clothing is saved in the database");
        } catch (SQLException e) {
            throw new OrionDatabaseException(DATABASE_EXCEPTION, e);
        }
    }

    /**
     * Finds only active clothing or all clothing if the user role is an admin
     *
     * @param limit number of products per page
     * @param offset index of the first product on the page
     * @param isAdmin is the user role an admin
     * @return {@link List} of {@link Clothing} the list of found clothing
     * @throws OrionDatabaseException the OrionDatabaseException exception
     */
    public List<Clothing> searchClothing(int limit, int offset, boolean isAdmin) throws OrionDatabaseException {
        List<Clothing> clothing = new ArrayList<>();
        String query = SELECT_ACTIVE_CLOTHING;
        if (isAdmin) {
            query = SELECT_ALL_CLOTHING;
        }
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clothing.add(createClothing(resultSet));
                }
            }
            logger.log(Level.INFO, () -> "Clothing search completed successfully");
        } catch (SQLException e) {
            throw new OrionDatabaseException(DATABASE_EXCEPTION, e);
        }
        return clothing;
    }

    /**
     * Finds the clothing by id
     *
     * @param id the clothing id
     * @return {@link Optional} of {@link Clothing} the Optional of found clothing
     * @throws OrionDatabaseException the OrionDatabaseException exception
     */
    public Optional<Clothing> findClothingById(int id) throws OrionDatabaseException {
        Optional<Clothing> optionalClothing;
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_CLOTHING_BY_ID)) {
            statement.setInt(CLOTHING_ID_INDEX, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Clothing clothing = createClothing(resultSet);
                    optionalClothing = Optional.of(clothing);
                } else {
                    throw new OrionDatabaseException(String.format("Clothing with id = %s is not found", id));
                }
            }
            logger.log(Level.DEBUG, () -> String.format("Clothing with id = %s got from the database", id));
        } catch (SQLException e) {
            throw new OrionDatabaseException(DATABASE_EXCEPTION, e);
        }
        return optionalClothing;
    }

    /**
     * Updates the clothing
     *
     * @param clothing {@link Clothing} the clothing
     * @throws OrionDatabaseException the OrionDatabaseException exception
     */
    public void updateProduct(Clothing clothing) throws OrionDatabaseException {
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_CLOTHING)){
            fillStatement(statement, clothing);
            statement.setInt(UPDATE_ID_INDEX, clothing.getClothingId());
            statement.executeUpdate();
            logger.log(Level.INFO, () -> "The accessory is updated in the database");
        } catch (SQLException e) {
            throw new OrionDatabaseException(DATABASE_EXCEPTION, e);
        }
    }

    /**
     * Creates the clothing
     *
     * @param resultSet {@link ResultSet} the accessory
     * @return {@link Clothing} the clothing
     * @throws SQLException, OrionDatabaseException the SQLException and OrionDatabaseException exceptions
     */
    private Clothing createClothing(ResultSet resultSet) throws SQLException, OrionDatabaseException {
        int clothingId = resultSet.getInt(CLOTHING_ID);
        String typeRu = resultSet.getString(TYPE_RU);
        String typeEn = resultSet.getString(TYPE_EN);
        String color = resultSet.getString(COLOR);
        ProductDetails productDetails = ProductDaoJdbc.createProductDetails(resultSet);
        return new Clothing(clothingId, typeRu, typeEn, productDetails, color);
    }

    /**
     * Fills the statement
     *
     * @param statement {@link PreparedStatement} the preparedStatement
     * @param clothing {@link Clothing} the accessory
     * @throws SQLException the SQLException exception
     */
    private void fillStatement(PreparedStatement statement, Clothing clothing) throws SQLException {
        statement.setString(TYPE_RU_INDEX - 1, clothing.getTypeRu());
        statement.setString(TYPE_EN_INDEX - 1, clothing.getTypeEn());
        statement.setString(COLOR_INDEX - 1, clothing.getColor());
        ProductDetails productDetails = clothing.getProductDetails();
        ProductDaoJdbc.setProductDetailsInStatement(statement, productDetails, indices);
    }
}
