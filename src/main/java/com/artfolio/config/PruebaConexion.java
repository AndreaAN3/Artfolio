
package com.artfolio.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConexion {

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("PRUEBA DE CONEXION - ARTFOLIO");
        System.out.println("================================");

        try (Connection conexion = ConexionDB.conectar()) {

            System.out.println("CONEXION EXITOSA CON MYSQL");

            DatabaseMetaData datos = conexion.getMetaData();

            System.out.println(
                "Servidor: " + datos.getDatabaseProductName()
            );

            System.out.println(
                "Version: " + datos.getDatabaseProductVersion()
            );

            try (Statement consulta = conexion.createStatement();
                 ResultSet resultado = consulta.executeQuery(
                     "SELECT DATABASE()")) {

                if (resultado.next()) {
                    System.out.println(
                        "Base de datos: " + resultado.getString(1)
                    );
                }
            }

        } catch (Exception e) {

            System.out.println("ERROR AL CONECTAR ARTFOLIO");
            System.out.println("Tipo: " + e.getClass().getName());
            System.out.println("Detalle: " + e.getMessage());

        }
    }
}
