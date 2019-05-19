package group.jedai.panic.srv;

import java.util.List;

import group.jedai.panic.dto.Notificacion;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NotificacionSrv {

    @GET("notificacion")
    Call<List<Notificacion>> findAllNot();

    @POST("notificacion")
    Call<Notificacion> addNotificacion(@Body Notificacion notificacion);

}
