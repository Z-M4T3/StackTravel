package hu.unideb.inf.repository;

import hu.unideb.inf.model.dto.UserTripDto;
import hu.unideb.inf.model.entity.User;
import hu.unideb.inf.model.entity.UserTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTripRepository extends JpaRepository<UserTrip, UserTrip.Id> {
    List<UserTrip> findByUser(User user);
}
