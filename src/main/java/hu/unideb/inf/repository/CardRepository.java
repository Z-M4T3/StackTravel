package hu.unideb.inf.repository;

import hu.unideb.inf.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findByCategoryId(Integer categoryId);
}
