package org.example.transactionservice.repositories;

import org.example.transactionservice.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndId(Long userId, Long id);
    boolean existsByIdAndUserId(Long id, Long userId);
}
