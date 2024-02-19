package edu.alexey.spring.library.api.dto;

import java.time.LocalDateTime;

public record IssueResponseDto(Long issueId, long bookId, long readerId, LocalDateTime issuedAt,
		LocalDateTime returnedAt) {
}
