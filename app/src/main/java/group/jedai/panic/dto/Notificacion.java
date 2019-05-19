package group.jedai.panic.dto;



public class Notificacion {

    private String id;
    private String idUsuario;//guardia
    private String fcAlerta;
    private Double latitude;
    private Double longitude;

    public Notificacion() {
    }

    public Notificacion(String idUsuario, String fcAlerta, Double latitud, Double longitud) {
        this.idUsuario = idUsuario;
        this.fcAlerta = fcAlerta;
        this.latitude = latitud;
        this.longitude = longitud;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFcAlerta() {
        return fcAlerta;
    }

    public void setFcAlerta(String fcAlerta) {
        this.fcAlerta = fcAlerta;
    }

    public Double getLatitud() {
        return latitude;
    }

    public void setLatitud(Double latitud) {
        this.latitude = latitud;
    }

    public Double getLongitud() {
        return longitude;
    }

    public void setLongitud(Double longitud) {
        this.longitude = longitud;
    }
}
