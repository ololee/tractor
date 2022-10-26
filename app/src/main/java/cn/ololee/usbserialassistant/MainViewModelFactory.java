package cn.ololee.usbserialassistant;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
  private Application application;

  public MainViewModelFactory(@NonNull Application application) {
    super(application);
    this.application = application;
  }

  @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(MainViewModel.class)) {
      return (T) new MainViewModel(application);
    }
    return super.create(modelClass);
  }
}
