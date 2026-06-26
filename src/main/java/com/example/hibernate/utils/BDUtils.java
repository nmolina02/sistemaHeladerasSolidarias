package com.example.hibernate.utils;

import config.EnvConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class BDUtils {
    private static final EntityManagerFactory emf = crearEntityManagerFactory();

    private static EntityManagerFactory crearEntityManagerFactory() {
        Map<String, String> props = new HashMap<>();
        props.put("hibernate.connection.url", EnvConfig.get("DB_URL"));
        props.put("hibernate.connection.username", EnvConfig.get("DB_USERNAME"));
        props.put("hibernate.connection.password", EnvConfig.get("DB_PASSWORD"));
        return Persistence.createEntityManagerFactory("demo-hibernate-PU", props);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void comenzarTransaccion(EntityManager em) {
        em.getTransaction().begin();
    }

    public static void commit(EntityManager em) {
        em.getTransaction().commit();
    }

    public static void close() {
        emf.close();
    }
}