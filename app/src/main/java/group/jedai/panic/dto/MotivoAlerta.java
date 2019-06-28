package group.jedai.panic.dto;

import java.io.Serializable;

public class MotivoAlerta implements Serializable {
    private String id;
    private String nm;

    public MotivoAlerta() {
    }

    public MotivoAlerta(String id, String nm) {
        this.id = id;
        this.nm = nm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }
}
