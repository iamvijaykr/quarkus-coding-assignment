package com.warehouse.api;

import com.warehouse.api.beans.Warehouse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WarehouseResource {

  @GET
  List<Warehouse> listAllWarehousesUnits();

  @POST
  Warehouse createANewWarehouseUnit(@NotNull Warehouse data);

  @GET
  @Path("{id}")
  Warehouse getAWarehouseUnitByID(@PathParam("id") String id);

  @DELETE
  @Path("{id}")
  void archiveAWarehouseUnitByID(@PathParam("id") String id);

  @POST
  @Path("{businessUnitCode}/replacement")
  Warehouse replaceTheCurrentActiveWarehouse(
      @PathParam("businessUnitCode") String businessUnitCode,
      @NotNull Warehouse data);

  @GET
  @Path("search")
  List<Warehouse> search(
      @QueryParam("location") String location,
      @QueryParam("minCapacity") Integer minCapacity,
      @QueryParam("maxCapacity") Integer maxCapacity,
      @QueryParam("sortBy") String sortBy,
      @QueryParam("sortOrder") String sortOrder,
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize);
}
