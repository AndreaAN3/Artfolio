
package com.artfolio;

public class Artista {

    private int id;
    private String nombre;
    private String nombreArtistico;
    private String biografia;
    private String especialidad;
    private String fotoPerfil;
    private String instagram;
    private String sitioWeb;

    public Artista() {
    }

    public Artista(int id, String nombre,
            String nombreArtistico, String biografia,
            String especialidad, String fotoPerfil,
            String instagram, String sitioWeb) {

        this.id = id;
        this.nombre = nombre;
        this.nombreArtistico = nombreArtistico;
        this.biografia = biografia;
        this.especialidad = especialidad;
        this.fotoPerfil = fotoPerfil;
        this.instagram = instagram;
        this.sitioWeb = sitioWeb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreArtistico() {
        return nombreArtistico;
    }

    public void setNombreArtistico(String nombreArtistico) {
        this.nombreArtistico = nombreArtistico;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }
}
