package com.gm.template.di;

import android.app.Application;
import coil.ImageLoader;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class CoilModule_ProvideImageLoaderFactory implements Factory<ImageLoader> {
  private final Provider<Application> appProvider;

  public CoilModule_ProvideImageLoaderFactory(Provider<Application> appProvider) {
    this.appProvider = appProvider;
  }

  @Override
  public ImageLoader get() {
    return provideImageLoader(appProvider.get());
  }

  public static CoilModule_ProvideImageLoaderFactory create(Provider<Application> appProvider) {
    return new CoilModule_ProvideImageLoaderFactory(appProvider);
  }

  public static ImageLoader provideImageLoader(Application app) {
    return Preconditions.checkNotNullFromProvides(CoilModule.INSTANCE.provideImageLoader(app));
  }
}
