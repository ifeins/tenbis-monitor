package com.ifeins.tenbismonit.fragments;

/**
 * @author ifeins
 */

public interface HomeAdapterFragment {

    void subscribeForUpdates();

    void unsubscribeForUpdates();

    void onRefreshError();
}
