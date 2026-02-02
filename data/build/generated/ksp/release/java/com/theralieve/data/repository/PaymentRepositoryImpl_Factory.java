package com.theralieve.data.repository;

import com.theralieve.data.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PaymentRepositoryImpl_Factory implements Factory<PaymentRepositoryImpl> {
  private final Provider<ApiService> apiServiceProvider;

  private PaymentRepositoryImpl_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public PaymentRepositoryImpl get() {
    return newInstance(apiServiceProvider.get());
  }

  public static PaymentRepositoryImpl_Factory create(Provider<ApiService> apiServiceProvider) {
    return new PaymentRepositoryImpl_Factory(apiServiceProvider);
  }

  public static PaymentRepositoryImpl newInstance(ApiService apiService) {
    return new PaymentRepositoryImpl(apiService);
  }
}
