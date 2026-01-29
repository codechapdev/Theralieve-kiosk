package com.codechaps.therajet.data.repository;

import com.codechaps.therajet.data.api.ApiService;
import com.codechaps.therajet.data.local.TheraJetDatabase;
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

  private EquipmentRepositoryImpl_Factory(Provider<ApiService> apiServiceProvider,
      Provider<TheraJetDatabase> databaseProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public EquipmentRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), databaseProvider.get());
  }

  public static EquipmentRepositoryImpl_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<TheraJetDatabase> databaseProvider) {
    return new EquipmentRepositoryImpl_Factory(apiServiceProvider, databaseProvider);
  }

  public static EquipmentRepositoryImpl newInstance(ApiService apiService,
      TheraJetDatabase database) {
    return new EquipmentRepositoryImpl(apiService, database);
  }
}
