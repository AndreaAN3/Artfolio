
package com.artfolio.servlet;

import com.artfolio.Artista;
import com.artfolio.ConfiguracionPerfil;

import com.artfolio.dao.ArtistaDAO;
import com.artfolio.dao.ConfiguracionPerfilDAO;

import jakarta.json.Json;
import jakarta.json.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/publico/perfil")
public class PerfilPublicoServlet extends HttpServlet {

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

            // Obtener identificador del artista

            String idParametro =
                    request.getParameter("id");

            if (
                    idParametro == null ||
                    idParametro.isBlank()
            ) {

                responderError(
                        response,
                        400,
                        "Debes indicar el identificador del artista."
                );

                return;
            }

            int artistaId =
                    Integer.parseInt(idParametro);

            if (artistaId <= 0) {

                responderError(
                        response,
                        400,
                        "El identificador del artista no es válido."
                );

                return;
            }

            // Consultar artista

            Artista artista =
                    artistaDAO.buscarPorId(artistaId);

            if (artista == null) {

                responderError(
                        response,
                        404,
                        "El artista no existe."
                );

                return;
            }

            // Consultar personalización

            ConfiguracionPerfil configuracion =
                    configuracionDAO.obtenerPorArtista(
                            artistaId
                    );

            // Construir respuesta pública

            JsonObject resultado =
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
                        "instagram",
                        texto(
                            artista.getInstagram()
                        )
                    )

                    .add(
                        "sitioWeb",
                        texto(
                            artista.getSitioWeb()
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

                    .add(
                        "colorEnlaces",
                        texto(
                            configuracion.getColorEnlaces()
                        )
                    )

                    .add(
                        "mensajeBienvenida",
                        texto(
                            configuracion.getMensajeBienvenida()
                        )
                    )

                    .build();

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter().write(
                    resultado.toString()
            );

        } catch (NumberFormatException e) {

            responderError(
                    response,
                    400,
                    "El identificador debe ser un número válido."
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al consultar perfil público.",
                    e
            );

            responderError(
                    response,
                    500,
                    "No se pudo consultar el perfil."
            );
        }
    }

    private String texto(String valor) {

        return valor == null ? "" : valor;
    }

    private void responderError(
            HttpServletResponse response,
            int codigo,
            String mensaje
    ) throws IOException {

        response.setStatus(codigo);

        response.getWriter().write(

                Json.createObjectBuilder()

                    .add(
                        "error",
                        mensaje
                    )

                    .build()

                    .toString()
        );
    }
}
