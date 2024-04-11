package wdsjk.project.avitobalancemicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wdsjk.project.avitobalancemicroservice.domain.Balance;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {
    Optional<Balance> findByUserId(String userId);
}
