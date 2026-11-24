
package com.artfolio.dao;

import com.artfolio.Artista;
import java.sql.SQLException;

public class PruebaArtistaDAO {

    public static void main(String[] args) {

        Artista artista = new Artista();

        artista.setNombre("a sunny");
        artista.setNombreArtistico("a sunny");

        artista.setBiografia(
            "Artista multidisciplinaria. " +
            "Exploro mundos creativos a través del arte."
        );

        artista.setEspecialidad(
            "Ilustración, Fotografía, Diseño digital, " +
            "Animación y Diseño gráfico"
        );

        artista.setFotoPerfil(null);
        artista.setInstagram(null);
        artista.setSitioWeb(null);

        try {

            ArtistaDAO dao = new ArtistaDAO();

            int id = dao.guardar(artista);

            System.out.println(
                "ARTISTA GUARDADO CORRECTAMENTE"
            );

            System.out.println("ID: " + id);

            System.out.println(
                "Nombre artístico: "
                + artista.getNombreArtistico()
            );

        } catch (SQLException e) {

            System.err.println(
                "ERROR AL GUARDAR ARTISTA: "
                + e.getMessage()
            );

        }
    }
}
