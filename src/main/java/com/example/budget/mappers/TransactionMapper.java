package com.example.budget.mappers;

import com.example.budget.dto.TransactionDTO;
import com.example.budget.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "categoryId", target = "category.id")
    Transaction toTransaction(TransactionDTO transactionDTO);

    @Mapping(source = "category.id", target = "categoryId")
    TransactionDTO toTransactionDTO(Transaction transaction);
}
