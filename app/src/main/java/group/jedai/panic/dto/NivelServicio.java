package group.jedai.panic.dto;

public class NivelServicio {

    private String id;
    private String idA;//id alerta
    private String m; //motivo de alerta
    private String na; //nivel de atencion


    public NivelServicio() {
    }

    public NivelServicio(String idA, String m, String na) {
        this.idA = idA;
        this.m = m;
        this.na = na;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdA() {
        return idA;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getNa() {
        return na;
    }

    public void setNa(String na) {
        this.na = na;
    }
}
