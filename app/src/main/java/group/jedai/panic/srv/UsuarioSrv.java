package group.jedai.panic.srv;

import java.util.List;

import group.jedai.panic.dto.Usuario;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsuarioSrv {
    @GET("usuario")
    Call<List<Usuario>> findAllUsers();

    @GET("usuarioVerificar/{mail}")
    Call<Usuario> findUserByMail(@Path("mail") String mail);

    @GET("usuario/activar/{m}/{c}")
    Call<Usuario> findUserByMailAndCod(@Path("m") String mail, @Path("c") String codigo);

    @GET("usuario/{mail}/{password}")
    Call<Usuario> findUserByLogin(@Path("mail") String mail, @Path("password") String password);

    @POST("usuario")
    Call<Usuario> addUser(@Body Usuario usuario);

    @GET("usuario/reenviar/{mail}")
    Call<Usuario> reenviarCod(@Path("mail") String mail);

    @GET("usuario/resetPass/{mail}")
    Call<Usuario> resetPass(@Path("mail") String mail);

    @POST("usuario/cambiarPass")
    Call<ResponseBody> cambiarPass(@Body List<String> user);
}
