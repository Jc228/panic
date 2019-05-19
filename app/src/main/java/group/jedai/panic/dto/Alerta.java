package group.jedai.panic.dto;


public class Alerta {

    private String id;
    private String idUsuario;
    private String fcAlerta;
    private Double latitude;
    private Double longitude;
    private Double latitudeG;
    private Double longitudeG;
    private String idGuardia;
    private String fcInicio;
    private String fcFin;
    private boolean activo;

    public Alerta() {
    }

    public Alerta(String idUsuario, String fcAlerta, Double latitud, Double longitud, Double latitudeG, Double longitudeG, String idGuardia, String fcInicio, String fcFin, boolean activo) {
        this.idUsuario = idUsuario;
        this.fcAlerta = fcAlerta;
        this.latitude = latitud;
        this.longitude = longitud;
        this.idGuardia = idGuardia;
        this.fcInicio = fcInicio;
        this.fcFin = fcFin;
        this.activo = activo;
        this.latitudeG = latitudeG;
        this.longitudeG = longitudeG;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getIdGuardia() {
        return idGuardia;
    }

    public void setIdGuardia(String idGuardia) {
        this.idGuardia = idGuardia;
    }

    public String getFcInicio() {
        return fcInicio;
    }

    public void setFcInicio(String fcInicio) {
        this.fcInicio = fcInicio;
    }

    public String getFcFin() {
        return fcFin;
    }

    public void setFcFin(String fcFin) {
        this.fcFin = fcFin;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Double getLatitudeG() {
        return latitudeG;
    }

    public void setLatitudeG(Double latitudeG) {
        this.latitudeG = latitudeG;
    }

    public Double getLongitudeG() {
        return longitudeG;
    }

    public void setLongitudeG(Double longitudeG) {
        this.longitudeG = longitudeG;
    }
}
