package com.art.orion.model.dao.impl;

import com.art.orion.controller.command.util.TextHandler;
import com.art.orion.exception.OrionDatabaseException;
import com.art.orion.model.dao.ProductDao;
import com.art.orion.model.entity.Accessory;
import com.art.orion.model.entity.Clothing;
import com.art.orion.model.entity.ProductCategory;
import com.art.orion.model.entity.ProductDetails;
import com.art.orion.model.entity.Shoes;
import com.art.orion.model.pool.ConnectionPool;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.art.orion.model.dao.column.ShoesColumn.ACTIVE;
import static com.art.orion.model.dao.column.ShoesColumn.BRAND;
import static com.art.orion.model.dao.column.ShoesColumn.COST;
import static com.art.orion.model.dao.column.ShoesColumn.DESCRIPTION_EN;
import static com.art.orion.model.dao.column.ShoesColumn.DESCRIPTION_RU;
import static com.art.orion.model.dao.column.ShoesColumn.IMAGE_PATH;
import static com.art.orion.model.dao.column.ShoesColumn.MODEL_NAME;
import static com.art.orion.util.Constant.DATABASE_EXCEPTION;

/**
 * The {@code ProductDaoJdbc} class works with database tables: accessories, clothing and shoes
 *
 * @author Aliaksandr Artsikhovich
 * @version 1.0
 * @see ProductDao
 */
public class ProductDaoJdbc implements ProductDao {
    private static final Logger logger = LogManager.getLogger();
    private static final ProductDaoJdbc INSTANCE = new ProductDaoJdbc();
    private static final AccessoryJdbc ACCESSORY_JDBC = AccessoryJdbc.getInstance();
    private static final ClothingJdbc CLOTHING_JDBC = ClothingJdbc.getInstance();
    private static final ShoesJdbc SHOES_JDBC = ShoesJdbc.getInstance();
    private static final String COUNT_ALL_ACCESSORIES = "SELECT count(*) FROM accessories";
    private static final String COUNT_ALL_CLOTHING = "SELECT count(*) FROM clothing";
    private static final String COUNT_ALL_SHOES = "SELECT count(*) FROM shoes";
    private static final String COUNT_ACTIVE_ACCESSORIES = "SELECT count(*) FROM accessories WHERE active = 1";
    private static final String COUNT_ACTIVE_CLOTHING = "SELECT count(*) FROM clothing WHERE active = 1";
    private static final String COUNT_ACTIVE_SHOES = "SELECT count(*) FROM shoes WHERE active = 1";

    private ProductDaoJdbc() {
    }

    public static ProductDaoJdbc getInstance() {
        return INSTANCE;
    }

    @Override
    public void addProductToDatabase(Object product) throws OrionDatabaseException {
        if (product instanceof Accessory) {
            ACCESSORY_JDBC.addAccessoryToDatabase((Accessory) product);
        } else if (product instanceof Clothing) {
            CLOTHING_JDBC.addClothingToDatabase((Clothing) product);
        } else if (product instanceof Shoes) {
            SHOES_JDBC.addShoesToDatabase((Shoes) product);
        }
        logger.log(Level.DEBUG, "Product is added in the database");
    }

    @Override
    public List<Accessory> searchAccessories(int limit, int offset, boolean isAdmin) throws OrionDatabaseException {
        return ACCESSORY_JDBC.searchAccessories(limit, offset, isAdmin);
    }

    @Override
    public Optional<Accessory> findAccessoryById(int id) throws OrionDatabaseException {
        return ACCESSORY_JDBC.findAccessoryById(id);
    }

    @Override
    public List<Clothing> searchClothing(int limit, int offset, boolean isAdmin) throws OrionDatabaseException {
        return CLOTHING_JDBC.searchClothing(limit, offset, isAdmin);
    }

    @Override
    public Optional<Clothing> findClothingById(int id) throws OrionDatabaseException {
        return CLOTHING_JDBC.findClothingById(id);
    }

    @Override
    public List<Shoes> searchShoes(int limit, int offset, boolean isAdmin) throws OrionDatabaseException {
        return SHOES_JDBC.searchShoes(limit, offset, isAdmin);
    }

    @Override
    public Optional<Shoes> findShoesById(int id) throws OrionDatabaseException {
        return SHOES_JDBC.findShoesById(id);
    }

