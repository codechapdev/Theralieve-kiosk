package com.theralieve.data.repository;

import com.theralieve.data.api.ApiService;
import com.theralieve.data.local.TheraJetDatabase;
import com.theralieve.data.storage.PreferenceManager;
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
public final class EquipmentRepositoryImpl_Factory implements Factory<EquipmentRepositoryImpl> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<TheraJetDatabase> databaseProvider;

  private final Provider<PreferenceManager> preferenceManagerProvider;

  private EquipmentRepositoryImpl_Factory(Provider<ApiService> apiServiceProvider,
      Provider<TheraJetDatabase> databaseProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.databaseProvider = databaseProvider;
    this.preferenceManagerProvider = preferenceManagerProvider;
  }

  @Override
  public EquipmentRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), databaseProvider.get(), preferenceManagerProvider.get());
  }

  public static EquipmentRepositoryImpl_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<TheraJetDatabase> databaseProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    return new EquipmentRepositoryImpl_Factory(apiServiceProvider, databaseProvider, preferenceManagerProvider);
  }

  public static EquipmentRepositoryImpl newInstance(ApiService apiService,
      TheraJetDatabase database, PreferenceManager preferenceManager) {
    return new EquipmentRepositoryImpl(apiService, database, preferenceManager);
  }
}
