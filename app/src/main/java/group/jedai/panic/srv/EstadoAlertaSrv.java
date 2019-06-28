package group.jedai.panic.srv;


import java.util.List;

import group.jedai.panic.dto.Alerta;
import group.jedai.panic.dto.EstadoAlerta;
import group.jedai.panic.dto.EstadoAlertaList;
import group.jedai.panic.dto.NivelServicio;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface EstadoAlertaSrv {
    @GET("estadoalerta")
    Call<EstadoAlertaList> findAllEstado();
}
