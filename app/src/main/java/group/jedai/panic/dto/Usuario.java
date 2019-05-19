package group.jedai.panic.dto;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.io.Serializable;


public class Usuario implements Serializable {

    private String id;
    private String nombre;
    private String mail;
    private String password;
    private String claveAcceso;
    private String tipo;
    private String sexo;
    private boolean activo;
    private String idFacultad;

    private boolean asig;
    private Notificacion noti;
    private LocalDateTime time;

    private DateTime fcCre;
    private DateTime fcMod;

    private DateTime fcIntFal;
    private DateTime fcCamCla;
    private Integer intFal;
    private Long contSe = 0L;
    private DateTime fcLastLogin;
    private DateTime fcLastLoginOld;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getIdFacultad() {
        return idFacultad;
    }

    public void setIdFacultad(String idFacultad) {
        this.idFacultad = idFacultad;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isAsig() {
        return asig;
    }

    public void setAsig(boolean asig) {
        this.asig = asig;
    }

    public Notificacion getNoti() {
        return noti;
    }

    public void setNoti(Notificacion noti) {
        this.noti = noti;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public DateTime getFcCre() {
        return fcCre;
    }

    public void setFcCre(DateTime fcCre) {
        this.fcCre = fcCre;
    }

    public DateTime getFcMod() {
        return fcMod;
    }

    public void setFcMod(DateTime fcMod) {
        this.fcMod = fcMod;
    }

    public DateTime getFcIntFal() {
        return fcIntFal;
    }

    public void setFcIntFal(DateTime fcIntFal) {
        this.fcIntFal = fcIntFal;
    }

    public DateTime getFcCamCla() {
        return fcCamCla;
    }

    public void setFcCamCla(DateTime fcCamCla) {
        this.fcCamCla = fcCamCla;
    }

    public Integer getIntFal() {
        return intFal;
    }

    public void setIntFal(Integer intFal) {
        this.intFal = intFal;
    }

    public Long getContSe() {
        return contSe;
    }

    public void setContSe(Long contSe) {
        this.contSe = contSe;
    }

    public DateTime getFcLastLogin() {
        return fcLastLogin;
    }

    public void setFcLastLogin(DateTime fcLastLogin) {
        this.fcLastLogin = fcLastLogin;
    }

    public DateTime getFcLastLoginOld() {
        return fcLastLoginOld;
    }

    public void setFcLastLoginOld(DateTime fcLastLoginOld) {
        this.fcLastLoginOld = fcLastLoginOld;
    }

    public Usuario(String nombre, String mail, String password, String claveAcceso, String tipo, String sexo, boolean activo, String idFacultad) {
        this.nombre = nombre;
        this.mail = mail;
        this.password = password;
        this.claveAcceso = claveAcceso;
        this.tipo = tipo;
        this.sexo = sexo;
        this.activo = activo;
        this.idFacultad = idFacultad;
    }

    public Usuario() {
    }
}
