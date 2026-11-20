package com.example.projectaethera.viewmodel;



import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;



import com.example.projectaethera.model.remote.SearchResponse;

import com.example.projectaethera.repository.SearchRepository;



import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



/**

 * ViewModel for the search screen — finds users by query string.

 * <p>

 * I call the search API as the user types (from the fragment) and expose

 * results or errors through LiveData so the list updates without blocking the UI.

 */

public class SearchingViewModel extends ViewModel {



    /** Matching users and metadata from the last successful search. */

    private final MutableLiveData<SearchResponse> searchResult = new MutableLiveData<>();

    /** Message when search fails or returns nothing useful. */

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();



    private final SearchRepository searchRepository;



    /** Creates the ViewModel and search repository. */

    public SearchingViewModel() {

        this.searchRepository = new SearchRepository();

    }



    /**

     * Runs a user search for the given query.

     *

     * @param userId logged-in user (some APIs need this for privacy/block lists)

     * @param query text the user typed in the search box

     */

    public void searchUser(int userId, String query) {

        searchRepository.searching(userId, query).enqueue(new Callback<SearchResponse>() {

            @Override

            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    searchResult.setValue(response.body());

                } else {

                    errorMessage.setValue("unable to search users");

                }

            }



            @Override

            public void onFailure(Call<SearchResponse> call, Throwable throwable) {

                errorMessage.setValue("network error");

            }

        });

    }



    /**

     * Latest search results for the RecyclerView.

     *

     * @return LiveData with {@link SearchResponse}

     */

    public MutableLiveData<SearchResponse> getSearchResult() {

        return searchResult;

    }



    /**

     * Error from the last search attempt.

     *

     * @return LiveData with error text

     */

    public MutableLiveData<String> getErrorMessage() {

        return errorMessage;

    }

}

