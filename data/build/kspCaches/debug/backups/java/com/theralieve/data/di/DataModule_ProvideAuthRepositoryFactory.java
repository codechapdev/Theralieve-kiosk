package com.theralieve.data.di;

import com.theralieve.data.api.ApiService;
import com.theralieve.data.local.TheraJetDatabase;
import com.theralieve.domain.repository.AuthRepository;
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
public final class DataModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<TheraJetDatabase> databaseProvider;

  private DataModule_ProvideAuthRepositoryFactory(Provider<ApiService> apiServiceProvider,
      Provider<TheraJetDatabase> databaseProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(apiServiceProvider.get(), databaseProvider.get());
  }

  public static DataModule_ProvideAuthRepositoryFactory create(
      Provider<ApiService> apiServiceProvider, Provider<TheraJetDatabase> databaseProvider) {
    return new DataModule_ProvideAuthRepositoryFactory(apiServiceProvider, databaseProvider);
  }

  public static AuthRepository provideAuthRepository(ApiService apiService,
      TheraJetDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideAuthRepository(apiService, database));
  }
}
