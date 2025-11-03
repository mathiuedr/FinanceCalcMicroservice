package org.example.transactionservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
@Entity
@Table(name="Transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TransactionType type;
    private Double sum;
    private Long userId;
}


