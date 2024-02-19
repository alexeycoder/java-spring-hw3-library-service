package edu.alexey.spring.library.api.dto;

import java.time.LocalDateTime;

public record IssueCreationDto(long bookId, long readerId, LocalDateTime issuedAt) {
}
