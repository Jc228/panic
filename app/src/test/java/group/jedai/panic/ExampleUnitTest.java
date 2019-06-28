package group.jedai.panic;

import android.util.Log;
import android.widget.Toast;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import group.jedai.panic.dto.EstadoAlertaList;
import group.jedai.panic.srv.EstadoAlertaSrv;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

//        assertEquals(4, 2 + 2);
        System.out.println(getEstadoss());
    }

    public List<String> getEstadoss() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
         List<String> lista = new ArrayList<>();
        EstadoAlertaSrv estadoAlertaSrv = retrofit.create(EstadoAlertaSrv.class);
        Call<EstadoAlertaList> estadoAlertaListCall = estadoAlertaSrv.findAllEstado();
        estadoAlertaListCall.enqueue(new Callback<EstadoAlertaList>() {
            @Override
            public void onResponse(Call<EstadoAlertaList> call, Response<EstadoAlertaList> response) {
                if (response.isSuccessful()){
                    System.out.println("ENTRO");
                }
            }

            @Override
            public void onFailure(Call<EstadoAlertaList> call, Throwable t) {

            }
        });


        return lista;
    }
}