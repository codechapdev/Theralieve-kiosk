package com.theralieve.data.di;

import com.theralieve.data.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DataModule_ProvideApiServiceFactory implements Factory<ApiService> {
  @Override
  public ApiService get() {
    return provideApiService();
  }

  public static DataModule_ProvideApiServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ApiService provideApiService() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideApiService());
  }

  private static final class InstanceHolder {
    static final DataModule_ProvideApiServiceFactory INSTANCE = new DataModule_ProvideApiServiceFactory();
  }
}
