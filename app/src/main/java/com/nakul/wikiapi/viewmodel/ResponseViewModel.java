package com.nakul.wikiapi.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nakul.wikiapi.model.NetworkResponseModel;
import com.nakul.wikiapi.model.SearchResultModel;
import com.nakul.wikiapi.network.VolleyHelper;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResponseViewModel extends ViewModel {

    private MutableLiveData<NetworkResponseModel> categoryDataList;
    private final String URL = "https://en.wikipedia.org/w/api.php?format=json&action=query&generator=search&prop=pageimages|extracts&pilimit=max&exintro&explaintext&exsentences=1&exlimit=max&gsrsearch=";

    public LiveData<NetworkResponseModel> getSearchObservable() {
        if (categoryDataList == null) {
            categoryDataList = new MutableLiveData<>();
        }
        return categoryDataList;
    }

    public void getSearchResult(String query) {

        VolleyHelper.getInstance().sendRequestToApi(Request.Method.GET, URL + query, null, new VolleyHelper.VolleyCallback() {

            @Override
            public void onSuccessResponse(String result, int responseCode, boolean error) {
                try {
                    JSONObject objects = new JSONObject(result).optJSONObject("query").optJSONObject("pages");
                    Iterator<String> objectIterator = objects.keys();
                    Type type = new TypeToken<SearchResultModel>() {
                    }.getType();
                    Gson gson = new Gson();
                    List<SearchResultModel> languageResponseModelList = new ArrayList<>();

                    while (objectIterator.hasNext()) {
                        String key = objectIterator.next();
                        JSONObject searchJsonObj = objects.optJSONObject(key);
                        SearchResultModel model = gson.fromJson(searchJsonObj.toString(), type);
                        if (searchJsonObj.has("thumbnail"))
                            model.setThumbImage(searchJsonObj.optJSONObject("thumbnail").optString("source"));
                        languageResponseModelList.add(model);
                    }

                    NetworkResponseModel responseModel = new NetworkResponseModel();
                    responseModel.setResponseModelSet(languageResponseModelList);
                    categoryDataList.setValue(responseModel);
                } catch (JSONException e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                    NetworkResponseModel errorResponseModel = new NetworkResponseModel();
                    errorResponseModel.setResponseCode(responseCode);
                    errorResponseModel.setResponseStr(e.getMessage());
                    categoryDataList.setValue(errorResponseModel);
                }
            }

            @Override
            public void onErrorResponse(String result, int responseCode) {
                NetworkResponseModel errorResponseModel = new NetworkResponseModel();
                errorResponseModel.setResponseCode(responseCode);
                errorResponseModel.setResponseStr(result);
                categoryDataList.setValue(errorResponseModel);
            }
        });

    }

}
