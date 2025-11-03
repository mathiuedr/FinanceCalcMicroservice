package org.example.transactionservice.dto;

import org.example.transactionservice.entities.TransactionType;

public record CreateTranstactionDto(TransactionType type, Double sum) {
}