    @Override
    public int countNumberProducts(ProductCategory productCategory, boolean isAdmin)
                                    throws OrionDatabaseException {
        int number = 0;
        String query;
        switch (productCategory) {
            case ACCESSORIES -> query = isAdmin ? COUNT_ALL_ACCESSORIES : COUNT_ACTIVE_ACCESSORIES;
            case CLOTHING -> query = isAdmin ? COUNT_ALL_CLOTHING : COUNT_ACTIVE_CLOTHING;
            case SHOES -> query = isAdmin ? COUNT_ALL_SHOES : COUNT_ACTIVE_SHOES;
            default -> throw new OrionDatabaseException("Invalid product category - " + productCategory);
        }
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                number = resultSet.getInt(1);
            }
            logger.log(Level.DEBUG, () -> "Counting the quantity of products");
        } catch (SQLException e) {
            throw new OrionDatabaseException(DATABASE_EXCEPTION, e);
        }
        return number;
    }

    @Override
    public void updateProduct(Object product) throws OrionDatabaseException {
        if (product instanceof Accessory) {
            ACCESSORY_JDBC.updateProduct((Accessory) product);
        } else if (product instanceof Clothing) {
            CLOTHING_JDBC.updateProduct((Clothing) product);
        } else if (product instanceof Shoes) {
            SHOES_JDBC.updateProduct((Shoes) product);
        }
        logger.log(Level.DEBUG, "Product is updated in the database");
    }

    /**
     * Creates the product details
     *
     * @param resultSet {@link ResultSet} the resultSet
     * @return {@link ProductDetails} the product details
     * @throws OrionDatabaseException the OrionDatabaseException exception
     */
    protected static ProductDetails createProductDetails (ResultSet resultSet) throws OrionDatabaseException {
        ProductDetails productDetails;
        try {
            String brand = resultSet.getString(BRAND);
            String modelName = resultSet.getString(MODEL_NAME);
            List<String> descriptionRu = TextHandler.createListFromText(resultSet.getString(DESCRIPTION_RU));
            List<String> descriptionEn = TextHandler.createListFromText(resultSet.getString(DESCRIPTION_EN));
            String imagePath = resultSet.getString(IMAGE_PATH);
            BigDecimal cost = BigDecimal.valueOf(resultSet.getDouble(COST)).setScale(2, RoundingMode.HALF_UP);
            boolean active = resultSet.getBoolean(ACTIVE);
            productDetails = new ProductDetails(brand, modelName, descriptionRu, descriptionEn,
                    imagePath, cost, active);
            logger.log(Level.DEBUG, () -> "Creating details of product");
        } catch (SQLException e) {
            throw new OrionDatabaseException(DATABASE_EXCEPTION, e);
        }
        return productDetails;
    }

    /**
     * Fill the statement with the product details
     *
     * @param statement {@link PreparedStatement} the preparedStatement
     * @param productDetails {@link ProductDetails} the product details
     * @param indices {@link Map} of {@link String} and {@link Integer} indices of table columns of product
     * @throws SQLException the SQLException exception
     */
    protected static void setProductDetailsInStatement (PreparedStatement statement, ProductDetails productDetails,
                                                        Map<String, Integer> indices) throws SQLException {
        int brandIndex = indices.get(BRAND);
        int modelNameIndex = indices.get(MODEL_NAME);
        int descriptionRuIndex = indices.get(DESCRIPTION_RU);
        int descriptionEnIndex = indices.get(DESCRIPTION_EN);
        int imagePathIndex = indices.get(IMAGE_PATH);
        int costIndex = indices.get(COST);
        int activeIndex = indices.get(ACTIVE);
        statement.setString(brandIndex - 1, productDetails.getBrand());
        statement.setString(modelNameIndex - 1, productDetails.getModelName());
        statement.setString(descriptionRuIndex- 1,
                TextHandler.createTextFromList(productDetails.getDescriptionRu()));
        statement.setString(descriptionEnIndex - 1,
                TextHandler.createTextFromList(productDetails.getDescriptionEn()));
        statement.setString(imagePathIndex - 1, productDetails.getImgPath());
        statement.setBigDecimal(costIndex - 1, productDetails.getCost());
        statement.setBoolean(activeIndex - 1, productDetails.isActive());
    }
}
