package edu.alexey.spring.library.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Issue implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long issueId;

	private long bookId;

	private long readerId;

	private LocalDateTime issuedAt;

	private LocalDateTime returnedAt;
}
