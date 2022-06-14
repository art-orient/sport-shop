package com.art.orion.controller.command.impl;

import com.art.orion.controller.command.Command;
import com.art.orion.model.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.art.orion.controller.command.PagePath.CART_PAGE;
import static com.art.orion.util.Constant.CART;
import static com.art.orion.util.Constant.ERROR;
import static com.art.orion.util.Constant.GROUPED_CART;
import static com.art.orion.util.Constant.NUMBER;
import static com.art.orion.util.Constant.TOTAL_COST;

/**
 * The command is responsible for the formation of the user's cart
 *
 * @author Aliaksandr Artsikhovich
 * @see Command
 */
public class CartCommand implements Command {
    private static final Logger logger = LogManager.getLogger();
    private final CartService cartService;

    public CartCommand(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public String execute(HttpServletRequest req) {
        List<Object> products = (ArrayList<Object>) req.getSession().getAttribute(CART);
        if (products != null) {
            Map<Object, Long> groupedProducts = cartService.groupProducts(products);
            req.setAttribute(GROUPED_CART, new ArrayList<>(groupedProducts.entrySet()));
            BigDecimal totalCost = cartService.findTotalCost(groupedProducts);
            req.setAttribute(TOTAL_COST, totalCost);
            req.setAttribute(NUMBER, products.size());
            req.setAttribute(ERROR, req.getParameter(ERROR));
        }
        logger.log(Level.DEBUG, () -> "Created a cart");
        return CART_PAGE;
    }
}
