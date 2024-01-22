package edu.alexey.spring.library.exceptions;

import java.time.LocalDateTime;

public class AlreadyCoveredIssueException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	public AlreadyCoveredIssueException(long issueId, LocalDateTime returnedAt) {
		super("Возврат по данной выдаче \"" + issueId + "\" уже был произведён " + returnedAt);
	}

}
