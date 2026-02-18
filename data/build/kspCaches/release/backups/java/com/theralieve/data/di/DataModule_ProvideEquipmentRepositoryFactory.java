package com.theralieve.data.di;

import com.theralieve.data.api.ApiService;
import com.theralieve.data.local.TheraJetDatabase;
import com.theralieve.data.storage.PreferenceManager;
import com.theralieve.domain.repository.EquipmentRepository;
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
public final class DataModule_ProvideEquipmentRepositoryFactory implements Factory<EquipmentRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<TheraJetDatabase> databaseProvider;

  private final Provider<PreferenceManager> preferenceManagerProvider;

  private DataModule_ProvideEquipmentRepositoryFactory(Provider<ApiService> apiServiceProvider,
      Provider<TheraJetDatabase> databaseProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.databaseProvider = databaseProvider;
    this.preferenceManagerProvider = preferenceManagerProvider;
  }

  @Override
  public EquipmentRepository get() {
    return provideEquipmentRepository(apiServiceProvider.get(), databaseProvider.get(), preferenceManagerProvider.get());
  }

  public static DataModule_ProvideEquipmentRepositoryFactory create(
      Provider<ApiService> apiServiceProvider, Provider<TheraJetDatabase> databaseProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    return new DataModule_ProvideEquipmentRepositoryFactory(apiServiceProvider, databaseProvider, preferenceManagerProvider);
  }

  public static EquipmentRepository provideEquipmentRepository(ApiService apiService,
      TheraJetDatabase database, PreferenceManager preferenceManager) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideEquipmentRepository(apiService, database, preferenceManager));
  }
}
