package group.jedai.panic.srv;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FotoSrv {
    @Multipart
//    @Headers("Content-Type:multipart/form-data;charset=UTF-8")
    @POST("usuario/foto")
    Call<ResponseBody> enviarFoto(@Part MultipartBody.Part file, @Part("id") RequestBody id);
}
