package com.example.demo.utils;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

public class TestUtils {
    public static void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if (!f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);

            if (wasPrivate) {
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("PhuocNQ");
        user.setPassword("123");
        user.setCart(new Cart());
        return user;
    }

    public static User getUserWithCart() {
        User user = new User();
        user.setId(1L);
        user.setUsername("PhuocNQ");
        user.setPassword("123");
        Cart cart = new Cart();
        cart.setItems(Collections.singletonList(getItem()));
        user.setCart(cart);
        return user;
    }

    public static Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("PhuocNQ");
        item.setPrice(BigDecimal.valueOf(10.0));
        item.setDescription("PhuocNQ");

        return item;
    }

    public static UserOrder getUserOrder() {
        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        User user = getUserWithCart();
        userOrder.setUser(user);
        userOrder.setItems(new ArrayList<>());
        userOrder.setTotal(BigDecimal.valueOf(10.0));
        return userOrder;
    }
}
