
package com.artfolio.dao;

import com.artfolio.Obra;
import com.artfolio.config.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class PruebaObraDAO {

    public static void main(String[] args) {

        System.out.println("PRUEBA DE OBRAS - ARTFOLIO");

        try {
            int artistaId = 0;

            String buscarArtista = """
                SELECT id FROM artistas
                WHERE nombre_artistico = ?
                ORDER BY id LIMIT 1
                """;

            try (Connection conexion = ConexionDB.conectar();
                 PreparedStatement ps =
                     conexion.prepareStatement(buscarArtista)) {

                ps.setString(1, "a sunny");

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        artistaId = rs.getInt("id");
                    }
                }
            }

            if (artistaId == 0) {
                System.out.println(
                    "No se encontro el perfil a sunny."
                );
                return;
            }

            Obra obra = new Obra();

            obra.setArtistaId(artistaId);
            obra.setTitulo("Mi primera ilustración");
            obra.setDescripcion(
                "Primera obra de prueba publicada en Artfolio."
            );
            obra.setCategoria("Ilustración");
            obra.setArchivoUrl(null);
            obra.setImagenPortada(null);
            obra.setFechaPublicacion(LocalDateTime.now());
            obra.setPublicada(false);

            ObraDAO dao = new ObraDAO();
            int id = dao.guardar(obra);

            System.out.println("OBRA GUARDADA CORRECTAMENTE");
            System.out.println("ID de obra: " + id);
            System.out.println("Artista ID: " + artistaId);
            System.out.println("Título: " + obra.getTitulo());

        } catch (Exception e) {
            System.err.println(
                "ERROR AL GUARDAR OBRA: " + e.getMessage()
            );
        }
    }
}
