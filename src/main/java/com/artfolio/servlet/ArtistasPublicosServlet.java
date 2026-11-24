
package com.artfolio.servlet;

import com.artfolio.Artista;
import com.artfolio.ConfiguracionPerfil;

import com.artfolio.dao.ArtistaDAO;
import com.artfolio.dao.ConfiguracionPerfilDAO;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/publico/artistas")
public class ArtistasPublicosServlet extends HttpServlet {

    private final ArtistaDAO artistaDAO =
            new ArtistaDAO();

    private final ConfiguracionPerfilDAO configuracionDAO =
            new ConfiguracionPerfilDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setCharacterEncoding("UTF-8");

        response.setContentType(
                "application/json;charset=UTF-8"
        );

        response.setHeader(
                "Cache-Control",
                "no-store"
        );

        try {

            List<Artista> artistas =
                    artistaDAO.listarArtistas();

            JsonArrayBuilder resultado =
                    Json.createArrayBuilder();

            for (Artista artista : artistas) {

                ConfiguracionPerfil configuracion =
                        configuracionDAO.obtenerPorArtista(
                                artista.getId()
                        );

                JsonObject artistaJson =
                        Json.createObjectBuilder()

                        .add(
                            "id",
                            artista.getId()
                        )

                        .add(
                            "nombreArtistico",
                            texto(
                                artista.getNombreArtistico()
                            )
                        )

                        .add(
                            "biografia",
                            texto(
                                artista.getBiografia()
                            )
                        )

                        .add(
                            "especialidad",
                            texto(
                                artista.getEspecialidad()
                            )
                        )

                        .add(
                            "fotoPerfil",
                            texto(
                                artista.getFotoPerfil()
                            )
                        )

                        .add(
                            "avatar",
                            texto(
                                configuracion.getAvatar()
                            )
                        )

                        .add(
                            "colorPrincipal",
                            texto(
                                configuracion.getColorPrincipal()
                            )
                        )

                        .build();

                resultado.add(artistaJson);
            }

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter().write(
                    resultado.build().toString()
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al consultar artistas públicos.",
                    e
            );

            response.setStatus(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(

                    Json.createObjectBuilder()

                        .add(
                            "error",
                            "No se pudieron consultar los artistas."
                        )

                        .build()

                        .toString()
            );
        }
    }

    private String texto(String valor) {

        return valor == null ? "" : valor;
    }
}
