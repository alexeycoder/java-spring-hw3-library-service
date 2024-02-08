package edu.alexey.spring.library.aux;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class RecoverExceptionAspect {

	@Around("@annotation(edu.alexey.spring.library.aux.RecoverException)")
	public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint)
			throws Throwable {

		try {
			Object result = joinPoint.proceed();
			return result;

		} catch (RuntimeException e) {

			Class<? extends RuntimeException>[] noRecoverFor = extractNoRecoverFor(joinPoint);
			for (Class<? extends RuntimeException> exType : noRecoverFor) {

				if (exType.getClass().isAssignableFrom(e.getClass())) {
					throw e;
				}
			}

			log.info("Подавлено исключение {} в {}.{}",
					e.getClass().getSimpleName(),
					joinPoint.getTarget().getClass().getSimpleName(),
					joinPoint.getSignature().getName());

			return getDefault(joinPoint);
		}
	}

	private Class<? extends RuntimeException>[] extractNoRecoverFor(ProceedingJoinPoint joinPoint) {

		if (joinPoint.getSignature() instanceof MethodSignature signature) {

			RecoverException anno = signature.getMethod().getAnnotation(RecoverException.class);
			return anno.noRecoverFor();

		} else {
			throw new IllegalArgumentException();
		}
	}

	private Object getDefault(ProceedingJoinPoint joinPoint) {
		if (joinPoint.getSignature() instanceof MethodSignature signature) {

			Class<?> returnType = signature.getMethod().getReturnType();
			if (returnType == Boolean.class) {
				return false;
			} else if (returnType == Byte.class) {
				return (byte) 0;
			} else if (returnType == Character.class) {
				return '\0';
			} else if (Number.class.isAssignableFrom(returnType)) {
				return 0;
			}

			return null;

		} else {
			throw new IllegalArgumentException();
		}
	}

	//	public static void main(String[] args) {
	//		boolean variable = true;
	//		System.out.println(((Object) variable).getClass());
	//
	//		System.out.println(Number.class.isAssignableFrom(((Object) 10).getClass()));
	//	}
}
