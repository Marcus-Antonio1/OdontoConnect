package com.odontoconnect.conversation.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

// Representa uma conversa iniciada por um aluno em determinado canal.
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificação do aluno que iniciou a conversa.
    @Column(name = "student_name", nullable = false, length = 150)
    private String studentName;

    @Column(name = "student_email", length = 180)
    private String studentEmail;

    // Canal pelo qual a conversa foi iniciada.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Channel channel;

    // Situação atual da conversa.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConversationStatus status = ConversationStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Construtor exigido pelo JPA.
    protected Conversation() {
    }

    // Construtor utilizado para criar novas conversas.
    public Conversation(String studentName, String studentEmail, Channel channel) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.channel = channel;
    }

    // Define automaticamente as datas antes de inserir o registro.
    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    // Atualiza a data sempre que a entidade for modificada.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Getters.
    public Long getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Channel getChannel() {
        return channel;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}