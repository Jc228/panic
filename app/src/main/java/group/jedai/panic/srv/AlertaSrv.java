package group.jedai.panic.srv;


import java.util.List;

import group.jedai.panic.dto.Alerta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AlertaSrv {
    @GET("alerta")
    Call<List<Alerta>> findAllAlerta();

    @POST("alerta")
    Call<Alerta> addAlerta(@Body Alerta alerta);
}
