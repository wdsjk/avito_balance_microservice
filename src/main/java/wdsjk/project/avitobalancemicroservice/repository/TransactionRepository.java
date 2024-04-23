package wdsjk.project.avitobalancemicroservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wdsjk.project.avitobalancemicroservice.domain.Transaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query(value = "SELECT t FROM Transaction t WHERE t.userFromId = :userId OR t.userToId = :userId")
    Optional<List<Transaction>> findAllByUserId(String userId, Pageable pageable);
}
