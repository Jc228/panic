package group.jedai.panic.srv;


import java.util.List;

import group.jedai.panic.dto.EstadoAlerta;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EstadoAlertaSrv {
    @GET("estadoalerta")
    Call<List<EstadoAlerta>> findAllEstado();
}
