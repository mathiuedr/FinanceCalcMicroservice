package org.example.transactionservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.transactionservice.entities.Transaction;
import org.example.transactionservice.entities.TransactionType;
import org.example.transactionservice.exceptions.TransactionNotFoundException;
import org.example.transactionservice.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public void addTransaction(TransactionType type, Double sum, Long userId){
        Transaction transaction = new Transaction();
        transaction.setSum(sum);
        transaction.setType(type);
        transaction.setUserId(userId);
        transactionRepository.save(transaction);
    }
    public List<Transaction> getHistoryById(Long userId){
        return transactionRepository.findByUserId(userId);
    }
    @Transactional
    public void updateTransaction(TransactionType type,Double sum, Long userId,Long transactionId){
        if(!transactionRepository.existsByIdAndUserId(transactionId,userId)) throw new TransactionNotFoundException();
        Transaction transaction = new Transaction(transactionId,type,sum,userId);
        transactionRepository.save(transaction);
    }
    public void deleteTransaction(Long userId, Long id){
        if(transactionRepository.findByUserIdAndId(userId,id).isEmpty()) throw new TransactionNotFoundException();
        transactionRepository.deleteById(id);
    }

}
