package edu.alexey.spring.library.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long bookId;

	private String name;
}
