
package com.artfolio.resources;

import com.artfolio.Obra;
import com.artfolio.dao.ObraDAO;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("obras")
@Produces(MediaType.APPLICATION_JSON)
public class ObrasResource {

    private static final Logger LOGGER =
            Logger.getLogger(
                    ObrasResource.class.getName()
            );

    private final ObraDAO obraDAO =
            new ObraDAO();

    @GET
    public Response listarObras(
            @QueryParam("artistaId") Integer artistaId
    ) {

        try {

            List<Obra> obras;

            // Consultar todas las obras publicadas

            if (artistaId == null) {

                obras = obraDAO.listarPublicadas();

            } else {

                // Validar identificador del artista

                if (artistaId <= 0) {

                    return Response
                            .status(
                                Response.Status.BAD_REQUEST
                            )
                            .entity(
                                Map.of(
                                    "error",
                                    "El identificador del artista no es válido."
                                )
                            )
                            .build();
                }

                // Consultar únicamente las obras
                // publicadas del artista seleccionado

                obras = obraDAO
                        .listarPorArtista(artistaId)
                        .stream()
                        .filter(Obra::isPublicada)
                        .toList();
            }

            return Response
                    .ok(obras)
                    .header(
                        "Cache-Control",
                        "no-store"
                    )
                    .build();

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Error al consultar las obras públicas.",
                    e
            );

            return Response
                    .serverError()
                    .entity(
                        Map.of(
                            "error",
                            "No se pudieron consultar las obras."
                        )
                    )
                    .build();
        }
    }
}
