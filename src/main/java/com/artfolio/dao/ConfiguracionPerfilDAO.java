
package com.artfolio.dao;

import com.artfolio.ConfiguracionPerfil;
import com.artfolio.config.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ConfiguracionPerfilDAO {

    // ==========================================
    // OBTENER CONFIGURACIÓN DEL ARTISTA
    // ==========================================

    public ConfiguracionPerfil obtenerPorArtista(
            int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        String sql = """
            SELECT id, artista_id, avatar,
                   color_principal, color_enlaces,
                   mensaje_bienvenida,
                   fecha_actualizacion
            FROM configuracion_perfil
            WHERE artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, artistaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearConfiguracion(rs);
                }
            }
        }

        // Si todavía no existe una configuración,
        // se devuelve el diseño predeterminado.

        return new ConfiguracionPerfil(artistaId);
    }

    // ==========================================
    // GUARDAR O ACTUALIZAR CONFIGURACIÓN
    // ==========================================

    public void guardar(
            ConfiguracionPerfil configuracion)
            throws SQLException {

        validarConfiguracion(configuracion);

        String sql = """
            INSERT INTO configuracion_perfil
            (artista_id, avatar, color_principal,
             color_enlaces, mensaje_bienvenida)
            VALUES (?, ?, ?, ?, ?)
            AS nueva
            ON DUPLICATE KEY UPDATE
                avatar = nueva.avatar,
                color_principal = nueva.color_principal,
                color_enlaces = nueva.color_enlaces,
                mensaje_bienvenida =
                    nueva.mensaje_bienvenida
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(
                1,
                configuracion.getArtistaId()
            );

            ps.setString(
                2,
                configuracion.getAvatar()
            );

            ps.setString(
                3,
                configuracion.getColorPrincipal()
            );

            ps.setString(
                4,
                configuracion.getColorEnlaces()
            );

            ps.setString(
                5,
                configuracion.getMensajeBienvenida()
            );

            ps.executeUpdate();
        }
    }

    // ==========================================
    // RESTABLECER DISEÑO PREDETERMINADO
    // ==========================================

    public void restablecer(int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        ConfiguracionPerfil configuracion =
                new ConfiguracionPerfil(artistaId);

        guardar(configuracion);
    }

    // ==========================================
    // MAPEAR RESULTADOS DE MYSQL
    // ==========================================

    private ConfiguracionPerfil mapearConfiguracion(
            ResultSet rs)
            throws SQLException {

        ConfiguracionPerfil configuracion =
                new ConfiguracionPerfil();

        configuracion.setId(
            rs.getInt("id")
        );

        configuracion.setArtistaId(
            rs.getInt("artista_id")
        );

        configuracion.setAvatar(
            rs.getString("avatar")
        );

        configuracion.setColorPrincipal(
            rs.getString("color_principal")
        );

        configuracion.setColorEnlaces(
            rs.getString("color_enlaces")
        );

        configuracion.setMensajeBienvenida(
            rs.getString("mensaje_bienvenida")
        );

        Timestamp fecha =
            rs.getTimestamp("fecha_actualizacion");

        if (fecha != null) {

            configuracion.setFechaActualizacion(
                fecha.toLocalDateTime()
            );
        }

        return configuracion;
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarArtistaId(int artistaId) {

        if (artistaId <= 0) {

            throw new IllegalArgumentException(
                "El artista no es válido."
            );
        }
    }

    private void validarConfiguracion(
            ConfiguracionPerfil configuracion) {

        if (configuracion == null) {

            throw new IllegalArgumentException(
                "La configuración es obligatoria."
            );
        }

        validarArtistaId(
            configuracion.getArtistaId()
        );

        if (configuracion.getAvatar() == null
                || configuracion.getAvatar().isBlank()) {

            configuracion.setAvatar("☀️");
        }

        if (configuracion.getAvatar().length() > 20) {

            throw new IllegalArgumentException(
                "El avatar es demasiado largo."
            );
        }

        validarColor(
            configuracion.getColorPrincipal()
        );

        validarColor(
            configuracion.getColorEnlaces()
        );

        if (configuracion.getMensajeBienvenida() == null) {

            configuracion.setMensajeBienvenida(
                "♡ WELCOME TO MY WORLD"
            );
        }

        if (configuracion.getMensajeBienvenida()
                .length() > 255) {

            throw new IllegalArgumentException(
                "El mensaje de bienvenida es demasiado largo."
            );
        }
    }

    private void validarColor(String color) {

        if (color == null
                || !color.matches("^#[0-9a-fA-F]{6}$")) {

            throw new IllegalArgumentException(
                "El color debe tener formato hexadecimal."
            );
        }
    }
}
