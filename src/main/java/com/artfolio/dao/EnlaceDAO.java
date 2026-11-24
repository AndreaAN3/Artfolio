
package com.artfolio.dao;

import com.artfolio.Enlace;
import com.artfolio.config.ConexionDB;

import java.net.URI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class EnlaceDAO {

    // ==========================================
    // GUARDAR ENLACE
    // ==========================================

    public int guardar(Enlace enlace)
            throws SQLException {

        validarEnlace(enlace);

        String sql = """
            INSERT INTO enlaces
            (artista_id, titulo, url, icono, orden)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
                 )) {

            ps.setInt(1, enlace.getArtistaId());
            ps.setString(2, enlace.getTitulo());
            ps.setString(3, enlace.getUrl());
            ps.setString(4, enlace.getIcono());
            ps.setInt(5, enlace.getOrden());

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                    "No se pudo guardar el enlace."
                );
            }

            try (ResultSet claves =
                     ps.getGeneratedKeys()) {

                if (claves.next()) {

                    int id = claves.getInt(1);

                    enlace.setId(id);

                    return id;
                }
            }

            throw new SQLException(
                "No se obtuvo el ID del enlace."
            );
        }
    }

    // ==========================================
    // LISTAR ENLACES POR ARTISTA
    // ==========================================

    public List<Enlace> listarPorArtista(
            int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        List<Enlace> enlaces = new ArrayList<>();

        String sql = """
            SELECT id, artista_id, titulo,
                   url, icono, orden, fecha_creacion
            FROM enlaces
            WHERE artista_id = ?
            ORDER BY orden ASC, id ASC
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, artistaId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    enlaces.add(mapearEnlace(rs));
                }
            }
        }

        return enlaces;
    }

    // ==========================================
    // BUSCAR ENLACE
    // ==========================================

    public Enlace buscarPorIdYArtista(
            int enlaceId,
            int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        String sql = """
            SELECT id, artista_id, titulo,
                   url, icono, orden, fecha_creacion
            FROM enlaces
            WHERE id = ? AND artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, enlaceId);
            ps.setInt(2, artistaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearEnlace(rs);
                }
            }
        }

        return null;
    }

    // ==========================================
    // ACTUALIZAR ENLACE
    // ==========================================

    public boolean actualizar(Enlace enlace)
            throws SQLException {

        validarEnlace(enlace);

        if (enlace.getId() <= 0) {
            throw new IllegalArgumentException(
                "El ID del enlace no es válido."
            );
        }

        String sql = """
            UPDATE enlaces
            SET titulo = ?,
                url = ?,
                icono = ?,
                orden = ?
            WHERE id = ? AND artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setString(1, enlace.getTitulo());
            ps.setString(2, enlace.getUrl());
            ps.setString(3, enlace.getIcono());
            ps.setInt(4, enlace.getOrden());

            ps.setInt(5, enlace.getId());
            ps.setInt(6, enlace.getArtistaId());

            return ps.executeUpdate() == 1;
        }
    }

    // ==========================================
    // ACTUALIZAR ORDEN
    // ==========================================

    public boolean actualizarOrden(
            int enlaceId,
            int artistaId,
            int orden)
            throws SQLException {

        validarArtistaId(artistaId);

        if (orden < 0) {
            throw new IllegalArgumentException(
                "El orden no puede ser negativo."
            );
        }

        String sql = """
            UPDATE enlaces
            SET orden = ?
            WHERE id = ? AND artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, orden);
            ps.setInt(2, enlaceId);
            ps.setInt(3, artistaId);

            return ps.executeUpdate() == 1;
        }
    }

    // ==========================================
    // ELIMINAR ENLACE
    // ==========================================

    public boolean eliminar(
            int enlaceId,
            int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        String sql = """
            DELETE FROM enlaces
            WHERE id = ? AND artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, enlaceId);
            ps.setInt(2, artistaId);

            return ps.executeUpdate() == 1;
        }
    }

    // ==========================================
    // MAPEAR RESULTADOS
    // ==========================================

    private Enlace mapearEnlace(ResultSet rs)
            throws SQLException {

        Enlace enlace = new Enlace();

        enlace.setId(rs.getInt("id"));

        enlace.setArtistaId(
            rs.getInt("artista_id")
        );

        enlace.setTitulo(
            rs.getString("titulo")
        );

        enlace.setUrl(
            rs.getString("url")
        );

        enlace.setIcono(
            rs.getString("icono")
        );

        enlace.setOrden(
            rs.getInt("orden")
        );

        Timestamp fecha =
            rs.getTimestamp("fecha_creacion");

        if (fecha != null) {
            enlace.setFechaCreacion(
                fecha.toLocalDateTime()
            );
        }

        return enlace;
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

    private void validarEnlace(Enlace enlace) {

        if (enlace == null) {
            throw new IllegalArgumentException(
                "El enlace es obligatorio."
            );
        }

        validarArtistaId(enlace.getArtistaId());

        if (enlace.getTitulo() == null
                || enlace.getTitulo().isBlank()
                || enlace.getTitulo().trim().length() > 70) {

            throw new IllegalArgumentException(
                "El título del enlace no es válido."
            );
        }

        if (enlace.getUrl() == null
                || enlace.getUrl().isBlank()
                || enlace.getUrl().trim().length() > 2000) {

            throw new IllegalArgumentException(
                "La URL no es válida."
            );
        }

        enlace.setTitulo(
            enlace.getTitulo().trim()
        );

        enlace.setUrl(
            enlace.getUrl().trim()
        );

        try {

            URI uri = URI.create(enlace.getUrl());

            String esquema = uri.getScheme();

            if (esquema == null
                    || (!esquema.equalsIgnoreCase("https")
                    && !esquema.equalsIgnoreCase("http"))
                    || uri.getHost() == null
                    || uri.getUserInfo() != null) {

                throw new IllegalArgumentException(
                    "Solo se permiten enlaces HTTP o HTTPS."
                );
            }

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                "Introduce una URL HTTP o HTTPS válida."
            );
        }

        if (enlace.getIcono() == null
                || enlace.getIcono().isBlank()) {

            enlace.setIcono("🌷");
        }

        if (enlace.getIcono().length() > 20) {

            throw new IllegalArgumentException(
                "El icono es demasiado largo."
            );
        }

        if (enlace.getOrden() < 0) {

            throw new IllegalArgumentException(
                "El orden no puede ser negativo."
            );
        }
    }
}
