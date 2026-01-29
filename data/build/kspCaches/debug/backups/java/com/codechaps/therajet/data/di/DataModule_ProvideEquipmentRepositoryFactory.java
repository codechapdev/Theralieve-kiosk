package com.codechaps.therajet.data.di;

import com.codechaps.therajet.data.api.ApiService;
import com.codechaps.therajet.data.local.TheraJetDatabase;
import com.codechaps.therajet.domain.repository.EquipmentRepository;
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

  private DataModule_ProvideEquipmentRepositoryFactory(Provider<ApiService> apiServiceProvider,
      Provider<TheraJetDatabase> databaseProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public EquipmentRepository get() {
    return provideEquipmentRepository(apiServiceProvider.get(), databaseProvider.get());
  }

  public static DataModule_ProvideEquipmentRepositoryFactory create(
      Provider<ApiService> apiServiceProvider, Provider<TheraJetDatabase> databaseProvider) {
    return new DataModule_ProvideEquipmentRepositoryFactory(apiServiceProvider, databaseProvider);
  }

  public static EquipmentRepository provideEquipmentRepository(ApiService apiService,
      TheraJetDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideEquipmentRepository(apiService, database));
  }
}
