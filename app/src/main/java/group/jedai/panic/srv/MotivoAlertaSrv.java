package group.jedai.panic.srv;


import java.util.List;

import group.jedai.panic.dto.MotivoAlerta;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MotivoAlertaSrv {
    @GET("motivoalerta")
    Call<List<MotivoAlerta>> findAllMotivo();
}
