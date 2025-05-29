package com.bruce.erp.service;

import com.bruce.erp.dto.message.MessageResponse;
import com.bruce.erp.model.entity.Message;
import com.bruce.erp.model.entity.Payslip;
import com.bruce.erp.repository.EmployeeRepository;
import com.bruce.erp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmployeeRepository employeeRepository;
    private final JavaMailSender emailSender;

    public List<MessageResponse> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getMessagesByEmployeeCode(String employeeCode) {
        var employee = employeeRepository.findByCodeIgnoreCase(employeeCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + employeeCode));

        return messageRepository.findByEmployee(employee).stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getMessagesByMonthAndYear(Integer month, Integer year) {
        return messageRepository.findByMonthAndYear(month, year).stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageResponse createPayslipPaidMessage(Payslip payslip) {
        var employee = payslip.getEmployee();
        var monthName = Month.of(payslip.getMonth()).name();
        var year = payslip.getYear();
        var netSalary = payslip.getNetSalary();

        // Format message according to requirements
        String messageContent = String.format(
                "Dear %s, your salary for %s/%d from RCA amounting to %s has been credited to your account %s successfully.",
                employee.getFirstName(),
                monthName,
                year,
                netSalary.toString(),
                employee.getCode()
        );

        var message = Message.builder()
                .employee(employee)
                .message(messageContent)
                .month(payslip.getMonth())
                .year(payslip.getYear())
                .createdAt(LocalDateTime.now())
                .status(Message.MessageStatus.PENDING)
                .build();

        var savedMessage = messageRepository.save(message);

        // Send email asynchronously
        sendEmail(savedMessage);

        return MessageResponse.fromEntity(savedMessage);
    }

    @Async
    public void sendEmail(Message message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(message.getEmployee().getEmail());
            mailMessage.setSubject("Salary Payment Notification");
            mailMessage.setText(message.getMessage());

            emailSender.send(mailMessage);

            message.setStatus(Message.MessageStatus.SENT);
            message.setSentAt(LocalDateTime.now());
            messageRepository.save(message);
        } catch (Exception e) {
            message.setStatus(Message.MessageStatus.FAILED);
            messageRepository.save(message);
        }
    }
}
