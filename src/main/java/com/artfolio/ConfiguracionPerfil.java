
package com.artfolio;

import java.time.LocalDateTime;

public class ConfiguracionPerfil {

    private int id;
    private int artistaId;

    private String avatar = "☀️";
    private String colorPrincipal = "#e8ddff";
    private String colorEnlaces = "#e8ddff";
    private String mensajeBienvenida =
            "♡ WELCOME TO MY WORLD";

    private LocalDateTime fechaActualizacion;

    public ConfiguracionPerfil() {
    }

    public ConfiguracionPerfil(int artistaId) {
        this.artistaId = artistaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArtistaId() {
        return artistaId;
    }

    public void setArtistaId(int artistaId) {
        this.artistaId = artistaId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getColorPrincipal() {
        return colorPrincipal;
    }

    public void setColorPrincipal(String colorPrincipal) {
        this.colorPrincipal = colorPrincipal;
    }

    public String getColorEnlaces() {
        return colorEnlaces;
    }

    public void setColorEnlaces(String colorEnlaces) {
        this.colorEnlaces = colorEnlaces;
    }

    public String getMensajeBienvenida() {
        return mensajeBienvenida;
    }

    public void setMensajeBienvenida(
            String mensajeBienvenida) {

        this.mensajeBienvenida = mensajeBienvenida;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion) {

        this.fechaActualizacion = fechaActualizacion;
    }
}
