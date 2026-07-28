package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StoreEventObserver {

  private static final Logger LOGGER = Logger.getLogger(StoreEventObserver.class.getName());

  @Inject 
  LegacyStoreManagerGateway legacyStoreManagerGateway;

  // Observe asynchronously, but only after the transaction has successfully committed
  public void onStoreCreated(@ObservesAsync(during = TransactionPhase.AFTER_SUCCESS) StoreCreatedEvent event) {
    LOGGER.info("Store created event received, syncing with legacy system: " + event.getStore().id);
    legacyStoreManagerGateway.createStoreOnLegacySystem(event.getStore());
  }

  // Observe asynchronously, but only after the transaction has successfully committed
  public void onStoreUpdated(@ObservesAsync(during = TransactionPhase.AFTER_SUCCESS) StoreUpdatedEvent event) {
    LOGGER.info("Store updated event received, syncing with legacy system: " + event.getStore().id);
    legacyStoreManagerGateway.updateStoreOnLegacySystem(event.getStore());
  }
}
