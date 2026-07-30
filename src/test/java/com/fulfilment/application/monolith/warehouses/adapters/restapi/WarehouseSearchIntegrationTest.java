package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class WarehouseSearchIntegrationTest {

  @Inject
  WarehouseRepository repository;

  @Test
  @jakarta.transaction.Transactional
  public void testSearchFiltersAndPagination() {
    // cleanup existing
    repository.deleteAll();

    Warehouse w1 = new Warehouse();
    w1.businessUnitCode = "W1";
    w1.location = "AMSTERDAM-001";
    w1.capacity = 50;
    w1.stock = 10;
    repository.create(w1);

    Warehouse w2 = new Warehouse();
    w2.businessUnitCode = "W2";
    w2.location = "ZWOLLE-001";
    w2.capacity = 40;
    w2.stock = 5;
    repository.create(w2);

    Warehouse w3 = new Warehouse();
    w3.businessUnitCode = "W3";
    w3.location = "AMSTERDAM-001";
    w3.capacity = 80;
    w3.stock = 20;
    repository.create(w3);

    // search by location
    List<com.fulfilment.application.monolith.warehouses.domain.models.Warehouse> res1 =
        repository.search("AMSTERDAM-001", null, null, "createdAt", "asc", 0, 10);
    Assertions.assertEquals(2, res1.size());

    // minCapacity filter
    List<com.fulfilment.application.monolith.warehouses.domain.models.Warehouse> res2 =
        repository.search(null, 60, null, "createdAt", "asc", 0, 10);
    Assertions.assertEquals(1, res2.size());

    // pagination
    List<com.fulfilment.application.monolith.warehouses.domain.models.Warehouse> page1 =
        repository.search(null, null, null, "createdAt", "asc", 0, 2);
    List<com.fulfilment.application.monolith.warehouses.domain.models.Warehouse> page2 =
        repository.search(null, null, null, "createdAt", "asc", 1, 2);
    Assertions.assertEquals(2, page1.size());
    Assertions.assertTrue(page2.size() >= 1);
  }
}
