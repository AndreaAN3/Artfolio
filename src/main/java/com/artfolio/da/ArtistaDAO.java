
package com.artfolio.dao;

import com.artfolio.Artista;
import com.artfolio.config.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class ArtistaDAO {

    // ==========================================
    // GUARDAR ARTISTA
    // ==========================================

    public int guardar(Artista artista)
            throws SQLException {

        validarArtista(artista);

        String sql = """
            INSERT INTO artistas
            (nombre, nombre_artistico, biografia,
             especialidad, foto_perfil, instagram,
             sitio_web)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
                 )) {

            asignarDatos(ps, artista);

            if (ps.executeUpdate() != 1) {

                throw new SQLException(
                    "No se pudo guardar el artista."
                );
            }

            try (ResultSet claves =
                     ps.getGeneratedKeys()) {

                if (claves.next()) {

                    int id = claves.getInt(1);

                    artista.setId(id);

                    return id;
                }
            }

            throw new SQLException(
                "No se obtuvo el ID del artista."
            );
        }
    }

    // ==========================================
    // BUSCAR ARTISTA POR ID
    // ==========================================

    public Artista buscarPorId(int artistaId)
            throws SQLException {

        validarId(artistaId);

        String sql = """
            SELECT id, nombre, nombre_artistico,
                   biografia, especialidad,
                   foto_perfil, instagram, sitio_web
            FROM artistas
            WHERE id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            ps.setInt(1, artistaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return mapearArtista(rs);
                }
            }
        }

        return null;
    }

    // ==========================================
    // ACTUALIZAR PERFIL ARTISTICO
    // ==========================================

    public boolean actualizar(Artista artista)
            throws SQLException {

        validarArtista(artista);
        validarId(artista.getId());

        String sql = """
            UPDATE artistas
            SET nombre = ?,
                nombre_artistico = ?,
                biografia = ?,
                especialidad = ?,
                foto_perfil = ?,
                instagram = ?,
                sitio_web = ?
            WHERE id = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

            asignarDatos(ps, artista);

            ps.setInt(8, artista.getId());

            return ps.executeUpdate() == 1;
        }
    }

    // ==========================================
    // LISTAR PERFILES ARTISTICOS
    // ==========================================

    public List<Artista> listarArtistas()
            throws SQLException {

        List<Artista> artistas = new ArrayList<>();

        String sql = """
            SELECT id, nombre, nombre_artistico,
                   biografia, especialidad,
                   foto_perfil, instagram, sitio_web
            FROM artistas
            ORDER BY nombre_artistico ASC, id ASC
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                 conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                artistas.add(mapearArtista(rs));
            }
        }

        return artistas;
    }

    // ==========================================
    // ASIGNAR DATOS AL PREPARED STATEMENT
    // ==========================================

    private void asignarDatos(
            PreparedStatement ps,
            Artista artista)
            throws SQLException {

        ps.setString(1, artista.getNombre());
        ps.setString(2, artista.getNombreArtistico());
        ps.setString(3, artista.getBiografia());
        ps.setString(4, artista.getEspecialidad());
        ps.setString(5, artista.getFotoPerfil());
        ps.setString(6, artista.getInstagram());
        ps.setString(7, artista.getSitioWeb());
    }

    // ==========================================
    // MAPEAR RESULTADOS DE MYSQL
    // ==========================================

    private Artista mapearArtista(ResultSet rs)
            throws SQLException {

        Artista artista = new Artista();

        artista.setId(
            rs.getInt("id")
        );

        artista.setNombre(
            rs.getString("nombre")
        );

        artista.setNombreArtistico(
            rs.getString("nombre_artistico")
        );

        artista.setBiografia(
            rs.getString("biografia")
        );

        artista.setEspecialidad(
            rs.getString("especialidad")
        );

        artista.setFotoPerfil(
            rs.getString("foto_perfil")
        );

        artista.setInstagram(
            rs.getString("instagram")
        );

        artista.setSitioWeb(
            rs.getString("sitio_web")
        );

        return artista;
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarId(int artistaId) {

        if (artistaId <= 0) {

            throw new IllegalArgumentException(
                "El ID del artista no es válido."
            );
        }
    }

    private void validarArtista(Artista artista) {

        if (artista == null) {

            throw new IllegalArgumentException(
                "El artista es obligatorio."
            );
        }

        if (artista.getNombre() == null
                || artista.getNombre().isBlank()
                || artista.getNombre().trim().length() > 100) {

            throw new IllegalArgumentException(
                "El nombre no es válido."
            );
        }

        if (artista.getNombreArtistico() == null
                || artista.getNombreArtistico().isBlank()
                || artista.getNombreArtistico()
                          .trim().length() > 100) {

            throw new IllegalArgumentException(
                "El nombre artístico no es válido."
            );
        }

        artista.setNombre(
            artista.getNombre().trim()
        );

        artista.setNombreArtistico(
            artista.getNombreArtistico().trim()
        );

        validarLongitud(
            artista.getBiografia(),
            10000,
            "Biografía"
        );

        validarLongitud(
            artista.getEspecialidad(),
            100,
            "Especialidad"
        );

        validarLongitud(
            artista.getFotoPerfil(),
            500,
            "Fotografía de perfil"
        );

        validarLongitud(
            artista.getInstagram(),
            255,
            "Instagram"
        );

        validarLongitud(
            artista.getSitioWeb(),
            500,
            "Sitio web"
        );
    }

    private void validarLongitud(
            String valor,
            int maximo,
            String campo) {

        if (valor != null
                && valor.length() > maximo) {

            throw new IllegalArgumentException(
                campo + " supera la longitud permitida."
            );
        }
    }
}
