
package com.artfolio;

import java.time.LocalDateTime;

public class Enlace {

    private int id;
    private int artistaId;
    private String titulo;
    private String url;
    private String icono;
    private int orden;
    private LocalDateTime fechaCreacion;

    public Enlace() {
    }

    public Enlace(int id, int artistaId,
            String titulo, String url,
            String icono, int orden,
            LocalDateTime fechaCreacion) {

        this.id = id;
        this.artistaId = artistaId;
        this.titulo = titulo;
        this.url = url;
        this.icono = icono;
        this.orden = orden;
        this.fechaCreacion = fechaCreacion;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            LocalDateTime fechaCreacion) {

        this.fechaCreacion = fechaCreacion;
    }
}
