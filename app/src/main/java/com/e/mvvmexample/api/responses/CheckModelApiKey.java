package com.e.mvvmexample.api.responses;

public class CheckModelApiKey {
    protected static boolean isModelApiKeyValid(ListResponse response){
        return response.getError() == null;
    }

    protected static boolean isModelApiKeyValid(ItemResponse response) {
        return response.getError() == null;
    }
}
