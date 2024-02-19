package edu.alexey.spring.library.api.dto;

import java.time.LocalDateTime;

public record IssueDescriptionResponceDto(
		long issueId,
		LocalDateTime issuedAt,
		LocalDateTime returnedAt,
		BookResponseDto book,
		ReaderResponseDto reader) {
}
