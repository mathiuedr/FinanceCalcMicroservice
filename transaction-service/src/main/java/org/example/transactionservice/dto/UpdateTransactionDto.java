package org.example.transactionservice.dto;

import org.example.transactionservice.entities.TransactionType;

public record UpdateTransactionDto(TransactionType type,Double sum, Long transactionId) {
}
