package com.mapbox.services.android.navigation.v5.navigation;

import android.location.Location;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.services.android.navigation.v5.location.LocationValidator;

import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class NavigationLocationEngineTest {

  @Test
  public void onConnected_engineRequestsUpdates() {
    LocationEngine locationEngine = mock(LocationEngine.class);
    NavigationLocationEngine listener = buildListener(locationEngine);

    listener.onConnected();

    verify(locationEngine).requestLocationUpdates();
  }

  @Test
  public void queueValidLocationUpdate_threadReceivesUpdate() {
    RouteProcessorBackgroundThread thread = mock(RouteProcessorBackgroundThread.class);
    LocationValidator validator = mock(LocationValidator.class);
    when(validator.isValidUpdate(any(Location.class))).thenReturn(true);
    NavigationLocationEngine listener = buildListener(thread, validator);

    listener.onLocationChanged(mock(Location.class));

    verify(thread).queueUpdate(any(NavigationLocationUpdate.class));
  }

  @Test
  public void queueInvalidLocationUpdate_threadDoesNotReceiveUpdate() {
    RouteProcessorBackgroundThread thread = mock(RouteProcessorBackgroundThread.class);
    LocationValidator validator = mock(LocationValidator.class);
    when(validator.isValidUpdate(any(Location.class))).thenReturn(false);
    NavigationLocationEngine listener = buildListener(thread, validator);

    listener.onLocationChanged(mock(Location.class));

    verifyZeroInteractions(thread);
  }

  private NavigationLocationEngine buildListener(RouteProcessorBackgroundThread thread,
                                                 LocationValidator validator) {
    MapboxNavigation mapboxNavigation = mock(MapboxNavigation.class);
    when(mapboxNavigation.options()).thenReturn(MapboxNavigationOptions.builder().build());
    return new NavigationLocationEngine(thread, mapboxNavigation,
      mock(LocationEngine.class), validator);
  }

  private NavigationLocationEngine buildListener(LocationEngine locationEngine) {
    MapboxNavigation mapboxNavigation = mock(MapboxNavigation.class);
    when(mapboxNavigation.options()).thenReturn(MapboxNavigationOptions.builder().build());
    return new NavigationLocationEngine(mock(RouteProcessorBackgroundThread.class),
      mapboxNavigation, locationEngine, mock(LocationValidator.class));
  }
}