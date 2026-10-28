package hu.unideb.inf.repository;

import hu.unideb.inf.model.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {
    List<Trip> findAllByUserId(Integer userId);

    Optional<Trip> findByIdAndUserId(Integer id, Integer userId);

}