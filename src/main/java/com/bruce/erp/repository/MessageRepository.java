package com.bruce.erp.repository;

import com.bruce.erp.model.entity.Employee;
import com.bruce.erp.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByEmployee(Employee employee);
    
    List<Message> findByEmployeeAndStatus(Employee employee, Message.MessageStatus status);
    
    List<Message> findByMonthAndYear(Integer month, Integer year);
    
    List<Message> findByStatus(Message.MessageStatus status);
}