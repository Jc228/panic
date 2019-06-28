package group.jedai.panic.srv;


import java.util.List;

import group.jedai.panic.dto.EstadoAlerta;
import group.jedai.panic.dto.MotivoAlerta;
import group.jedai.panic.dto.MotivoAlertaList;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MotivoAlertaSrv {
    @GET("motivoalerta")
    Call<MotivoAlertaList> findAllMotivo();
}
