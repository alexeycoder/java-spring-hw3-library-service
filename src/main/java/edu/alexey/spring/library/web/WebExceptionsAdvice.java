//package edu.alexey.spring.library.web;
//
//import java.util.NoSuchElementException;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ControllerAdvice
//public class WebExceptionsAdvice {
//
//	@ExceptionHandler(NoSuchElementException.class)
//	@ResponseStatus(HttpStatus.NOT_FOUND)
//	String noSuchEntryHandler(NoSuchElementException ex) {
//		return "error404";
//	}
//
//	@ExceptionHandler(RuntimeException.class)
//	@ResponseStatus(HttpStatus.FORBIDDEN)
//	String wrongRequestHandler(RuntimeException ex) {
//		return "error403";
//	}
//
//}
