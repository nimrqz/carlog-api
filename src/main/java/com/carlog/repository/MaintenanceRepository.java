package com.carlog.repository;

import com.carlog.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByVehicleId(Long vehicleId);

    @Query("SELECT COALESCE(SUM(m.value), 0) FROM Maintenance m WHERE m.vehicle.id = :vehicleId")
    BigDecimal sumValuesByVehicleId(@Param("vehicleId") Long vehicleId);
}
