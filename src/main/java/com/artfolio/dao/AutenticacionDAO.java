
package com.artfolio.dao;

import com.artfolio.config.ConexionDB;
import com.artfolio.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Locale;

public class AutenticacionDAO {

    public boolean correoExiste(String correo)
            throws SQLException {

        String sql =
                "SELECT id FROM artistas WHERE correo = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, normalizarCorreo(correo));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int registrarArtista(
            String nombre,
            String nombreArtistico,
            String correo,
            String password)
            throws SQLException {

        if (nombre == null || nombre.isBlank()
                || nombreArtistico == null
                || nombreArtistico.isBlank()
                || correo == null
                || correo.isBlank()
                || password == null
                || password.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios"
            );
        }

        String correoNormalizado =
                normalizarCorreo(correo);

        if (nombre.trim().length() > 100
                || nombreArtistico.trim().length() > 100
                || correoNormalizado.length() > 255
                || !correoNormalizado.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
                )
                || password.length() < 12
                || password.length() > 128) {

            throw new IllegalArgumentException(
                    "Los datos del registro no son válidos"
            );
        }

        String hash =
                PasswordUtil.hashPassword(password);

        String sql =
                "INSERT INTO artistas "
                + "(nombre, nombre_artistico, correo, "
                + "password_hash) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            ps.setString(1, nombre.trim());
            ps.setString(2, nombreArtistico.trim());
            ps.setString(3, correoNormalizado);
            ps.setString(4, hash);

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "No se pudo registrar el artista"
                );
            }

            try (ResultSet keys =
                         ps.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new SQLException(
                    "No se obtuvo el ID del artista"
            );
        }
    }

    public Integer iniciarSesion(
            String correo,
            String password)
            throws SQLException {

        if (correo == null || password == null) {
            return null;
        }

        String sql =
                "SELECT id, password_hash "
                + "FROM artistas "
                + "WHERE correo = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, normalizarCorreo(correo));

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                int artistaId = rs.getInt("id");

                String hash =
                        rs.getString("password_hash");

                if (PasswordUtil.verifyPassword(
                        password,
                        hash)) {

                    return artistaId;
                }

                return null;
            }
        }
    }

    private String normalizarCorreo(String correo) {

        if (correo == null) {
            return "";
        }

        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
