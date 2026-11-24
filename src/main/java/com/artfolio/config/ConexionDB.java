
package com.artfolio.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/artfoliio_db"
            + "?serverTimezone=UTC&sslMode=DISABLED";

    private static final String USUARIO = "root";

    private static final String PASSWORD =
            System.getenv("ARTFOLIO_DB_PASSWORD");

    public static Connection conectar() throws SQLException {

        System.out.println(
            "ARTFOLIO - Variable de contraseña disponible: "
            + (PASSWORD != null && !PASSWORD.isBlank())
        );

        if (PASSWORD == null || PASSWORD.isBlank()) {

            throw new SQLException(
                "Falta configurar ARTFOLIO_DB_PASSWORD"
            );
        }

        // Cargar explicitamente el controlador MySQL

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println(
                "ARTFOLIO - Driver MySQL cargado correctamente"
            );

        } catch (ClassNotFoundException ex) {

            throw new SQLException(
                "No se encontro el controlador JDBC de MySQL",
                ex
            );
        }

        // Establecer conexion

        Connection conexion = DriverManager.getConnection(
                URL,
                USUARIO,
                PASSWORD
        );

        System.out.println(
            "ARTFOLIO - Conexion exitosa con MySQL"
        );

        return conexion;
    }
}
