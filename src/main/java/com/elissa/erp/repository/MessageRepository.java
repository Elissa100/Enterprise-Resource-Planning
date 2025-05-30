package com.elissa.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elissa.erp.model.Employee;
import com.elissa.erp.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByEmployee(Employee employee);
    
    List<Message> findByEmployeeAndStatus(Employee employee, Message.MessageStatus status);
    
    List<Message> findByMonthAndYear(Integer month, Integer year);
    
    List<Message> findByStatus(Message.MessageStatus status);
}