package co.inventorsoft.academy.schoolapplication.service;

public interface MailService {
    void sendEmail(String to, String subject, String body);
}
