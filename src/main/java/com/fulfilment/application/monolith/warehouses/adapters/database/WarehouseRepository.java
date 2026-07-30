package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Page;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  /**
   * Search warehouses with optional filters, sorting and pagination.
   */
  public List<Warehouse> search(
      String location,
      Integer minCapacity,
      Integer maxCapacity,
      String sortBy,
      String sortOrder,
      Integer page,
      Integer pageSize) {

    StringBuilder query = new StringBuilder("archivedAt is null");
    java.util.Map<String, Object> params = new java.util.HashMap<>();

    if (location != null) {
      query.append(" and location = :location");
      params.put("location", location);
    }
    if (minCapacity != null) {
      query.append(" and capacity >= :minCapacity");
      params.put("minCapacity", minCapacity);
    }
    if (maxCapacity != null) {
      query.append(" and capacity <= :maxCapacity");
      params.put("maxCapacity", maxCapacity);
    }

    // Determine sort
    Sort sort = Sort.by("createdAt");
    if ("capacity".equalsIgnoreCase(sortBy)) {
      sort = Sort.by("capacity");
    }
    if ("desc".equalsIgnoreCase(sortOrder)) {
      sort = sort.descending();
    } else {
      sort = sort.ascending();
    }

    PanacheQuery<DbWarehouse> pq = find(query.toString(), sort, params);

    // Apply pagination defaults and limits
    int p = page != null && page >= 0 ? page : 0;
    int ps = pageSize != null && pageSize > 0 ? Math.min(pageSize, 100) : 10;
    pq.page(Page.of(p, ps));

    return pq.list().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;
    
    this.persist(dbWarehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    // Prefer a managed entity update so JPA optimistic locking and change tracking work
    DbWarehouse db = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (db != null) {
      db.location = warehouse.location;
      db.capacity = warehouse.capacity;
      db.stock = warehouse.stock;
      db.archivedAt = warehouse.archivedAt;
      // Persisting is not needed for managed entities, but ensure flush to apply within transaction
      getEntityManager().flush();
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'remove'");
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = find("businessUnitCode", buCode).firstResult();
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }
}
