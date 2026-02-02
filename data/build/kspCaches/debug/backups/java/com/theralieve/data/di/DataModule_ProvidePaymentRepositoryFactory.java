package com.theralieve.data.di;

import com.theralieve.data.api.ApiService;
import com.theralieve.domain.repository.PaymentRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DataModule_ProvidePaymentRepositoryFactory implements Factory<PaymentRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private DataModule_ProvidePaymentRepositoryFactory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public PaymentRepository get() {
    return providePaymentRepository(apiServiceProvider.get());
  }

  public static DataModule_ProvidePaymentRepositoryFactory create(
      Provider<ApiService> apiServiceProvider) {
    return new DataModule_ProvidePaymentRepositoryFactory(apiServiceProvider);
  }

  public static PaymentRepository providePaymentRepository(ApiService apiService) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.providePaymentRepository(apiService));
  }
}
