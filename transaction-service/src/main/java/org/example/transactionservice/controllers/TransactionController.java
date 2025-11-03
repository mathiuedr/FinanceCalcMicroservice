package org.example.transactionservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.dto.CreateTranstactionDto;
import org.example.transactionservice.dto.UpdateTransactionDto;
import org.example.transactionservice.entities.Transaction;
import org.example.transactionservice.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping()
    public ResponseEntity<String> addTransaction(@RequestHeader(name = "User-Id") Long userId, @RequestBody CreateTranstactionDto transactionDto){
        transactionService.addTransaction(transactionDto.type(),transactionDto.sum(),userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction successfully created");
    }
    @GetMapping()
    public ResponseEntity<List<Transaction>> getHistory(@RequestHeader(name="User-Id") Long userId){
        return ResponseEntity.ok(transactionService.getHistoryById(userId));
    }
    @PatchMapping()
    public ResponseEntity<String> updateTransaction(@RequestHeader(name = "User-Id") Long userId, @RequestBody UpdateTransactionDto transactionDto){
        transactionService.updateTransaction(transactionDto.type(),transactionDto.sum(),userId, transactionDto.transactionId());
        return ResponseEntity.ok("Sum of transaction was successfully updated");
    }
    @DeleteMapping()
    public ResponseEntity<String> deleteTransaction(@RequestHeader(name="User-id") Long userId,@RequestParam("id") Long id){
        transactionService.deleteTransaction(userId,id);
        return ResponseEntity.ok("Transaction was successfully deleted");
    }
}
