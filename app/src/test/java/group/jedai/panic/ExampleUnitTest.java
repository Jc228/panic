package group.jedai.panic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import group.jedai.panic.dto.EstadoAlerta;
import group.jedai.panic.srv.EstadoAlertaSrv;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        final ArrayList<String> lista = new ArrayList<>();
        int i = 0;
        while (lista.size() == 0) {

            EstadoAlertaSrv estadoAlertaSrv = retrofit.create(EstadoAlertaSrv.class);
            Call<List<EstadoAlerta>> estadoAlertaListCall = estadoAlertaSrv.findAllEstado();
            estadoAlertaListCall.enqueue(new Callback<List<EstadoAlerta>>() {
                @Override
                public void onResponse(Call<List<EstadoAlerta>> call, Response<List<EstadoAlerta>> response) {
                    if (response.isSuccessful()) {
                        List<EstadoAlerta> list = response.body();
                        for (EstadoAlerta est: list){
                            lista.add(est.getNm());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<EstadoAlerta>> call, Throwable t) {

                }
            });
            i++;
        }
        return lista;
    }
}