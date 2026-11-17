package hu.unideb.inf.repository;

import hu.unideb.inf.model.entity.Card;
import hu.unideb.inf.model.entity.Trip;
import hu.unideb.inf.model.entity.TripCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TripCardRepository extends JpaRepository<TripCard, TripCard.Id> {
    Collection<TripCard> findByIdTripId(Integer tripId);
}
