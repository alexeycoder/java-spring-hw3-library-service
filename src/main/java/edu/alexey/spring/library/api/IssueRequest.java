package edu.alexey.spring.library.api;

import lombok.Data;

@Data
public class IssueRequest {

	private long readerId;
	private long bookId;
}
