package com.techie.microservices.accountservice.service;

import org.flywaydb.core.Flyway;

public class FlywayRepair {

    public static void main(String[] args) {

        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:mysql://localhost:3306/banksphere_account",
                        "root",
                        "root"
                )
                .load();

        flyway.repair();

        System.out.println("Flyway repair completed");
    }
}