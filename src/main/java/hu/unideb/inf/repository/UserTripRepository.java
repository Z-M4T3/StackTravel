package hu.unideb.inf.repository;

import hu.unideb.inf.model.entity.UserTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTripRepository extends JpaRepository<UserTrip, UserTrip.Id> {
}
