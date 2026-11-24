
package com.artfolio.dao;

import com.artfolio.Obra;
import com.artfolio.config.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class ObraDAO {

    // ==========================================
    // GUARDAR OBRA
    // ==========================================

    public int guardar(Obra obra) throws SQLException {

        if (obra == null || obra.getArtistaId() <= 0) {
            throw new IllegalArgumentException(
                "La obra debe tener un artista válido."
            );
        }

        String sql = """
            INSERT INTO obras
            (artista_id, titulo, descripcion,
             categoria, archivo_url, imagen_portada,
             fecha_publicacion, publicada)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
                 )) {

            ps.setInt(1, obra.getArtistaId());
            ps.setString(2, obra.getTitulo());
            ps.setString(3, obra.getDescripcion());
            ps.setString(4, obra.getCategoria());
            ps.setString(5, obra.getArchivoUrl());
            ps.setString(6, obra.getImagenPortada());

            LocalDateTime fecha =
                obra.getFechaPublicacion() != null
                    ? obra.getFechaPublicacion()
                    : LocalDateTime.now();

            ps.setTimestamp(
                7,
                Timestamp.valueOf(fecha)
            );

            ps.setBoolean(8, obra.isPublicada());

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                    "No se pudo guardar la obra."
                );
            }

            try (ResultSet claves = ps.getGeneratedKeys()) {

                if (claves.next()) {
                    int id = claves.getInt(1);
                    obra.setId(id);
                    return id;
                }
            }

            throw new SQLException(
                "No se obtuvo el ID de la obra."
            );
        }
    }

    // ==========================================
    // LISTAR OBRAS PUBLICADAS
    // ==========================================

    public List<Obra> listarPublicadas()
            throws SQLException {

        List<Obra> obras = new ArrayList<>();

        String sql = """
            SELECT id, artista_id, titulo, descripcion,
                   categoria, archivo_url, imagen_portada,
                   fecha_publicacion, publicada
            FROM obras
            WHERE publicada = TRUE
            ORDER BY fecha_publicacion DESC, id DESC
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                obras.add(mapearObra(rs));
            }
        }

        return obras;
    }

    // ==========================================
    // LISTAR OBRAS DE UN ARTISTA
    // ==========================================

    public List<Obra> listarPorArtista(int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        List<Obra> obras = new ArrayList<>();

        String sql = """
            SELECT id, artista_id, titulo, descripcion,
                   categoria, archivo_url, imagen_portada,
                   fecha_publicacion, publicada
            FROM obras
            WHERE artista_id = ?
            ORDER BY fecha_publicacion DESC, id DESC
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, artistaId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    obras.add(mapearObra(rs));
                }
            }
        }

        return obras;
    }

    // ==========================================
    // BUSCAR OBRA DEL ARTISTA
    // ==========================================

    public Obra buscarPorIdYArtista(
            int obraId,
            int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        String sql = """
            SELECT id, artista_id, titulo, descripcion,
                   categoria, archivo_url, imagen_portada,
                   fecha_publicacion, publicada
            FROM obras
            WHERE id = ? AND artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, obraId);
            ps.setInt(2, artistaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearObra(rs);
                }
            }
        }

        return null;
    }

    // ==========================================
    // ACTUALIZAR OBRA
    // ==========================================

    public boolean actualizar(Obra obra)
            throws SQLException {

        if (obra == null ||
            obra.getId() <= 0 ||
            obra.getArtistaId() <= 0) {

            throw new IllegalArgumentException(
                "La obra y el artista deben ser válidos."
            );
        }

        String sql = """
            UPDATE obras
            SET titulo = ?,
                descripcion = ?,
                categoria = ?,
                archivo_url = ?,
                imagen_portada = ?,
                publicada = ?
            WHERE id = ? AND artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setString(1, obra.getTitulo());
            ps.setString(2, obra.getDescripcion());
            ps.setString(3, obra.getCategoria());
            ps.setString(4, obra.getArchivoUrl());
            ps.setString(5, obra.getImagenPortada());
            ps.setBoolean(6, obra.isPublicada());

            ps.setInt(7, obra.getId());
            ps.setInt(8, obra.getArtistaId());

            return ps.executeUpdate() == 1;
        }
    }

    // ==========================================
    // ELIMINAR OBRA
    // ==========================================

    public boolean eliminar(
            int obraId,
            int artistaId)
            throws SQLException {

        validarArtistaId(artistaId);

        String sql = """
            DELETE FROM obras
            WHERE id = ? AND artista_id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, obraId);
            ps.setInt(2, artistaId);

            return ps.executeUpdate() == 1;
        }
    }

    // ==========================================
    // MAPEAR RESULTADO DE MYSQL
    // ==========================================

    private Obra mapearObra(ResultSet rs)
            throws SQLException {

        Obra obra = new Obra();

        obra.setId(rs.getInt("id"));
        obra.setArtistaId(rs.getInt("artista_id"));

        obra.setTitulo(
            rs.getString("titulo")
        );

        obra.setDescripcion(
            rs.getString("descripcion")
        );

        obra.setCategoria(
            rs.getString("categoria")
        );

        obra.setArchivoUrl(
            rs.getString("archivo_url")
        );

        obra.setImagenPortada(
            rs.getString("imagen_portada")
        );

        Timestamp fecha =
            rs.getTimestamp("fecha_publicacion");

        if (fecha != null) {
            obra.setFechaPublicacion(
                fecha.toLocalDateTime()
            );
        }

        obra.setPublicada(
            rs.getBoolean("publicada")
        );

        return obra;
    }

    // ==========================================
    // VALIDAR ID DEL ARTISTA
    // ==========================================

    private void validarArtistaId(int artistaId) {

        if (artistaId <= 0) {
            throw new IllegalArgumentException(
                "El ID del artista no es válido."
            );
        }
    }
}
