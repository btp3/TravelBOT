package retrofit;

import models.LocationModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Iron_Man on 16/09/17.
 */

public interface APIServices {

    @POST("/api/location/create/")
    Call<LocationModel> savePost(@Body LocationModel locationModel);

}