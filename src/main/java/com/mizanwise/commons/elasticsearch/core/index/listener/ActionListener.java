package com.mizanwise.commons.elasticsearch.core.index.listener;

import com.mizanwise.commons.elasticsearch.core.index.response.IndexResponse;

public interface ActionListener<E> {

    void onSuccess(E e, IndexResponse response);

    void onFailure(E e, Exception exception);

}
