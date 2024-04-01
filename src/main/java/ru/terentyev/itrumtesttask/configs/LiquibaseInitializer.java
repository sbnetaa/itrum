package ru.terentyev.itrumtesttask.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

@Component
public class LiquibaseInitializer {

    private final SpringLiquibase liquibase;

    @Autowired
    public LiquibaseInitializer(SpringLiquibase liquibase) {
        this.liquibase = liquibase;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(10000); // Подождать 10 секунд перед инициализацией Liquibase
                liquibase.afterPropertiesSet();
            } catch (InterruptedException | LiquibaseException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
